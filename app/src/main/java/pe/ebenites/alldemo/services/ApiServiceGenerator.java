package pe.ebenites.alldemo.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import pe.ebenites.alldemo.BuildConfig;
import pe.ebenites.alldemo.activities.LoginActivity;
import pe.ebenites.alldemo.models.ResponseError;
import pe.ebenites.alldemo.models.User;
import pe.ebenites.alldemo.util.Constants;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ebenites on 07/01/2017.
 * Best Practice: ApiServiceGenerator https://futurestud.io/blog/retrofit-getting-started-and-android-client#servicegenerator
 */
public final class ApiServiceGenerator {

    private static final String TAG = ApiServiceGenerator.class.getSimpleName();

    private ApiServiceGenerator() {
    }

    private static Retrofit retrofit;

    /**
     * ApiService with JWT Auth
     * @param context Application context
     * @param serviceClass ApiService interface class
     * @param <S> ApiService interface type parameter
     * @return ApiService instance
     */
    public static <S> S createService(final Context context, final Class<S> serviceClass) {

        if (retrofit == null) {

            OkHttpClient.Builder httpClient = OkHttpClientBuilder(context, true);

            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiService.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build()).build();
        }

        return retrofit.create(serviceClass);
    }

    private static Picasso picasso;

    /**
     * Picasso with JWT Auth
     * @param context Application context
     * @return Picasso instance
     */
    public static Picasso createPicasso(final Context context) {

        if (picasso == null) {

            OkHttpClient.Builder httpClient = OkHttpClientBuilder(context, true);

            // Picasso Auth with downloader: Require implementation 'com.jakewharton.picasso:picasso2-okhttp3-downloader:1.1.0'
            picasso = new Picasso.Builder(context)
                    .downloader(new OkHttp3Downloader(httpClient.build()))
                    .listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Log.e(TAG, exception.toString(), exception);
                        }
                    })
                    .build();

            if(BuildConfig.DEBUG) {
                // Indicators: https://futurestud.io/tutorials/picasso-cache-indicators-logging-stats
                picasso.setIndicatorsEnabled(true);
                picasso.setLoggingEnabled(true);
            }

        }

        return picasso;
    }

    private static OkHttpClient.Builder OkHttpClientBuilder(final Context context, boolean withTokenInterceptor){

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Set Timeout
        httpClient.readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);

        if(BuildConfig.DEBUG) {
            // Retrofit Debug: https://futurestud.io/blog/retrofit-2-log-requests-and-responses
            httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        if(!withTokenInterceptor)
            return httpClient;

        // Retrofit Token: https://futurestud.io/tutorials/retrofit-token-authentication-on-android
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {

                try {

                    Request originalRequest = chain.request();

                    String token = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREF_TOKEN, null);
                    Log.d(TAG, "Loaded Token: " + token);   // load Token from SharedPreferences

                    if (token == null) {    //  firsttime is null
                        return chain.proceed(originalRequest);
                    }

                    Request modifiedRequest = originalRequest.newBuilder()
                            .header("Authorization", token) // add Authorization header
                            .build();

                    Response response = chain.proceed(modifiedRequest); // send request with token

                    if (response.code() == 401) {   // If 'token_expired' -> refresh token

                        try {

                            Log.w(TAG, "Response " + response.code() + ": token_expired, refreshing token...");

                            OkHttpClient.Builder httpClient = OkHttpClientBuilder(context, false);

                            // https://futurestud.io/tutorials/retrofit-synchronous-and-asynchronous-requests
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(ApiService.API_BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .client(httpClient.build())
                                    .build();

                            retrofit2.Response<User> tokenResponse = retrofit.create(ApiService.class).refreshToken(token).execute();
                            // posible: com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 2 path

                            if (!tokenResponse.isSuccessful()) {    // jwt.php JWT_BLACKLIST_GRACE_PERIOD = 30 (multiple concurrent requests)
                                throw new Exception("Response " + tokenResponse.code() + ": Error on refreshing token!");
                            }

                            User userRefreshed = tokenResponse.body();
                            Log.d(TAG, "Refreshed Token: " + userRefreshed.getToken());

                            // Save new token in SharedPreferences
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constants.PREF_TOKEN, userRefreshed.getToken()).apply();

                            modifiedRequest = originalRequest.newBuilder()
                                    .header("Authorization", userRefreshed.getToken()) // add Authorization header
                                    .build();

                            response = chain.proceed(modifiedRequest); // send request with token

                        } catch (final Throwable t) {
                            Log.e(TAG, "onThrowable: " + t.toString(), t);

                            if(context instanceof Activity){

                                PreferenceManager.getDefaultSharedPreferences(context).edit()
                                        .remove(Constants.PREF_TOKEN)
                                        .remove(Constants.PREF_ISLOGGED)
                                        .apply();

                                Activity activity = (Activity) context;
                                if(!activity.isFinishing()){
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toasty.error(context, t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    activity.startActivity(new Intent(activity, LoginActivity.class));
                                    activity.finish();
                                }

                            }

                        }

                    }

                    Log.d(TAG, "HTTP code status " + response.code());

                    return response;

                } catch (Throwable t) {
                    Log.e(TAG, t.toString(), t);
                    throw t;
                }

            }
        });

        return httpClient;
    }

    // Best Practice ResponseError: https://futurestud.io/blog/retrofit-2-simple-error-handling
    public static ResponseError parseError(retrofit2.Response<?> response) {
        if (response.code() == 401)
            return new ResponseError("Su sesión ha finalizado");
        if (response.code() == 404)
            return new ResponseError("Servicio no disponible");
        Converter<ResponseBody, ResponseError> converter = retrofit.responseBodyConverter(ResponseError.class, new Annotation[0]);
        try {
            return converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ResponseError("Error en el servicio");
        }
    }

}
