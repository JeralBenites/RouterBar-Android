package com.benites.jeral.router_bar.ui.dialog;

import android.view.View;

import com.benites.jeral.router_bar.model.CustomerEntity;

public interface ResgisterDialog {
    void onRegisterClick(View view, CustomerEntity customerEntity);

    void onCalenderClick(int year, int month, int day);
}
