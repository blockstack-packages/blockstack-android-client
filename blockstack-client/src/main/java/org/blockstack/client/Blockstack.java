package org.blockstack.client;

import android.support.annotation.NonNull;

/**
 * The Blockstack <code>class</code> provides methods to interact with the blockstack-server.
 * Copy
 *
 * @author  Jorge Tapia (@itsProf)
 * @version 1.0
 */
public class Blockstack {
    private static String mAppId;
    private static String mAppSecret;

    /**
     * Prevents default class instantiation.
     */
    private Blockstack() {}

    /**
     * Initializes the Blockstack client for Android.
     *
     * @param appId the app id obtained from the Onename API.
     * @param appSecret the app secret obtained from the Onename API.
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
}
