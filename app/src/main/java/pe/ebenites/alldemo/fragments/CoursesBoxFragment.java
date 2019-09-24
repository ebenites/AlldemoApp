package pe.ebenites.alldemo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.List;

import es.dmoral.toasty.Toasty;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.adapters.CoursesBoxRVAdapter;
import pe.ebenites.alldemo.models.Course;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoursesBoxFragment extends Fragment {

    private static final String TAG = CoursesBoxFragment.class.getSimpleName();

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
        Fragment fragment = new CoursesBoxFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_courses);
        View view = inflater.inflate(R.layout.fragment_courses_box, container, false);

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
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(new CoursesBoxRVAdapter(this));
        progressLayout = view.findViewById(R.id.progress_layout);
        progressLayout.showLoading();

        initialize();

        return view;
    }

    public void initialize(){
        Log.d(TAG, "initialize");

        ApiServiceGenerator.createService(getContext(), ApiService.class).getMyCourses().enqueue(new Callback<List<Course>>() {
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

                    CoursesBoxRVAdapter adapter = (CoursesBoxRVAdapter) recyclerView.getAdapter();
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

}
