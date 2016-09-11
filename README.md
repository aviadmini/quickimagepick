[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-QuickImagePick-blue.svg?style=flat-square)](http://android-arsenal.com/details/1/4248) [![license](https://img.shields.io/github/license/aviadmini/quickimagepick.svg?maxAge=2592000&style=flat-square)]() [![Release](https://jitpack.io/v/aviadmini/quickimagepick.svg?style=flat-square)](https://jitpack.io/#aviadmini/quickimagepick)

# QuickImagePick

Easy to use and highly customizable image picker for Android with helpful extras to deal with outputs. Minimum API 9+, gets better with newer API versions
 
API 9+ (Gingerbread) functionality:

* Pick image from gallery (using existing apps)
* Use content manager apps (usually file managers) to pick image
* Take picture with device camera (using existing apps)
* Mix and match: allow user to choose from any combination of above
* Save `Uri` content to a `File`
* Set allowed MIME type of files that can be picked
* Single call to get MIME type and file extension of picked image (from returned Uri)
* Supports activities and fragments (both plain and support ones)

API 11+ (Honeycomb):

* Allow only local content to be picked

API 18+ (Jelly Bean MR2):

* Allow picking multiple images via documents pick source (where supported)

API 19+ (KitKat):

* Additionally use Documents app (API 19+) to pick image from gallery, cloud storage etc.
* Allow multiple MIME types of files that can be picked

## Why this library?

* Simple yet powerful API with chained calls
* Activity, Fragment and support Fragment targets for returning picked result
* More functionality (numerous useful operations with `Uri`, multiple pick options, etc) 
* It works with content data type rather than filesystem (solving `FileUriExposedException` issue on API 24+ target)

## Dependency

```groovy
repositories {
    maven { url "https://jitpack.io" }
}
    
dependencies {
    compile 'com.github.aviadmini:quickimagepick:${qip.release.version}'
}
```

Latest release: [![GitHub release](https://img.shields.io/github/release/aviadmini/quickimagepick.svg)]()

## Usage

For detailed explanation refer to [Documentation](https://github.com/aviadmini/quickimagepick/wiki/Documentation) and sample app

#### Fire pick request

```java
@PickTriggerResult final int triggerResult;
triggerResult = QiPick.in(Activity or Fragment)
                      .fromMultipleSources("All sources", PickSource.CAMERA, PickSource.DOCUMENTS);
```
Here `triggerResult` defines whether pick request was fired successfully. If not, it can be treated as error code.

#### Consume result
In `onActivityResult` of Activity or Fragment:

```java
private final PickCallback mCallback = new PickCallback() {

    @Override
    public void onImagePicked(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final Uri pImageUri) {
        // Do something with Uri, for example load image into an ImageView
        Glide.with(getApplicationContext())
             .load(pImageUri)
             .fitCenter()
             .into(mImageView);
    }

    @Override
    public void onMultipleImagesPicked(final int pRequestType, @NonNull final List<Uri> pImageUris) {
        // meh whatever, just show first picked ;D
        this.onImagePicked(PickSource.DOCUMENTS, pRequestType, pImageUris.get(0));
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
    super.onActivityResult(pRequestCode, pResultCode, pData);
    
    QiPick.handleActivityResult(getApplicationContext(), pRequestCode, pResultCode, pData, this.mCallback);
            
}
```

#### Dessert

There's a bunch of useful methods to work with `Uri` in `UriUtils`

## Vector

There's a few extension ideas that might make the library even more useful. 

- v3.0 create PickResult that will encapsulate both PickRequest details and pick results
- ability to explicitly add file picker(-s) as pick sources
- tell me more ;)

If your app uses this library send me market links ;)

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