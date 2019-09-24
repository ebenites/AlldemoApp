package pe.ebenites.alldemo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import es.dmoral.toasty.Toasty;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.activities.MainActivity;
import pe.ebenites.alldemo.adapters.ClassroomsBoxRVAdapter;
import pe.ebenites.alldemo.models.Book;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksDetailFragment extends Fragment {

    private static final String TAG = BooksDetailFragment.class.getSimpleName();

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";

    private Integer id;
    private String title;

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

    public static Fragment newInstance(Integer id, String title){
        Fragment fragment = new BooksDetailFragment();
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

    private ImageView logoImage;
    private TextView titleText;
    private View gradientView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(title);
        View view = inflater.inflate(R.layout.fragment_books_detail, container, false);

        logoImage = view.findViewById(R.id.logo_image);
        titleText = view.findViewById(R.id.title_text);
        gradientView = view.findViewById(R.id.gradient);

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
        recyclerView.setAdapter(new ClassroomsBoxRVAdapter(getActivity()));
        progressLayout = view.findViewById(R.id.progress_layout);
        progressLayout.showLoading();

        initialize();

        return view;
    }

    public void initialize(){
        Log.d(TAG, "initialize");

        ApiServiceGenerator.createService(getContext(), ApiService.class).getMyBooksDetail(id).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(@NonNull Call<Book> call, @NonNull Response<Book> response) {
                try {
                    if (!response.isSuccessful()) {
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                    Book book = response.body();
                    Log.d(TAG, "book: " +  book);

                    if(book == null || book.getLessons() == null || book.getLessons().isEmpty()){
                        progressLayout.showEmpty(R.drawable.ic_empty, getString(R.string.data_empty), getString(R.string.data_empty_detail));
                        return;
                    }

                    // https://github.com/amulyakhare/TextDrawable
                    int color = ColorGenerator.MATERIAL.getColor(book.getTitle());

                    if(book.getImage() == null) {
                        TextDrawable drawable = TextDrawable.builder().buildRect(book.getTitle().substring(0, 1), color);
                        logoImage.setImageDrawable(drawable);
                    } else {
                        String url = ApiService.API_BASE_URL + "api/store/books/" + book.getId() + "/image" + book.getImage().substring(book.getImage().lastIndexOf("/"));
                        Log.d(TAG, "photo url: " + url);
//                        Picasso.with(context).load(url).into(viewHolder.logoImage);
                        ApiServiceGenerator.createPicasso(getContext()).load(url).into(logoImage);    // Picasso with JWT Auth
                    }

                    gradientView.setBackgroundColor(color);

                    titleText.setText(book.getTitle());

                    ClassroomsBoxRVAdapter adapter = (ClassroomsBoxRVAdapter) recyclerView.getAdapter();
                    adapter.setLessons(book.getLessons());
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
            public void onFailure(@NonNull Call<Book> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                if (getActivity() == null) return;
                progressLayout.showError(R.drawable.ic_error, getString(R.string.data_error), t.toString(),getString(R.string.data_tryagain_button), onTryAgain);
                Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                refreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).toggleToolbar(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).toggleToolbar(false);
        }
    }


}
