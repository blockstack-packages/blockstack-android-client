package org.blockstack.client;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The Blockstack <code>class</code> provides Android apps with methods to interact
 * with the blockstack-server.
 *
 * @author  Jorge Tapia (@itsProf)
 * @version 1.0
 */
// TODO: implement missing operations
public class Blockstack {
    private static final String TAG = Blockstack.class.getSimpleName();
    private static String mAppId;
    private static String mAppSecret;

    /**
     * Prevents default class instantiation.
     */
    private Blockstack() {}

    /**
     * Initializes the Blockstack client for Android.
     *
     * @param appId app id obtained from <a href="https://api.onename.com">Onename API</a>.
     * @param appSecret app secret obtained from <a href="https://api.onename.com">Onename API</a>.
     */
    public static void initialize(@NonNull String appId, @NonNull String appSecret) {
        mAppId = appId;
        mAppSecret = appSecret;
    }

    /**
     * Determines if the app id and app secret are set and valid.
     *
     * @return a boolean value indicating wether the client is valid or not.
     */
    private static boolean isValid() {
        return mAppId != null && mAppSecret != null;
    }

    // region User operations
    public static JSONObject lookup(@NonNull String[] users) {
        if (isValid()) {
            String lookupUsers = TextUtils.join(",", users).replaceAll(" ", "").trim();
            String lookupUrl = String.format("%s/%s", Endpoints.USERS, lookupUsers);

            return call(lookupUrl);
        } else {
            Log.e(TAG, "Client is not valid. Did you forget to initialize the client?");
            return null;
        }
    }
    // endregion

    // region Networking
    /**
     * Calls a blockstack-server endpoint.
     *
     * @return a <code>JSONObject</code> with the blockstack-server response.
     */
    private static JSONObject call(@NonNull String endpointUrl) {
        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(endpointUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            StringBuilder stringBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return new JSONObject(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
    // TODO: implement POST
    // endregion
}
