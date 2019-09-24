package pe.ebenites.alldemo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.fragments.BooksStoreFragment;
import pe.ebenites.alldemo.models.Book;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;

public class BooksStoreRVAdapter extends Adapter<BooksStoreRVAdapter.ViewHolder> {

    private static final String TAG = BooksStoreRVAdapter.class.getSimpleName();

    private BooksStoreFragment fragment;

    private List<Book> books;

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public BooksStoreRVAdapter(BooksStoreFragment fragment){
        this.fragment = fragment;
        this.books = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView logoImage;
        TextView titleText;
        TextView priceText;
        Button buyButton;
        View gradientView;

        ViewHolder(View itemView) {
            super(itemView);
            logoImage = itemView.findViewById(R.id.logo_image);
            titleText = itemView.findViewById(R.id.title_text);
            priceText = itemView.findViewById(R.id.price_text);
            buyButton = itemView.findViewById(R.id.buy_button);
            gradientView = itemView.findViewById(R.id.gradient);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_books_store, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

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

        viewHolder.priceText.setText(book.getPriceFormatted());

        viewHolder.buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.buy(book);
            }
        }) ;

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

}
