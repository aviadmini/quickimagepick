# QuickImagePick

Easy to use and customizable image pick for Android. 
 
* Pick image from gallery
* Use Documents app (API 19+) to pick image from gallery, cloud storage etc.
* Take picture with device camera (using existing apps)
* Mix and match: allow user to choose from any combination of above
* Supports activities and fragments (both plain and support ones)

## Usage

### Step 1: import Gradle dependency

```groovy
repositories {
    maven { url "https://jitpack.io" }
}
    
dependencies {
    compile 'com.github.aviadmini:quickimagepick:1.0.0'
}
```

### Step 2: trigger pick flow 

#### Show gallery apps to choose picture
- Use overloads of ```QuickImagePick.pickFromGallery(...)```

#### Choose picture using Documents app
- Use overloads of ```QuickImagePick.pickFromDocuments(...)```
- Note: on pre-KitKat this will show file manager apps to choose a picture

#### Show camera apps to take a picture
- Use overloads of ```QuickImagePick.pickFromCamera(...)```
- You can change the directory where pictures are saved by calling `setCameraPicsDirectory(Context pContext, String pDirPath)`, set to `null` to use default (subdirectory of app directory on external storage) 
- **EXTREMELY IMPORTANT: library does not delete the images taken by camera. You must do it yourself after you're done with them to prevent using too much storage space** 
- Default settings don't need `WRITE_EXTERNAL_STORAGE` permission, but if you want to save camera pics to a different location you might need to grant the permission
- Library **does not** need CAMERA permission. **However** if you have it declared in your Manifest, you must grant it to use `pickFromCamera(...)` methods. Refer [here](http://stackoverflow.com/questions/32789027/android-m-camera-intent-permission-bug)

#### Use a combination of above by specifying which types to show `PickSource.CAMERA`, `PickSource.GALLERY` and/or `PickSource.DOCUMENTS` (refer to sample app for usage)
- ```QuickImagePick.pickFromMultipleSources(...)```

### Step 3: Get your picture `Uri`

```java

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
protected void onActivityResult(final int pRequestCode, final int pResultCode, final Intent pData) {
super.onActivityResult(pRequestCode, pResultCode, pData);

    if (!QuickImagePick.handleActivityResult(getApplicationContext(), pRequestCode, pResultCode, pData, this.mCallback)) {
        super.onActivityResult(pRequestCode, pResultCode, pData);
    }
            
}
```

## License

    Copyright 2016 Bohdan Semeniuk

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.