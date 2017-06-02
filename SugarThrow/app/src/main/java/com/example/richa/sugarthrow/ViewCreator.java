package com.example.richa.sugarthrow;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by richa on 02/06/2017.
 */

public class ViewCreator extends MainActivity {

    private Context context;

    public ViewCreator(Context context) {
        this.context = context;
    }

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

    public ImageView createImageInDiary(int row, int drawableResource) {

        ImageView minus = new ImageView(context);
        int minusWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60 ,
                context.getResources().getDisplayMetrics());

        minus.setImageResource(R.drawable.ic_remove_circle_black);
        minus.setLayoutParams(new LinearLayoutCompat.LayoutParams(minusWidth,
                LinearLayout.LayoutParams.MATCH_PARENT));
        minus.setForegroundGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        minus.setClickable(true);

        minus.setId(row);
        minus.setTag("minusTag");

        return minus;
    }

    /**
     *
     * @param text
     * @param width
     * @param id
     * @param margins
     * @return
     */
    public TextView createTextInDiary(String text, int width, int id, int... margins) {

        TextView textView = new TextView(context);
        int textWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width ,
                context.getResources().getDisplayMetrics());

        textView.setText(text);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setClickable(true);
        textView.setId(id);
        textView.setTag("diaryEntryTag");

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(textWidth,
                LinearLayout.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(llp);

        if(checkMarginsValid(margins) == false) {
            llp.setMargins(0, 0, 0, 0);
        }
        else {
            llp.setMargins(margins[0], margins[1], margins[2], margins[3]);
        }

        return textView;
    }

    /**
     *
     * @param margins
     * @return
     */
    private boolean checkMarginsValid(int ... margins) {
        if(margins.length != 4) {
            return false;
        }
        return true;
    }

}
