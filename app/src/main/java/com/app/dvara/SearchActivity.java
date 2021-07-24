package com.app.dvara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatTextView mMobileNumberLabel, mUploadLabel, mDownloadLabel, mTimeLabel;
    AppCompatEditText mSearchEdit;
    ImageView mBackImg, mSearchImg;
    ConstraintLayout mResultLay;
    DatabaseReference mDbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mDbReference = FirebaseDatabase.getInstance("https://dvara-a82a8-default-rtdb.firebaseio.com/").getReference().child("Network");

        mMobileNumberLabel = findViewById(R.id.mobilenumber);
        mUploadLabel = findViewById(R.id.upload);
        mDownloadLabel = findViewById(R.id.download);
        mTimeLabel = findViewById(R.id.time);
        mSearchEdit = findViewById(R.id.search_edit);
        mBackImg = findViewById(R.id.back);
        mSearchImg = findViewById(R.id.search);
        mResultLay = findViewById(R.id.result_layout);

        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() > 0) {
                    mSearchImg.setVisibility(View.VISIBLE);
                } else {
                    mSearchImg.setVisibility(View.GONE);
                    mResultLay.setVisibility(View.GONE);
                }
            }
        });

        mBackImg.setOnClickListener(this);
        mSearchImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View mView) {
        switch (mView.getId()) {
            case R.id.back:
                onBackPressed();
                break;

            case R.id.search:
                if (mSearchEdit.getText().toString().trim().length() != 10) {
                    Toast.makeText(this, "Enter valid mobile number.", Toast.LENGTH_SHORT).show();
                } else {
                    fetchItems();
                }
                break;
        }
    }

    private void fetchItems() {
        mDbReference.child(mSearchEdit.getText().toString().trim());
        mDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot mDataSnapshot) {
                if (mDataSnapshot.hasChild(mSearchEdit.getText().toString().trim())) {
                    for (DataSnapshot mData : mDataSnapshot.getChildren()) {
                        NetworkModel mModel = mData.getValue(NetworkModel.class);
                        if (mModel.getMobile_number().equals(mSearchEdit.getText().toString().trim())) {
                            mResultLay.setVisibility(View.VISIBLE);
                            mMobileNumberLabel.setText(mModel.getMobile_number());
                            mTimeLabel.setText(mModel.getDate_time());
                            mUploadLabel.setText("Upload:" + mModel.getUpload_speed() + " Mbps");
                            mDownloadLabel.setText("Download: " + mModel.getDownload_speed() + " Mbps");
                        }
                    }
                } else {
                    mResultLay.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError mDatabaseError) {
                Toast.makeText(SearchActivity.this, "Unable to fetch your data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}