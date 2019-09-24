package pe.ebenites.alldemo.models;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Executor {

    private static final String TAG = Executor.class.getSimpleName();

    public static final String[] letters = new String[]{"A", "B", "C", "D", "E"};

    private QuizPhase quizPhase;

    private Map<String, CircularToggle> circularToggleMap;

    private Map<String, Boolean> webViewIsLoaded;

    private RecyclerView answersRecyclerView;

    private RecyclerView questionsRecyclerView;

    private Boolean finished;

    private Date starttime;
    private Date endtime;
    private Long duration;

    private Integer ncorrects;
    private Integer nwrongs;
    private Integer nblanks;
    private Double total;
    private Double score;
    private Double percentage;
    private Double vigesimal;

    private ViewPager viewPager;

    public Executor(@NonNull QuizPhase quizPhase){
        this.quizPhase = quizPhase;
        this.circularToggleMap = new HashMap<>();
        this.webViewIsLoaded = new HashMap<>();
        this.starttime = new Date();
        this.ncorrects = 0;
        this.nwrongs = 0;
        this.nblanks = 0;
        this.total = 0d;
        this.score = 0d;
        this.vigesimal = 0d;
        this.finished = false;
    }

    public void start() {
        this.starttime = new Date();
    }

    public void stop() {
        try {

            this.finished = true;

            // Set duration in seconds
            this.endtime = new Date();

            long diff = endtime.getTime() - starttime.getTime();
            this.duration = diff / 1000;


            // ncorrects, nwrongs, nblanks
            this.ncorrects = 0;
            this.nwrongs = 0;
            this.nblanks = 0;
            this.total = 0d;
            this.score = 0d;

            for(Question question : quizPhase.getQuestions()){
                Boolean isCorrecto = question.isCorrect();
                if(isCorrecto != null) {
                    if (isCorrecto) {   // Correct
                        ncorrects++;
                        score += question.getWeight(); //asignatura.getPuntaje_bueno();
                        question.setScore(question.getWeight());
                    }else{  // Wrong
                        nwrongs++;
                        score += 0; //asignatura.getPuntaje_malo();
                        question.setScore(0d);
                    }
                }else{  // Blank
                    nblanks++;
                    score += 0; //asignatura.getPuntaje_blanco();
                    question.setScore(0d);
                }
            }

            // percentage
            this.percentage = this.score / this.total;

            // Nota vigesimal (sobre 20)
            this.vigesimal = this.percentage * this.total;

        }catch (Exception e){
            Log.d(TAG, e.toString(), e);
            throw e;
        }
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Long getDuration() {
        return duration;
    }

    public Integer getNcorrects() {
        return ncorrects;
    }

    public Integer getNwrongs() {
        return nwrongs;
    }

    public Integer getNblanks() {
        return nblanks;
    }

    public Double getTotal() {
        return total;
    }

    public Double getScore() {
        return score;
    }

    public Double getPercentage() {
        return percentage;
    }

    public QuizPhase getQuizPhase() {
        return quizPhase;
    }

    public void setAnswersRecyclerView(RecyclerView answersRecyclerView) {
        this.answersRecyclerView = answersRecyclerView;
    }

    public RecyclerView getAnswersRecyclerView() {
        return answersRecyclerView;
    }

    public RecyclerView getQuestionsRecyclerView() {
        return questionsRecyclerView;
    }

    public void setQuestionsRecyclerView(RecyclerView questionsRecyclerView) {
        this.questionsRecyclerView = questionsRecyclerView;
    }

    public CircularToggle getCircularToggle(@NonNull Integer idpregunta, @NonNull Integer idrespuesta){
        return circularToggleMap.get(idpregunta + "|" + idrespuesta);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public void focusOnView(@NonNull final Integer position){
        if(viewPager != null) {
            viewPager.setCurrentItem(0);    // Go to first page from viewpager
            if(questionsRecyclerView != null) {
                questionsRecyclerView.getLayoutManager().scrollToPosition(position);
            }
        }
    }

}
