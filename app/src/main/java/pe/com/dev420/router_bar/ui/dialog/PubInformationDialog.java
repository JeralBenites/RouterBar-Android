package pe.com.dev420.router_bar.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.benites.jeral.router_bar.R;

import pe.com.dev420.router_bar.activities.MainMapsActivity;

public class PubInformationDialog extends DialogFragment {
    private ImageView pubImageView, pubGpsImageView, pubPhoneImageView;
    private TextView pubNameTextView, pubHourTextView, pubAdrressTextView, pubPhoneTextView;
    private CheckBox pubDeliveryCheckBox, pub24CheckBox;
    private PubInformation mListener;
    private View customView;
    private MainMapsActivity mainMapsActivity;

    public PubInformationDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        customView = inflater.inflate(R.layout.bar_information, null);
        ui();
        builder.setView(customView);
        loadData();
        return builder.create();
    }

    private void ui() {
        mainMapsActivity = (MainMapsActivity) getActivity();
        pubImageView = customView.findViewById(R.id.pubImageView);
        pubGpsImageView = customView.findViewById(R.id.pubGpsImageView);
        pubPhoneImageView = customView.findViewById(R.id.pubPhoneImageView);
        pubNameTextView = customView.findViewById(R.id.pubNameEditText);
        pubHourTextView = customView.findViewById(R.id.pubHourTextView);
        pubAdrressTextView = customView.findViewById(R.id.pubAdrressEditText);
        pubPhoneTextView = customView.findViewById(R.id.pubPhoneEditText);
        pubDeliveryCheckBox = customView.findViewById(R.id.pubDeliveryCheckBox);
        pub24CheckBox = customView.findViewById(R.id.pub24CheckBox);
        pub24CheckBox.setEnabled(false);
        pubDeliveryCheckBox.setEnabled(false);
    }

    private void loadData() {
        byte[] decodedString = Base64.decode(mainMapsActivity.PubEntityInfo.getImage(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        pubImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options));
        pubNameTextView.setText(mainMapsActivity.PubEntityInfo.getName());
        pubHourTextView.setText(hourFormat(mainMapsActivity.PubEntityInfo.getHour().getHourOpen(), mainMapsActivity.PubEntityInfo.getHour().getHourClose()));
        pubAdrressTextView.setText(mainMapsActivity.PubEntityInfo.getAddress().getStreet());
        pubPhoneTextView.setText(mainMapsActivity.PubEntityInfo.getSocial().getPhone());
        pubDeliveryCheckBox.setChecked(mainMapsActivity.PubEntityInfo.getDelivery());
        pub24CheckBox.setChecked(mainMapsActivity.PubEntityInfo.getHora24());

        pubImageView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.goCatalog(mainMapsActivity.PubEntityInfo);
            }
        });
        pubGpsImageView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.goGps(mainMapsActivity.PubEntityInfo);
            }

        });
        pubPhoneImageView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.goPhone(mainMapsActivity.PubEntityInfo);
            }
        });
    }

    private String hourFormat(String open, String close) {
        return "Habre: " + open + " -- Cierra: " + close;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PubInformation) {
            mListener = (PubInformation) context;
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
}
