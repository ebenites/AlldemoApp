package pe.ebenites.alldemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.florent37.viewanimator.ViewAnimator;
import com.vstechlab.easyfonts.EasyFonts;

import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.util.Constants;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title (must be set before setting the content view of your activity)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        ViewAnimator.animate(findViewById(R.id.logo)).alpha(0, 1).duration(SPLASH_DISPLAY_LENGTH/3).start();

        TextView title = findViewById(R.id.title);
        title.setTypeface(EasyFonts.tangerineBold(this));
        ViewAnimator.animate(title).alpha(0, 1).duration(SPLASH_DISPLAY_LENGTH/3).start();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                verifyLogged();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void verifyLogged(){
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREF_ISLOGGED, false)){
            if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREF_USER_ISNEW, false)){
                startActivity(new Intent(SplashActivity.this, ProfileActivity.class));
            }else{
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }else{
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish();
    }

}
