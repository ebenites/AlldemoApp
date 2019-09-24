package pe.ebenites.alldemo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.activities.LoginActivity;
import pe.ebenites.alldemo.models.User;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private View classroomsView;
    private View coursesView;
    private View booksView;
    private View assignmentsView;
    private View operationalAbilityView;
    private View gradebookView;

    public static Fragment newInstance(){
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_home);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        classroomsView = view.findViewById(R.id.classrooms_card);
        classroomsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = ClassroomsFragment.newInstance();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
            }
        });

        coursesView = view.findViewById(R.id.courses_card);
        coursesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = CoursesFragment.newInstance();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
            }
        });

        booksView = view.findViewById(R.id.books_card);
        booksView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = BooksFragment.newInstance();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
            }
        });

        assignmentsView = view.findViewById(R.id.assignments_card);
        assignmentsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = AssignmentsFragment.newInstance();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
            }
        });

        operationalAbilityView = view.findViewById(R.id.operational_ability_card);
        gradebookView = view.findViewById(R.id.gradebook_card);

        initialize();

        return view;
    }

    private void initialize(){
        Log.d(TAG, "initialize");

        ApiServiceGenerator.createService(getContext(), ApiService.class).getMe().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                try {
                    Log.d(TAG, "response.code(): " +  response.code());
                    if (response.code() == 401) {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                    if (!response.isSuccessful()) {
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                    User user = response.body();
                    Log.d(TAG, "user: " +  user);

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                    if (getActivity() == null) return;
                    Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                if (getActivity() == null) return;
                Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
