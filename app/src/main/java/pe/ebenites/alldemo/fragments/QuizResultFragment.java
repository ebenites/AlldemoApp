package pe.ebenites.alldemo.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import es.dmoral.toasty.Toasty;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.activities.MainActivity;
import pe.ebenites.alldemo.adapters.QuizQuestionsRVAdapter;
import pe.ebenites.alldemo.models.Executor;

public class QuizResultFragment extends Fragment {

    private static final String TAG = QuizResultFragment.class.getSimpleName();

    private Executor executor;

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public static Fragment newInstance(Executor executor){
        QuizResultFragment fragment = new QuizResultFragment();
        fragment.setExecutor(executor);
        return fragment;
    }

    private TextView messageText;
    private TextView tiempoText;
    private TextView nbuenasText;
    private TextView nmalasText;
    private TextView nnomarcadasText;
    private TextView puntajeText;
    private AppCompatImageView faceImage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_result, container, false);
        try {

            if(executor == null) return view;

            tiempoText = view.findViewById(R.id.tiempo_text);
            nbuenasText = view.findViewById(R.id.nbuenas_text);
            nmalasText = view.findViewById(R.id.nmalas_text);
            nnomarcadasText = view.findViewById(R.id.nnomarcadas_text);
            puntajeText = view.findViewById(R.id.puntaje_text);
            messageText = view.findViewById(R.id.message_text);
            faceImage = view.findViewById(R.id.face_image);

            long duration = executor.getDuration();
            //long hours = duration/3600;
            //long minutes = (duration % 3600)/60;
            long minutes = duration/60;
            long seconds = duration % 60;
            tiempoText.setText(String.format(Locale.getDefault(), "%02d min y %02d seg", minutes, seconds));

            nbuenasText.setText(String.valueOf(executor.getNcorrects()));
            nmalasText.setText(String.valueOf(executor.getNwrongs()));
            nnomarcadasText.setText(String.valueOf(executor.getNblanks()));
            puntajeText.setText(String.format("%s/%s", executor.getScore(), executor.getTotal()));

            if(executor.getPercentage() > 0.5){
                messageText.setText(R.string.quiz_reult_success_message);
                faceImage.setImageResource(R.drawable.ic_face_smile);
            }else{
                messageText.setText(R.string.quiz_reult_fail_message);
                faceImage.setImageResource(R.drawable.ic_face_sad);
            }

            RecyclerView preguntaRecyclerView = view.findViewById(R.id.preguntas_list);
            preguntaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            preguntaRecyclerView.setAdapter(new QuizQuestionsRVAdapter(executor));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(getActivity() != null) Toasty.success(getActivity(), R.string.quiz_completed_message, Toast.LENGTH_LONG).show();
                }
            }, 1000);

        }catch (Throwable t){
            Log.e(TAG, t.toString(), t);
            if(getContext() != null) Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // lock to portrait
        if (getActivity() != null) getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // Show back menu button
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).toggleToolbar(true);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        // unlock orientation
        if (getActivity() != null) getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // Hide back menu button
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).toggleToolbar(false);
        }

    }

}
