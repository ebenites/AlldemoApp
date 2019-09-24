package pe.ebenites.alldemo.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alexfu.countdownview.CountDownListener;
import com.alexfu.countdownview.CountDownView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.activities.MainActivity;
import pe.ebenites.alldemo.adapters.ViewPagerAdapter;
import pe.ebenites.alldemo.models.Executor;
import pe.ebenites.alldemo.models.Question;
import pe.ebenites.alldemo.models.QuizPhase;
import pe.ebenites.alldemo.models.ResponseSuccess;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizFragment extends Fragment {

    private static final String TAG = QuizFragment.class.getSimpleName();

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";

    private Integer id;
    private String title;

    public static Fragment newInstance(Integer id, String title){
        Fragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_ID);
            title = getArguments().getString(ARG_TITLE);
        }
    }

    private ProgressRelativeLayout progressLayout;

    private View.OnClickListener onTryAgain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initialize();
        }
    };

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private CountDownView countDownView;

    private Button finalizarButton;

    private Executor executor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(title);
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        final Context context = container.getContext();

        // Tab Configuration
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        countDownView = view.findViewById(R.id.count_down);
        countDownView.setListener(new CountDownListener() {
            @Override
            public void onFinishCountDown() {
                finalizar();
            }
        });

        finalizarButton = view.findViewById(R.id.finalizar_button);
        finalizarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // https://github.com/javiersantos/BottomDialogs
                new BottomDialog.Builder(context)
                        .setIcon(R.drawable.ic_assignments)
                        .setTitle(R.string.quiz_submission_confirm_title)
                        .setContent(R.string.quiz_submission_confirm_message)
                        .setPositiveText(android.R.string.ok)
                        .setNegativeText(android.R.string.cancel)
                        .setPositiveBackgroundColorResource(R.color.colorAccent)
                        .setPositiveTextColorResource(android.R.color.white)
                        .onPositive(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(@NonNull BottomDialog dialog) {
                                finalizar();
                            }
                        })
                        .onNegative(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(@NonNull BottomDialog dialog) {

                            }
                        }).show();
            }
        });

        progressLayout = view.findViewById(R.id.progress_layout);
        progressLayout.showLoading();

        initialize();

        // Habk: Debug Preguntas
        /*countDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = 1;
                for (Pregunta pregunta : executor.getQuizPhase().getCuestionario().getPreguntas()){
                    Log.e(TAG, String.format(context.getString(R.string.cuestionario_pregunta_indice_text), position++) +  " - " + pregunta.getId() + " | " + Html.fromHtml(pregunta.getEnunciado().length()>100?pregunta.getEnunciado().substring(0, 100):pregunta.getEnunciado())
                            + " - isChecked: " + pregunta.isChecked() + " - getCheckedAnswer: " + pregunta.getCheckedAnswer() + " - getCorrectAnswer: " + pregunta.getCorrectAnswer() + " - isCorrect: " + pregunta.isCorrect());
                    for(Respuesta respuesta : pregunta.getRespuestas()){
                        Log.w(TAG, "\tRespuesta: " + respuesta.getId() + " | " + Html.fromHtml(respuesta.getDescripcion().length()>100?respuesta.getDescripcion().substring(0, 100):respuesta.getDescripcion())
                                + " - getCheckedAnswer: " + respuesta.getCheckedAnswer() + " - getCorrecto: " + respuesta.getCorrecto());
                    }
                }
            }
        });*/

        return view;
    }

    private void initialize(){
        Log.d(TAG, "initialize: " + id);

        ApiServiceGenerator.createService(getContext(), ApiService.class).getQuiz(id).enqueue(new Callback<QuizPhase>() {
            @Override
            public void onResponse(@NonNull Call<QuizPhase> call, @NonNull Response<QuizPhase> response) {
                try {
                    if (!response.isSuccessful()) {
                        throw new Exception( ApiServiceGenerator.parseError(response).getMessage());
                    }

                    QuizPhase quizPhase = response.body();
//                    Log.d(TAG, "quizPhase: " +  quizPhase);

                    if(quizPhase == null || quizPhase.getQuestions() == null || quizPhase.getQuestions().isEmpty()){
                        progressLayout.showEmpty(R.drawable.ic_empty, getString(R.string.data_empty), getString(R.string.data_empty_detail));
                        return;
                    }

                    executor = new Executor(quizPhase);

                    ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
                    adapter.addFragment(QuizQuestionsFragment.newInstance(executor), getString(R.string.quiz_tab_questions));
                    adapter.addFragment(QuizAnswersFragment.newInstance(executor), getString(R.string.quiz_tab_answers));
                    viewPager.setAdapter(adapter);

                    executor.setViewPager(viewPager);   // Set ViewPager to control position


                    // CountDown
//                    Date fecinicio = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(cuestionarioParticipante.getHorario().getFecha_ini());
//                    Date fecfin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(cuestionarioParticipante.getHorario().getFecha_fin());

//                    long diff = fecfin.getTime() - new Date().getTime();
                    long diff = 3600 * 1000;
                    Log.d(TAG, "diff: " + diff);

                    countDownView.setStartDuration(diff);
                    countDownView.start();

                    // Start countdown
                    executor.start();

                    progressLayout.showContent();

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                    progressLayout.showError(R.drawable.ic_error, getString(R.string.data_error), t.toString(), getString(R.string.data_tryagain_button), onTryAgain);
                    Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }finally {

                }
            }
            @Override
            public void onFailure(@NonNull Call<QuizPhase> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                progressLayout.showError(R.drawable.ic_error, getString(R.string.data_error), t.toString(),getString(R.string.data_tryagain_button), onTryAgain);
                Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void finalizar(){
        Log.d(TAG, "finalizar()");
        try{

            // Stop countdown
            executor.stop();

            // Show progress dialog
            final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Finalizando...", true, false);

            // Send to register
            Integer id = executor.getQuizPhase().getId();
            Double final_score = executor.getScore();

            List<Integer> questions_id = new ArrayList<>();
            List<Integer> answers_id = new ArrayList<>();
            List<Integer> checkeds_id = new ArrayList<>();
            List<Boolean> corrects = new ArrayList<>();
            List<Double> scores = new ArrayList<>();
            for (Question question: executor.getQuizPhase().getQuestions()) {
                questions_id.add(question.getId());
                answers_id.add((question.getCorrectAnswer() != null)?question.getCorrectAnswer():-1);
                checkeds_id.add((question.getCheckedAnswer() != null)?question.getCheckedAnswer():-1);
                corrects.add((question.isCorrect() != null)?question.isCorrect():false);
                scores.add((question.getScore() != null)?question.getScore():-1);
            }

            ApiServiceGenerator.createService(getContext(), ApiService.class).submissionQuiz(id, final_score, questions_id, answers_id, checkeds_id, corrects, scores).enqueue(new Callback<ResponseSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ResponseSuccess> call, @NonNull Response<ResponseSuccess> response) {
                    try {
                        if (!response.isSuccessful()) {
                            throw new Exception( ApiServiceGenerator.parseError(response).getMessage());
                        }

                        ResponseSuccess responseSuccess = response.body();
                        Log.d(TAG, "responseSuccess: " +  responseSuccess);

                        // Go to Resultado
                        Fragment fragment = QuizResultFragment.newInstance(executor);

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack(); // Remove current fragment https://stackoverflow.com/a/28088798/2823916
                        fragmentManager.beginTransaction().replace(R.id.main_content, fragment).addToBackStack("").commit();

                    } catch (Throwable t) {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        if (getActivity() == null) return;
                        Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }finally {
                        if (getActivity() != null) progressDialog.dismiss();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ResponseSuccess> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                    if (getActivity() == null) return;
                    Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        } catch (Throwable t) {
            Log.e(TAG, "onThrowable: " + t.toString(), t);
            if (getActivity() == null) return;
            Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
        }
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

        // Hide Bottom Navigation
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).toggleBottomNavigation(false);
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

        // Hide Bottom Navigation
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).toggleBottomNavigation(true);
        }
    }

}
