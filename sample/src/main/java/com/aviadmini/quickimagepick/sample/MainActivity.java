package com.aviadmini.quickimagepick.sample;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.aviadmini.quickimagepick.PickSource;
import com.aviadmini.quickimagepick.QuickImagePick;
import com.bumptech.glide.Glide;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity
        extends AppCompatActivity {

    private static final String TAG = "QIP Sample";

    private ImageView mImageView;

    final QuickImagePick.Callback mCallback = new QuickImagePick.Callback() {

        @Override
        public void onImagePicked(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final Uri pImageUri) {

            Log.i(TAG, "Picked: " + pImageUri.toString());

            // Do something with Uri, for example load image into and ImageView
            Glide.with(getApplicationContext())
                 .load(pImageUri)
                 .fitCenter()
                 .into(mImageView);

        }

        @Override
        public void onError(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final String pErrorString) {

            Log.e(TAG, "Err: " + pErrorString);

        }

        @Override
        public void onCancel(@NonNull final PickSource pPickSource, final int pRequestType) {

            Log.d(TAG, "Cancel: " + pPickSource.name());

        }

    };

    @Override
    protected void onCreate(@Nullable final Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);

        this.setContentView(R.layout.activity_main);

        this.mImageView = (ImageView) findViewById(R.id.main_iv);

//        final File outDir = Environment.getExternalStorageDirectory();
//
//        QuickImagePick.setCameraPicsDirectory(this, outDir.getAbsolutePath());
//
//        Log.d(TAG, outDir.getAbsolutePath() + ", can write: " + outDir.canWrite());

    }

    @Override
    protected void onActivityResult(final int pRequestCode, final int pResultCode, final Intent pData) {

        if (!QuickImagePick.handleActivityResult(getApplicationContext(), pRequestCode, pResultCode, pData, this.mCallback)) {
            super.onActivityResult(pRequestCode, pResultCode, pData);
        }

    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void btnCLick(View view) {
        QuickImagePick.pickFromMultipleSources(this, "Title", PickSource.CAMERA, PickSource.DOCUMENTS, PickSource.GALLERY);
    }

}
