package com.example.richa.sugarthrow;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AndroidImageAdapter extends PagerAdapter {

    Context context;

    AndroidImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderImagesId.length;
    }

    private int[] sliderImagesId = new int[] {
            R.drawable.scenic, R.drawable.app_banner, R.drawable.sugar_cube,
            R.drawable.scenic, R.drawable.app_banner, R.drawable.sugar_cube,
    };

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(sliderImagesId[i]);
        ((ViewPager)container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object object) {
        ((ViewPager)container).removeView((ImageView)object);
    }

}
