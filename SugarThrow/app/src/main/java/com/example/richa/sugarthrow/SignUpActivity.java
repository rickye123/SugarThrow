package com.example.richa.sugarthrow;

/*
Activity called when user attempts to sign up for an account
 */

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private Spinner gender, feet, inches, pounds;
    private EditText firstNameView, lastNameView, dobView, stoneView, usernameView,
    passwordView, confirmView, genderView, inchesView, feetView, poundsView;
    private TimeKeeper date = new TimeKeeper();
    private Execute executeSQL;
    private ServerDatabaseHandler serverDatabaseHandler;
    private ContentValues contents;
    private PasswordHash passwordHash = new PasswordHash();
    private LinearLayout signUpProgress;
    private Button createAccount;

    // invoked when activity starts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the layout to the home_activity.xml file
        setContentView(R.layout.signup_activity);

        genderView = new EditText(this);
        inchesView = new EditText(this);
        feetView = new EditText(this);
        poundsView = new EditText(this);

        Connector database = LoginActivity.getDatabaseConnection();
        executeSQL = new Execute(database);
        serverDatabaseHandler = new ServerDatabaseHandler(SignUpActivity.this);

        // create edit text views
        createTextFields();

        // create spinners (drop downs) for gender, height (ft and inches), and weight (pounds)
        createSpinners();

        // click sign up button authenticates all the fields
        clickSignUpButton();


    }

    /**
     * Clicking the sign up button results in the form being authenticated
     */
    private void clickSignUpButton() {
        createAccount = (Button)findViewById(R.id.sign_up_create_account);
        createAccount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    createAccount.setBackgroundResource(R.drawable.account_clicked);
                    authenticate();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    createAccount.setBackgroundColor(ContextCompat.getColor(SignUpActivity.this,
                            R.color.orange));
                }
                return false;
            }
        });
    }

    /**
     * Authenticate the form - checks for errors
     */
    public void authenticate() {

        View focusView = null;
        boolean cancel = false;

        // Reset errors.
        firstNameView.setError(null);
        lastNameView.setError(null);
        dobView.setError(null);
        genderView.setError(null);
        feetView.setError(null);
        inchesView.setError(null);
        stoneView.setError(null);
        poundsView.setError(null);
        usernameView.setError(null);
        passwordView.setError(null);
        confirmView.setError(null);
        gender.setBackgroundResource(R.drawable.login_rounded);
        feetView.setBackgroundResource(R.drawable.login_rounded);
        inchesView.setBackgroundResource(R.drawable.login_rounded);
        poundsView.setBackgroundResource(R.drawable.login_rounded);

        if(TextUtils.isEmpty(firstNameView.getText().toString())) {
            focusView = firstNameView;
            firstNameView.setError("This field is required");
            cancel = true;
        }
        else if(!isValidName(firstNameView.getText().toString())) {
            focusView = firstNameView;
            firstNameView.setError("First name is invalid");
            cancel = true;
        }

        if(TextUtils.isEmpty(lastNameView.getText().toString())) {
            focusView = lastNameView;
            lastNameView.setError("This field is required");
            cancel = true;
        }
        else if(!isValidName(lastNameView.getText().toString())) {
            focusView = lastNameView;
            lastNameView.setError("Last name is invalid");
            cancel = true;
        }

        if(TextUtils.isEmpty(dobView.getText().toString())) {
            focusView = dobView;
            dobView.setError("This field is required");
            cancel = true;
        }
        else if(!isValidDateOfBirth(dobView.getText().toString())) {
            focusView = dobView;
            dobView.setError("Date of birth is invalid");
            cancel = true;
        }

        if(TextUtils.isEmpty(genderView.getText().toString())) {
            focusView = genderView;
            genderView.setError("This field is required");
            gender.setBackgroundResource(R.drawable.sign_up_wrong);
            cancel = true;
        }
        else {
            if (!isGenderValid(genderView.getText().toString())) {
                focusView = genderView;
                genderView.setError("Gender is invalid");
                gender.setBackgroundResource(R.drawable.sign_up_wrong);
                cancel = true;
            }
        }

        if(TextUtils.isEmpty(feetView.getText().toString())) {
            focusView = feetView;
            feetView.setError("This field is required");
            feet.setBackgroundResource(R.drawable.sign_up_wrong);
            cancel = true;
        }
        else if(!isFeetValid(feetView.getText().toString())) {
            focusView = feetView;
            feetView.setError("Height in feet is invalid");
            feet.setBackgroundResource(R.drawable.sign_up_wrong);
            cancel = true;
        }

        if(TextUtils.isEmpty(inchesView.getText().toString())) {
            focusView = inchesView;
            inchesView.setError("This field is required");
            inches.setBackgroundResource(R.drawable.sign_up_wrong);
            cancel = true;
        }
        else if(!isInchesValid(inchesView.getText().toString())) {
            focusView = inchesView;
            inchesView.setError("Height in inches is invalid");
            inches.setBackgroundResource(R.drawable.sign_up_wrong);
            cancel = true;
        }

        if(TextUtils.isEmpty(stoneView.getText().toString())) {
            focusView = stoneView;
            stoneView.setError("This field is required");
            cancel = true;
        }
        else if(!isStoneValid(stoneView.getText().toString())) {
            focusView = stoneView;
            stoneView.setError("Weight in stone is invalid");
            cancel = true;
        }

        if(TextUtils.isEmpty(poundsView.getText().toString())) {
            focusView = poundsView;
            poundsView.setError("This field is required");
            pounds.setBackgroundResource(R.drawable.sign_up_wrong);
            cancel = true;
        }
        else if(!isPoundsValid(poundsView.getText().toString())) {
            focusView = poundsView;
            poundsView.setError("Weight in pounds is invalid");
            pounds.setBackgroundResource(R.drawable.sign_up_wrong);
            cancel = true;
        }

        if(TextUtils.isEmpty(usernameView.getText().toString())) {
            focusView = usernameView;
            usernameView.setError("This field is required");
            cancel = true;
        }
        else if(!isUsernameValid(usernameView.getText().toString())) {
            focusView = usernameView;
            usernameView.setError("Username is invalid");
            cancel = true;
        }

        if(TextUtils.isEmpty(passwordView.getText().toString())) {
            focusView = passwordView;
            passwordView.setError("This field is required");
            cancel = true;
        }

        if(TextUtils.isEmpty(confirmView.getText().toString())) {
            focusView = confirmView;
            confirmView.setError("This field is required");
            cancel = true;
        }


        if(!isPasswordValid(passwordView.getText().toString(), confirmView.getText().toString())) {
            focusView = confirmView;
            confirmView.setError("Password don't match");
            cancel = true;
        }

        // if there is an invalid field, then focus on that field
        if(cancel) {
            focusView.requestFocus();
        }
        else {

            signUpProgress = (LinearLayout)findViewById(R.id.sign_up_progress);
            signUpProgress.setVisibility(View.VISIBLE);
            createAccount.setVisibility(View.GONE);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    signUpProgress.setVisibility(View.GONE);
                    createAccount.setVisibility(View.VISIBLE);
                }

            }, 6000);

            signUpProgress = (LinearLayout)findViewById(R.id.sign_up_progress);
            signUpProgress.setVisibility(View.VISIBLE);
            createAccount.setVisibility(View.GONE);

            // check if user exists on online database
            Map<String, String> params = serverDatabaseHandler.setUserParams(usernameView.getText().toString());
            String url = "http://sugarthrow.hopto.org/my_server/select.php";
            serverDatabaseHandler.select(url, params, "Users", new ServerCallBack() {
                @Override
                public void onSuccess(String result) {

                    contents = serverDatabaseHandler.getContents();
                    if(contents == null) {
                        // if user doesn't exist on online database then insert them
                        // into local and online db
                        ContentValues values = getUserValues();
                        executeSQL.sqlInsert("User", values);
                        List<List<String>> userInfo = executeSQL.sqlGetFromQuery(SqlQueries.SQL_USER, usernameView.getText().toString());
                        String insertURL = "http://sugarthrow.hopto.org/my_server/insert_into_user.php";
                        Map<String, String> userContents = serverDatabaseHandler.setUserContents(userInfo);
                        serverDatabaseHandler.insert(insertURL, userContents, new ServerCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                // launch the app on completion of insertion
                                SaveSharedPreference.setUserName(SignUpActivity.this, usernameView.getText().toString());
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.putExtra("username", usernameView.getText().toString());
                                intent.putExtra("activity", this.getClass().toString());
                                startActivity(intent);
                            }
                        });
                    }
                }
            });


        }



    }

    /**
     * Get the user information from the EditText fields
     * @return content values referencing the user information
     */
    private ContentValues getUserValues() {

        ContentValues values = new ContentValues();

        values.put("userName", usernameView.getText().toString());
        values.put("name", firstNameView.getText().toString()
                + " " + lastNameView.getText().toString());
        values.put("gender", genderView.getText().toString());
        values.put("dob", date.convertDateFormat(dobView.getText().toString()));
        values.put("height", feetView.getText().toString()
                + "." + inchesView.getText().toString());
        values.put("weight", stoneView.getText().toString()
                + "." + poundsView.getText().toString());

        values.put("points", 0);

        String hash = passwordHash.computeSHAHash(passwordView.getText().toString());
        values.put("password", hash);


        return values;
    }

    /**
     * Uses regular expression to determine whether a name is valid
     * @param name - the name of the person as a string
     * @return true if is a valid name, false otherwise
     */
    private boolean isValidName(String name) {

        String expression = "^[a-zA-Z]+";

        return name.matches(expression);

    }

    /**
     * Uses regular expression to first determine whether the
     * format of the dob is valid. Then determines whether the
     * date exists
     * @param dob - the date of birth as a string
     * @return true if dob is valid, false otherwise
     */
    private boolean isValidDateOfBirth(String dob) {

        String expression = "\\d{2}-\\d{2}-\\d{4}";

        if(dob.isEmpty() || dob.equals("")) {
            return false;
        }

        if(dob.matches(expression)) {

            Map<String, String> var = date.getDateVariables(dob);

            if(Integer.parseInt(var.get("day")) <= 0) {
                return false;
            }
            if(Integer.parseInt(var.get("month")) == 2) {
                if(Integer.parseInt(var.get("day")) > 29) {
                    return false;
                }
            }
            else if(Integer.parseInt(var.get("month")) == 1 ||
                    Integer.parseInt(var.get("month")) == 3 ||
                    Integer.parseInt(var.get("month")) == 5 ||
                    Integer.parseInt(var.get("month")) == 7 ||
                    Integer.parseInt(var.get("month")) == 8 ||
                    Integer.parseInt(var.get("month")) == 10 ||
                    Integer.parseInt(var.get("month")) == 12) {
                if(Integer.parseInt(var.get("day")) > 31) {
                    return false;
                }
            }
            else {
                if(Integer.parseInt(var.get("day")) > 30) {
                    return false;
                }
            }
            return true;
        }

        return false;

    }

    /**
     * Determine if gender is valid, i.e. that the user has selected
     * one of the options
     * @param gender - the gender (M, F, X)
     * @return true if gender is not equals to "Gender", false otherwise
     */
    private boolean isGenderValid(String gender) {

        return !gender.equals("Gender");
    }

    /**
     * Determine whether the user has entered anything into the feet field
     * @param feet - feet as a string
     * @return true if feet is not equal to "ft", false otherwise
     */
    private boolean isFeetValid(String feet) {
        return !feet.equals("ft");
    }

    /**
     * Determine whether the user has entered anything into the inches field
     * @param inches - inches as a string
     * @return true if inches is not equal to "in", false otherwise
     */
    private boolean isInchesValid(String inches) {
        return !inches.equals("in");
    }

    /**
     * Checks to see if the stone field is valid, i.e. that a number was entere
     * @param stone - the stone measurement as a string (must be a number)
     * @return true if valid, false otherwise
     */
    private boolean isStoneValid(String stone) {

        for(int i = 0; i < stone.length(); i++) {
            if(stone.charAt(i) > '9' || stone.charAt(i) < '0') {
                return false;
            }
        }

        return Integer.parseInt(stone) <= 50;

    }

    /**
     * Determine whether user has entered anything in the pounds field
     * @param pounds - pounds as a string, set field from spinner
     * @return true if not equal to "lbs", false otherwise
     */
    private boolean isPoundsValid(String pounds) {
        return !pounds.equals("lbs");
    }

    /**
     * Uses regular expression to determine if a username is valid. It is allowed
     * the letters 0-9, the letters A-Z (lowercase and uppercase), underscore,
     * period, and dash
     * @param username - the username
     * @return true if username matches regex, false otherwise
     */
    private boolean isUsernameValid(String username) {

        String regex = "[a-zA-Z0-9._\\-]{3,}";

        return username.matches(regex);

    }

    /**
     * Determine whether password entered is valid. If password is
     * shorter than 5 characters, or if the two password fields
     * don't equal one another, return false
     * @param password - the password as a string
     * @param confirm - the confirm password field
     * @return true if valid, false otherwise
     */
    private boolean isPasswordValid(String password, String confirm) {
        return password.length() >= 5 && password.equals(confirm);
    }

    /**
     * Create text fields from the corresponding ids in the layout
     */
    private void createTextFields() {

        firstNameView = (EditText)findViewById(R.id.sign_up_fname);
        lastNameView = (EditText)findViewById(R.id.sign_up_lname);
        dobView = (EditText)findViewById(R.id.sign_up_dob);
        stoneView = (EditText)findViewById(R.id.sign_up_stone);
        usernameView = (EditText)findViewById(R.id.sign_up_username);
        passwordView = (EditText)findViewById(R.id.sign_up_password);
        confirmView = (EditText)findViewById(R.id.sign_up_confirm);

    }

    /**
     * Create spinners from the corresponding ids in the layout. Set the spinner listners
     */
    private void createSpinners() {

        // create adapters
        ArrayAdapter<CharSequence> genderAdapter = createSpinnerAdapter(R.array.gender_array);
        ArrayAdapter<CharSequence> inchesAdapter = createSpinnerAdapter(R.array.inches_array);
        ArrayAdapter<CharSequence> feetAdapter = createSpinnerAdapter(R.array.feet_array);
        ArrayAdapter<CharSequence> poundsAdapter = createSpinnerAdapter(R.array.pounds_array);

        // create spinners according to id in XML file
        gender = (Spinner)findViewById(R.id.spinner_gender);
        feet = (Spinner)findViewById(R.id.spinner_height_feet);
        inches = (Spinner)findViewById(R.id.spinner_height_inches);
        pounds = (Spinner)findViewById(R.id.spinner_weight_pounds);

        // set spinners
        setSpinner(gender, genderAdapter, "Gender");
        setSpinner(feet, feetAdapter, "ft");
        setSpinner(inches, inchesAdapter, "in");
        setSpinner(pounds, poundsAdapter, "lbs");

    }

    /**
     * Sets which spinner field is selected (first field), and what happens when a spiner
     * is selected
     * @param spinner - the spinner
     * @param adapter - the adapter is used to set the spinner's fields
     * @param prompt - the prompt that appears when nothing is selected
     */
    private void setSpinner(Spinner spinner, SpinnerAdapter adapter, String prompt) {

        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        selectSpinner(spinner, prompt);

    }

    /**
     *
     * @param id - the id referenced in the layout
     * @return the adapter containing the dropdown for that spinner
     */
    private ArrayAdapter<CharSequence> createSpinnerAdapter(int id) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    /**
     * Set the text for when the spinner is selected
     * @param spinner - the spinner, whose id is accessed
     * @param prompt - the prompt for the spinner if nothing selected
     */
    public void selectSpinner(final Spinner spinner, final String prompt) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                switch(spinner.getId()) {
                    case R.id.spinner_gender:
                        genderView.setText(selected.toString());
                        gender.setBackgroundResource(R.drawable.login_rounded);
                        break;
                    case R.id.spinner_height_feet:
                        feetView.setText(selected.toString());
                        feet.setBackgroundResource(R.drawable.login_rounded);
                        break;
                    case R.id.spinner_height_inches:
                        inchesView.setText(selected.toString());
                        inches.setBackgroundResource(R.drawable.login_rounded);
                        break;
                    case R.id.spinner_weight_pounds:
                        poundsView.setText(selected.toString());
                        pounds.setBackgroundResource(R.drawable.login_rounded);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setPrompt(prompt);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // do something
            System.out.println("BACK BUTTON PRESSED");
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode, event);
    }

}
