package com.benites.jeral.router_bar.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benites.jeral.router_bar.R;
import com.benites.jeral.router_bar.model.UserEntity;
import com.benites.jeral.router_bar.storage.network.ApiClass;
import com.benites.jeral.router_bar.storage.network.RouterApi;
import com.benites.jeral.router_bar.storage.network.entity.UserRaw;
import com.benites.jeral.router_bar.storage.preferences.PreferencesHelper;
import com.benites.jeral.router_bar.util.SnackBarHelper;
import com.benites.jeral.router_bar.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText passwordEditText;
    private TextView loginTextView;
    private Button registerButton;
    private EditText loginEditText;
    private View flayLoading;

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

        loginTextView.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginTextView:
                showLoading();
                if (Util.checkConnection(getApplicationContext())) {
                    if (validateLogin()) {
                        UserEntity userEntity = new UserEntity();
                        userEntity.setEmail(loginEditText.getText().toString());
                        userEntity.setPassword(passwordEditText.getText().toString());
                        Call<UserRaw> userRawCall = ApiClass.getRetrofit().create(RouterApi.class).LoginUser(userEntity);
                        userRawCall.enqueue(new Callback<UserRaw>() {
                            @Override
                            public void onResponse(Call<UserRaw> call, Response<UserRaw> response) {
                                hideLoading();
                                if (response.isSuccessful()) {
                                    UserRaw c = response.body();
                                    if (c.getUserEntity() != null) {
                                        saveSession(c);
                                        if (c.getUserEntity().getState().equals("1"))
                                            next(PubsActivity.class, null, true);
                                        else
                                            next(MainActivity.class, null, true);
                                    }
                                } else {
                                    SnackBarHelper.showErrorMessage(flayLoading, getApplicationContext(), getString(R.string.error));
                                }
                            }

                            @Override
                            public void onFailure(Call<UserRaw> call, Throwable t) {
                                hideLoading();
                            }
                        });
                    } else {
                        hideLoading();
                    }
                } else {
                    hideLoading();
                }
                break;
            case R.id.registerButton:
                break;
        }
    }

    private boolean validateLogin() {
        String user = loginEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (user.isEmpty()) {
            loginEditText.setError(getString(R.string.error_user));
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError(getString(R.string.error_password));
            return false;
        }
        return true;
    }

    private void showLoading() {
        flayLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        flayLoading.setVisibility(View.GONE);
    }

    private void saveSession(UserRaw userRaw) {
        PreferencesHelper.saveBLSession(this, userRaw.getUserEntity().getEmail(), userRaw.getUserEntity().getPassword(), userRaw.getUserEntity().getState());
    }
}
