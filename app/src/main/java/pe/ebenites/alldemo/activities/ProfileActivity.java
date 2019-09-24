package pe.ebenites.alldemo.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.fragments.ProfileFragment;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle(R.string.app_name);

        Fragment fragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
    }

}
