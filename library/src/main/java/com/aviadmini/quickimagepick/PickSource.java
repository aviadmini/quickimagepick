package com.aviadmini.quickimagepick;

/**
 * Sources from which images can be picked
 *
 * @author aviadmini
 */
public enum PickSource {

    /**
     * Take photo using a camera app
     */
    CAMERA,

    /**
     * Grab image from device gallery
     */
    GALLERY,

    /**
     * Pick file using content manager. For KitKat (API 19+) also adds Documents app
     */
    DOCUMENTS

}