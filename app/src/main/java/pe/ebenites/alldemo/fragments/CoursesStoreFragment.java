package pe.ebenites.alldemo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.adapters.CoursesStoreRVAdapter;
import pe.ebenites.alldemo.adapters.ViewPagerAdapter;
import pe.ebenites.alldemo.models.Course;
import pe.ebenites.alldemo.models.ResponseSuccess;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoursesStoreFragment extends Fragment {

    private static final String TAG = CoursesStoreFragment.class.getSimpleName();

    private ProgressRelativeLayout progressLayout;

    private View.OnClickListener onTryAgain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressLayout.showLoading();
            initialize();
        }
    };

    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    public static Fragment newInstance(){
        Fragment fragment = new CoursesStoreFragment();
        return fragment;
    }

    private ViewGroup container;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_courses);
        View view = inflater.inflate(R.layout.fragment_courses_store, container, false);

        refreshLayout = view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        initialize();
                    }
                }
        );

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new CoursesStoreRVAdapter(this));

        progressLayout = view.findViewById(R.id.progress_layout);
        progressLayout.showLoading();

        this.container = container;

        initialize();

        return view;
    }

    public void initialize(){
        Log.d(TAG, "initialize");

        ApiServiceGenerator.createService(getContext(), ApiService.class).getCourses().enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                try {
                    if (!response.isSuccessful()) {
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                    List<Course> courses = response.body();
                    Log.d(TAG, "courses: " +  courses);

                    if(courses == null || courses.isEmpty()){
                        progressLayout.showEmpty(R.drawable.ic_empty, getString(R.string.data_empty), getString(R.string.data_empty_detail));
                        return;
                    }

                    CoursesStoreRVAdapter adapter = (CoursesStoreRVAdapter) recyclerView.getAdapter();
                    adapter.setCourses(courses);
                    adapter.notifyDataSetChanged();

                    progressLayout.showContent();

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                    if (getActivity() == null) return;
                    progressLayout.showError(R.drawable.ic_error, getString(R.string.data_error), t.toString(), getString(R.string.data_tryagain_button), onTryAgain);
                    Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }finally {
                    if (getActivity() != null) refreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                if (getActivity() == null) return;
                progressLayout.showError(R.drawable.ic_error, getString(R.string.data_error), t.toString(),getString(R.string.data_tryagain_button), onTryAgain);
                Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                refreshLayout.setRefreshing(false);
            }
        });

    }

    public void buy(final Course course) {

        /*new AlertDialog.Builder(getContext())
                .setName(R.string.app_buy_dialog_title)
                .setMessage(getString(R.string.app_buy_dialog_message, course.getName()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buy(course);
                    }
                })
                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();*/

        new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setCustomImage(R.drawable.ic_cart)
                .setTitleText(getString(R.string.app_buy_dialog_title))
                .setContentText(getString(R.string.app_buy_dialog_message, course.getTitle()))
                .setConfirmText(getString(R.string.app_buy))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {

                        sDialog
//                                .setTitleText(getString(R.string.loading_information))
//                                .showContentText(false)
                                .setTitleText("")
                                .setContentText(getString(R.string.loading_information))
                                .setConfirmClickListener(null)
                                .showCancelButton(false)
                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

                        ApiServiceGenerator.createService(getContext(), ApiService.class).checkoutCourses(course.getId()).enqueue(new Callback<ResponseSuccess>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseSuccess> call, @NonNull Response<ResponseSuccess> response) {
                                try {
                                    if (!response.isSuccessful()) {
                                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                                    }

                                    ResponseSuccess success = response.body();
                                    Log.d(TAG, "success: " +  success);

                                    sDialog
                                            .setTitleText(getString(R.string.app_buy_dialog_title))
                                            .setContentText(success.getMessage())
                                            .setConfirmText(getString(android.R.string.ok))
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                    // refresh ClassroomsStore
                                                    initialize();

                                                    // refresh ClassroomsBox
                                                    if(container instanceof ViewPager){
                                                        ViewPager viewPager = (ViewPager) container;
                                                        ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
                                                        if(viewPagerAdapter != null) {
                                                            Fragment fragment = viewPagerAdapter.getItem(0);
                                                            if (fragment instanceof CoursesBoxFragment){
                                                                ((CoursesBoxFragment)fragment).initialize();
                                                            }
                                                        }
                                                    }

                                                    Toasty.success(getContext(), R.string.store_checkout_success, Toasty.LENGTH_LONG).show();

                                                    sDialog.dismissWithAnimation();
                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                } catch (Throwable t) {
                                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                                    if (getActivity() == null) return;
                                    Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                    sDialog.dismissWithAnimation();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<ResponseSuccess> call, @NonNull Throwable t) {
                                Log.e(TAG, "onFailure: " + t.toString());
                                if (getActivity() == null) return;
                                Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                sDialog.dismissWithAnimation();
                            }
                        });

                    }
                })
                .showCancelButton(true)
                .setCancelText(getString(android.R.string.cancel))
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();

    }

}
