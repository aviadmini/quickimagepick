package com.aviadmini.quickimagepick.sample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aviadmini.quickimagepick.PickSource;
import com.aviadmini.quickimagepick.QuickImagePick;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity
        extends AppCompatActivity {

    private static final String TAG = "QIP Sample";

    private static final String QIP_DIR_NAME = "QuickImagePick Sample";

    private ImageView mImageView;

    final QuickImagePick.Callback mCallback = new QuickImagePick.Callback() {

        @Override
        public void onImagePicked(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final Uri pImageUri) {

            final Context context = getApplicationContext();

            final String extension = QuickImagePick.getFileExtension(context, pImageUri);
            Log.i(TAG, "Picked: " + pImageUri.toString() + "\nMIME type: " + QuickImagePick.getMimeType(context,
                    pImageUri) + "\nFile extension: " + extension + "\nRequest type: " + pRequestType);

            // Do something with Uri, for example load image into and ImageView
            Glide.with(context)
                 .load(pImageUri)
                 .fitCenter()
                 .into(mImageView);

            // DO NOT do this on main thread. This is only for reference
            try {

                final String ext = extension == null ? "" : "." + extension;
                final File outDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), QIP_DIR_NAME);
                final File file = new File(outDir, "qip_temp" + ext);

                //noinspection ResultOfMethodCallIgnored
                outDir.mkdirs();

                QuickImagePick.saveContentToFile(getApplicationContext(), pImageUri, file);

                Toast.makeText(getApplicationContext(), "Save complete", Toast.LENGTH_SHORT)
                     .show();

            } catch (IOException pE) {
                Toast.makeText(getApplicationContext(), "Save failed: " + pE.getMessage(), Toast.LENGTH_SHORT)
                     .show();
            }

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

        final View btn1 = this.findViewById(R.id.btn_pick_local_jpg_webp);
        final View btn2 = this.findViewById(R.id.btn_pick_png_jpg_camera_docs);
        final View btn3 = this.findViewById(R.id.btn_pick_cam_only);

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View pView) {
                MainActivityPermissionsDispatcher.btnCLickWithCheck(MainActivity.this, pView);
            }
        };
        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);

        final File outDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), QIP_DIR_NAME);
        Log.d(TAG, outDir.getAbsolutePath() + ", can write: " + outDir.canWrite());
        QuickImagePick.setCameraPicsDirectory(this, outDir.getAbsolutePath());

    }

    @Override
    protected void onActivityResult(final int pRequestCode, final int pResultCode, final Intent pData) {

        if (!QuickImagePick.handleActivityResult(getApplicationContext(), pRequestCode, pResultCode, pData, this.mCallback)) {
            super.onActivityResult(pRequestCode, pResultCode, pData);
        }

    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void btnCLick(final View pView) {

        switch (pView.getId()) {

            case R.id.btn_pick_local_jpg_webp: {

                QuickImagePick.allowOnlyLocalContent(this, true);

                final String[] types = {QuickImagePick.MIME_TYPE_IMAGE_JPEG, QuickImagePick.MIME_TYPE_IMAGE_WEBP};
                QuickImagePick.setAllowedMimeTypes(this, types);

                QuickImagePick.pickFromMultipleSources(this, 1, "All sources", PickSource.CAMERA, PickSource.DOCUMENTS, PickSource.GALLERY);

                break;
            }

            case R.id.btn_pick_png_jpg_camera_docs: {

                QuickImagePick.allowOnlyLocalContent(this, false);

                final String[] types = {QuickImagePick.MIME_TYPE_IMAGE_JPEG, QuickImagePick.MIME_TYPE_IMAGE_PNG};
                QuickImagePick.setAllowedMimeTypes(this, types);

                QuickImagePick.pickFromMultipleSources(this, 2, "Camera or Docs", PickSource.CAMERA, PickSource.DOCUMENTS);

                break;
            }

            case R.id.btn_pick_cam_only: {

                // doesn't matter for camera I guess, but doesn't hurt
                QuickImagePick.allowOnlyLocalContent(this, false);

                QuickImagePick.setAllImageMimeTypesAllowed(this);

                QuickImagePick.pickFromCamera(this, 3);

                break;
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(final int pRequestCode, @NonNull final String[] pPermissions, @NonNull final int[] pGrantResults) {
        super.onRequestPermissionsResult(pRequestCode, pPermissions, pGrantResults);

        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, pRequestCode, pGrantResults);

    }

}
