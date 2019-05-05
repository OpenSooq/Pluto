<a href="https://opensooq.com/">
    <img src="https://opensooqui2.os-cdn.com/os_web/desktop/opensooq-logo.svg" alt="OpenSooq logo" title="OpenSooq" align="right" height="70" />
</a>

Pluto [Android Slider View Library]
======================
![API](https://img.shields.io/badge/API-17%2B-green.svg?style=flat) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

:star: Star us on GitHub — it helps!

[![forthebadge](https://forthebadge.com/images/badges/built-with-love.svg)](https://forthebadge.com)

##### Pluto is an Easy, lightweight  and high performance slider view library for Android!  You can customize it to any view since it based RecyclerView. The differnce of this library, we are not following the concept of images model. Pluto is not depending on any Image loading library 



Demo
======================
<img src="https://github.com/OpenSooq/Pluto/blob/master/demoassets/demo_1.gif"  height="440" /> <img src="https://github.com/OpenSooq/Pluto/blob/master/demoassets/demo_2.gif"  height="440" />

## Table of content

* [Download](#download)
* [Sample Project](#sample-project)
* [Usage](#usage)
* [Event Listeners](#event-listeners)
* [Custom indicator](#Custom-indicator)
- [License](#license)

# Download

This library is available in **jCenter** which is the default Maven repository used in Android Studio. You can also import this library from source as a module.
 
```groovy
dependencies {
    // other dependencies here
    implementation 'com.opensooq.android:pluto:1.2'
}
```


# Sample Project
We have a sample project demonstrating how to use the library.

Checkout the demo  [here](https://github.com/OpenSooq/Pluto/tree/master/app/src/main/java/com/opensooq/plutodemo)



# Usage
#### First create your own adapter extending the ``PlutoAdapter``

```java
public class YourAdapter extends PlutoAdapter<YourModel, YourViewHolder> {

    public YourAdapter(List<YourModel> items) {
        super(items);
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new YourViewHolder(parent, R.layout.item_your_layout);
    }

    public static class YourViewHolder extends PlutoViewHolder<YourModel> {
        ImageView ivPoster;
        TextView tvRating;

        public YourViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivPoster = getView(R.id.iv_poster);
            tvRating = getView(R.id.tv_rating);
        }

        @Override
        public void set(YourModel item, int pos) {
           //  yourImageLoader.with(mContext).load(item.getPosterId()).into(ivPoster);
            tvRating.setText(item.getImdbRating());
        }
    }
}
```
#### Then in your xml 
```XML
<com.opensooq.pluto.PlutoView
        android:id="@+id/slider_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:indicator_visibility="true"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>
``` 
### finaly attach the adapter to Pluto 
```java
  PlutoView pluto = findViewById(R.id.slider_view);
        YourAdapter adapter = new YourAdapter(yourModelsList);
        pluto.create(adapter);     
```
| Method | usage |
| ------ | ------ |
| ``` create(PlutoAdapter adapter, long duration)``` | it is the initialization method it take your adapter and the duration of waiting between each slide |
| ``` startAutoCycle() ``` | by default the value of autocycle is true, it determine to start autocycling or not  |
| ``` stopAutoCycle() ``` | it stops the auto cycling of the view |
| ``` moveNextPosition() ``` | if you are the auto cylce is off you can manually move next with this method  |
| ``` movePrevPosition() ``` | if you are the auto cylce is off you can manually move to the previus item with this method |
| ``` setIndicatorPosition(IndicatorPosition presetIndicator) ``` | to set the position of the indicator where values are ```CENTER_BOTTOM```   ```RIGHT_BOTTOM```    ```LEFT_BOTTOM``` ```CENTER_TOP``` ```RIGHT_TOP``` ```LEFT_TOP```|
| ``` setCustomIndicator(PlutoIndicator indicator) ``` | if you want to custom the indicator use this method for custom indicators check [Custom indicators](#indicator) |

# Event Listeners 
### for item click listener its the responsibility of the ```PlutoAdapter```  to handle it, 
#### Example 
```java 

        SliderAdapter adapter = new SliderAdapter(getAvengers(), (item, position) -> {
            //handle clickhere
        });
        
        adapter.setOnItemClickListener((item, position) -> {
            //handle click here
        });
```
### you can attach listener to the ```PlutoView``` to listen for sliding event 
#### Example 

```java 

       pluto.setOnSlideChangeListener(new OnSlideChangeListener() {
            @Override
            public void onSlideChange(PlutoAdapter adapter, int position) {
                
            }
        });
```



# Custom indicator
Add the following xml to your layout:

```xml
<com.opensooq.pluto.PlutoIndicator
		android:id="@+id/custom_indicator"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:gravity="center"
/>
```
### Customizable Properties

|		Property		|		Description		|
|:----------------------|:---------------------:|
|		`shape`			|	`oval` &#124; `rect`			|
|		`visibility`		|	`visible` &#124; `invisible`	|
|	`selected_drawable unselected_drawable`	| You can use an image or custom drawable as the indicator. If you decide to use your own drawable, then the built-in drawable and the properties associated with the built-in drawable will not work. |
|	`selected_color` `unselected_color`   | the color of the indicator |
|	`selected_width` `unselected_width`   | the width of the shape     |
|	`selected_height` `unselected_height` | the height of the shape    |
|`selected_padding_left` `unselected_padding_left` `selected_padding_right` `unselected_padding_right` `selected_padding_top` `unselected_padding_top` `selected_padding_bottom` `unselected_padding_bottom` | the padding of the indicator |

#### Examples

![Demo-1](http://ww3.sinaimg.cn/mw690/610dc034jw1eh7metysj6j201y0150jn.jpg)
```xml
    <com.opensooq.pluto.PlutoIndicator
        android:id="@+id/custom_indicator"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:gravity="center"
        custom:selected_color="#555555"
        custom:unselected_color="#55555555"
        custom:shape="oval"
        custom:selected_padding_left="3dp"
        custom:selected_padding_right="3dp"
        custom:unselected_padding_left="3dp"
        custom:unselected_padding_right="3dp"
        custom:selected_width="8dp"
        custom:selected_height="8dp"
        custom:unselected_width="4dp"
        custom:unselected_height="4dp"
        />
```

***

![Demo-2](http://ww2.sinaimg.cn/mw690/610dc034jw1eh7ml8me63j203h00z3y9.jpg)
	
```xml
    <com.opensooq.pluto.PlutoIndicator
        android:id="@+id/custom_indicator"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:gravity="center"
        custom:selected_color="#FF5500"
        custom:unselected_color="#55333333"
        custom:shape="rect"
        custom:selected_padding_left="2dp"
        custom:selected_padding_right="2dp"
        custom:unselected_padding_left="2dp"
        custom:unselected_padding_right="2dp"
        custom:selected_width="16dp"
        custom:selected_height="3dp"
        custom:unselected_width="16dp"
        custom:unselected_height="3dp"
        />
```

***

![Demo-3](http://ww4.sinaimg.cn/mw690/610dc034jw1eh7mp7q3fxj202900y3y9.jpg)
```xml
    <com.opensooq.pluto.PlutoIndicator
        android:id="@+id/custom_indicator"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:gravity="center"
        custom:selected_color="#0095BF"
        custom:unselected_color="#55333333"
        custom:shape="rect"
        custom:selected_padding_left="2dp"
        custom:selected_padding_right="2dp"
        custom:unselected_padding_left="2dp"
        custom:unselected_padding_right="2dp"
        custom:selected_width="6dp"
        custom:selected_height="6dp"
        custom:unselected_width="6dp"
        custom:unselected_height="6dp"
        />
```

***

![Demo-4](http://ww4.sinaimg.cn/mw690/610dc034jw1eh7n82vqk3j203401e0sh.jpg)
```xml
    <com.opensooq.pluto.PlutoIndicator
        android:id="@+id/custom_indicator"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:gravity="center"
        custom:selected_color="#0095BF" 
        custom:unselected_color="#55333333"
        custom:selected_drawable="@drawable/bird"
        custom:shape="oval"
        custom:selected_padding_left="6dp"
        custom:selected_padding_right="6dp"
        custom:unselected_padding_left="2dp"
        custom:unselected_padding_right="2dp"
        custom:selected_width="6dp"
        custom:selected_height="6dp"
        custom:unselected_width="6dp"
        custom:unselected_height="6dp"
        />
```

**NOTE**: Because a custom image is used for the indicator, following properties will not work: 

- `custom:selected_color`
- `custom:selected_width`
- `custom:selected_height`
- `custom:shape`
- `custom:color`
- `custom:width`
- `custom:height`

### Preset Styles

Source is [here](https://github.com/OpenSooq/Pluto/blob/master/pluto/src/main/res/values/styles.xml).

Preset-1:

![](http://ww3.sinaimg.cn/mw690/610dc034jw1ehdhc9drczj202l0160p7.jpg)
```xml
    <com.opensooq.pluto.PlutoIndicator
        android:id="@+id/custom_indicator"
        style="@style/Pluto_Magnifier_Oval_Black"
        />
```
***
Preset-2:

![](http://ww4.sinaimg.cn/mw690/610dc034jw1ehdhc5zj9gj203a019jr5.jpg)
```xml
    <com.opensooq.pluto.PlutoIndicator
        android:id="@+id/custom_indicator"
        style="@style/Pluto_Attractive_Rect_Blue"
        />
```
***
Preset-3:

![](http://ww2.sinaimg.cn/mw690/610dc034jw1ehdhc0hzb1j202g01aa9t.jpg)
```xml
    <com.opensooq.pluto.PlutoIndicator
        android:id="@+id/custom_indicator"
        style="@style/Pluto_Corner_Oval_Orange"
        />
```

### Using the View

Bind it with a `PlutoView` instance.

```java
pluto.setCustomIndicator(findViewById(R.id.custom_indicator));
```


# License

```
Copyright 2019 OpenSooq

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
