package com.example.richa.sugarthrow;

/*
This class handles some of the creation of Views, such as TextViews and ImageViews
 */

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewCreator extends MainActivity {

    private Context context;

    /**
     * Constructor allows access to an Activity's contents
     * @param context - the context of an Activity
     */
    public ViewCreator(Context context) {
        this.context = context;
    }

    /**
     * Create the text which appears in the drop down layouts
     * @param width - the width of the text
     * @param text - the actual text as a String
     * @param gravity - the position of the text
     * @param color - the text color
     * @return the TextView for drop down text
     */
    public TextView createDropDownText(int width, String text, int gravity, int color) {

        int textWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width ,
                context.getResources().getDisplayMetrics());

        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(textWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        textView.setTextSize(14);
        textView.setGravity(gravity);
        textView.setTextColor(color);

        return textView;

    }

    /**
     * Create an ImageView programatically
     * @param row - the id which refers to the position in the search / diary
     * @param drawableResource - the drawable resource referenced
     * @param height - the height of the image
     * @param width - the width of the image
     * @param gravity - the position of the image
     * @param tag - the tag
     * @return ImageView
     */
    public ImageView createImage(int row, int drawableResource, int height,
                                 int width, int gravity, String tag) {

        ImageView image = new ImageView(context);
        int imageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width,
                context.getResources().getDisplayMetrics());
        int imageHeight;

        if(height != LinearLayout.LayoutParams.MATCH_PARENT) {
            imageHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height,
                    context.getResources().getDisplayMetrics());
        }
        else {
            imageHeight = height;
        }

        image.setImageResource(drawableResource);
        image.setLayoutParams(new LinearLayoutCompat.LayoutParams(imageWidth,
                imageHeight));
        image.setForegroundGravity(gravity);
        image.setClickable(true);
        image.setId(row);
        image.setTag(tag);

        return image;

    }

    /**
     * Checks to see if margins are valid. If there are margins, there
     * must be exactly 4, or the method returns false
     * @param margins - passed variable length of margins
     * @return boolean representing whether the margins entered are valid
     */
    public boolean checkMarginsValid(int ... margins) {
        return margins.length == 4;
    }

    /**
     * Method which creates text programatically
     * @param id - the id referring to position in diary / search
     * @param text - the actual text as a String
     * @param width - the width of the text
     * @param height - the height of the text
     * @param gravity - the position of the text
     * @param background - the background of the text
     * @param textSize - the size of the text
     * @param textColor - the color of the text
     * @param tag - the tag
     * @param margins - variable length of margins
     * @return the TextView
     */
    public TextView createText(int id, String text, int width, int height, int gravity, int background,
                           int textSize, int textColor, String tag, int ... margins) {

        int textWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width ,
                context.getResources().getDisplayMetrics());
        int textHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height ,
                context.getResources().getDisplayMetrics());

        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(textWidth, textHeight));
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setGravity(gravity);
        textView.setTextColor(textColor);
        textView.setBackgroundResource(background);
        textView.setId(id);
        textView.setTag(tag);

        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSingleLine(true);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(textWidth,
                LinearLayout.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(llp);

        if(!checkMarginsValid(margins)) {
            llp.setMargins(0, 0, 0, 0);
        }
        else {
            llp.setMargins(margins[0], margins[1], margins[2], margins[3]);
        }

        return textView;

    }

}
