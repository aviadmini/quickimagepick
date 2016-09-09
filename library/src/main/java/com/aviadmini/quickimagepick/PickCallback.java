package com.aviadmini.quickimagepick;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Callback for {@code handleActivityResult(...)} methods
 *
 * @since v2.0.0
 */
public interface PickCallback {

    /**
     * Triggered when an image {@link Uri} was successfully retrieved
     *
     * @param pPickSource  source from which image {@link Uri} was retrieved
     * @param pRequestType request type that was (optionally) set when starting pick flow
     * @param pImageUri    {@link Uri} of the image
     */
    void onImagePicked(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final Uri pImageUri);


    /**
     * Callback to handle multiple images picked.
     * @param pPickSource  source from which image {@link Uri} was retrieved
     * @param pRequestType request type that was (optionally) set when starting pick flow
     * @param images   Array list of {@link Uri} of the image
     */
    void onMultiImagePicked(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final ArrayList<Uri> images);

    /**
     * Triggered when an error occurred in process of image picking
     *
     * @param pPickSource  source from which image {@link Uri} was retrieved
     * @param pRequestType request type that was (optionally) set when starting pick flow
     * @param pErrorString error string describing the error. One of public {@code ERR_} constants in {@link QiPick} class
     */
    void onError(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final String pErrorString);

    /**
     * Triggered when picking flow was cancelled (usually by user)
     *
     * @param pPickSource  source from which image {@link Uri} was retrieved.
     *                     Value might be inaccurate when multiple pick sources are requested and
     *                     user has not yet picked an exact one (from chooser).
     * @param pRequestType request type that was (optionally) set when starting pick flow
     */
    void onCancel(@NonNull final PickSource pPickSource, final int pRequestType);

}
