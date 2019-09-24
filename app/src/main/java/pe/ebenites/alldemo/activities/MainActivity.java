package pe.ebenites.alldemo.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import pe.ebenites.alldemo.BuildConfig;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.fragments.AssignmentsFragment;
import pe.ebenites.alldemo.fragments.BooksFragment;
import pe.ebenites.alldemo.fragments.ClassroomsFragment;
import pe.ebenites.alldemo.fragments.CoursesFragment;
import pe.ebenites.alldemo.fragments.HomeFragment;
import pe.ebenites.alldemo.fragments.ProfileFragment;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import pe.ebenites.alldemo.util.Constants;
import pe.ebenites.alldemo.util.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;

    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    private View mainContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        // Setear Toolbar como action bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        final ActionBar ab = getSupportActionBar();
//        if (ab != null) {
//            // Poner ícono del drawer toggle
//            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
//            ab.setDisplayHomeAsUpEnabled(true);
//        }

        // Setear DrawerLayout y su NavigationItemSelectedListener
        drawerLayout = findViewById(R.id.drawer_layout);

        // Setting the actionbarToggle to drawer layout
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, android.R.string.ok, android.R.string.cancel);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        mainContentView = findViewById(R.id.main_content);

        navigationView = findViewById(R.id.nav_view);

        // NavigationView
        TextView textFullname = navigationView.getHeaderView(0).findViewById(R.id.menu_fullname);
        textFullname.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_USER_NAME, null));

        TextView textEmail = navigationView.getHeaderView(0).findViewById(R.id.menu_email);
        textEmail.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_USER_EMAIL, null));

        CircleImageView imagePhoto = navigationView.getHeaderView(0).findViewById(R.id.menu_photo);
        String url = ApiService.API_BASE_URL + "api/profile/image/picture.jpg";
        ApiServiceGenerator.createPicasso(this).load(url).placeholder(R.drawable.ic_picture).into(imagePhoto);

        // NavigationItem
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Marcar item presionado
                menuItem.setChecked(true);
                // Crear nuevo fragmento
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        showHomeFragment(null);
                        break;
                    case R.id.nav_classrooms:
                        showClassroomsFragment(null);
                        break;
                    case R.id.nav_courses:
                        showCoursesFragment(null);
                        break;
                    case R.id.nav_books:
                        showBooksFragment(null);
                        break;
                    case R.id.nav_assignments:
                        showAssignmentsFragment(null);
                        break;
//                    case R.id.nav_statistics:
//                        showDashboardFragment(null);
//                        break;
                    case R.id.nav_profile:
                        showProfileFragment(null);
                        break;
                    case R.id.nav_logout:
                        logout();
                        break;
                }
                return true;
            }
        });

        // Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        showHomeFragment(null);
                        break;
                    case R.id.menu_classrooms:
                        showClassroomsFragment(null);
                        break;
                    case R.id.menu_courses:
                        showCoursesFragment(null);
                        break;
                    case R.id.menu_books:
                        showBooksFragment(null);
                        break;
                    case R.id.menu_assignments:
                        showAssignmentsFragment(null);
                        break;
                }
                return true;
            }
        });

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentResumed(fm, f);
                Log.d(TAG, "onFragmentAttached: " + f);

                int navigationId = Integer.MIN_VALUE;
                int bottomNavigationId = Integer.MIN_VALUE;
                if(f instanceof HomeFragment) {
                    navigationId = R.id.nav_home;
                    bottomNavigationId = R.id.menu_home;
                } else if (f instanceof ClassroomsFragment) {
                    navigationId = R.id.nav_classrooms;
                    bottomNavigationId = R.id.menu_classrooms;
                } else if(f instanceof CoursesFragment) {
                    navigationId = R.id.nav_courses;
                    bottomNavigationId = R.id.menu_courses;
                } else if(f instanceof BooksFragment) {
                    navigationId = R.id.nav_books;
                    bottomNavigationId = R.id.menu_books;
                } else if(f instanceof AssignmentsFragment) {
                    navigationId = R.id.nav_assignments;
                    bottomNavigationId = R.id.menu_assignments;
                } else if(f instanceof ProfileFragment) {
                    navigationId = R.id.nav_profile;
                }

                if(navigationView.getCheckedItem() != null && navigationView.getCheckedItem().getItemId() != navigationId){
                    navigationView.setCheckedItem(navigationId);
                }

                if(bottomNavigationView.getSelectedItemId() != bottomNavigationId){
                    bottomNavigationView.setSelectedItemId(bottomNavigationId);
                }
            }
        }, false);  // recursive child to false

        // Fragment inicial (Home)
        if (savedInstanceState == null) {
            showHomeFragment(null);
        }

        // Go From Notification
        onNewIntent(this.getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + intent);

//        if(intent.getExtras() != null && intent.getExtras().containsKey("ACTION")) {
//            String go = intent.getExtras().getString("ACTION");
//            Log.d(TAG, "ACTION" + ": " + go);
//            if ("GO-SIMULACRO".equals(go)) {
//                showMisSimulacrosFragment(null);
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Option open drawer
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
//            case R.id.action_home:
//                showHomeFragment(null);
//                return true;
//            case R.id.action_suggestion:
//                showSuggestion();
//                return true;
            case R.id.action_debug:
                showDebug();
                return true;
            case R.id.action_about:
                showAbout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*public void showSuggestion() {
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 0, 30, 0);

        final EditText editText = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setGravity(Gravity.TOP | Gravity.START);
        editText.setLines(3);
        editText.setMaxLines(5);
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(500) });
        editText.setHint("Ingrese un contenido...");

        layout.addView(editText, layoutParams);

        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setName(R.string.menu_suggestion)
                .setMessage(R.string.suggestion_information)
                .setView(layout)
                .setPositiveButton(R.string.app_send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.d(TAG, "Sending Suggestion...");

                        String content = editText.getText().toString();

                        if(content.isEmpty()){
                            Toasty.warning(MainActivity.this, getString(R.string.suggestion_content_message), Toast.LENGTH_LONG).show();
                            return;
                        }

                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setMessage("Enviando información...");
                        progressDialog.show();

                        ApiServiceGenerator.createService(ApiService.class).sendSuggestion(content).enqueue(new Callback<Sugerencia>() {
                            @Override
                            public void onResponse(@NonNull Call<Sugerencia> call, @NonNull Response<Sugerencia> response) {
                                try {
                                    if (!response.isSuccessful()) {
                                        throw new Exception( ApiServiceGenerator.parseError(response).getMessage());
                                    }

                                    Sugerencia sugerencia = response.body();
                                    Log.d(TAG, "sugerencia: " +  sugerencia);

                                    Toasty.success(MainActivity.this, getString(R.string.suggestion_success), Toast.LENGTH_LONG).show();

                                } catch (Throwable t) {
                                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                                    Toasty.error(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                }finally {
                                    progressDialog.dismiss();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<Sugerencia> call, @NonNull Throwable t) {
                                Log.e(TAG, "onFailure: " + t.toString());
                                Toasty.error(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create().show();

        editText.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if(imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }*/

    public void showDebug() {
        Log.d(TAG, "TOKEN: " + PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_TOKEN, null));
        Log.d(TAG, "INSTANCEID: " + PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_TOKEN_GCM, null));
        new AlertDialog.Builder(this)
                .setMessage(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_TOKEN, null))
                .setPositiveButton(android.R.string.copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copyToken();
                    }
                }).create().show();
    }

    public void copyToken() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(Constants.PREF_TOKEN, PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(Constants.PREF_TOKEN, ""));
        if(clipboard != null) clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.this, "Token copiado!", Toast.LENGTH_SHORT).show();
    }

    public void showAbout() {
        TextView textView = new TextView(this);
        textView.setText(R.string.app_about);
        textView.setPadding(0, 32, 0, 0);
        textView.setGravity(Gravity.CENTER_HORIZONTAL); // Centrar texto de AlertDialog

        // copyToken hack
        textView.setOnClickListener(new View.OnClickListener() {
            private int i = 0;
            @Override
            public void onClick(View v) {
                if(i == 0){
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            i = 0;
                        }
                    }, 2000);
                }
                i++;
                if(i == 5){
                    copyToken();
                }
            }
        });

        new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME).setView(textView).create().show();
    }

    public void showHomeFragment(View view) {
        Fragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putString(DashboardFragment.ARG_DUMMY, "Bienvenido");
//        fragment.setArguments(args);
        showFragment(fragment);
    }

    public void showClassroomsFragment(View view) {
        Fragment fragment = new ClassroomsFragment();
        showFragment(fragment);
    }

    public void showCoursesFragment(View view) {
        Fragment fragment = new CoursesFragment();
        showFragment(fragment);
    }

    public void showBooksFragment(View view) {
        Fragment fragment = new BooksFragment();
        showFragment(fragment);
    }

    public void showAssignmentsFragment(View view) {
        Fragment fragment = new AssignmentsFragment();
        showFragment(fragment);
    }

    public void showProfileFragment(View view) {
        Fragment fragment = new ProfileFragment();
        showFragment(fragment);
    }

    private void showFragment(Fragment fragment){

        if (!NetworkUtils.isConnected(this)) {
            new AlertDialog.Builder(this).setIcon(R.drawable.ic_alert).setTitle(R.string.connection_error).setMessage(R.string.disconnection_error).create().show();
            return;
        }

        if(fragment instanceof HomeFragment) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);   // https://stackoverflow.com/questions/6186433/clear-back-stack-using-fragments
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
        }

        drawerLayout.closeDrawers(); // Cerrar drawer
    }
    /**
     * En fragments usar:
     *  onResume(){ if(getActivity() instanceof MainActivity) ((MainActivity)getActivity()).toggleToolbar(true); }
     *  onPause(){ if(getActivity() instanceof MainActivity) ((MainActivity)getActivity()).toggleToolbar(false); }
     * @param showHomeAsUp
     */
    public void toggleToolbar(boolean showHomeAsUp){
        if(getSupportActionBar() == null) return;
        if(showHomeAsUp){
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }else{
            // Show home button
            drawerToggle.setDrawerIndicatorEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                        drawerLayout.closeDrawers();
                    }else{
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
        }
        drawerToggle.syncState();
    }

    public void toggleBottomNavigation(boolean toShow){
        if(toShow){
            bottomNavigationView.setVisibility(View.VISIBLE);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)mainContentView.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, (int) (56 * Resources.getSystem().getDisplayMetrics().density));
            mainContentView.setLayoutParams(layoutParams);
        }else{
            bottomNavigationView.setVisibility(View.GONE);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)mainContentView.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, 0);
            mainContentView.setLayoutParams(layoutParams);
        }
    }

    private void logout(){
        Log.d(TAG, "logout");

        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .remove(Constants.PREF_TOKEN)
                .remove(Constants.PREF_ISLOGGED)
                .apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
