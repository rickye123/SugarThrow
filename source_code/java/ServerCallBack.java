package com.example.richa.sugarthrow;

/*
Interface sed to wait for the server to respond (callback function)
 */

interface ServerCallBack {

    /**
     * The server call back method onSuccess waits for when
     * the server returns the required response
     * @param result - the String response
     */
    void onSuccess(String result);


}
