package com.aviadmini.quickimagepick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Easy to use image picker. Use one of {@code pickFrom...} methods to trigger image pick and
 * get result in {@code onActivityResult} method of the same Activity or Fragment.
 *
 * @see <a href="https://github.com/aviadmini/quickimagepick">GitHub repo</a>
 * @since v1.0.0
 * @deprecated use {@link QiPick}. Even though it contains latest fixes, this class will be removed in one of next releases
 */
@SuppressWarnings({"unused", "deprecation"})
@Deprecated
public class QuickImagePick {

    public static final String ERR_CAMERA_NULL_RESULT         = QiPick.ERR_CAMERA_NULL_RESULT;
    public static final String ERR_CAMERA_CANNOT_WRITE_OUTPUT = QiPick.ERR_CAMERA_CANNOT_WRITE_OUTPUT;
    public static final String ERR_GALLERY_NULL_RESULT        = QiPick.ERR_GALLERY_NULL_RESULT;
    public static final String ERR_DOCS_NULL_RESULT           = QiPick.ERR_DOCS_NULL_RESULT;

    public static final String MIME_TYPE_IMAGE_BMP  = QiPick.MIME_TYPE_IMAGE_BMP;
    public static final String MIME_TYPE_IMAGE_GIF  = QiPick.MIME_TYPE_IMAGE_GIF;
    public static final String MIME_TYPE_IMAGE_JPEG = QiPick.MIME_TYPE_IMAGE_JPEG;
    public static final String MIME_TYPE_IMAGE_PNG  = QiPick.MIME_TYPE_IMAGE_PNG;
    public static final String MIME_TYPE_IMAGE_WEBP = QiPick.MIME_TYPE_IMAGE_WEBP;

    // ==== CAMERA ==== //

    /**
     * Capture new image using one of camera apps
     *
     * @param pActivity    activity which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static boolean pickFromCamera(@NonNull final Activity pActivity, final int pRequestType) {

        final File file = createImageFile(pActivity);
        if (file == null) {
            return false;
        }
        final Intent intent = prepareCameraIntent(pActivity, createImageUri(pActivity, file), pRequestType);
        if (intent == null) {
            return false;
        }

        pActivity.startActivityForResult(intent, QiPick.REQ_CAMERA);

        return true;
    }

    /**
     * Capture new image using one of camera apps
     *
     * @param pActivity activity which gets the result after pick flow
     */
    public static boolean pickFromCamera(@NonNull final Activity pActivity) {
        return pickFromCamera(pActivity, 0);
    }

    /**
     * Capture new image using one of camera apps
     *
     * @param pFragment    support fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static boolean pickFromCamera(@NonNull final Fragment pFragment, final int pRequestType) {

        final Context context = pFragment.getContext();

        final File file = createImageFile(context);
        if (file == null) {
            return false;
        }
        final Intent intent = prepareCameraIntent(context, createImageUri(context, file), pRequestType);
        if (intent == null) {
            return false;
        }

        pFragment.startActivityForResult(intent, QiPick.REQ_CAMERA);

        return true;
    }

    /**
     * Capture new image using one of camera apps
     *
     * @param pFragment support fragment which gets the result after pick flow
     */
    public static boolean pickFromCamera(@NonNull final Fragment pFragment) {
        return pickFromCamera(pFragment, 0);
    }

    /**
     * Capture new image using one of camera apps
     *
     * @param pFragment    fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    @SuppressLint("NewApi")
    public static boolean pickFromCamera(@NonNull final android.app.Fragment pFragment, final int pRequestType) {

        final Context context = QiPick.API_23 ? pFragment.getContext() : pFragment.getActivity();

        final File file = createImageFile(context);
        if (file == null) {
            return false;
        }
        final Intent intent = prepareCameraIntent(context, createImageUri(context, file), pRequestType);
        if (intent == null) {
            return false;
        }

        pFragment.startActivityForResult(intent, QiPick.REQ_CAMERA);

        return true;
    }

    /**
     * Capture new image using camera apps
     *
     * @param pFragment fragment which gets the result after pick flow
     */
    public static boolean pickFromCamera(@NonNull final android.app.Fragment pFragment) {
        return pickFromCamera(pFragment, 0);
    }

    @Nullable
    private static Intent prepareCameraIntent(@NonNull final Context pContext, @NonNull final Uri pOutputFileUri, final int pRequestType) {

        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .putInt(QiPick.PREFS_REQUEST_TYPE, pRequestType)
                         .putString(QiPick.PREFS_LAST_CAMERA_URI, pOutputFileUri.toString())
                         .apply();

        final Intent result = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        result.putExtra(MediaStore.EXTRA_OUTPUT, pOutputFileUri);

        return result;
    }

    @Nullable
    private static File createImageFile(@NonNull final Context pContext) {

        final File dir = getCameraPicsDirectory(pContext);
        if (dir == null) {
            return null;
        }

        // Assuming JPG output seems to be a correct way
        final String fileName = System.nanoTime() + ".jpg";

        return new File(dir, fileName);
    }

    @NonNull
    private static Uri createImageUri(@NonNull final Context pContext, @NonNull final File pFile) {
        return FileProvider.getUriForFile(pContext, pContext.getPackageName() + ".qip_file_provider", pFile);
    }

    // ==== GALLERY ==== //

    /**
     * Pick image from Gallery
     *
     * @param pActivity    activity which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static void pickFromGallery(@NonNull final Activity pActivity, final int pRequestType) {

        final Intent intent = prepareGalleryIntent(pActivity, pRequestType);

        pActivity.startActivityForResult(intent, QiPick.REQ_GALLERY);

    }

    /**
     * Pick image from Gallery
     *
     * @param pActivity activity which gets the result after pick flow
     */
    public static void pickFromGallery(@NonNull final Activity pActivity) {
        pickFromGallery(pActivity, 0);
    }

    /**
     * Pick image from Gallery
     *
     * @param pFragment    support fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static void pickFromGallery(@NonNull final Fragment pFragment, final int pRequestType) {

        final Intent intent = prepareGalleryIntent(pFragment.getContext(), pRequestType);

        pFragment.startActivityForResult(intent, QiPick.REQ_GALLERY);

    }

    /**
     * Pick image from Gallery
     *
     * @param pFragment support fragment which gets the result after pick flow
     */
    public static void pickFromGallery(@NonNull final Fragment pFragment) {
        pickFromGallery(pFragment, 0);
    }

    /**
     * Pick image from Gallery
     *
     * @param pFragment    fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    @SuppressLint("NewApi")
    public static void pickFromGallery(@NonNull final android.app.Fragment pFragment, final int pRequestType) {

        final Intent intent = prepareGalleryIntent(QiPick.API_23 ? pFragment.getContext() : pFragment.getActivity(), pRequestType);

        pFragment.startActivityForResult(intent, QiPick.REQ_GALLERY);

    }

    /**
     * Pick image from Gallery
     *
     * @param pFragment fragment which gets the result after pick flow
     */
    public static void pickFromGallery(@NonNull final android.app.Fragment pFragment) {
        pickFromGallery(pFragment, 0);
    }

    @NonNull
    private static Intent prepareGalleryIntent(@NonNull final Context pContext, final int pRequestType) {

        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .putInt(QiPick.PREFS_REQUEST_TYPE, pRequestType)
                         .apply();

        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        setIntentLocalContentOnly(pContext, intent);

        setIntentAllowedMimeTypes(pContext, intent);

        return intent;
    }

    // ==== DOCUMENTS ==== //

    /**
     * Pick image using Documents app or content manager (Documents not available before KitKat)
     *
     * @param pActivity    activity which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static void pickFromDocuments(@NonNull final Activity pActivity, final int pRequestType) {

        final Intent intent = prepareDocumentsIntent(pActivity, pRequestType);

        pActivity.startActivityForResult(intent, QiPick.REQ_DOCUMENTS);

    }

    /**
     * Pick image using Documents app or content manager (Documents not available before KitKat)
     *
     * @param pActivity activity which gets the result after pick flow
     */
    public static void pickFromDocuments(@NonNull final Activity pActivity) {
        pickFromDocuments(pActivity, 0);
    }

    /**
     * Pick image using Documents app or content manager (Documents not available before KitKat)
     *
     * @param pFragment    support fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    public static void pickFromDocuments(@NonNull final Fragment pFragment, final int pRequestType) {

        final Intent intent = prepareDocumentsIntent(pFragment.getContext(), pRequestType);

        pFragment.startActivityForResult(intent, QiPick.REQ_DOCUMENTS);

    }

    /**
     * Pick image using Documents app or content manager (Documents not available before KitKat)
     *
     * @param pFragment support fragment which gets the result after pick flow
     */
    public static void pickFromDocuments(@NonNull final Fragment pFragment) {
        pickFromDocuments(pFragment, 0);
    }

    /**
     * Pick image using Documents app or content manager (Documents not available before KitKat)
     *
     * @param pFragment    fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     */
    @SuppressLint("NewApi")
    public static void pickFromDocuments(@NonNull final android.app.Fragment pFragment, final int pRequestType) {

        final Intent intent = prepareDocumentsIntent(QiPick.API_23 ? pFragment.getContext() : pFragment.getActivity(), pRequestType);

        pFragment.startActivityForResult(intent, QiPick.REQ_DOCUMENTS);

    }

    /**
     * Pick image using Documents app or content manager (Documents not available before KitKat)
     *
     * @param pFragment fragment which gets the result after pick flow
     */
    public static void pickFromDocuments(@NonNull final android.app.Fragment pFragment) {
        pickFromDocuments(pFragment, 0);
    }

    @NonNull
    private static Intent prepareDocumentsIntent(@NonNull final Context pContext, final int pRequestType) {

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);

        sharedPreferences.edit()
                         .putInt(QiPick.PREFS_REQUEST_TYPE, pRequestType)
                         .apply();

        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        setIntentLocalContentOnly(pContext, intent);

        setIntentAllowedMimeTypes(pContext, intent);

        return intent;
    }

    // ==== MULTITYPE ==== //

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pActivity    activity which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     * @param pTitle       intent chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final Activity pActivity, final int pRequestType, @Nullable final String pTitle,
                                                  @NonNull final PickSource... pPickSources) {

        final Intent intent = prepareMultipleSourcesIntent(pActivity, pRequestType, pTitle, pPickSources);
        if (intent == null) {
            return false;
        }

        pActivity.startActivityForResult(intent, QiPick.REQ_MULTIPLE);

        return true;
    }

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pActivity    activity which gets the result after pick flow
     * @param pTitle       intent chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final Activity pActivity, @Nullable final String pTitle,
                                                  @NonNull final PickSource... pPickSources) {
        return pickFromMultipleSources(pActivity, 0, pTitle, pPickSources);
    }

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pFragment    support fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     * @param pTitle       intent chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final Fragment pFragment, final int pRequestType, @Nullable final String pTitle,
                                                  @NonNull final PickSource... pPickSources) {

        final Intent intent = prepareMultipleSourcesIntent(pFragment.getContext(), pRequestType, pTitle, pPickSources);
        if (intent == null) {
            return false;
        }

        pFragment.startActivityForResult(intent, QiPick.REQ_MULTIPLE);

        return true;
    }

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pFragment    support fragment which gets the result after pick flow
     * @param pTitle       intent chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final Fragment pFragment, @Nullable final String pTitle,
                                                  @NonNull final PickSource... pPickSources) {
        return pickFromMultipleSources(pFragment, 0, pTitle, pPickSources);
    }

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pFragment    fragment which gets the result after pick flow
     * @param pRequestType request type (for different pick types), returned in callback
     * @param pTitle       intent chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    @SuppressLint("NewApi")
    public static boolean pickFromMultipleSources(@NonNull final android.app.Fragment pFragment, final int pRequestType,
                                                  @Nullable final String pTitle, @NonNull final PickSource... pPickSources) {

        final Intent intent = prepareMultipleSourcesIntent(QiPick.API_23 ? pFragment.getContext() : pFragment.getActivity(), pRequestType,
                pTitle, pPickSources);
        if (intent == null) {
            return false;
        }

        pFragment.startActivityForResult(intent, QiPick.REQ_MULTIPLE);

        return true;
    }

    /**
     * Pick image from multiple sources (camera, gallery and documents are the options)
     *
     * @param pFragment    fragment which gets the result after pick flow
     * @param pTitle       chooser title
     * @param pPickSources sources offered to user to pick with
     * @return true if process started successfully
     */
    public static boolean pickFromMultipleSources(@NonNull final android.app.Fragment pFragment, @Nullable final String pTitle,
                                                  @NonNull final PickSource... pPickSources) {
        return pickFromMultipleSources(pFragment, 0, pTitle, pPickSources);
    }

    @Nullable
    private static Intent prepareMultipleSourcesIntent(@NonNull final Context pContext, final int pRequestType, @Nullable final String pTitle,
                                                       @NonNull final PickSource... pSources) {

        // no sources - no work
        if (pSources.length == 0) {
            return null;
        }

        boolean addCamera = false;
        boolean addGallery = false;
        boolean addDocuments = false;
        for (final PickSource source : pSources) {

            switch (source) {

                case CAMERA: {

                    addCamera = true;

                    break;
                }

                case GALLERY: {

                    addGallery = true;

                    break;
                }

                case DOCUMENTS: {

                    addDocuments = true;

                    break;
                }

            }

        }

        final PackageManager packageManager = pContext.getPackageManager();

        final ArrayList<Intent> resultIntents = new ArrayList<>();

        // gather camera intents
        if (addCamera) {

            final List<Intent> cameraIntents = new ArrayList<>();

            final File file = createImageFile(pContext);

            if (file != null) {

                final Uri outputFileUri = createImageUri(pContext, file);

                final Intent cameraIntent = prepareCameraIntent(pContext, outputFileUri, pRequestType);

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

        }

        // gather gallery intents
        if (addGallery) {

            final List<Intent> galleryIntents = new ArrayList<>();

            final Intent galleryIntent = prepareGalleryIntent(pContext, pRequestType);

            final List<ResolveInfo> camList = packageManager.queryIntentActivities(galleryIntent, 0);
            for (final ResolveInfo resolveInfo : camList) {

                final String packageName = resolveInfo.activityInfo.packageName;

                final Intent intent = new Intent(galleryIntent);
                intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                intent.setPackage(packageName);

                galleryIntents.add(intent);

            }

            resultIntents.addAll(galleryIntents);

        }

        // add documents intent
        if (addDocuments) {
            resultIntents.add(prepareDocumentsIntent(pContext, pRequestType));
        }

        // no components are able to perform pick
        if (resultIntents.size() == 0) {
            return null;
        }

        // create chooser intent
        final Intent result = Intent.createChooser(new Intent(), pTitle);
        result.putExtra(Intent.EXTRA_INITIAL_INTENTS, resultIntents.toArray(new Parcelable[resultIntents.size()]));

        return result;
    }

    // ==== RESULT HANDLING ==== //

    /**
     * Use this in {@code onActivityResult(...)} of Activity or Fragment to retrieve image {@link Uri}
     *
     * @param pFragment    support fragment
     * @param pRequestCode request code from {@code onActivityResult(...)} parameter
     * @param pResultCode  result code from {@code onActivityResult(...)} parameter
     * @param pData        Intent from {@code onActivityResult(...)} parameter
     * @param pCallback    result callback
     * @return true if result was accepted by QuickImagePick
     */
    public static boolean handleActivityResult(@NonNull final Fragment pFragment, final int pRequestCode, final int pResultCode,
                                               @Nullable final Intent pData, @NonNull final PickCallback pCallback) {
        return handleActivityResult(pFragment.getContext(), pRequestCode, pResultCode, pData, pCallback);
    }

    /**
     * Use this in {@code onActivityResult(...)} of Activity or Fragment to retrieve image {@link Uri}
     *
     * @param pFragment    fragment
     * @param pRequestCode request code from {@code onActivityResult(...)} parameter
     * @param pResultCode  result code from {@code onActivityResult(...)} parameter
     * @param pData        Intent from {@code onActivityResult(...)} parameter
     * @param pCallback    result callback
     * @return true if result was accepted by QuickImagePick
     */
    @SuppressLint("NewApi")
    public static boolean handleActivityResult(@NonNull final android.app.Fragment pFragment, final int pRequestCode, final int pResultCode,
                                               @Nullable final Intent pData, @NonNull final PickCallback pCallback) {
        return handleActivityResult(QiPick.API_23 ? pFragment.getContext() : pFragment.getActivity(), pRequestCode, pResultCode, pData,
                pCallback);
    }

    /**
     * Use this in {@code onActivityResult(...)} of Activity or Fragment to retrieve image {@link Uri}
     * <br>
     * Note: for Fragments it's advised to use Fragment-specific overloads of this method
     *
     * @param pContext     app {@link Context}
     * @param pRequestCode request code from {@code onActivityResult(...)} parameter
     * @param pResultCode  result code from {@code onActivityResult(...)} parameter
     * @param pData        Intent from {@code onActivityResult(...)} parameter
     * @param pCallback    result callback
     * @return true if result was accepted by QuickImagePick
     * @see #handleActivityResult(Fragment, int, int, Intent, PickCallback)
     * @see #handleActivityResult(android.app.Fragment, int, int, Intent, PickCallback)
     */
    @SuppressLint("NewApi")
    public static boolean handleActivityResult(@NonNull final Context pContext, final int pRequestCode, final int pResultCode,
                                               @Nullable final Intent pData, @NonNull final PickCallback pCallback) {
        return QiPick.handleActivityResult(pContext, pRequestCode, pResultCode, pData, pCallback);
    }

    // ==== CAMERA DIR ==== //

    /**
     * @param pContext app {@link Context}
     * @return directory where pictures taken by camera apps will be saved or null if an error occurs.
     * By default it's a pictures directory on external storage.
     */
    public static File getCameraPicsDirectory(@NonNull final Context pContext) {
        return QiPick.getCameraPicsDirectory(pContext);
    }

    /**
     * Change directory where pictures taken by camera apps will be saved. By default it's a pictures directory on external storage.
     * <br>
     * <b>Important: you are responsible for deleting the files once you're done with them.</b>
     *
     * @param pContext app {@link Context}
     * @param pDirPath path where images from camera will be saved. Use null to set default
     */
    public static void setCameraPicsDirectory(@NonNull final Context pContext, @Nullable final String pDirPath) {

        if (pDirPath == null) {
            PreferenceManager.getDefaultSharedPreferences(pContext)
                             .edit()
                             .remove(QiPick.PREFS_CAMERA_DIR)
                             .apply();
        } else {
            PreferenceManager.getDefaultSharedPreferences(pContext)
                             .edit()
                             .putString(QiPick.PREFS_CAMERA_DIR, pDirPath)
                             .apply();
        }

    }

    // ==== LAST CAM PIC ==== //

    /**
     * @param pContext app {@link Context}
     * @return Uri of last camera pic. Not necessarily valid content
     */
    public static Uri getLastCameraUri(@NonNull final Context pContext) {
        return QiPick.getLastCameraUri(pContext);
    }

    /**
     * @param pContext app {@link Context}
     * @return number of rows deleted from content provider
     */
    public static int deleteLastCameraPic(@NonNull final Context pContext) {
        return QiPick.deleteLastCameraPic(pContext);
    }

    // ==== ALLOWED MIME TYPES ==== //

    /**
     * Note: has no effect on pre-KitKat (API 19)
     *
     * @param pContext          app {@link Context}
     * @param pAllowedMimeTypes MIME types of files that will be allowed to be picked from gallery/documents
     */
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void setAllowedMimeTypes(@NonNull final Context pContext, @NonNull final String... pAllowedMimeTypes) {

        if (!QiPick.API_19) {
            return;
        }

        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .putStringSet(QiPick.PREFS_ALLOWED_MIME_TYPES_KITKAT, new HashSet<>(Arrays.asList(pAllowedMimeTypes)))
                         .apply();

    }

    /**
     * Note: has no effect on pre-KitKat (API 19)
     *
     * @param pContext          app {@link Context}
     * @param pAllowedMimeTypes MIME types of files that will be allowed to be picked from gallery/documents
     */
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void setAllowedMimeTypes(@NonNull final Context pContext, @NonNull final List<String> pAllowedMimeTypes) {

        if (!QiPick.API_19) {
            return;
        }

        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .putStringSet(QiPick.PREFS_ALLOWED_MIME_TYPES_KITKAT, new HashSet<>(pAllowedMimeTypes))
                         .apply();
    }

    /**
     * Note: has no effect on pre-KitKat (API 19)
     *
     * @param pContext          app {@link Context}
     * @param pAllowedMimeTypes MIME types of files that will be allowed to be picked from gallery/documents
     */
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void setAllowedMimeTypes(@NonNull final Context pContext, @NonNull final Set<String> pAllowedMimeTypes) {

        if (!QiPick.API_19) {
            return;
        }

        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .putStringSet(QiPick.PREFS_ALLOWED_MIME_TYPES_KITKAT, pAllowedMimeTypes)
                         .apply();

    }

    /**
     * @param pContext         app {@link Context}
     * @param pAllowedMimeType MIME type of files that will be allowed to be picked from gallery/documents
     */
    public static void setAllowedMimeType(@NonNull final Context pContext, @NonNull final String pAllowedMimeType) {
        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .putString(QiPick.PREFS_ALLOWED_MIME_TYPE, pAllowedMimeType)
                         .apply();
    }

    /**
     * Allow all image types to be selected (using MIME type 'image/*')
     *
     * @param pContext app {@link Context}
     */
    public static void setAllImageMimeTypesAllowed(@NonNull final Context pContext) {
        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .remove(QiPick.PREFS_ALLOWED_MIME_TYPES_KITKAT)
                         .remove(QiPick.PREFS_ALLOWED_MIME_TYPE)
                         .apply();
    }

    /**
     * @param pContext app {@link Context}
     * @return a set with all allowed mime types (KitKat and later)
     */
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static Set<String> getAllowedMimeTypes(@NonNull final Context pContext) {

        final Set<String> set = PreferenceManager.getDefaultSharedPreferences(pContext)
                                                 .getStringSet(QiPick.PREFS_ALLOWED_MIME_TYPES_KITKAT, new HashSet<String>(1));

        if (set.size() == 0) {
            set.add(QiPick.MIME_TYPE_IMAGES_ALL);
        }

        return set;
    }

    /**
     * @param pContext app {@link Context}
     * @return a set with all allowed mime types
     */
    public static String getAllowedMimeType(@NonNull final Context pContext) {
        return PreferenceManager.getDefaultSharedPreferences(pContext)
                                .getString(QiPick.PREFS_ALLOWED_MIME_TYPE, QiPick.MIME_TYPE_IMAGES_ALL);
    }

    // ==== INTENT EXTRAS ==== //

    @SuppressLint("NewApi")
    private static void setIntentLocalContentOnly(@NonNull final Context pContext, @NonNull final Intent pIntent) {
        pIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, PreferenceManager.getDefaultSharedPreferences(pContext)
                                                                   .getBoolean(QiPick.PREFS_ALLOW_LOCAL_CONTENT_ONLY, false));
    }

    @SuppressLint("NewApi")
    private static void setIntentAllowedMimeTypes(@NonNull final Context pContext, @NonNull final Intent pIntent) {

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);

        pIntent.setType(sharedPreferences.getString(QiPick.PREFS_ALLOWED_MIME_TYPE, QiPick.MIME_TYPE_IMAGES_ALL));

        if (QiPick.API_19) {

            final Set<String> allowedMimeTypes = sharedPreferences.getStringSet(QiPick.PREFS_ALLOWED_MIME_TYPES_KITKAT, null);
            if (allowedMimeTypes != null && allowedMimeTypes.size() != 0) {

                final String[] types = allowedMimeTypes.toArray(new String[allowedMimeTypes.size()]);
                pIntent.putExtra(Intent.EXTRA_MIME_TYPES, types);

            }

        }

    }

    // ==== LOCAL ONLY ==== //

    /**
     * Note: only works for Honeycomb and later (API 11+)
     *
     * @param pContext        app {@link Context}
     * @param pAllowLocalOnly pass true to not allow remote content
     */
    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    public static void allowOnlyLocalContent(@NonNull final Context pContext, final boolean pAllowLocalOnly) {
        PreferenceManager.getDefaultSharedPreferences(pContext)
                         .edit()
                         .putBoolean(QiPick.PREFS_ALLOW_LOCAL_CONTENT_ONLY, pAllowLocalOnly)
                         .apply();
    }

    // ==== //

    // hide constructor
    private QuickImagePick() {}

    // ==== //

    /**
     * Callback for {@code handleActivityResult(...)} methods
     *
     * @deprecated use {@link PickCallback}. This interface will be removed in one of next releases
     */
    @Deprecated
    public interface Callback
            extends PickCallback {}

}
