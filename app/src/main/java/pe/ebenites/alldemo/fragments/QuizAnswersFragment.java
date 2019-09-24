package pe.ebenites.alldemo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.adapters.QuizAnswersRVAdapter;
import pe.ebenites.alldemo.models.Executor;

public class QuizAnswersFragment extends Fragment {

    private static final String TAG = QuizAnswersFragment.class.getSimpleName();

    private Executor executor;

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public static Fragment newInstance(Executor executor){
        QuizAnswersFragment fragment = new QuizAnswersFragment();
        fragment.setExecutor(executor);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_answers, container, false);

        if(executor == null) return view;

        RecyclerView resumenRecyclerView = view.findViewById(R.id.resumen_list);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getContext());
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        resumenRecyclerView.setLayoutManager(flexboxLayoutManager);

        resumenRecyclerView.setAdapter(new QuizAnswersRVAdapter(executor));

        executor.setAnswersRecyclerView(resumenRecyclerView);

        return view;
    }

}
