package pe.ebenites.alldemo.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.activities.LoginActivity;
import pe.ebenites.alldemo.activities.MainActivity;
import pe.ebenites.alldemo.activities.ProfileActivity;
import pe.ebenites.alldemo.models.Department;
import pe.ebenites.alldemo.models.District;
import pe.ebenites.alldemo.models.Gender;
import pe.ebenites.alldemo.models.Province;
import pe.ebenites.alldemo.models.User;
import pe.ebenites.alldemo.services.ApiService;
import pe.ebenites.alldemo.services.ApiServiceGenerator;
import pe.ebenites.alldemo.util.BitmapUtils;
import pe.ebenites.alldemo.util.Constants;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private ProgressRelativeLayout progressLayout;

    private View.OnClickListener onTryAgain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressLayout.showLoading();
            initialize();
        }
    };

    private CircleImageView pictureImage;
    private FloatingActionButton takepictureButton;
    private EditText firstnameInput;
    private EditText lastnameInput;
    private EditText emailInput;
    private EditText phonenumberInput;
    private EditText birthdateInput;
    private MaterialSpinner genderSpinner;
    private MaterialSpinner departmentSpinner;
    private MaterialSpinner provinceSpinner;
    private MaterialSpinner districtSpinner;
//    private AppCompatAutoCompleteTextView colegioAutocomplete;
    private Button saveButton;
    private View dummy;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_profile);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // https://github.com/tajchert/Nammu/tree/1.2.1
        Nammu.init(getContext());

        // https://kylewbanks.com/blog/Setting-the-Color-of-a-TextView-Drawable-for-Android
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeTintColor(v, hasFocus);
            }
        };

        pictureImage = view.findViewById(R.id.picture_image);
        String url = ApiService.API_BASE_URL + "api/profile/image/picture.jpg";
        ApiServiceGenerator.createPicasso(getContext()).load(url).placeholder(R.drawable.ic_picture).into(pictureImage);

        takepictureButton = view.findViewById(R.id.takepicture_button);
        takepictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        firstnameInput = view.findViewById(R.id.firstname_input);
        firstnameInput.setOnFocusChangeListener(onFocusChangeListener);

        lastnameInput = view.findViewById(R.id.lastname_input);
        lastnameInput.setOnFocusChangeListener(onFocusChangeListener);

        emailInput = view.findViewById(R.id.email_input);
        emailInput.setOnFocusChangeListener(onFocusChangeListener);

        phonenumberInput = view.findViewById(R.id.phonenumber_input);
        phonenumberInput.setOnFocusChangeListener(onFocusChangeListener);
        phonenumberInput.setEnabled(false); // phonenumber readonly

        birthdateInput = view.findViewById(R.id.birthdate_input);
        birthdateInput.setKeyListener(null);
        birthdateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null && view.getId() == v.getId()) {
                    showSpinnerDatePicker();
                }
            }
        });
        birthdateInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showSpinnerDatePicker();
                }
                changeTintColor(v, hasFocus);
            }
        });

        /**
         * Sexo
         */
        List<Gender> genders = new ArrayList<>();
        genders.add(new Gender("M", "Masculino"));
        genders.add(new Gender("F", "Femenino"));
        ArrayAdapter<Gender> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner = view.findViewById(R.id.gender_spinner);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setFocusable(true);
        genderSpinner.setFocusableInTouchMode(true);
        genderSpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (v.getWindowToken() != null) {
                        v.performClick();
                    }
                }
                changeTintColor(v, hasFocus);
            }
        });

        /**
         * Departamento
         */
        departmentSpinner = view.findViewById(R.id.department_spinner);

        ArrayAdapter<District> departmentAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<District>());
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);

        departmentSpinner.setFocusable(true);
        departmentSpinner.setFocusableInTouchMode(true);
        departmentSpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (v.getWindowToken() != null) {
                        v.performClick();
                    }
                }
                changeTintColor(v, hasFocus);
            }
        });
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "departmentSpinner:onItemSelected(" + parent.getSelectedItem() + ")");
                Department department = (Department) parent.getSelectedItem();
                if (department != null)
                    loadProvinces(department.getId());
                else
                    clearProvinces();
                clearDistricts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "departmentSpinner:onNothingSelected(" + parent.getSelectedItem() + ")");
                clearProvinces();
                clearDistricts();
            }
        });

        /**
         * Provincia
         */
        provinceSpinner = view.findViewById(R.id.province_spinner);

        ArrayAdapter<District> provinceAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<District>());
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        provinceSpinner.setFocusable(true);
        provinceSpinner.setFocusableInTouchMode(true);
        provinceSpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (v.getWindowToken() != null) {
                        v.performClick();
                    }
                }
                changeTintColor(v, hasFocus);
            }
        });
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "provinceSpinner:onItemSelected(" + parent.getSelectedItem() + ")");
                Province province = (Province) parent.getSelectedItem();
                if(province != null)
                    loadDistricts(province.getId());
                else
                    clearDistricts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "provinceSpinner:onNothingSelected(" + parent.getSelectedItem() + ")");
                clearDistricts();
            }
        });

        /**
         * Distrito
         */
        districtSpinner = view.findViewById(R.id.district_spinner);

        ArrayAdapter<District> districtAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<District>());
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);

        districtSpinner.setFocusable(true);
        districtSpinner.setFocusableInTouchMode(true);
        districtSpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (v.getWindowToken() != null) {
                        v.performClick();
                    }
                }
                changeTintColor(v, hasFocus);
            }
        });
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "districtSpinner:onItemSelected(" + parent.getSelectedItem() + ")");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "districtSpinner:onNothingSelected(" + parent.getSelectedItem() + ")");
            }
        });

        /**
         * Colegio
         */
//        colegioAutocomplete = view.findViewById(R.id.colegio_autocomplete);
//        colegioAutocomplete.setOnFocusChangeListener(onFocusChangeListener);
//        colegioAutocomplete.setThreshold(3); //will start working from third character
//        colegioAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                currentColegio = (Colegio) parent.getItemAtPosition(position);
//            }
//        });

        saveButton = view.findViewById(R.id.update_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUpdate();
            }
        });

        progressLayout = view.findViewById(R.id.progress_layout);
        progressLayout.showLoading();

        dummy = view.findViewById(R.id.dummy_id);
        dummy.requestFocus();

        initialize();

        return view;
    }

    private void changeTintColor(View v, boolean hasFocus){
        Log.d(TAG, "v: " + v);

        ViewGroup viewGroup = null;
        if(v instanceof EditText) {
            viewGroup = (ViewGroup) v.getParent().getParent().getParent();
        } else if(v instanceof MaterialSpinner) {
            viewGroup = (ViewGroup) v.getParent().getParent();
        }

        if(!(viewGroup != null && viewGroup.getChildAt(0) instanceof  ImageView)) return;

        ImageView icon = (ImageView) viewGroup.getChildAt(0);
        if(hasFocus) {
            icon.getDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN));
        } else {
            icon.getDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_IN));
        }
    }

    private void showSpinnerDatePicker(){
        Calendar now = Calendar.getInstance();

        new SpinnerDatePickerDialogBuilder()
                .context(getContext())
                .callback(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        birthdateInput.setText(String.format(getString(R.string.profile_dateformat), dayOfMonth, monthOfYear + 1, year));
                    }
                })
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                .maxDate(now.get(Calendar.YEAR) + 2, 11, 31)
                .minDate(1900, 0, 1)
                .build()
                .show();
    }

    private void takePicture() {

        final String[] permissions = new String[]{
//                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        // ValidatePermissions: https://github.com/tajchert/Nammu
        if(!Nammu.hasPermission(getActivity(), permissions)){
            Nammu.askForPermission(this, permissions, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    Log.d(TAG, "permissionGranted");
                    EasyImage.openChooserWithGallery(ProfileFragment.this, getString(R.string.choose_picture), 0);
                }

                @Override
                public void permissionRefused() {
                    Log.d(TAG, "permissionRefused");
                    Toasty.error(getContext(), R.string.donthave_permission, Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        // Start camera/gallery: https://github.com/jkwiecien/EasyImage
        EasyImage.openChooserWithGallery(this, getString(R.string.choose_picture), 0);
    }

    private File file;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult(requestCode: " + requestCode + ", permissions: " + permissions + ", grantResults: " + grantResults + ")");
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data + ")");

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Log.e(TAG, "onImagePickerError: " + e.getMessage(), e);
            }

            @Override
            public void onImagesPicked(@NonNull List<File> files, EasyImage.ImageSource imageSource, int i) {
                Log.d(TAG, "onImagePicked: files: " + files);
                Log.d(TAG, "onImagePicked: imageSource: " + imageSource);

                if(files.size() == 0) return;

                file = files.get(0);

                Picasso.with(getActivity()).load(file).into(pictureImage);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getContext());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private Department curDepartment;
    private Province curProvince;
    private District curDistrict;

    public void initialize(){
        Log.d(TAG, "call initialize");

        ApiServiceGenerator.createService(getContext(), ApiService.class).getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                try {

                    if (response.code() == 401) {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                    if (!response.isSuccessful()) {
                        throw new Exception( ApiServiceGenerator.parseError(response).getMessage());
                    }

                    User user = response.body();
                    Log.d(TAG, "user: " +  user);

                    firstnameInput.setText(user.getFirstname());
                    lastnameInput.setText(user.getLastname());

                    if(user.getGender() != null){
                        ArrayAdapter<Gender> adapter = (ArrayAdapter<Gender>) genderSpinner.getAdapter();
                        int position = adapter.getPosition(new Gender(user.getGender(), null));
                        if(position != -1) {
                            genderSpinner.setSelection(position + 1);
                        }
                    }

                    try{
                        if(user.getBirthdate() != null) {
                            String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(user.getBirthdate()) );
                            birthdateInput.setText(fecha);
                        }
                    }catch (Exception e){
                        Log.e(TAG, e.toString(), e);
                    }

                    emailInput.setText(user.getEmail());
                    phonenumberInput.setText(user.getPhonenumber());

                    // Set departamentoSpinner value
                    if (user.getDistrict() != null && user.getDistrict().getProvince() != null && user.getDistrict().getProvince().getDepartment() != null) {
                        curDepartment = user.getDistrict().getProvince().getDepartment();
                    }

                    // Set provinceSpinner value
                    if (user.getDistrict() != null && user.getDistrict().getProvince() != null) {
                        curProvince = user.getDistrict().getProvince();
                    }

                    // Set districtSpinner value
                    if (user.getDistrict() != null) {
                        curDistrict = user.getDistrict();
                    }

                    loadDepartments();

                    progressLayout.showContent();

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                    if (getActivity() == null) return;
                    progressLayout.showError(R.drawable.ic_error, getString(R.string.data_error), t.toString(), getString(R.string.data_tryagain_button), onTryAgain);
                    Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                if (getActivity() == null) return;
                progressLayout.showError(R.drawable.ic_error, getString(R.string.data_error), t.toString(), getString(R.string.data_tryagain_button), onTryAgain);
                Toasty.error(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadDepartments() {
        Log.d(TAG, "loadDepartments()");

        ApiServiceGenerator.createService(getContext(), ApiService.class).getDepartments().enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(@NonNull Call<List<Department>> call, @NonNull Response<List<Department>> response) {
                try {

                    if (!response.isSuccessful()) {
                        throw new Exception( ApiServiceGenerator.parseError(response).getMessage());
                    }

                    List<Department> departments = response.body();
                    Log.d(TAG, "departments: " + departments);

                    ArrayAdapter<Department> adapter = (ArrayAdapter<Department>)departmentSpinner.getAdapter();
                    adapter.clear();
                    adapter.addAll(departments);

                    // Set departmentSpinner value
                    if (curDepartment != null) {
                        int position = adapter.getPosition(curDepartment);
                        if (position != -1) {
                            departmentSpinner.setSelection(position + 1);
                        }
                        curDepartment = null;
                    }else{
                        departmentSpinner.setSelection(0);
                    }

                    Log.d(TAG, "loadDepartments: Complete!");

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                    if (getContext() == null) return;
                    Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Department>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                if (getContext() == null) return;
                Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void loadProvinces(String id) {
        Log.d(TAG, "loadProvinces(id: " + id + ")");

        ApiServiceGenerator.createService(getContext(), ApiService.class).getProvinces(id).enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(@NonNull Call<List<Province>> call, @NonNull Response<List<Province>> response) {
                try {

                    if (!response.isSuccessful()) {
                        throw new Exception( ApiServiceGenerator.parseError(response).getMessage());
                    }

                    List<Province> provinces = response.body();
                    Log.d(TAG, "provinces: " + provinces);

                    ArrayAdapter<Province> adapter = (ArrayAdapter<Province>)provinceSpinner.getAdapter();
                    adapter.clear();
                    adapter.addAll(provinces);

                    // Set provinceSpinner value
                    if (curProvince != null) {
                        int position = adapter.getPosition(curProvince);
                        if (position != -1) {
                            provinceSpinner.setSelection(position + 1);
                        }
                        curProvince = null;
                    }else{
                        provinceSpinner.setSelection(0);
                    }

                    Log.d(TAG, "loadProvinces: Complete!");

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                    if (getContext() == null) return;
                    Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Province>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                if (getContext() == null) return;
                Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void loadDistricts(String id) {
        Log.d(TAG, "loadDistricts(id: " + id + ")");

        ApiServiceGenerator.createService(getContext(), ApiService.class).getDistricts(id).enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(@NonNull Call<List<District>> call, @NonNull Response<List<District>> response) {
                try {

                    if (!response.isSuccessful()) {
                        throw new Exception( ApiServiceGenerator.parseError(response).getMessage());
                    }

                    List<District> districts = response.body();
                    Log.d(TAG, "districts: " + districts);

                    ArrayAdapter<District> adapter = (ArrayAdapter<District>)districtSpinner.getAdapter();
                    adapter.clear();
                    adapter.addAll(districts);

                    // Set provinceSpinner value
                    if (curDistrict != null) {
                        int position = adapter.getPosition(curDistrict);
                        if (position != -1) {
                            districtSpinner.setSelection(position + 1);
                        }
                        curDistrict = null;
                    }else{
                        districtSpinner.setSelection(0);
                    }

                    Log.d(TAG, "loadDistricts: Complete!");

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                    if (getContext() == null) return;
                    Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<District>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                if (getContext() == null) return;
                Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void clearDepartments(){
        ArrayAdapter<Department> adapter = (ArrayAdapter<Department>) departmentSpinner.getAdapter();
        adapter.clear();
        departmentSpinner.setSelection(0);
    }

    private void clearProvinces(){
        ArrayAdapter<Province> adapter = (ArrayAdapter<Province>) provinceSpinner.getAdapter();
        adapter.clear();
        provinceSpinner.setSelection(0);
    }

    private void clearDistricts(){
        ArrayAdapter<District> adapter = (ArrayAdapter<District>) districtSpinner.getAdapter();
        adapter.clear();
        districtSpinner.setSelection(0);
    }

    private void callUpdate(){
        Log.d(TAG, "callUpdate");
        try{

            if(getContext() == null) return;

            hideSoftKeyboard();

            String firstname = firstnameInput.getText().toString();
            String lastname = lastnameInput.getText().toString();
            Gender gender = (Gender) genderSpinner.getSelectedItem();
            String birthdate = birthdateInput.getText().toString();
            String email = emailInput.getText().toString();
            String phonenumber = phonenumberInput.getText().toString();
            Department department = (Department) departmentSpinner.getSelectedItem();
            Province province = (Province) provinceSpinner.getSelectedItem();
            District district = (District) districtSpinner.getSelectedItem();
//            Integer school = (currentColegio!=null)?currentColegio.getId():0;

            if(firstname.isEmpty()){
                Toasty.error(getContext(), getString(R.string.profile_firstname_require_message), Toast.LENGTH_SHORT).show();
                firstnameInput.setError(getString(R.string.profile_firstname_require_message));
                return;
            }

            if(lastname.isEmpty()){
                Toasty.error(getContext(), getString(R.string.profile_lastname_require_message), Toast.LENGTH_SHORT).show();
                lastnameInput.setError(getString(R.string.profile_lastname_require_message));
                return;
            }

            if(gender == null){
                Toasty.error(getContext(), getString(R.string.profile_gender_require_message), Toast.LENGTH_SHORT).show();
                genderSpinner.setError(getString(R.string.profile_gender_require_message));
                return;
            }

            if(birthdate.isEmpty()){
                Toasty.error(getContext(), getString(R.string.profile_birthdate_require_message), Toast.LENGTH_SHORT).show();
                birthdateInput.setError(getString(R.string.profile_birthdate_require_message));
                return;
            }

            // Convert "dd/MM/yyyy" to "yyyy-MM-dd"
            birthdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format( new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(birthdate) );

            if(email.isEmpty()){
                Toasty.error(getContext(), getString(R.string.profile_email_require_message), Toast.LENGTH_SHORT).show();
                emailInput.setError(getString(R.string.profile_email_require_message));
                return;
            }

            if(TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toasty.error(getContext(), getString(R.string.profile_email_invalid_message), Toast.LENGTH_SHORT).show();
                emailInput.setError(getString(R.string.profile_email_invalid_message));
                return;
            }

            if(phonenumber.isEmpty()){
                Toasty.error(getContext(), getString(R.string.profile_telefono_require_message), Toast.LENGTH_SHORT).show();
                phonenumberInput.setError(getString(R.string.profile_telefono_require_message));
                return;
            }

            if(department == null){
                Toasty.error(getContext(), getString(R.string.profile_department_require_message), Toast.LENGTH_SHORT).show();
                emailInput.setError(getString(R.string.profile_email_invalid_message));
                return;
            }

            if(province == null){
                Toasty.error(getContext(), getString(R.string.profile_province_require_message), Toast.LENGTH_SHORT).show();
                provinceSpinner.setError(getString(R.string.profile_province_require_message));
                return;
            }

            if(district == null){
                Toasty.error(getContext(), getString(R.string.profile_district_require_message), Toast.LENGTH_SHORT).show();
                districtSpinner.setError(getString(R.string.profile_district_require_message));
                return;
            }

//            if(colegio == 0){
//                Toasty.error(getContext(), getString(R.string.profile_school_require_message), Toast.LENGTH_SHORT).show();
//                schoolInput.setError(getString(R.string.profile_email_invalid_message));
//                return;
//            }

            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Guardando información...");
            progressDialog.show();

            Log.d(TAG, "Updating profile...");


            Call<User> call;

            if(this.file == null) {
                call = ApiServiceGenerator.createService(getContext(), ApiService.class).updateProfile(firstname, lastname, gender.getId(), birthdate, email, phonenumber, department.getId(), province.getId(), district.getId());
            } else {
                Log.d(TAG, "File: " + file.getPath() + " - exists: " + file.exists());

                // Podemos enviar la imagen con el tamaño original, pero lo mejor será comprimila antes de subir (byteArray)
                // RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);

                // Create bitmap from pathName
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

                // Corregir la orientación a normal de una imagen bitmap
                bitmap = BitmapUtils.fixBitmapOrientation(bitmap, file.getPath());

                // Reducir la imagen a 800px solo si lo supera
                bitmap = BitmapUtils.scaleBitmapDown(bitmap, 800);

                // Compress for upload
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Fixed OutOfMemoryError with android:largeHeap="true" : https://stackoverflow.com/a/32245018/2823916
                bitmap.recycle();
                Log.d(TAG, "isRecycled: " + bitmap.isRecycled());

                // Prepare Multipart
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                // Paramestros a Part
                RequestBody firstnamePart = RequestBody.create(MultipartBody.FORM, firstname);
                RequestBody lastnamePart = RequestBody.create(MultipartBody.FORM, lastname);
                RequestBody genderPart = RequestBody.create(MultipartBody.FORM, gender.getId());
                RequestBody birthdatePart = RequestBody.create(MultipartBody.FORM, birthdate);
                RequestBody emailPart = RequestBody.create(MultipartBody.FORM, email);
                RequestBody phonenumberPart = RequestBody.create(MultipartBody.FORM, phonenumber);
                RequestBody departmentPart = RequestBody.create(MultipartBody.FORM, department.getId());
                RequestBody provincePart = RequestBody.create(MultipartBody.FORM, province.getId());
                RequestBody districtPart = RequestBody.create(MultipartBody.FORM, district.getId());

                call = ApiServiceGenerator.createService(getContext(), ApiService.class).updateProfile(imagePart, firstnamePart, lastnamePart, genderPart, birthdatePart, emailPart, phonenumberPart, departmentPart, provincePart, districtPart);
            }

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    try {
                        if (!response.isSuccessful()) {
                            throw new Exception( ApiServiceGenerator.parseError(response).getMessage());
                        }

                        User user = response.body();
                        Log.d(TAG, "user: " +  user);

                        Toasty.success(getContext(), getString(R.string.profile_update_success), Toast.LENGTH_LONG).show();

                        // Remember updated
                        boolean r = PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
//                                .putInt(Constants.PREF_USER_ID, user.getId())
//                                .putInt(Constants.PREF_ROLE_ID, user.getRoles_id())
                                .putString(Constants.PREF_USER_NAME, user.getFullname())
//                                .putString(Constants.PREF_USER_PHONENUMBER, user.getPhonenumber())
                                .putString(Constants.PREF_USER_EMAIL, user.getEmail())
                                .putBoolean(Constants.PREF_USER_ISNEW, user.getIsnew())
//                                .putString(Constants.PREF_TOKEN, user.getToken())
//                                .putBoolean(Constants.PREF_ISLOGGED, true)
                                .commit();

                        if(getActivity() instanceof ProfileActivity){
                            startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        }

                    } catch (Throwable t) {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        if (getActivity() == null) return;
                        Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }finally {
                        if (getActivity() != null) progressDialog.dismiss();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                    if (getActivity() == null) return;
                    Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        } catch (Throwable t) {
            Log.e(TAG, "onThrowable: " + t.toString(), t);
            if (getActivity() == null) return;
            Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected void hideSoftKeyboard() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
