package pe.ebenites.alldemo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;

import pe.ebenites.alldemo.R;

public class PDFViewActivity extends AppCompatActivity {

    private static final String TAG = PDFViewActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 100;

    private ProgressBar progressBar;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        setTitle(R.string.title_pdfviewer);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        pdfView = findViewById(R.id.pdfView);

        if(getIntent().getExtras() != null && getIntent().getExtras().getString("uri") != null) {
            String title = getIntent().getExtras().getString("title");
            if(title != null) setTitle(title);
            Uri uri = Uri.parse(getIntent().getExtras().getString("uri"));
            displayFromUri(uri);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, REQUEST_CODE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayFromUri(Uri uri) {
        pdfView.fromUri(uri)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        Log.d(TAG, "Load complete with " + nbPages + " pages");
                    }
                })
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                        Log.d(TAG, "Render complete");
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .load();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            displayFromUri(uri);
        }
    }

}
