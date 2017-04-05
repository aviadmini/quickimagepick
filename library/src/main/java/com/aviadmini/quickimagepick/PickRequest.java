package com.aviadmini.quickimagepick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A reusable object used to trigger pick flow.
 *
 * @author aviadmini
 * @since v2.0.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class PickRequest {

    @NonNull
    private final Context mContext;

    @Nullable
    private final Activity mActivity;

    @Nullable
    private final Fragment mSupportFragment;

    @Nullable
    private final android.app.Fragment mAppFragment;

    private int mRequestType = 0;

    private boolean mAllowOnlyLocalContent = false;

    @Nullable
    private String mCustomCameraPicsDirPath = null;

    @NonNull
    private String mMimeType = QiPick.MIME_TYPE_IMAGES_ALL;

    @Nullable
    private Set<String> mMimeTypesKitKat = null;

    @Nullable
    private String mLastCameraUriString = null;

    PickRequest(@NonNull final Activity pActivity) {

        this.mContext = pActivity;

        this.mActivity = pActivity;

        this.mAppFragment = null;
        this.mSupportFragment = null;

    }

    PickRequest(@NonNull final Fragment pFragment) {

        this.mContext = pFragment.getContext();

        this.mSupportFragment = pFragment;

        this.mActivity = null;
        this.mAppFragment = null;

    }


    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    PickRequest(@NonNull final android.app.Fragment pFragment) {

        this.mAppFragment = pFragment;

        this.mContext = QiPick.API_23 ? pFragment.getContext() : pFragment.getActivity();

        this.mActivity = null;
        this.mSupportFragment = null;

    }

    // ==== REQUEST CUSTOMIZATION ==== //

    /**
     * @param pAllowOnlyLocalContent pass true to restrict content that is not available locally on device and should be downloaded
     * @return same PickRequest object for chained calls
     */
    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    public PickRequest allowOnlyLocalContent(final boolean pAllowOnlyLocalContent) {

        this.mAllowOnlyLocalContent = pAllowOnlyLocalContent;

        return this;
    }

    /**
     * @return true if only local content is allowed
     */
    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean isOnlyLocalContentAllowed() {
        return this.mAllowOnlyLocalContent;
    }

    /**
     * Set a directory to which pictures taken by camera will be saved. Useful only when camera is a pick source.
     * Directory will be created if it doesn't exist
     *
     * @param pDirPath the directory
     * @return same PickRequest object for chained calls
     */
    public PickRequest withCameraPicsDirectory(@Nullable final String pDirPath) {

        this.mCustomCameraPicsDirPath = pDirPath;

        return this;
    }

    /**
     * Set a directory to which pictures taken by camera will be saved. Useful only when camera is a pick source
     *
     * @param pDirFile the directory File
     * @return same PickRequest object for chained calls
     */
    public PickRequest withCameraPicsDirectory(@Nullable final File pDirFile) {

        this.mCustomCameraPicsDirPath = pDirFile == null ? null : pDirFile.getAbsolutePath();

        return this;
    }

    /**
     * @return directory where pictures taken by camera apps will be saved.
     * By default it's a pictures directory on external storage.
     * Returns null if an error occurs
     */
    @Nullable
    public File getCameraPicsDirectory() {
        return this.mCustomCameraPicsDirPath == null ? this.mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) :
               new File(this.mCustomCameraPicsDirPath);
    }

    /**
     * Set a request type integer. Helpful when app has different types of pick requests that should be distinguished when getting result
     *
     * @param pRequestType the value
     * @return same PickRequest object for chained calls
     */
    public PickRequest withRequestType(final int pRequestType) {

        this.mRequestType = pRequestType;

        return this;
    }

    /**
     * @return request type of this PickRequest
     */
    public int getRequestType() {
        return this.mRequestType;
    }

    /**
     * Set multiple exact MIME types of files that can be picked. Only works for KitKat and later (API 19+)
     * <br><br>
     * Note: if you use {@link #withAllowedMimeType(String)} then only types within the argument passed will be available for pick.
     * Probably better to not use both methods at the same time
     *
     * @param pAllowedMimeTypesKitKat the MIME types
     * @return same PickRequest object for chained calls
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public PickRequest withAllowedMimeTypes(@NonNull final Set<String> pAllowedMimeTypesKitKat) {

        this.mMimeTypesKitKat = QiPick.API_19 ? pAllowedMimeTypesKitKat : null;

        return this;
    }

    /**
     * Set multiple exact MIME types of files that can be picked. Only works for KitKat and later (API 19+)
     * <br><br>
     * Note: if you use {@link #withAllowedMimeType(String)} then only types within the argument passed will be available for pick.
     * Probably better to not use both methods at the same time
     *
     * @param pAllowedMimeTypesKitKat the MIME types
     * @return same PickRequest object for chained calls
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public PickRequest withAllowedMimeTypes(@NonNull final List<String> pAllowedMimeTypesKitKat) {

        this.mMimeTypesKitKat = QiPick.API_19 ? new HashSet<>(pAllowedMimeTypesKitKat) : null;

        return this;
    }

    /**
     * Set multiple exact MIME types of files that can be picked. Only works for KitKat and later (API 19+)
     * <br><br>
     * Note: if you use {@link #withAllowedMimeType(String)} then only types within the argument passed will be available for pick.
     * Probably better to not use both methods at the same time
     *
     * @param pAllowedMimeTypesKitKat the MIME types
     * @return same PickRequest object for chained calls
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public PickRequest withAllowedMimeTypes(@NonNull final String... pAllowedMimeTypesKitKat) {

        this.mMimeTypesKitKat = QiPick.API_19 ? new HashSet<>(Arrays.asList(pAllowedMimeTypesKitKat)) : null;

        return this;
    }

    /**
     * Allow picking files of all image MIME types.
     * This is set by default so unless other MIME type changes were made to request there's no need to call it
     *
     * @return same PickRequest object for chained calls
     */
    public PickRequest withAllImageMimeTypesAllowed() {

        this.mMimeType = QiPick.MIME_TYPE_IMAGES_ALL;

        this.mMimeTypesKitKat = null;

        return this;
    }

    /**
     * @return allowed MIME types of files to be picked.
     * Returns null for pre-KitKat (API 19) and if there is no MIME type restriction from multiple MIME types
     */
    @Nullable
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public Set<String> getAllowedMimeTypes() {
        return this.mMimeTypesKitKat;
    }

    /**
     * Set MIME type of files that can be picked. It is the only way for pre-KitKat (API 19)
     *
     * @param pAllowedMimeType the MIME type
     * @return same PickRequest object for chained calls
     */
    public PickRequest withAllowedMimeType(@Nullable final String pAllowedMimeType) {

        this.mMimeType = pAllowedMimeType == null ? QiPick.MIME_TYPE_IMAGES_ALL : pAllowedMimeType;

        return this;
    }

    /**
     * @return allowed MIME type of files to be picked
     */
    @NonNull
    public String getAllowedMimeType() {
        return this.mMimeType;
    }

    // ==== PICK METHODS ==== //

    /**
     * Launch take picture from camera pick flow
     *
     * @return launch status code. One of {@link PickTriggerResult} constants
     */
    @PickTriggerResult
    public int fromCamera() {

        final File outputFile = this.createCameraImageFile();
        if (outputFile == null) {
            return PickTriggerResult.TRIGGER_PICK_ERR_CAM_FILE;
        }

        return this.triggerPick(this.prepareCameraIntent(this.createCameraImageUri(outputFile)), QiPick.REQ_CAMERA);
    }

    @NonNull
    private Intent prepareCameraIntent(@NonNull final Uri pOutputFileUri) {

        this.mLastCameraUriString = pOutputFileUri.toString();

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, pOutputFileUri);

        final PackageManager packageManager = this.mContext.getPackageManager();

        final List<ResolveInfo> camList = packageManager.queryIntentActivities(intent, 0);
        for (final ResolveInfo resolveInfo : camList) {
            // grant r/w permissions
            this.mContext.getApplicationContext()
                         .grantUriPermission(resolveInfo.activityInfo.packageName, pOutputFileUri,
                                 Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        return intent;
    }

    /**
     * Launch gallery pick flow
     *
     * @return launch status code. One of {@link PickTriggerResult} constants
     */
    @PickTriggerResult
    public int fromGallery() {
        return this.triggerPick(this.prepareGalleryIntent(), QiPick.REQ_GALLERY);
    }

    @SuppressLint("InlinedApi")
    @NonNull
    private Intent prepareGalleryIntent() {

        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, this.mAllowOnlyLocalContent);

        this.setIntentAllowedMimeTypes(galleryIntent);

        return galleryIntent;
    }

    /**
     * Launch documents pick flow
     *
     * @return launch status code. One of {@link PickTriggerResult} constants
     */
    @SuppressWarnings("NewApi")
    @PickTriggerResult
    public int fromDocuments() {
        return this.fromDocuments(false);
    }

    /**
     * Launch documents pick flow
     *
     * @param pAllowMultiple pass true to allow multiple images to be picked
     * @return launch status code. One of {@link PickTriggerResult} constants
     */
    @PickTriggerResult
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public int fromDocuments(final boolean pAllowMultiple) {
        return this.triggerPick(this.prepareDocumentsIntent(pAllowMultiple), QiPick.REQ_DOCUMENTS);
    }

    @SuppressLint("InlinedApi")
    @NonNull
    private Intent prepareDocumentsIntent(final boolean pAllowMultiple) {

        final Intent docsIntent = new Intent(Intent.ACTION_GET_CONTENT);
        docsIntent.addCategory(Intent.CATEGORY_OPENABLE);
        docsIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, this.mAllowOnlyLocalContent);
        docsIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, pAllowMultiple);

        this.setIntentAllowedMimeTypes(docsIntent);

        return docsIntent;
    }

    /**
     * Launch multi-source pick flow
     *
     * @param pTitleRes    resource id of a intent chooser title
     * @param pPickSources the pick sources
     * @return launch status code. One of {@link PickTriggerResult} constants
     */
    @PickTriggerResult
    public int fromMultipleSources(@StringRes final int pTitleRes, @NonNull final PickSource... pPickSources) {
        return this.fromMultipleSources(this.mContext.getString(pTitleRes), pPickSources);
    }

    /**
     * Launch multi-source pick flow
     *
     * @param pTitle       intent chooser title
     * @param pPickSources the pick sources
     * @return launch status code. One of {@link PickTriggerResult} constants
     */
    @PickTriggerResult
    public int fromMultipleSources(@NonNull final CharSequence pTitle, @NonNull final PickSource... pPickSources) {

        if (pPickSources.length == 0) {
            return PickTriggerResult.TRIGGER_PICK_ERR_NO_PICK_SOURCES;
        }

        final PackageManager packageManager = this.mContext.getPackageManager();

        final ArrayList<Intent> resultIntents = new ArrayList<>();

        for (final PickSource source : pPickSources) {

            switch (source) {

                case CAMERA: {

                    final List<Intent> cameraIntents = new ArrayList<>();

                    final File file = this.createCameraImageFile();

                    if (file != null) {

                        final Uri outputFileUri = this.createCameraImageUri(file);

                        final Intent cameraIntent = this.prepareCameraIntent(outputFileUri);

                        final List<ResolveInfo> camList = packageManager.queryIntentActivities(cameraIntent, 0);
                        for (final ResolveInfo resolveInfo : camList) {

                            final String packageName = resolveInfo.activityInfo.packageName;

                            final Intent intent = new Intent(cameraIntent);
                            intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                            intent.setPackage(packageName);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                            cameraIntents.add(intent);

                        }

                    }

                    resultIntents.addAll(cameraIntents);

                    break;
                }

                case GALLERY: {

                    final List<Intent> galleryIntents = new ArrayList<>();

                    final Intent galleryIntent = this.prepareGalleryIntent();

                    final List<ResolveInfo> camList = packageManager.queryIntentActivities(galleryIntent, 0);
                    for (final ResolveInfo resolveInfo : camList) {

                        final String packageName = resolveInfo.activityInfo.packageName;

                        final Intent intent = new Intent(galleryIntent);
                        intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                        intent.setPackage(packageName);

                        galleryIntents.add(intent);

                    }

                    resultIntents.addAll(galleryIntents);

                    break;
                }

                case DOCUMENTS: {

                    resultIntents.add(this.prepareDocumentsIntent(false));

                    break;
                }

            }

        }

        // no components are able to perform pick
        if (resultIntents.size() == 0) {
            return PickTriggerResult.TRIGGER_PICK_ERR_NO_ACTIVITY;
        }

        // create chooser intent
        final Intent result = Intent.createChooser(resultIntents.remove(resultIntents.size() - 1), pTitle);
        result.putExtra(Intent.EXTRA_INITIAL_INTENTS, resultIntents.toArray(new Parcelable[resultIntents.size()]));

        return this.triggerPick(result, QiPick.REQ_MULTIPLE);
    }

    @SuppressLint("NewApi")
    @PickTriggerResult
    private int triggerPick(@NonNull final Intent pIntent, final int pRequestCode) {

        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mContext)
                                                                 .edit();

        editor.putInt(QiPick.PREFS_REQUEST_TYPE, this.mRequestType)
              .putBoolean(QiPick.PREFS_ALLOW_LOCAL_CONTENT_ONLY, this.mAllowOnlyLocalContent)
              .putString(QiPick.PREFS_ALLOWED_MIME_TYPE, this.mMimeType)
              .putString(QiPick.PREFS_CAMERA_DIR, this.mCustomCameraPicsDirPath)
              .putString(QiPick.PREFS_LAST_CAMERA_URI, this.mLastCameraUriString);

        if (QiPick.API_19) {
            editor.putStringSet(QiPick.PREFS_ALLOWED_MIME_TYPES_KITKAT, this.mMimeTypesKitKat);
        }

        editor.apply();

        try {

            if (this.mActivity != null) {
                this.mActivity.startActivityForResult(pIntent, pRequestCode);
            } else if (this.mAppFragment != null) {
                this.mAppFragment.startActivityForResult(pIntent, pRequestCode);
            } else if (this.mSupportFragment != null) {
                this.mSupportFragment.startActivityForResult(pIntent, pRequestCode);
            }

        } catch (final ActivityNotFoundException e) {
            return PickTriggerResult.TRIGGER_PICK_ERR_NO_ACTIVITY;
        }

        return PickTriggerResult.TRIGGER_PICK_OK;
    }

    // ==== //

    @Nullable
    private File createCameraImageFile() {

        final File dir = this.getCameraPicsDirectory();
        if (dir == null) {
            return null;
        }

        // Workaround for #7 https://github.com/aviadmini/quickimagepick/issues/7
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }

        // Assuming JPG output seems to be a correct way
        final String fileName = System.nanoTime() + ".jpg";

        return new File(dir, fileName);
    }

    @NonNull
    private Uri createCameraImageUri(@NonNull final File pFile) {
        return FileProvider.getUriForFile(this.mContext, this.mContext.getPackageName() + ".qip_file_provider", pFile);
    }

    @SuppressLint("InlinedApi")
    private void setIntentAllowedMimeTypes(@NonNull final Intent pIntent) {

        pIntent.setType(this.mMimeType);

        if (QiPick.API_19 && this.mMimeTypesKitKat != null && this.mMimeTypesKitKat.size() > 0) {
            pIntent.putExtra(Intent.EXTRA_MIME_TYPES, this.mMimeTypesKitKat.toArray(new String[this.mMimeTypesKitKat.size()]));
        }

    }

    // ==== //

}
