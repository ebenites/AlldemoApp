package pe.ebenites.alldemo.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nex3z.togglebuttongroup.button.CircularToggle;

import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.models.Executor;
import pe.ebenites.alldemo.models.Question;

public class QuizAnswersRVAdapter extends Adapter<QuizAnswersRVAdapter.ViewHolder> {

    private static final String TAG = QuizAnswersRVAdapter.class.getSimpleName();

    private Executor executor;

    public QuizAnswersRVAdapter(Executor executor){
        this.executor = executor;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CircularToggle circularToggle;

        ViewHolder(View itemView) {
            super(itemView);
            circularToggle = itemView.findViewById(R.id.resumen_button);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_quiz_answers, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        final Question question = executor.getQuizPhase().getQuestions().get(position);

        viewHolder.circularToggle.setText(String.valueOf(position + 1));

        if(question.isChecked()){
            viewHolder.circularToggle.setChecked(true);
        }else{
            if(viewHolder.circularToggle.isChecked()) {
                viewHolder.circularToggle.setChecked(false);
            }
        }

        // circularToggle readonly
        //viewHolder.circularToggle.setEnabled(false);

        viewHolder.circularToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(question.isChecked()){
                    ((CircularToggle)v).setChecked(true);
                }else{
                    ((CircularToggle)v).setChecked(false);
                }
                executor.focusOnView(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return executor.getQuizPhase().getQuestions().size();
    }

}
