package com.app.dvara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ConnectivityManager mConnectionManager;
    String mUpload, mDownload;
    AppCompatTextView mNetworkAvailabilityLabel, mTimeLabel, mUploadLabel, mDownloadLabel;
    AppCompatButton mSubmitBut;
    AppCompatEditText mMobileNumberEdit;
    Timer mTimer = new Timer();
    int mInterval = 1 * 1000;
    boolean isNetwork;
    DatabaseReference mDbReference;
    NetworkModel mModel;
    ImageView mBackImg, mSearchImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNetworkAvailabilityLabel = findViewById(R.id.internet_availability_label);
        mTimeLabel = findViewById(R.id.time_value);
        mUploadLabel = findViewById(R.id.upload_speed);
        mDownloadLabel = findViewById(R.id.download_speed);
        mSubmitBut = findViewById(R.id.submit);
        mMobileNumberEdit = findViewById(R.id.mobilenumber);
        mBackImg = findViewById(R.id.back);
        mSearchImg = findViewById(R.id.search);
        mDbReference = FirebaseDatabase.getInstance("https://dvara-a82a8-default-rtdb.firebaseio.com/").getReference().child("Network");

        mConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mSubmitBut.setOnClickListener(this);
        mBackImg.setOnClickListener(this);
        mSearchImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {
        switch (mView.getId()) {
            case R.id.submit:
                if (isNetwork) {
                    if (mMobileNumberEdit.getText().toString().trim().length() == 0) {
                        Toast.makeText(this, "Mobile number should not be empty", Toast.LENGTH_SHORT).show();
                    } else if (mMobileNumberEdit.getText().toString().trim().length() != 10) {
                        Toast.makeText(this, "Enter valid mobile number.", Toast.LENGTH_SHORT).show();
                    } else {
                        mModel = new NetworkModel();
                        mModel.setDate_time(mTimeLabel.getText().toString().trim());
                        mModel.setUpload_speed(mUploadLabel.getText().toString().trim());
                        mModel.setDownload_speed(mDownloadLabel.getText().toString().trim());
                        mModel.setMobile_number(mMobileNumberEdit.getText().toString().trim());
                        insertUpdateData();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.networkNotAvailable), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.back:
                onBackPressed();
                break;

            case R.id.search:
                mTimer.cancel();
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
        }
    }

    public void insertUpdateData() {
        mDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot mDataSnapshot) {
                mDbReference.child(mMobileNumberEdit.getText().toString().trim()).setValue(mModel);
                if (mDataSnapshot.hasChild(mMobileNumberEdit.getText().toString().trim())) {
                    Toast.makeText(MainActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                }
                mMobileNumberEdit.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError mDatabaseError) {
                Toast.makeText(MainActivity.this, "Failed to add the data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startTimer() {
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Thread(new Runnable() {
                    public void run() {
                        setValues();
                    }
                }));

            }
        }, mInterval, mInterval);
    }

    public void setValues() {
        SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a");
        mTimeLabel.setText(mFormat.format(Calendar.getInstance().getTimeInMillis()));
        isNetwork = networkAvailablity();
        if (isNetwork) {
            mNetworkAvailabilityLabel.setText(getString(R.string.networkAvailable));
            mNetworkAvailabilityLabel.setTextColor(getColor(R.color.colorPrimary));
        } else {
            mNetworkAvailabilityLabel.setText(getString(R.string.networkNotAvailable));
            mNetworkAvailabilityLabel.setTextColor(getColor(R.color.red));
        }
        mUploadLabel.setText(mUpload);
        mDownloadLabel.setText(mDownload);
    }

    public boolean networkAvailablity() {
        mUpload = "0";
        mDownload = "0";
        NetworkInfo mNetworkInfo = mConnectionManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            if (mNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                NetworkCapabilities mNetworkCapabilities = mConnectionManager.getNetworkCapabilities(mConnectionManager.getActiveNetwork());
                mUpload = (mNetworkCapabilities.getLinkDownstreamBandwidthKbps() / 1024) + "";
                mDownload = (mNetworkCapabilities.getLinkUpstreamBandwidthKbps() / 1024) + "";
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setValues();
        mTimer = new Timer();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        mTimer.cancel();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finish();
    }
}