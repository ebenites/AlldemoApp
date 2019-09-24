package pe.ebenites.alldemo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.adapters.ViewPagerAdapter;
import pe.ebenites.alldemo.models.Course;
import pe.ebenites.alldemo.models.Lesson;
import pe.ebenites.alldemo.models.ResponseSuccess;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassroomsStoreFragment extends Fragment {

    private static final String TAG = ClassroomsStoreFragment.class.getSimpleName();

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
        Fragment fragment = new ClassroomsStoreFragment();
        return fragment;
    }

    private ViewGroup container;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_classrooms);
        View view = inflater.inflate(R.layout.fragment_classrooms_store, container, false);

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // https://github.com/luizgrp/SectionedRecyclerViewAdapter
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
        recyclerView.setAdapter(sectionAdapter);

        progressLayout = view.findViewById(R.id.progress_layout);
        progressLayout.showLoading();

        this.container = container;

        initialize();

        return view;
    }

    public void initialize(){
        Log.d(TAG, "initialize");

        ApiServiceGenerator.createService(getContext(), ApiService.class).getLessons().enqueue(new Callback<List<Course>>() {
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

                    SectionedRecyclerViewAdapter adapter = (SectionedRecyclerViewAdapter) recyclerView.getAdapter();

                    adapter.removeAllSections();

                    for(Course course : courses) {
                        adapter.addSection(new CourseSection(course));
                    }

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

    private void buy(final Lesson lesson) {

        /*new AlertDialog.Builder(getContext())
                .setName(R.string.app_buy_dialog_title)
                .setMessage(getString(R.string.app_buy_dialog_message, lesson.getName()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buy(lesson);
                    }
                })
                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();*/

        new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setCustomImage(R.drawable.ic_cart)
                .setTitleText(getString(R.string.app_buy_dialog_title))
                .setContentText(getString(R.string.app_buy_dialog_message, lesson.getTitle()))
                .setConfirmText(getString(R.string.app_buy))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {

                        sDialog
//                                .setTitleText(getString(R.string.loading_information))
//                                .showContentText(false)
                                .setTitleText("")
                                .setContentText(getString(R.string.loading_information))
                                .setConfirmClickListener(null)
                                .showCancelButton(false)
                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

                        ApiServiceGenerator.createService(getContext(), ApiService.class).checkoutLessons(lesson.getId()).enqueue(new Callback<ResponseSuccess>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseSuccess> call, @NonNull Response<ResponseSuccess> response) {
                                try {
                                    if (!response.isSuccessful()) {
                                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                                    }

                                    ResponseSuccess success = response.body();
                                    Log.d(TAG, "success: " +  success);

                                    sDialog
                                            .setTitleText(getString(R.string.app_buy_dialog_title))
                                            .setContentText(success.getMessage())
                                            .setConfirmText(getString(android.R.string.ok))
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                    // refresh ClassroomsStore
                                                    initialize();

                                                    // refresh ClassroomsBox
                                                    if(container instanceof ViewPager){
                                                        ViewPager viewPager = (ViewPager) container;
                                                        ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
                                                        if(viewPagerAdapter != null) {
                                                            Fragment fragment = viewPagerAdapter.getItem(0);
                                                            if (fragment instanceof ClassroomsBoxFragment){
                                                                ((ClassroomsBoxFragment)fragment).initialize();
                                                            }
                                                        }
                                                    }

                                                    Toasty.success(getContext(), R.string.store_checkout_success, Toasty.LENGTH_LONG).show();

                                                    sDialog.dismissWithAnimation();
                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                } catch (Throwable t) {
                                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                                    if (getActivity() == null) return;
                                    Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                    sDialog.dismissWithAnimation();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<ResponseSuccess> call, @NonNull Throwable t) {
                                Log.e(TAG, "onFailure: " + t.toString());
                                if (getActivity() == null) return;
                                Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                sDialog.dismissWithAnimation();
                            }
                        });

                    }
                })
                .showCancelButton(true)
                .setCancelText(getString(android.R.string.cancel))
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();

    }

    private class CourseSection extends StatelessSection {

        private Course course;

        CourseSection(Course course) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.section_lessons_item)
                    .headerResourceId(R.layout.section_lessons_header)
                    .footerResourceId(R.layout.section_lessons_footer)
                    .build());

            this.course = course;
            this.setHasFooter(false);   // Disable Footer
        }

        /* Item */

        private int selectedItem = -1;

        private class ItemViewHolder extends RecyclerView.ViewHolder {

            final TextView titleText;
            final TextView priceText;
            final Button buyButton;

            ItemViewHolder(View view) {
                super(view);

                titleText = view.findViewById(R.id.title_text);
                priceText = view.findViewById(R.id.price_text);
                buyButton = view.findViewById(R.id.buy_button);
            }
        }

        @Override
        public int getContentItemsTotal() {
            return this.course.getLessons().size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;

            final Lesson lesson = this.course.getLessons().get(position);


            itemHolder.titleText.setText(lesson.getTitle());
            itemHolder.priceText.setText(lesson.getPriceFormatted());

            itemHolder.buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buy(lesson);
                }
            });

        }

        /* Header */

        private class HeaderViewHolder extends RecyclerView.ViewHolder {

            final TextView titleText;

            HeaderViewHolder(View view) {
                super(view);

                titleText = view.findViewById(R.id.title);
            }
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.titleText.setText(this.course.getTitle());
        }

        /* Footer */

        private class FooterViewHolder extends RecyclerView.ViewHolder {

            FooterViewHolder(View view) {
                super(view);
            }
        }

        @Override
        public RecyclerView.ViewHolder getFooterViewHolder(View view) {
            return new FooterViewHolder(view);
        }

        @Override
        public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
        }

    }

}
