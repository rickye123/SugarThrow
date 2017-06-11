package com.example.richa.sugarthrow;

/*
This class is resposible for the creation of layouts in the
app
 */

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class LayoutCreator extends MainActivity {

    private Context context;
    private ViewCreator viewCreator;

    /**
     * LayoutCreator constructor is passed the Context and the
     * ViewCreator object
     * @param context - the activity the layout was called from
     * @param viewCreator - the viewCreator object used to create views
     */
    public LayoutCreator (Context context, ViewCreator viewCreator) {
        this.context = context;
        this.viewCreator = viewCreator;
    }

    /**
     * Create no entries layout that appears when there are no diary entries
     * @return the linear layout which displays "No Entries"
     */
    public LinearLayout createNoEntries() {

        // width, height, and textHeight measurements
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200 ,
                context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 158 ,
                context.getResources().getDisplayMetrics());
        int textHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42 ,
                context.getResources().getDisplayMetrics());

        // create the wrapper which allows text to appear in the centre of the layout
        LinearLayout noEntryWrapper = new LinearLayout(context);
        noEntryWrapper.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        noEntryWrapper.setOrientation(LinearLayout.HORIZONTAL);
        noEntryWrapper.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        noEntryWrapper.setId(R.id.no_entry_wrapper);

        // create the "No Entries" text
        TextView entryText = new TextView(context);
        entryText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                textHeight));
        entryText.setText(context.getString(R.string.placeholder_no_entries));
        entryText.setTextSize(24);
        entryText.setTextColor(Color.BLACK);
        entryText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        entryText.setBackgroundResource(R.drawable.dashed_border);

        noEntryWrapper.addView(entryText);

        return noEntryWrapper;

    }

    /**
     * Create the drop down wrapper than contains the nutritional information
     * @param row - the row in which the layout was created, so that it can be clicked
     *            on later. The row represents where the item is in the diary / search
     * @return the dropdownwrapper that contains the dropdown content (nutritional info)
     */
    public LinearLayout createDropDownWrapper(int row) {

        LinearLayout dropDownWrapper = new LinearLayout(context);
        dropDownWrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        dropDownWrapper.setOrientation(LinearLayout.VERTICAL);
        dropDownWrapper.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        dropDownWrapper.setId(row);
        dropDownWrapper.setTag("dropDownWrapperTag");

        return dropDownWrapper;

    }

    /**
     * The method invoked to create a basic linear layout programatically
     * @param orientation - Horiztonal or Vertical
     * @param width - the width of the layout
     * @param height - the height of the layout
     * @param gravity - the gravity of the layout, e.g. Gravity.CENTER_HORIZONTAL
     * @param margins - variable length arguments representing the margins
     * @return the linear layout created
     */
    public LinearLayout createBasicLinearLayout(int orientation, int width, int height,
                                                int gravity, int ... margins) {

        LinearLayout basicLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                width, height);
        basicLayout.setLayoutParams(layoutParams);
        basicLayout.setOrientation(orientation);
        basicLayout.setGravity(gravity);

        // check that if margins are specified, there are exactly 4 of them
        if(viewCreator.checkMarginsValid(margins)) {
            layoutParams.setMargins(0, 0, 0, 0);
        }
        else {
            layoutParams.setMargins(margins[0], margins[1], margins[2], margins[3]);
        }

        return basicLayout;

    }

    /**
     * The method invoked which creates the drop drop down layout
     * @param text - the array list of Strings containing the contents of nutritional info
     * @param color - the color, either red (if the contents is over 100%), or black
     * @return the layout
     */
    public LinearLayout createDropDownLayout(List<String> text, List<Integer> color) {
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250 ,
                context.getResources().getDisplayMetrics());

        LinearLayout dropDown = createBasicLinearLayout(LinearLayout.VERTICAL,
                width, LinearLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        LinearLayout layout = createBasicLinearLayout(LinearLayout.HORIZONTAL,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.NO_GRAVITY, 0, 0, 0, 5);

        layout.addView(viewCreator.createDropDownText(120, text.get(0), Gravity.START, color.get(0)));
        layout.addView(viewCreator.createDropDownText(60, text.get(1), Gravity.END, color.get(1)));
        layout.addView(viewCreator.createDropDownText(10, text.get(2), Gravity.START, color.get(2)));
        layout.addView(viewCreator.createDropDownText(50, text.get(3), Gravity.END, color.get(3)));
        layout.addView(viewCreator.createDropDownText(10, "%", Gravity.START, color.get(4)));

        dropDown.addView(layout);

        return dropDown;

    }

    /**
     * Create the layout which displays "No Results" when a search returns no results
     * @return the linear layout containing "No Results"
     */
    public LinearLayout createNoResultsLayout() {

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 232 ,
                context.getResources().getDisplayMetrics());

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 158 ,
                context.getResources().getDisplayMetrics());

        LinearLayout searchEntries = createBasicLinearLayout(LinearLayout.HORIZONTAL,
                width, height, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        TextView noResults = viewCreator.createText(0, "No Results", 232, 52,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, R.drawable.dashed_border,
                24, R.color.black, "noResultsTag");

        searchEntries.addView(noResults);

        return searchEntries;

    }



}
