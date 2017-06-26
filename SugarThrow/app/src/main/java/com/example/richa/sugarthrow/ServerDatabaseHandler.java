package com.example.richa.sugarthrow;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ServerDatabaseHandler {

    private String TAG = "ServerDatabaseHandler";
    private Context context;
    private ContentValues contents = new ContentValues();

    ServerDatabaseHandler(Context context) {
        this.context = context;
    }

    public ContentValues getContents() {
        return contents;
    }

    void insert(String url, final Map<String, String> params, final ServerCallBack callback) {

        StringRequest insertRequest = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response " + response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error: Cannot sync at this time", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error message: " + error.getMessage() + "," + error.toString());
                if(error.getMessage() == null) {
                    Toast.makeText(context, "Volley Error", Toast.LENGTH_SHORT).show();
                }
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(context, "Network time out error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Authentication Failure error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(context, "Parse error", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Error: Cannot sync at this time", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response.headers == null) {
                    response = new NetworkResponse(
                            response.statusCode,
                            response.data,
                            Collections.<String, String>emptyMap(),
                            response.notModified,
                            response.networkTimeMs);
                }
                return super.parseNetworkResponse(response);
            }
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(insertRequest);

    }

    void update(String url, final Map<String, String> params, final ServerCallBack callback) {

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest updateRequest = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error message: " + error.getMessage() + "," + error.toString());
                if(error.getMessage() == null) {
                    Toast.makeText(context, "Error: Cannot sync at this time", Toast.LENGTH_SHORT).show();
                }
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(context, "Network time out error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Authentication Failure error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(context, "Parse error", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Error: Cannot sync at this time", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response.headers == null) {
                    response = new NetworkResponse(
                            response.statusCode,
                            response.data,
                            Collections.<String, String>emptyMap(),
                            response.notModified,
                            response.networkTimeMs);
                }
                return super.parseNetworkResponse(response);
            }
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };

        queue.add(updateRequest);

    }

    void select(String url, final Map<String, String> params, final String tableName,
                final ServerCallBack callback) {

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest selectRequest = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                try {

                    String id = getTableIdentifier(tableName);
                    JSONArray hits = (JSONArray) new JSONTokener(response).nextValue();
                    // empty search results, create no search entries layout
                    if (hits.getJSONObject(0).getString(id).equals("null")) {
                        contents = null;
                    } else {
                        for (int i = 0; i < hits.length(); i++) {
                            JSONObject searchResults = hits.getJSONObject(i);
                            contents = getTableContents(tableName, searchResults);
                        }
                    }
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error: Cannot sync at this time", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error message: " + error.getMessage() + "," + error.toString());
                if(error.getMessage() == null) {
                    Toast.makeText(context, "Error: Cannot sync at this time", Toast.LENGTH_SHORT).show();
                }
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(context, "Network time out error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Authentication Failure error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(context, "Parse error", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Error: Cannot sync at this time", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response.headers == null) {
                    response = new NetworkResponse(
                            response.statusCode,
                            response.data,
                            Collections.<String, String>emptyMap(),
                            response.notModified,
                            response.networkTimeMs);
                }
                return super.parseNetworkResponse(response);
            }
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };

        queue.add(selectRequest);

    }

    private ContentValues getUserContents(JSONObject searchResults) throws JSONException {

        ContentValues values = new ContentValues();

        values.put("userId", searchResults.getString("userId"));
        values.put("userName", searchResults.getString("userName"));
        values.put("name", searchResults.getString("name"));
        values.put("gender", searchResults.getString("gender"));
        values.put("dob", searchResults.getString("dob"));
        values.put("height", searchResults.getString("height"));
        values.put("weight", searchResults.getString("weight"));
        values.put("password", searchResults.getString("password"));
        values.put("points", searchResults.getString("points"));

        return values;
    }

    private ContentValues getFoodContents(JSONObject searchResults) throws JSONException {

        ContentValues values = new ContentValues();

        values.put("foodId", searchResults.getString("foodId"));
        values.put("name", searchResults.getString("name"));
        values.put("calories", searchResults.getString("calories"));
        values.put("sugar", searchResults.getString("sugar"));
        values.put("fat", searchResults.getString("fat"));
        values.put("saturates", searchResults.getString("saturates"));
        values.put("carbs", searchResults.getString("carbs"));
        values.put("salt", searchResults.getString("salt"));
        values.put("protein", searchResults.getString("protein"));

        return values;
    }

    private ContentValues getContentContents(JSONObject searchResults) throws JSONException {

        ContentValues values = new ContentValues();

        values.put("contentId", searchResults.getString("contentId"));
        values.put("userId", searchResults.getString("userId"));
        values.put("theDate", searchResults.getString("theDate"));
        values.put("calories", searchResults.getString("calories"));
        values.put("sugar", searchResults.getString("sugar"));
        values.put("fat", searchResults.getString("fat"));
        values.put("saturates", searchResults.getString("saturates"));
        values.put("carbs", searchResults.getString("carbs"));
        values.put("salt", searchResults.getString("salt"));
        values.put("protein", searchResults.getString("protein"));

        return values;
    }

    private ContentValues getDiaryContents(JSONObject searchResults) throws JSONException {

        ContentValues values = new ContentValues();

        values.put("diaryId", searchResults.getString("diaryId"));
        values.put("contentId", searchResults.getString("contentId"));
        values.put("userId", searchResults.getString("userId"));
        values.put("foodId", searchResults.getString("foodId"));

        return values;
    }

    Map<String, String> setUserParams(String username) {

        Map<String, String> params = new HashMap<>();
        params.put("identifier", "userId");
        params.put("table", "Users");
        params.put("row", "userName");
        params.put("value", username);

        return params;
    }

    Map<String, String> setContentParams(String userId, String theDate) {
        Map<String, String> params = new HashMap<>();
        params.put("identifier", "contentId");
        params.put("table", "Contents");
        params.put("row", "userId");
        params.put("value", userId);
        params.put("other", "theDate");
        params.put("otherVal", theDate);

        return params;
    }

    Map<String, String> setUserContents(List<List<String>> userInfo) {

        Map<String, String> userContents = new HashMap<>();
        userContents.put("table", "Users");
        userContents.put("userId", userInfo.get(0).get(0));
        userContents.put("userName", userInfo.get(0).get(1));
        userContents.put("name", userInfo.get(0).get(2));
        userContents.put("gender", userInfo.get(0).get(3));
        userContents.put("dob", userInfo.get(0).get(4));
        userContents.put("height", userInfo.get(0).get(5));
        userContents.put("weight", userInfo.get(0).get(6));
        userContents.put("password", userInfo.get(0).get(7));
        userContents.put("points", userInfo.get(0).get(8));

        return userContents;

    }

    Map<String, String> setContentContents(List<List<String>> contentInfo,
                                                  String userId, String theDate) {

        Map<String, String> contentContents = new HashMap<>();
        contentContents.put("table", "Contents");
        contentContents.put("userId", userId);
        contentContents.put("theDate", theDate);
        contentContents.put("sugar", contentInfo.get(0).get(0));
        contentContents.put("calories", contentInfo.get(0).get(1));
        contentContents.put("fat", contentInfo.get(0).get(2));
        contentContents.put("saturates", contentInfo.get(0).get(3));
        contentContents.put("carbs", contentInfo.get(0).get(4));
        contentContents.put("salt", contentInfo.get(0).get(5));
        contentContents.put("protein", contentInfo.get(0).get(6));

        return contentContents;

    }

    private String getTableIdentifier(String tableName) {

        if(tableName.equals("Users")) {
            return "userId";
        }
        if(tableName.equals("Food")) {
            return "foodId";
        }
        if(tableName.equals("Contents")) {
            return "contentId";
        }
        else {
            return "diaryId";
        }
    }



    private ContentValues getTableContents(String tableName, JSONObject searchResults) throws JSONException {
        if(tableName.equals("Users")) {
            return getUserContents(searchResults);
        }
        if(tableName.equals("Food")) {
            return getFoodContents(searchResults);
        }
        if(tableName.equals("Contents")) {
            return getContentContents(searchResults);
        }
        else {
            return getDiaryContents(searchResults);
        }
    }

}
