package com.sharecontact.conactshare.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.sharecontact.conactshare.BARCODE.BarcodeCaptureActivity;
import com.sharecontact.conactshare.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    Button send_view;
    Button received_view;
    TextView phone;
    int BARCODE_READER_REQUEST_CODE = 111;
    View root;

    private AdView adView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        phone = root.findViewById(R.id.scan_number);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        send_view = root.findViewById(R.id.send_view);
        received_view = root.findViewById(R.id.received_view);
        send_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigation.findNavController(view).navigate(R.id.navigation_home);
                Navigation.findNavController(view).navigate(R.id.navigation_notifications);
            }
        });
        received_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });


        AudienceNetworkAds.initialize(getActivity());
        banner_add();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    phone.setVisibility(View.VISIBLE);
                    phone.setText(barcode.displayValue);
                    String r = barcode.displayValue;
                    if (r != null && !TextUtils.isEmpty(r) /*|| TextUtils.isDigitsOnly(r)*/ && Patterns.PHONE.matcher(r).matches()) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + r));
                        startActivity(intent);
                    } else {
                        phone.setText("Not a number");
                    }
                } else {
                    phone.setText(R.string.no_barcode_captured);
                }
            } else {
                phone.setVisibility(View.VISIBLE);
                phone.setText(String.format(getString(R.string.barcode_error_format),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void banner_add() {
        try {
            adView = new AdView(getActivity(), getResources().getString(R.string.home_page_banner), AdSize.BANNER_HEIGHT_50);

            // Find the Ad Container
            LinearLayout adContainer = root.findViewById(R.id.home_banner_container);

            // Add the ad view to your activity layout
            adContainer.addView(adView);

            adView.setAdListener(new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    // Ad error callback
                    //Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(),Toast.LENGTH_LONG).show();
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Ad loaded callback
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                }
            });


            // Request an ad
            adView.loadAd();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onDestroy() {
        try{
            if (adView != null) {
                adView.destroy();
            }
            super.onDestroy();
        }catch(Exception e){
            Log.d("Error Line Number",Log.getStackTraceString(e));
        }
    }

}