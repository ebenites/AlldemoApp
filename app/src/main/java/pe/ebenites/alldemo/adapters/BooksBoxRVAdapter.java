package pe.ebenites.alldemo.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.fragments.BooksBoxFragment;
import pe.ebenites.alldemo.fragments.BooksDetailFragment;
import pe.ebenites.alldemo.models.Book;
import pe.ebenites.alldemo.models.ResponseSuccess;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;

public class BooksBoxRVAdapter extends Adapter<BooksBoxRVAdapter.ViewHolder> {

    private static final String TAG = BooksBoxRVAdapter.class.getSimpleName();

    private BooksBoxFragment fragment;

    private List<Book> books;

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public BooksBoxRVAdapter(BooksBoxFragment fragment){
        this.fragment = fragment;
        this.books = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView logoImage;
        TextView titleText;
        TextView descriptionText;
        View gradientView;

        ViewHolder(View itemView) {
            super(itemView);
            logoImage = itemView.findViewById(R.id.logo_image);
            titleText = itemView.findViewById(R.id.title_text);
            descriptionText = itemView.findViewById(R.id.description_text);
            gradientView = itemView.findViewById(R.id.gradient);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_books_box, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        final Context context = viewHolder.itemView.getContext();

        final Book book = books.get(position);

        // https://github.com/amulyakhare/TextDrawable
        int color = ColorGenerator.MATERIAL.getColor(book.getTitle());

        if(book.getImage() == null) {
            TextDrawable drawable = TextDrawable.builder().buildRect(book.getTitle().substring(0, 1), color);
            viewHolder.logoImage.setImageDrawable(drawable);
        } else {
            String url = ApiService.API_BASE_URL + "api/store/books/" + book.getId() + "/image" + book.getImage().substring(book.getImage().lastIndexOf("/"));
            Log.d(TAG, "photo url: " + url);
//            Picasso.with(context).load(url).into(viewHolder.logoImage);
            ApiServiceGenerator.createPicasso(context).load(url).into(viewHolder.logoImage);    // Picasso with JWT Auth
        }

        viewHolder.gradientView.setBackgroundColor(color);

        viewHolder.titleText.setText(book.getTitle());

        viewHolder.descriptionText.setText(String.format(Locale.getDefault(), "(%d classes)", book.getTotalLessons()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = BooksDetailFragment.newInstance(book.getId(), book.getTitle());

                FragmentManager fragmentManager = BooksBoxRVAdapter.this.fragment.getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
            }
        }) ;

        final int MAX_WIDTH = 480;

        // Set Height same Width
        viewHolder.itemView.post(new Runnable() {
            @Override
            public void run() {
                int width = viewHolder.itemView.getWidth(); //height is ready
                Log.d(TAG, "width: " + width);
                if(width > MAX_WIDTH) {
                    viewHolder.itemView.getLayoutParams().height = MAX_WIDTH;
                    viewHolder.titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    viewHolder.descriptionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                } else {
                    viewHolder.itemView.getLayoutParams().height = width;
                }

            }
        });


        // TEMP: DELETE CONTENT ON LONG CLICK
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(fragment.getActivity())
                        .setTitle("Devolver contenido")
                        .setMessage("¿Realmente desea devolver el contenido?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ApiServiceGenerator.createService(context, ApiService.class).refundBooks(book.getId()).enqueue(new Callback<ResponseSuccess>() {
                                    @Override
                                    public void onResponse(@NonNull Call<ResponseSuccess> call, @NonNull retrofit2.Response<ResponseSuccess> response) {
                                        try {
                                            if (!response.isSuccessful()) {
                                                throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                                            }

                                            ResponseSuccess success = response.body();
                                            Log.d(TAG, "success: " +  success);

                                            // delete item from recyclerview and notify new size
                                            books.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, books.size());


                                            Toasty.success(fragment.getActivity(), success.getMessage(), Toasty.LENGTH_LONG).show();

                                        } catch (Throwable t) {
                                            Log.e(TAG, "onThrowable: " + t.toString(), t);
                                            Toasty.error(fragment.getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(@NonNull Call<ResponseSuccess> call, @NonNull Throwable t) {
                                        Log.e(TAG, "onFailure: " + t.toString());
                                        Toasty.error(fragment.getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
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
        return books.size();
    }

}
