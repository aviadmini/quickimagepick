package com.aviadmini.quickimagepick;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Result code for {@link PickRequest} trigger
 *
 * @since v2.0.0
 */
@IntDef({PickTriggerResult.TRIGGER_PICK_OK, PickTriggerResult.TRIGGER_PICK_ERR_NO_ACTIVITY, PickTriggerResult.TRIGGER_PICK_ERR_CAM_FILE, PickTriggerResult.TRIGGER_PICK_ERR_NO_PICK_SOURCES})
@Retention(RetentionPolicy.SOURCE)
public @interface PickTriggerResult {

    int TRIGGER_PICK_OK                  = 0;
    int TRIGGER_PICK_ERR_NO_ACTIVITY     = -1;
    int TRIGGER_PICK_ERR_CAM_FILE        = -2;
    int TRIGGER_PICK_ERR_NO_PICK_SOURCES = -3;

}