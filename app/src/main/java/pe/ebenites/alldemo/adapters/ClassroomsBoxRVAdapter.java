package pe.ebenites.alldemo.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.fragments.PageViewerFragment;
import pe.ebenites.alldemo.fragments.QuizFragment;
import pe.ebenites.alldemo.models.Lesson;
import pe.ebenites.alldemo.models.ResponseSuccess;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import pe.ebenites.alldemo.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassroomsBoxRVAdapter extends Adapter<ClassroomsBoxRVAdapter.ViewHolder> {

    private static final String TAG = ClassroomsBoxRVAdapter.class.getSimpleName();

    private FragmentActivity activity;

    private List<Lesson> lessons;

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public ClassroomsBoxRVAdapter(FragmentActivity activity){
        this.activity = activity;
        this.lessons = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iconImage;
        TextView titleText;
        AppCompatImageView arrowImage;
        FrameLayout expandButton;
        ExpandableLayout expandableLayout;

        FrameLayout theoryButton;
        FrameLayout exercice1Button;
        FrameLayout exercice2Button;
        FrameLayout examButton;

        ViewHolder(View itemView) {
            super(itemView);

            final Context context = itemView.getContext();

            iconImage = itemView.findViewById(R.id.icon_image);
            titleText = itemView.findViewById(R.id.title_text);
            arrowImage = itemView.findViewById(R.id.arrow_image);

            expandButton = itemView.findViewById(R.id.expand_button);
            expandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(expandableLayout.isExpanded()) {
                        Log.d(TAG, "COLLAPSING position:" + getAdapterPosition());
                        expandableLayout.collapse();
                        expandButton.setSelected(false);

                        titleText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
                        iconImage.getDrawable().mutate().setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_IN);    // mutate() https://stackoverflow.com/a/31945077/2823916
                        arrowImage.getDrawable().mutate().setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_IN);
//                        arrowImage.setImageResource(R.drawable.ic_expand_more);
                        arrowImage.animate().rotation(0).setDuration(200).start();
                    } else {
                        Log.d(TAG, "EXPANDING position:" + getAdapterPosition());
                        expandableLayout.expand();
                        expandButton.setSelected(true);

                        titleText.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        iconImage.getDrawable().mutate().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN); // mutate() https://stackoverflow.com/a/31945077/2823916
                        arrowImage.getDrawable().mutate().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
//                        arrowImage.setImageResource(R.drawable.ic_expand_less);
                        arrowImage.animate().rotation(-180).setDuration(200).start();
                    }
                }
            });


            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            expandableLayout.setInterpolator(new OvershootInterpolator());
            expandableLayout.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
                @Override
                public void onExpansionUpdate(float expansionFraction, int state) {
//                    Log.d(TAG, "onExpansionUpdate (" + expansionFraction + ")");
                    switch (state) {
                        case ExpandableLayout.State.EXPANDING:
                            Log.d(TAG, "EXPANDING (" + state + ")");
                            break;
                        case ExpandableLayout.State.EXPANDED:
                            Log.d(TAG, "EXPANDED (" + state + ")");
                            break;
                        case ExpandableLayout.State.COLLAPSING:
                            Log.d(TAG, "COLLAPSING (" + state + ")");
                            break;
                        case ExpandableLayout.State.COLLAPSED:
                            Log.d(TAG, "COLLAPSED (" + state + ")");
                            break;
                    }
                }
            });

            theoryButton = itemView.findViewById(R.id.theory_button);
            exercice1Button = itemView.findViewById(R.id.exercice1_button);
            exercice2Button = itemView.findViewById(R.id.exercice2_button);
            examButton = itemView.findViewById(R.id.exam_button);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_classrooms_box, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        final Context context = viewHolder.itemView.getContext();

        final Lesson lesson = lessons.get(position);

        viewHolder.titleText.setText(lesson.getTitle());

        viewHolder.theoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = PageViewerFragment.newInstance(lesson.getId(), lesson.getTitle(), Constants.PAGE_TYPE_THEORY);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
            }
        });

        viewHolder.exercice1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = PageViewerFragment.newInstance(lesson.getId(), lesson.getTitle(), Constants.PAGE_TYPE_PRACTICE);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
            }
        });

        viewHolder.exercice2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = PageViewerFragment.newInstance(lesson.getId(), lesson.getTitle(), Constants.PAGE_TYPE_REFORCE);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
            }
        });

        viewHolder.examButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setCustomImage(R.drawable.ic_assignments)
                        .setTitleText(context.getString(R.string.quiz_execute_confirm_title))
                        .setContentText(context.getString(R.string.quiz_execute_confirm_message))
                        .setConfirmText(context.getString(android.R.string.ok))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                Fragment fragment = QuizFragment.newInstance(lesson.getId(), lesson.getTitle());
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("").commit();
                            }
                        })
                        .showCancelButton(true)
                        .setCancelText(context.getString(android.R.string.cancel))
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();
            }
        });


        // TEMP: DELETE CONTENT ON LONG CLICK
        viewHolder.expandButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(activity)
                        .setTitle("Devolver contenido")
                        .setMessage("¿Realmente desea devolver el contenido?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ApiServiceGenerator.createService(context, ApiService.class).refundLessons(lesson.getId()).enqueue(new Callback<ResponseSuccess>() {
                                    @Override
                                    public void onResponse(@NonNull Call<ResponseSuccess> call, @NonNull Response<ResponseSuccess> response) {
                                        try {
                                            if (!response.isSuccessful()) {
                                                throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                                            }

                                            ResponseSuccess success = response.body();
                                            Log.d(TAG, "success: " +  success);

                                            // delete item from recyclerview and notify new size
                                            lessons.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, lessons.size());


                                            Toasty.success(activity, success.getMessage(), Toasty.LENGTH_LONG).show();

                                        } catch (Throwable t) {
                                            Log.e(TAG, "onThrowable: " + t.toString(), t);
                                            Toasty.error(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(@NonNull Call<ResponseSuccess> call, @NonNull Throwable t) {
                                        Log.e(TAG, "onFailure: " + t.toString());
                                        Toasty.error(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        })
                        .setNeutralButton(android.R.string.cancel, null)
                        .create().show();

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

}
