package pe.ebenites.alldemo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import fr.ganfra.materialspinner.MaterialSpinner;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.models.Role;
import pe.ebenites.alldemo.models.User;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import pe.ebenites.alldemo.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static boolean SHOWCARDID = false;

    private ProgressBar progressBar;

    private View loginView;
    private TextView welcomeText;
    private MaterialSpinner roleSpinner;
    private EditText phonenumberInput;
    private EditText passwordInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.sign_in_progress);

        welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setTypeface(EasyFonts.caviarDreamsBold(this));

        loginView = findViewById(R.id.login_form);
        roleSpinner = findViewById(R.id.role_spinner);
        phonenumberInput = findViewById(R.id.phonenumber_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // Role initialized
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(Constants.ROLE_STUDENT, "Alumno"));
        roles.add(new Role(Constants.ROLE_TEACHER, "Profesor"));
        ArrayAdapter<Role> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Load last role_id
        if(PreferenceManager.getDefaultSharedPreferences(this).contains(Constants.PREF_ROLE_ID)){
            roleSpinner.setSelection(adapter.getPosition(new Role(PreferenceManager.getDefaultSharedPreferences(this).getInt(Constants.PREF_ROLE_ID, 0), null)) + 1);
        }

        // Load last username
        if(PreferenceManager.getDefaultSharedPreferences(this).contains(Constants.PREF_USER_PHONENUMBER)){
            phonenumberInput.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_USER_PHONENUMBER, null));
            passwordInput.requestFocus();
        }

    }

    /**
     * Phonenumber SignIn
     */

    private String phoneNumber;

    private void signIn(){

        Role role = (Role) roleSpinner.getSelectedItem();
        Log.d(TAG, "role: " + role);

        phoneNumber = phonenumberInput.getText().toString();
        Log.d(TAG, "phoneNumber: " + phoneNumber);

        String password = passwordInput.getText().toString();
        Log.d(TAG, "password: " + password);

        if(role == null){
            Toasty.warning(this, "Seleccione su rol de acceso", Toast.LENGTH_LONG).show();
            roleSpinner.setError("Seleccione su rol de acceso");
            roleSpinner.requestFocus();
            return;
        }

        if(phoneNumber.isEmpty()){
            Toasty.warning(this, "Ingrese su número de celular", Toast.LENGTH_LONG).show();
            phonenumberInput.setError("Ingrese su número de celular");
            phonenumberInput.requestFocus();
            return;
        }

        if(password.isEmpty()){
            Toasty.warning(this, "Ingrese su contraseña", Toast.LENGTH_LONG).show();
            passwordInput.setError("Ingrese su número de celular");
            passwordInput.requestFocus();
            return;
        }

        if(!phoneNumber.startsWith("+51")) {
            phoneNumber = "+51" + phoneNumber;
        }

        hideSoftKeyboard();
        showProgress(true);


        hideSoftKeyboard();
        Toasty.info(getApplication(), R.string.login_verify_sucess, Toast.LENGTH_LONG).show();


        // Device information :

        String androidid = Settings.Secure.getString(getContentResolver(),  Settings.Secure.ANDROID_ID);
        Log.d(TAG, "ANDROIDID: " + androidid);
        String manufacturer = Build.MANUFACTURER;
        Log.d(TAG, "MANUFACTURER: " + manufacturer);
        String model = Build.MODEL;
        Log.d(TAG, "MODEL: " + model);
        String device = Build.DEVICE;
        Log.d(TAG, "DEVICE: " + device);
        String kernel = Build.VERSION.INCREMENTAL;
        Log.d(TAG, "VERSION: " + kernel);
        String version = Build.VERSION.RELEASE;
        Log.d(TAG, "RELEASE: " + version);
        Integer sdk = Build.VERSION.SDK_INT;
        Log.d(TAG, "SDK_INT: " + sdk);

        // Get selected Role

        Integer roles_id = ((Role) roleSpinner.getSelectedItem()).getId();
        Log.d(TAG, "ROLESID: " + roles_id);

        // Call SitecService

        ApiService service = ApiServiceGenerator.createService(LoginActivity.this, ApiService.class);

        Call<User> call = service.authenticate("Aqui va el secret registrado en el servidor", roles_id, phoneNumber, password);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                try {

                    if (!response.isSuccessful()) {
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                    User user = response.body();
                    Log.d(TAG, "User: " + user);

                    if(user.getFullname() == null) {
                        if(user.getRoles_id() == Constants.ROLE_TEACHER){
                            user.setFullname("Profesor");
                        }else{
                            user.setFullname("Alumno");
                        }
                    }

                    Toasty.success(getApplication(), getString(R.string.login_welcome_back) + " " + user.getFullname(), Toast.LENGTH_LONG).show();

                    // Save to SharePreferences
                    boolean r = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit()
                            .putInt(Constants.PREF_USER_ID, user.getId())
                            .putInt(Constants.PREF_ROLE_ID, user.getRoles_id())
                            .putString(Constants.PREF_USER_NAME, user.getFullname())
                            .putString(Constants.PREF_USER_PHONENUMBER, user.getPhonenumber())
                            .putString(Constants.PREF_USER_EMAIL, user.getEmail())
                            .putBoolean(Constants.PREF_USER_ISNEW, user.getIsnew())
                            .putString(Constants.PREF_TOKEN, user.getToken())
                            .putBoolean(Constants.PREF_ISLOGGED, true)
                            .commit();

                    // Go MainActivity/ProfileActivity
                    if (user.getIsnew())
                        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                    else
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                    Toasty.error(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                } finally {
                    showProgress(false);
                }

            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toasty.error(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                showProgress(false);
            }

        });

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        loginView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1);
        progressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0);
    }

    protected void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
