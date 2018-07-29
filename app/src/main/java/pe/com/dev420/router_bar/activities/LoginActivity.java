package pe.com.dev420.router_bar.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.benites.jeral.router_bar.R;

import pe.com.dev420.router_bar.model.CustomerEntity;
import pe.com.dev420.router_bar.model.UserEntity;
import pe.com.dev420.router_bar.storage.network.ApiClass;
import pe.com.dev420.router_bar.storage.network.RouterApi;
import pe.com.dev420.router_bar.storage.network.entity.CustomerRaw;
import pe.com.dev420.router_bar.storage.network.entity.UserRaw;
import pe.com.dev420.router_bar.storage.preferences.PreferencesHelper;
import pe.com.dev420.router_bar.ui.dialog.RegisterDialogFragment;
import pe.com.dev420.router_bar.ui.dialog.ResgisterDialog;
import pe.com.dev420.router_bar.util.SnackBarHelper;
import pe.com.dev420.router_bar.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pe.com.dev420.router_bar.storage.preferences.PreferencesHelper.getStateSession;
import static pe.com.dev420.router_bar.util.Util.getBitmapFromAssets;

public class LoginActivity extends BaseActivity implements View.OnClickListener, ResgisterDialog {

    private EditText passwordEditText, loginEditText;
    private TextView loginTextView;
    private Button registerButton;
    private FrameLayout flayLoading;
    private Context mContext;
    private ImageView ImageLogo;
    private RegisterDialogFragment dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ui();
    }

    private void ui() {
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginTextView = findViewById(R.id.loginTextView);
        registerButton = findViewById(R.id.registerButton);
        flayLoading = findViewById(R.id.flayLoading);
        ImageLogo = findViewById(R.id.ImageLogo);
        mContext = getApplicationContext();
        ImageLogo.setImageBitmap(getBitmapFromAssets("images/logo.png", this));
        loginTextView.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginTextView:
                loading(flayLoading, true);
                if (validateLogin())
                    login();
                else
                    loading(flayLoading, false);
                break;
            case R.id.registerButton:
                loadDialog();
                break;
        }
    }

    private boolean validateLogin() {
        if (!Util.checkConnection(mContext)) {
            SnackBarHelper.showWarningMessage(flayLoading, mContext, getString(R.string.string_internet_connection_warning));
            return false;
        }
        if (loginEditText.getText().toString().isEmpty()) {
            loginEditText.setError(getString(R.string.error_user));
            return false;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordEditText.setError(getString(R.string.error_password));
            return false;
        }
        return true;
    }

    private void login() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(loginEditText.getText().toString());
        userEntity.setPassword(passwordEditText.getText().toString());
        Call<UserRaw> userRawCall = ApiClass.getRetrofit().create(RouterApi.class).loginUser(userEntity);
        userRawCall.enqueue(new Callback<UserRaw>() {
            @Override
            public void onResponse(Call<UserRaw> call, Response<UserRaw> response) {
                loading(flayLoading, false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        PreferencesHelper.saveBLSession(getApplicationContext(), response.body());
                        if (getStateSession(mContext).equals("1"))
                            next(PubsActivity.class, null, true);
                        else if (getStateSession(mContext).equals("2"))
                            next(MainMapsActivity.class, null, true);
                    }
                } else {
                    SnackBarHelper.showErrorMessage(flayLoading, getApplicationContext(), getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<UserRaw> call, Throwable t) {
                loading(flayLoading, false);
            }
        });
    }

    private void loadDialog() {
        dialog = new RegisterDialogFragment();
        dialog.show(getSupportFragmentManager(), "RegisterDialogFragment");
    }


    @Override
    public void onRegisterClick(View view, CustomerEntity customerEntity) {
        loading(view, true);
        Call<CustomerRaw> listCall = ApiClass.getRetrofit().create(RouterApi.class).insertCustomer(customerEntity);
        listCall.enqueue(new Callback<CustomerRaw>() {
            @Override
            public void onResponse(@NonNull Call<CustomerRaw> call, @NonNull Response<CustomerRaw> response) {
                loading(view, false);
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    UserRaw userRaw = new UserRaw();
                    userRaw.setUserEntity(customerEntity.getUserEntity());
                    userRaw.getUserEntity().set_id(response.body().getUserEntity().get_id());
                    PreferencesHelper.saveBLSession(mContext, userRaw);
                    if (getStateSession(mContext).equals("1"))
                        next(PubsActivity.class, null, true);
                    else if (getStateSession(mContext).equals("2"))
                        next(MainMapsActivity.class, null, true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerRaw> call, @NonNull Throwable t) {
                loading(view, false);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onCalenderClick(int year, int month, int day) {

    }

    @Override
    public void onBackPressed() {
    }
}
