# VsBottomNavigationView
A library for building any BottomNavigationView you want without creating your custom implementation.

## Gradle
```groovy
androidExtensions {
    experimental = true //see https://kotlinlang.org/docs/tutorials/android-plugin.html#using-kotlin-android-extensions
}

dependencies {
    implementation 'com.wshtaits:vsbottomnavigationview:1.0.0'
}
```

## Methods

```kotlin
fun setAutoSelectable(isAutoSelectable: Boolean)

fun setItemAnimator(animator: ItemAnimator?)

fun setItemAnimatorForPosition(itemPosition: Int, animator: ItemAnimator)

fun setItemAnimatorForId(@IdRes itemId: Int, animator: ItemAnimator)

fun setOnItemClickAction(action: ((itemId: Int) -> Unit)?)

fun setOnItemSelectAction(action: ((itemId: Int) -> Unit)?)

fun selectItemById(@IdRes itemId: Int)

fun selectItemByPosition(itemPosition: Int)
```

## Simple example
**layout/activity_sample.xml**
```xml
<com.wshtaits.vsbottomnavigationview.VsBottomNavigationView
    android:id="@+id/vs_bottom_navigation_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#fff"
    android:elevation="8dp"
    app:menu="@menu/sample"
    app:itemLayout="@layout/item_sample" />
    
```

**layout/item_sample.xml**
```xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:background="?selectableItemBackgroundBorderless"
    >

    <ImageView
        android:id="@+id/icon_iv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="4dp"
        />

    <TextView
        android:id="@+id/title_tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="12sp"
        />

</LinearLayout>
```

**menu/sample.xml**
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/action_wifi"
        android:title="@string/wifi"
        android:icon="@drawable/ic_wifi"
        android:checked="true"
        />

    <item
        android:id="@+id/action_cellular"
        android:title="@string/cellular"
        android:icon="@drawable/ic_cellular"
        />

    <item
        android:id="@+id/action_battery"
        android:title="@string/battery"
        android:icon="@drawable/ic_battery"
        />

</menu>
```

**SampleItemAnimator.kt**
```kotlin
class SampleItemAnimator(context: Context) : ItemAnimator() {

    private val selectedColor = context.getColor(android.R.color.black)
    private val unselectedColor = context.getColor(android.R.color.darker_gray)

    override fun ItemViewHolder.onCreate(menuItem: MenuItem) {
        icon_iv.background = menuItem.icon
        title_tv.text = menuItem.title

        if (menuItem.isChecked) {
            menuItem.icon.setTint(selectedColor)
            title_tv.setTextColor(selectedColor)
        } else {
            menuItem.icon.setTint(unselectedColor)
            title_tv.setTextColor(unselectedColor)
        }
    }

    override fun ItemViewHolder.onSelect(menuItem: MenuItem) {
        menuItem.icon.setTint(selectedColor)
        title_tv.setTextColor(selectedColor)
    }

    override fun ItemViewHolder.onDeselect(menuItem: MenuItem) {
        menuItem.icon.setTint(unselectedColor)
        title_tv.setTextColor(unselectedColor)
    }
}
```

**SampleActivity.kt**
```kotlin
vs_bottom_navigation_view.setItemAnimator(SampleItemAnimator())
```

![](demo_gifs/simple.gif)

## Animated example
Just change `ItemAnimator` implementation to something like this: [SampleItemAnimator.kt](https://github.com/wshtaits/VsBottomNavigationView/blob/master/sample/src/main/java/com/example/vsbottomnavigationview/SampleItemAnimator.kt)

![](demo_gifs/animated.gif)

## Specific item example
Create specific item layout [layout/item_sample_specific.xml](https://github.com/wshtaits/VsBottomNavigationView/blob/master/sample/src/main/res/layout/item_sample_specific.xml) and separate `ItemAnimator` for it [SpecificSampleItemAnimator.kt](https://github.com/wshtaits/VsBottomNavigationView/blob/master/sample/src/main/java/com/example/vsbottomnavigationview/SpecificSampleItemAnimator.kt).

Then add it inside `VsBottomNavigationView` tag.
```xml
<com.wshtaits.vsbottomnavigationview.VsBottomNavigationView
    android:id="@+id/vs_bottom_navigation_view"
    android:layout_width="match_parent"
    android:layout_height="60dp"
    android:layout_gravity="bottom"
    android:background="#fff"
    android:elevation="8dp"
    app:menu="@menu/sample"
    app:itemLayout="@layout/item_sample"
    tools:targetApi="lollipop" >

    <include layout="@layout/item_sample_specific" />

</com.wshtaits.vsbottomnavigationview.VsBottomNavigationView>
```

And in Activity:
```kotlin
vs_bottom_navigation_view.setItemAnimatorForId(R.id.action_battery, SpecificSampleItemAnimator(this))
```

**Note:**
- `layout_menuPosition` corresponds to a menu item at a specified position.
- Root view id of your specific item layout become menu item id.
- If you use `<include>` tag then `layout_menuPosition` attribute must be specified where you specified other `layout_*` attributes.

![](demo_gifs/specific.gif)

## isAutoSelectable
If you want to handle the event first, and only then decide whether to make it selected then set `isAutoSelectable` attribute to false through xml or code and use `setOnItemClickAction` and `selectItemBy*` methods.

# License
>Copyright (c) 2019 Shtaits Valeriy.
>
>Licensed under the Apache License, Version 2.0 (the "License");
>you may not use this file except in compliance with the License.
>You may obtain a copy of the License at
>
>http://www.apache.org/licenses/LICENSE-2.0
>
>Unless required by applicable law or agreed to in writing, software
>distributed under the License is distributed on an "AS IS" BASIS,
>WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
>See the License for the specific language governing permissions and
>limitations under the License.