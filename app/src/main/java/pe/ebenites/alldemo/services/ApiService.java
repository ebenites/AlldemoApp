package pe.ebenites.alldemo.services;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pe.ebenites.alldemo.models.Book;
import pe.ebenites.alldemo.models.Course;
import pe.ebenites.alldemo.models.Department;
import pe.ebenites.alldemo.models.District;
import pe.ebenites.alldemo.models.Lesson;
import pe.ebenites.alldemo.models.Page;
import pe.ebenites.alldemo.models.Province;
import pe.ebenites.alldemo.models.QuizPhase;
import pe.ebenites.alldemo.models.ResponseSuccess;
import pe.ebenites.alldemo.models.User;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by ebenites on 05/08/2016.
 */
public interface ApiService {

    String API_BASE_URL = "http://www.campus.com/";
//    String API_BASE_URL = "http://192.168.1.10/campus/public/";
//    String API_BASE_URL = "http://10.0.2.2/campus/public/";

    @FormUrlEncoded
    @POST("api/auth/authenticate")
    Call<User> authenticate(@Field("token") String tokenid, @Field("roles_id") Integer roles_id, @Field("phonenumber") String phonenumber, @Field("instanceid") String instanceid);

    @GET("api/auth/refresh_token")
    Call<User> refreshToken(@Header("Authorization") String authorization);

    @GET("api/auth/me")
    Call<User> getMe();


    @GET("api/common/departments")
    Call<List<Department>> getDepartments();

    @GET("api/common/provinces/{id}")
    Call<List<Province>> getProvinces(@Path("id") String id);

    @GET("api/common/districts/{id}")
    Call<List<District>> getDistricts(@Path("id") String id);


    @GET("api/profile")
    Call<User> getProfile();

    @FormUrlEncoded
    @POST("api/profile")
    Call<User> updateProfile(@Field("firstname") String firstname, @Field("lastname") String lastname,
                             @Field("gender") String gender, @Field("birthdate") String birthdate, @Field("email") String email, @Field("phonenumber") String phonenumber,
                             @Field("departments_id") String departments_id, @Field("provinces_id") String provinces_id, @Field("districts_id") String districts_id);

    @Multipart
    @POST("api/profile")
    Call<User> updateProfile(@Part MultipartBody.Part file, @Part("firstname") RequestBody firstname, @Part("lastname") RequestBody lastname,
                             @Part("gender") RequestBody gender, @Part("birthdate") RequestBody birthdate, @Part("email") RequestBody email, @Part("phonenumber") RequestBody phonenumber,
                             @Part("departments_id") RequestBody departments_id, @Part("provinces_id") RequestBody provinces_id, @Part("districts_id") RequestBody districts_id);


    @GET("api/store/lessons")
    Call<List<Course>> getLessons();

    @GET("api/content/lessons")
    Call<List<Lesson>> getMyLessons();

    @POST("api/store/lessons/{id}")
    Call<ResponseSuccess> checkoutLessons(@Path("id") Integer id);

    @GET("api/content/lessons/{id}/pages/{type}")
    Call<Page> getMyPage(@Path("id") Integer id, @Path("type") String type);

    @GET("api/content/lessons/{id}/quiz")
    Call<QuizPhase> getQuiz(@Path("id") Integer id);

    @FormUrlEncoded
    @POST("api/content/lessons/{id}/quiz")
    Call<ResponseSuccess> submissionQuiz(@Path("id") Integer id, @Field("final_score") Double final_score,
                                         @Field("questions_id[]") List<Integer> questions_id,
                                         @Field("answers_id[]") List<Integer> answers_id,
                                         @Field("checkeds_id[]") List<Integer> checkeds_id,
                                         @Field("corrects[]") List<Boolean> corrects,
                                         @Field("scores[]") List<Double> scores);

    @DELETE("api/store/lessons/{id}")
    Call<ResponseSuccess> refundLessons(@Path("id") Integer id);


    @GET("api/store/courses")
    Call<List<Course>> getCourses();

    @GET("api/content/courses")
    Call<List<Course>> getMyCourses();

    @POST("api/store/courses/{id}")
    Call<ResponseSuccess> checkoutCourses(@Path("id") Integer id);

    @GET("api/content/courses/{id}")
    Call<Course> getMyCoursesDetail(@Path("id") Integer id);

    @DELETE("api/store/courses/{id}")
    Call<ResponseSuccess> refundCourses(@Path("id") Integer id);


    @GET("api/store/books")
    Call<List<Book>> getBooks();

    @GET("api/content/books")
    Call<List<Book>> getMyBooks();

    @POST("api/store/books/{id}")
    Call<ResponseSuccess> checkoutBooks(@Path("id") Integer id);

    @GET("api/content/books/{id}")
    Call<Book> getMyBooksDetail(@Path("id") Integer id);

    @DELETE("api/store/books/{id}")
    Call<ResponseSuccess> refundBooks(@Path("id") Integer id);

}
