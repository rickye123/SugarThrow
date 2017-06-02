package com.example.richa.sugarthrow;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richa on 02/06/2017.
 */

public class LayoutCreator extends MainActivity {

    private Context context;
    private ViewCreator viewCreator;

    public LayoutCreator (Context context, ViewCreator viewCreator) {
        this.context = context;
        this.viewCreator = viewCreator;
    }

    public LinearLayout createNoEntries() {

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 232 ,
                context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 158 ,
                context.getResources().getDisplayMetrics());
        int textHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42 ,
                context.getResources().getDisplayMetrics());

        LinearLayout noEntryWrapper = new LinearLayout(context);
        noEntryWrapper.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        noEntryWrapper.setOrientation(LinearLayout.HORIZONTAL);
        noEntryWrapper.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        noEntryWrapper.setId(R.id.no_entry_wrapper);

        TextView entryText = new TextView(context);
        entryText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                textHeight));
        entryText.setText("No Entries");
        entryText.setTextSize(24);
        entryText.setTextColor(Color.BLACK);
        entryText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        entryText.setBackgroundResource(R.drawable.dashed_border);

        noEntryWrapper.addView(entryText);

        return noEntryWrapper;

    }

    public LinearLayout createDropDownWrapper(int row, String foodName) {

        LinearLayout dropDownWrapper = new LinearLayout(context);
        dropDownWrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        dropDownWrapper.setOrientation(LinearLayout.VERTICAL);
        dropDownWrapper.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        //dropDownWrapper.setVisibility(View.GONE);
        dropDownWrapper.setId(row);
        dropDownWrapper.setTag("dropDownWrapperTag");

        return dropDownWrapper;

    }

    public LinearLayout createBasicLinearLayout(int orientation, int width, int height,
                                                int gravity, int ... margins) {

        LinearLayout basicLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                width, height);
        basicLayout.setLayoutParams(layoutParams);
        basicLayout.setOrientation(orientation);
        basicLayout.setGravity(gravity);

        if(checkMarginsValid(margins)) {
            layoutParams.setMargins(margins[0], margins[1], margins[2], margins[3]);
        }

        return basicLayout;

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

    public LinearLayout createDropDownLayout(List<String> text, List<Integer> color) {
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250 ,
                context.getResources().getDisplayMetrics());

        LinearLayout dropDown = createBasicLinearLayout(LinearLayout.VERTICAL,
                width, LinearLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        LinearLayout layout = createBasicLinearLayout(LinearLayout.HORIZONTAL,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.NO_GRAVITY, 0, 0, 0, 5);

        TextView name = viewCreator.createDropDownText(120, text.get(0), Gravity.START, color.get(0));
        TextView quant = viewCreator.createDropDownText(50, text.get(1), Gravity.END, color.get(1));
        TextView measure = viewCreator.createDropDownText(10, text.get(2), Gravity.START, color.get(2));
        TextView percent = viewCreator.createDropDownText(50, text.get(3), Gravity.END, color.get(3));
        TextView sign = viewCreator.createDropDownText(10, "%", Gravity.START, color.get(4));

        layout.addView(name);
        layout.addView(quant);
        layout.addView(measure);
        layout.addView(percent);
        layout.addView(sign);

        dropDown.addView(layout);

        return dropDown;

    }

}
