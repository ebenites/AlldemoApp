package pe.ebenites.alldemo.activities;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;

import pe.ebenites.alldemo.R;

/**
 * https://github.com/PierfrancescoSoffritti/android-youtube-player/tree/8.0.1
 * (Las versiones recientes requieren AndroidX)
 */
public class YouTubePlayerActivity extends AppCompatActivity {

    private static final String TAG = YouTubePlayerActivity.class.getSimpleName();

    public static final String ARG_ID = "id";
    public static final String ARG_TITLE = "title";

    private YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title (must be set before setting the content view of your activity)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_youtube_player);

        if(getIntent().getExtras() == null || getIntent().getExtras().get(ARG_ID) == null){
            finish();
            return;
        }

        final String videoId = getIntent().getExtras().getString(ARG_ID); // "6JYIGclVQdw"

        youTubePlayerView = findViewById(R.id.youtube_player_view);

        // The player will automatically release itself when the activity is destroyed.
        // The player will automatically pause when the activity is paused
        // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer youTubePlayer) {
                youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        loadVideo(youTubePlayer, videoId);
                    }
                });

                addFullScreenListenerToPlayer(youTubePlayer);
                //setPlayNextVideoButtonClickListener(youTubePlayer);
            }
        }, true);


        final String title = getIntent().getExtras().getString(ARG_TITLE);
        TextView titleText = findViewById(R.id.title_text);
        titleText.setText(title);

    }

    @Override
    public void onBackPressed() {
        if (youTubePlayerView.isFullScreen())
            youTubePlayerView.exitFullScreen();
        else
            super.onBackPressed();
    }

    private FullScreenHelper fullScreenHelper = new FullScreenHelper(this);

    /**
     * Load a video if the activity is resumed, cue it otherwise.
     * See difference between {@link YouTubePlayer#cueVideo(String, float)} and {@link YouTubePlayer#loadVideo(String, float)}
     *
     * With this library is possible to play videos even if the player is not visible.
     * But this goes against YouTube's terms of service therefore,
     * if you plan to publish your app on the Play Store, always pause the video when the player is not visible.
     * If you don't intend to publish your app on the Play Store you can play and pause whenever you want.
     */
    private void loadVideo(YouTubePlayer youTubePlayer, String videoId) {
        if(getLifecycle().getCurrentState() == Lifecycle.State.RESUMED)
            youTubePlayer.loadVideo(videoId, 0);
        else
            youTubePlayer.cueVideo(videoId, 0);
    }

    private void addFullScreenListenerToPlayer(final YouTubePlayer youTubePlayer) {
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                Log.d(TAG, "onYouTubePlayerEnterFullScreen()");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreenHelper.enterFullScreen();
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                Log.d(TAG, "onYouTubePlayerExitFullScreen()");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                fullScreenHelper.exitFullScreen();
            }
        });
    }

    /**
     * Class responsible for changing the view from full screen to non-full screen and vice versa.
     *
     * @author Pierfrancesco Soffritti
     */
    public class FullScreenHelper {

        private Activity context;
        private View[] views;

        /**
         * @param context
         * @param views to hide/show
         */
        public FullScreenHelper(Activity context, View ... views) {
            this.context = context;
            this.views = views;
        }

        /**
         * call this method to enter full screen
         */
        public void enterFullScreen() {
            View decorView = context.getWindow().getDecorView();

            hideSystemUI(decorView);

            for(View view : views) {
                view.setVisibility(View.GONE);
                view.invalidate();
            }
        }

        /**
         * call this method to exit full screen
         */
        public void exitFullScreen() {
            View decorView = context.getWindow().getDecorView();

            showSystemUI(decorView);

            for(View view : views) {
                view.setVisibility(View.VISIBLE);
                view.invalidate();
            }
        }

        // hides the system bars.
        private void hideSystemUI(View mDecorView) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        // This snippet shows the system bars.
        private void showSystemUI(View mDecorView) {
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        mDecorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

}
