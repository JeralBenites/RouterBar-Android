package pe.com.dev420.router_bar.ui.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.benites.jeral.router_bar.R;

import java.util.Calendar;
import java.util.Date;

import pe.com.dev420.router_bar.model.CustomerEntity;
import pe.com.dev420.router_bar.model.UserEntity;
import pe.com.dev420.router_bar.util.SnackBarHelper;
import pe.com.dev420.router_bar.util.Util;


public class RegisterDialogFragment extends DialogFragment {

    private EditText vDateBorn, vUser, vPassword, usuarioSingUp;
    private ImageView calenderView;
    private Button buttonLogin;
    private ResgisterDialog mListener;
    private String sPassword, sUser, sDateBorn, susuarioSingUp;
    private View customView;
    private Date date;

    public RegisterDialogFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResgisterDialog) {
            mListener = (ResgisterDialog) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CustomDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        customView = inflater.inflate(R.layout.layout_register, null);
        buttonLogin = customView.findViewById(R.id.signUpButton);
        calenderView = customView.findViewById(R.id.calenderView);
        vDateBorn = customView.findViewById(R.id.vDateBorn);
        vUser = customView.findViewById(R.id.emailSingUp);
        vPassword = customView.findViewById(R.id.passwordSingUp);
        usuarioSingUp = customView.findViewById(R.id.usuarioSingUp);

        calenderView.setOnClickListener(view -> {
            if (mListener != null) {
                final Calendar c = Calendar.getInstance();
                int YEAR_18 = c.get(Calendar.YEAR) - 18;
                DatePickerDialog dpd = new DatePickerDialog(customView.getContext(),
                        (view12, year, monthOfYear, dayOfMonth) -> {
                            date = new Date(year, monthOfYear, dayOfMonth);
                            vDateBorn.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        },
                        YEAR_18, c.get(Calendar.MONTH), c.get(Calendar.DATE));
                dpd.show();
            }
        });
        buttonLogin.setOnClickListener(view -> {
            if (mListener != null) {
                if (validateForm()) {
                    CustomerEntity customerEntity = new CustomerEntity();
                    customerEntity.setBirth(date);
                    customerEntity.setState("2");
                    UserEntity userEntity = new UserEntity();
                    userEntity.setEmail(sUser);
                    userEntity.setPassword(sPassword);
                    userEntity.setUserName(susuarioSingUp);
                    userEntity.setState("2");
                    customerEntity.setUserEntity(userEntity);
                    mListener.onRegisterClick(customView, customerEntity);
                }
            }
        });
        builder.setView(customView);
        return builder.create();
    }

    public boolean validateForm() {
        susuarioSingUp = usuarioSingUp.getText().toString();
        sUser = vUser.getText().toString();
        sPassword = vPassword.getText().toString();
        sDateBorn = vDateBorn.getText().toString();
        if (!Util.checkConnection(getContext())) {
            SnackBarHelper.showWarningMessage(customView, getContext(), getString(R.string.string_internet_connection_warning));
            return false;
        }
        if (susuarioSingUp.isEmpty()) {
            usuarioSingUp.setError(getString(R.string.error_user));
            return false;
        }
        if (sUser.isEmpty()) {
            vUser.setError(getString(R.string.error_password));
            return false;
        }
        if (sPassword.isEmpty()) {
            vPassword.setError(getString(R.string.error_password));
            return false;
        }
        if (sDateBorn.isEmpty()) {
            vDateBorn.setError(getString(R.string.error_fecha));
            return false;
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
