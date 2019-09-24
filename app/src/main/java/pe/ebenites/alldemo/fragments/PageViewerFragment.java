package pe.ebenites.alldemo.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import es.dmoral.toasty.Toasty;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.activities.MainActivity;
import pe.ebenites.alldemo.models.Page;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import pe.ebenites.alldemo.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoView;

public class PageViewerFragment extends Fragment {

    private static final String TAG = PageViewerFragment.class.getSimpleName();

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_TYPE = "type";

    private Integer id;
    private String title;
    private String type;

    public static Fragment newInstance(Integer id, String title, String type){
        Fragment fragment = new PageViewerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_ID);
            title = getArguments().getString(ARG_TITLE);
            type = getArguments().getString(ARG_TYPE);
        }
    }

    private ProgressRelativeLayout progressLayout;

    private View.OnClickListener onTryAgain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressLayout.showLoading();
            initialize();
        }
    };

    private TextView titleText;
    private WebView webView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(title);
        View view = inflater.inflate(R.layout.fragment_page_viewer, container, false);

        progressLayout = view.findViewById(R.id.progress_layout);
        progressLayout.showLoading();

        titleText = view.findViewById(R.id.title_text);

        if(Constants.PAGE_TYPE_THEORY.equals(type)){
            titleText.setText(R.string.content_theory_text);
        }else if(Constants.PAGE_TYPE_PRACTICE.equals(type)){
            titleText.setText(R.string.content_practice_text);
        }else if(Constants.PAGE_TYPE_REFORCE.equals(type)){
            titleText.setText(R.string.content_reforce_text);
        }

        webView = view.findViewById(R.id.webview);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setLongClickable(false);

        // Onclik img on webview: https://stackoverflow.com/a/22281171/2823916
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new Object(){
            @JavascriptInterface    // For API 17+
            public void performClick(String src) {
                showImage(getContext(), src);
            }
        }, "interface");

        // Onload
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:(function(){" +
                        "   alert('Hola Mundo');" +
                        "})()");
            }
        });

        // Launch an url in an external browser https://stackoverflow.com/a/32116116/2823916
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(i);
                return true;
            }
        });


        initialize();

        return view;
    }

    private void initialize(){
        Log.d(TAG, "initialize");

        ApiServiceGenerator.createService(getContext(), ApiService.class).getMyPage(id, type).enqueue(new Callback<Page>() {
            @Override
            public void onResponse(@NonNull Call<Page> call, @NonNull Response<Page> response) {
                try {
                    if (!response.isSuccessful()) {
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                    Page page = response.body();
                    Log.d(TAG, "page: " +  page);

                    if(page == null || page.getBody() == null || page.getBody().trim().length() == 0){
                        progressLayout.showEmpty(R.drawable.ic_empty, getString(R.string.data_empty), getString(R.string.data_empty_detail));
                        return;
                    }

                    String body = page.getBody().replaceAll("<img ", "<img onclick=\"ok.performClick(this.src)\" ");
                    // WithBaseURL para soporte de YouTube: https://stackoverflow.com/q/17783246/2823916
                    webView.loadDataWithBaseURL("http://admin.aulif.com/", "<body style='margin:0;padding:0;text-align:justify;'>" + body + "</body>", "text/html; charset=utf-8", "UTF-8", null);
//                    webView.loadData("<body style='margin:0;padding:0;text-align:justify;'>" + body + "</body>", "text/html; charset=utf-8", "UTF-8");

                    progressLayout.showContent();

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                    if (getActivity() == null) return;
                    progressLayout.showError(R.drawable.ic_error, getString(R.string.data_error), t.toString(), getString(R.string.data_tryagain_button), onTryAgain);
                    Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Page> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                if (getActivity() == null) return;
                progressLayout.showError(R.drawable.ic_error, getString(R.string.data_error), t.toString(),getString(R.string.data_tryagain_button), onTryAgain);
                Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showImage(Context context, String src) {
        Log.d(TAG, "src: " + src);
        try {
            // base64 to ImageView> https://stackoverflow.com/a/29375634/2823916    https://stackoverflow.com/q/30167205/2823916
            byte[] decodedString = Base64.decode(src.replace("data:image/jpeg;base64,","").replace("data:image/png;base64,", "").getBytes(), Base64.DEFAULT);
            Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            PhotoView photoView = new PhotoView(context);
            photoView.setImageBitmap(bitMap);
            photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            photoView.setAdjustViewBounds(true);

            new AlertDialog.Builder(context).setView(photoView).create().show();

            // Si solicitan que el dialog tenga más altura entonces: https://android--examples.blogspot.com/2016/10/android-alertdialog-width-height.html

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            Toasty.error(context, e.toString()).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).toggleToolbar(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).toggleToolbar(false);
        }
    }

}
