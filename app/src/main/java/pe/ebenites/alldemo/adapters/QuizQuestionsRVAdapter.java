package pe.ebenites.alldemo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.models.Answer;
import pe.ebenites.alldemo.models.Executor;
import pe.ebenites.alldemo.models.Question;
import uk.co.senab.photoview.PhotoView;

public class QuizQuestionsRVAdapter extends Adapter<QuizQuestionsRVAdapter.ViewHolder> {

    private static final String TAG = QuizQuestionsRVAdapter.class.getSimpleName();

    private static int TEXT_ZOOM_NORMAL = 100;
    private static int TEXT_ZOOM_LARGER = 130;

    private Executor executor;

    public QuizQuestionsRVAdapter(Executor executor){
        this.executor = executor;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView asignaturaText;
        TextView indiceText;
        TextView puntajeText;
        WebView preguntaWebView;
        Button desmarcarButton;
        LinearLayout respuestasList;
        ImageButton zoomButton;

        List<View> respuestaViewList = new ArrayList<>();
        List<CircularToggle> respuestaCircularToggleList = new ArrayList<>();
        List<WebView> respuestaWebViewList = new ArrayList<>();
        List<AppCompatImageView> checkImageList = new ArrayList<>();

        ViewHolder(View itemView) {
            super(itemView);

            final Context context = itemView.getContext();

            asignaturaText = itemView.findViewById(R.id.asignatura_text);
            indiceText = itemView.findViewById(R.id.indice_text);
            puntajeText = itemView.findViewById(R.id.puntaje_text);
            preguntaWebView = itemView.findViewById(R.id.content_web);
            desmarcarButton = itemView.findViewById(R.id.desmarcar_button);
            respuestasList = itemView.findViewById(R.id.respuestas_list);
            zoomButton = itemView.findViewById(R.id.zoom_button);

            for(int index=0; index<5; index++) {
                View respuestaItemView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_quiz_questions_answers, respuestasList, false);
                respuestaViewList.add(respuestaItemView);

                CircularToggle respuestaCircularToggle = respuestaItemView.findViewById(R.id.respuesta_button);
                respuestaCircularToggleList.add(respuestaCircularToggle);
                respuestaCircularToggle.setText(Executor.letters[index]);

                WebView respuestaWebView = respuestaItemView.findViewById(R.id.content_web);
                respuestaWebViewList.add(respuestaWebView);
                respuestaWebView.setBackgroundColor(Color.TRANSPARENT);
                respuestaWebView.setLongClickable(false);
                respuestaWebView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return true;
                    }
                });

                // Onclik img on webview: https://stackoverflow.com/a/22281171/2823916
                respuestaWebView.getSettings().setJavaScriptEnabled(true);
                respuestaWebView.addJavascriptInterface(new Object(){
                    @JavascriptInterface    // For API 17+
                    public void performClick(String src) {
                        showImage(context, src);
                    }
                }, "ok");

                AppCompatImageView correctoImage =respuestaItemView.findViewById(R.id.correcto_image);
                checkImageList.add(correctoImage);

                respuestasList.addView(respuestaItemView);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_quiz_questions, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        final Context context = viewHolder.itemView.getContext();

        final Question question = executor.getQuizPhase().getQuestions().get(position);

        viewHolder.asignaturaText.setText(String.format(context.getString(R.string.quiz_question_level_text), executor.getQuizPhase().getPhase(), question.getLevel()));

        viewHolder.indiceText.setText(String.format(context.getString(R.string.quiz_question_index_text), position + 1));

        viewHolder.puntajeText.setText(String.format(context.getString(R.string.quiz_question_score_text), new DecimalFormat("##.###").format(question.getWeight())));

        String margin = "0";    // En Android 8 poner margen por si acaso (reportaron un bug irreproducible)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            margin = "16px";

        String preguntaHTML = (question.getBody() != null) ? question.getBody().replaceAll("<img ", "<img onclick=\"ok.performClick(this.src)\" ") : question.getBody();

        viewHolder.preguntaWebView.loadData("<body style='margin:" + margin + ";padding:0;text-align:justify;'>" + preguntaHTML + "</body>", "text/html; charset=utf-8", "UTF-8");
        viewHolder.preguntaWebView.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.preguntaWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        viewHolder.preguntaWebView.setLongClickable(false);
        viewHolder.preguntaWebView.getSettings().setTextZoom(TEXT_ZOOM_NORMAL);

        // Onclik img on webview: https://stackoverflow.com/a/22281171/2823916
        viewHolder.preguntaWebView.getSettings().setJavaScriptEnabled(true);
        viewHolder.preguntaWebView.addJavascriptInterface(new Object(){
            @JavascriptInterface    // For API 17+
            public void performClick(String src) {
                showImage(context, src);
            }
        }, "ok");

        viewHolder.zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.preguntaWebView.getSettings().getTextZoom() == TEXT_ZOOM_NORMAL){
                    viewHolder.preguntaWebView.getSettings().setTextZoom(TEXT_ZOOM_LARGER);
                }else{
                    viewHolder.preguntaWebView.getSettings().setTextZoom(TEXT_ZOOM_NORMAL);
                }
            }
        });

        viewHolder.desmarcarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clearChecked: " + question.getId());

                question.clearChecked();

                for (CircularToggle circularToggle : viewHolder.respuestaCircularToggleList) {
                    if(circularToggle.isChecked()) {
                        circularToggle.setChecked(false);
                    }
                }

                if(executor.getAnswersRecyclerView() != null){
                    executor.getAnswersRecyclerView().getAdapter().notifyDataSetChanged();
                }
            }
        });

        // Si ha finalizado
        if(executor.getFinished()){
            viewHolder.desmarcarButton.setVisibility(View.GONE);
        }

//        viewHolder.recyclerView.setAdapter(new RespuestaRVAdapter(pregunta.getRespuestas()));

        int numrespuesta = 0;
        for (final Answer answer : question.getAnswers()) {

            final View respuestaView = viewHolder.respuestaViewList.get(numrespuesta);
            final CircularToggle respuestaCircularToggle = viewHolder.respuestaCircularToggleList.get(numrespuesta);
            final WebView respuestaWebView = viewHolder.respuestaWebViewList.get(numrespuesta);
            final AppCompatImageView checkImage = viewHolder.checkImageList.get(numrespuesta);

            // Si No ha finalizado
            if(!executor.getFinished()) {
                respuestaCircularToggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "marcar: " + question.getId() + "|" + answer.getId());

                        question.setChecked(answer.getId());

                        for (CircularToggle circularToggle : viewHolder.respuestaCircularToggleList) {
                            if (circularToggle.isChecked()) {
                                circularToggle.setChecked(false);
                            }
                        }

                        // CircularToggle fixed (marker color freezing bug on scrolling)
                        respuestaCircularToggle.setMarkerColor(ContextCompat.getColor(context, R.color.colorAccent));

                        respuestaCircularToggle.setChecked(true);

                        if (executor.getAnswersRecyclerView() != null) {
                            new Handler().postDelayed(new Runnable() { // with delay, because refreshing is too slow
                                @Override
                                public void run() {
                                    executor.getAnswersRecyclerView().getAdapter().notifyDataSetChanged();
                                }
                            }, 200);
                        }

                    }
                });
            }

            // CircularToggle fixed (marker color freezing bug on scrolling)
            respuestaCircularToggle.setMarkerColor(ContextCompat.getColor(context, android.R.color.transparent));

            // Set current checked
            if(answer.getChecked()) {
                // CircularToggle fixed (marker color freezing bug on scrolling)
                respuestaCircularToggle.setMarkerColor(ContextCompat.getColor(context, R.color.colorAccent));

                respuestaCircularToggle.setChecked(true);
            }else{ // CircularToggle fixed (bug on scrolling)
                if(respuestaCircularToggle.isChecked()) {
                    Log.e(TAG, "Esta seleccionado!!");
                    respuestaCircularToggle.setChecked(false);
                }
            }

            // Si ha finalizado
            if(executor.getFinished()){
                respuestaCircularToggle.setEnabled(false);
            }

            // Set correct or incorrect
            if(executor.getFinished()) {
                if(answer.getChecked()) {
                    if ("1".equals(answer.getCorrect())) {
                        respuestaView.setBackgroundColor(ContextCompat.getColor(context, R.color.success));
                        checkImage.setImageResource(R.drawable.ic_checkbox_marked_circle);
                    } else {
                        respuestaView.setBackgroundColor(ContextCompat.getColor(context, R.color.danger));
                        checkImage.setImageResource(R.drawable.ic_close_marked_circle);
                    }
                } else {
                    if ("1".equals(answer.getCorrect())) {
                        respuestaView.setBackgroundColor(ContextCompat.getColor(context, R.color.success));
                        checkImage.setImageResource(0);
                    }else{
                        respuestaView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                        checkImage.setImageResource(0);
                    }
                }
            }

            String respuestaHTML = (answer.getBody() != null) ? answer.getBody().replaceAll("<img ", "<img onclick=\"ok.performClick(this.src)\" ") : answer.getBody();

            respuestaWebView.loadData("<body style='margin-top:16;padding:0;text-align:justify;'>" + respuestaHTML + "</body>", "text/html; charset=utf-8", "UTF-8");

            numrespuesta++;
        }

    }

    @Override
    public int getItemCount() {
        return executor.getQuizPhase().getQuestions().size();
    }

    private void showImage(Context context, String src) {
        Log.d(TAG, "src: " + src);
        try {
            // base64 to ImageView> https://stackoverflow.com/a/29375634/2823916    https://stackoverflow.com/q/30167205/2823916
            byte[] decodedString = Base64.decode(src.replace("data:image/jpeg;base64,","").replace("data:image/png;base64,", "").getBytes(), Base64.DEFAULT);
            Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            PhotoView photoView = new PhotoView(context);
            photoView.setImageBitmap(bitMap);
            photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            photoView.setAdjustViewBounds(true);

            new AlertDialog.Builder(context).setView(photoView).create().show();

            // Si solicitan que el dialog tenga más altra entonces: https://android--examples.blogspot.com/2016/10/android-alertdialog-width-height.html

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            Toasty.error(context, e.toString()).show();
        }
    }

}
