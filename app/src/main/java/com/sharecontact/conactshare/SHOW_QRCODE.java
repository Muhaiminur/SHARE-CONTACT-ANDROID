package com.sharecontact.conactshare;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.glxn.qrgen.android.QRCode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SHOW_QRCODE extends AppCompatActivity {

    @BindView(R.id.qr_number)
    TextView qrNumber;
    @BindView(R.id.qr_view)
    ImageView qrView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__qrcode);
        ButterKnife.bind(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        if (!TextUtils.isEmpty(sessionId)) {
            Bitmap myBitmap = QRCode.from(sessionId).withSize(500, 500).bitmap();
            qrNumber.setText(sessionId);
            qrView.setImageBitmap(myBitmap);
        } else {
            qrNumber.setText(getResources().getString(R.string.no_Number_string));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
