package com.example.richa.sugarthrow;

/*
This class is responsible for any image sliders that appear in the app
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

class ImageSlider extends PagerAdapter {

    private Context context;

    /**
     * Constructor referring to the particular activity's context
     * @param context - the context of an activity
     */
    ImageSlider(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderImagesId.length;
    }

    /**
     * The slider images created
     */
    private int[] sliderImagesId = new int[] {
        R.drawable.sugar_background_small, R.drawable.raspberry_background, R.drawable.tip_background
    };

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(sliderImagesId[i]);
        (container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object object) {
        (container).removeView((ImageView)object);
    }

}
