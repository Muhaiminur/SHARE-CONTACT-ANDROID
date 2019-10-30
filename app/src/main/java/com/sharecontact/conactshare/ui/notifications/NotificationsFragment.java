package com.sharecontact.conactshare.ui.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.sharecontact.conactshare.ADAPTER.CONTACT_LIST_ADAPTER;
import com.sharecontact.conactshare.DATABASE.CONTACT;
import com.sharecontact.conactshare.R;
import com.sharecontact.conactshare.RecyclerTouchListener;
import com.sharecontact.conactshare.SHOW_QRCODE;
import com.sharecontact.conactshare.Utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    public static final int REQUEST_READ_CONTACTS = 79;
    private List<CONTACT> contactList = new ArrayList<>();
    private CONTACT_LIST_ADAPTER contactListAdapter;
    RecyclerView recyclerView;
    Context context;
    View root;
    private AdView adView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final EditText contact_search = root.findViewById(R.id.contact_search);
        recyclerView = root.findViewById(R.id.contact_recycler);
        try {
            context = getActivity();
            prepareRecyclerView();
            getlivedata();
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //new Send_data_task().execute("my string parameter");
                //getlivedata();
                new Load_task().execute("my string parameter");
            } else {
                requestPermission();
            }

            contact_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    /*new Handler().postDelayed(new Runnable() {
                        public void run() {

                        }
                    }, 5000);*/
                    if(!TextUtils.isEmpty(editable.toString())){
                        filter(editable.toString());
                    }else {
                        new Load_task().execute("my string parameter");
                    }
                }
            });


            banner_add();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
        return root;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_CONTACTS)) {
            //new Send_data_task().execute("my string parameter");
            new Load_task().execute("my string parameter");
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*mobileArray = */
                    new Send_data_task().execute("my string parameter");
                    //new Load_task().execute("my string parameter");
                } else {
                    // permission denied,Disable the
                }
                return;
            }
        }
    }

    private ArrayList doSomethingForEachUniquePhoneNumber(Context context) {
        ArrayList<CONTACT> nameList = new ArrayList<>();
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                //plus any other properties you wish to query
        };

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
        } catch (SecurityException e) {
            //SecurityException can be thrown if we don't have the right permissions
        }

        if (cursor != null) {
            try {
                HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                int indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                int indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    String normalizedNumber = cursor.getString(indexOfNormalizedNumber);
                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                        String displayName = cursor.getString(indexOfDisplayName);
                        String displayNumber = cursor.getString(indexOfDisplayNumber);
                        //haven't seen this number yet: do something with this contact!
                        nameList.add(new CONTACT(displayName, displayNumber));
                    } else {
                        //don't do anything with this contact because we've already found this number
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return nameList;
    }

    private class Send_data_task extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog4;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog4 = ProgressDialog.show(context, getResources().getString(R.string.loading_title_string), getResources().getString(R.string.loading_sub_title_string));
            progressDialog4.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                contactList = doSomethingForEachUniquePhoneNumber(context);
                Log.d("Number", contactList.size() + "");
            } catch (Exception e) {
                Log.d("Paisi send data 2", e.toString());
            }

            return "this string is passed to onPostExecute";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog4.dismiss();
            Log.d("Number 2", contactList.size() + "");
            //contactListAdapter.notifyDataSetChanged();
        }
    }

    private void filter(String text) {
        Log.d("Con_filter", contactList.size() + "");
        ArrayList<CONTACT> filterdNames = new ArrayList<>();
        for (CONTACT s : contactList) {
            if (s.getNummber().toLowerCase().contains(text.toLowerCase()) || s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        Log.d("Con_filter2", filterdNames.size() + "");
        contactList = filterdNames;
        contactListAdapter.filterList(filterdNames);
    }

    public void getlivedata() {
        try {
            notificationsViewModel.getAllBlog().observe(this, new Observer<List<CONTACT>>() {
                @Override
                public void onChanged(@Nullable List<CONTACT> blogList) {
                    Log.d("Con_size2", contactList.size() + "");
                    Log.d("blog_size1", blogList.size() + "");
                    contactList.addAll(blogList);
                    Log.d("Con_size3", contactList.size() + "");
                    contactListAdapter.notifyDataSetChanged();
                }
            });
            /*notificationsViewModel.getAllBlog().observe(this, new Observer<List<CONTACT>>() {
                @Override
                public void onChanged(@Nullable List<CONTACT> blogList) {
                    prepareRecyclerView(blogList);
                    notificationsViewModel.getIsLoading().setValue(false);
                }
            });
            notificationsViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean blogList) {
                    if (blogList) {

                    } else {

                    }
                }
            });*/
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    public void prepareRecyclerView(/*List<CONTACT> blogList*/) {
        contactListAdapter = new CONTACT_LIST_ADAPTER(contactList, context);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactListAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CONTACT movie = contactList.get(position);
                //Toast.makeText(getApplicationContext(), movie.getNummber() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, SHOW_QRCODE.class);
                intent.putExtra("EXTRA_SESSION_ID", movie.getNummber());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    private class Load_task extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog4;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog4 = ProgressDialog.show(context, getResources().getString(R.string.loading_title_string), getResources().getString(R.string.loading_title_string));
            progressDialog4.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                List<CONTACT> con_temp = doSomethingForEachUniquePhoneNumber(context);
                notificationsViewModel.getAllBlog().postValue(con_temp);
                Log.d("Con_size1", con_temp.size() + "");
            } catch (Exception e) {
                Log.d("Paisi send data 2", e.toString());
            }

            return "this string is passed to onPostExecute";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog4.dismiss();
        }
    }


    public void banner_add() {
        try {
            adView = new AdView(getActivity(), getResources().getString(R.string.send_page_banner), AdSize.BANNER_HEIGHT_50);

            // Find the Ad Container
            LinearLayout adContainer = root.findViewById(R.id.send_banner_container);

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
        try {
            if (adView != null) {
                adView.destroy();
            }
            super.onDestroy();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }
}