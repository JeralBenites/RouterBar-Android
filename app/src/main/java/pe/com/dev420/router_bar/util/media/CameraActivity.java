package pe.com.dev420.router_bar.util.media;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.benites.jeral.router_bar.R;

import pe.com.dev420.router_bar.fragments.OnCamerActionListener;


public class CameraActivity extends AppCompatActivity implements OnCamerActionListener {

    private final int REQUEST_CAMERA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }

    public void sendPhoto(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("PATH", path);
        Intent intent = new Intent();
        intent.putExtras(bundle);

        setResult(REQUEST_CAMERA, intent);
        finish();
    }
}
