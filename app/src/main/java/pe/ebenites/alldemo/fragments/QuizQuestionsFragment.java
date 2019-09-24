package pe.ebenites.alldemo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.adapters.QuizQuestionsRVAdapter;
import pe.ebenites.alldemo.models.Executor;

public class QuizQuestionsFragment extends Fragment {

    private static final String TAG = QuizQuestionsFragment.class.getSimpleName();

    private Executor executor;

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public static Fragment newInstance(Executor executor){
        QuizQuestionsFragment fragment = new QuizQuestionsFragment();
        fragment.setExecutor(executor);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_questions, container, false);

        if(executor == null) return view;

        RecyclerView preguntaRecyclerView = view.findViewById(R.id.preguntas_list);
        preguntaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        preguntaRecyclerView.setAdapter(new QuizQuestionsRVAdapter(executor));

        executor.setQuestionsRecyclerView(preguntaRecyclerView);

        return view;
    }

}
