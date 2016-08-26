package org.blockstack.client;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * The Blockstack <code>class</code> provides Android apps with methods to interact
 * with the blockstack-server.
 *
 * @author  Jorge Tapia (@itsProf)
 * @version 1.0
 * @see <a href="http://blockstack.org">Blockstack Website</a>
 */
public class Blockstack {
    private static final String TAG = Blockstack.class.getSimpleName();
    private String appId;
    private String appSecret;

    /**
     * Instantiates the Blockstack client for Android.
     *
     * @param appId app id obtained from <a href="https://api.onename.com">Onename API</a>.
     * @param appSecret app secret obtained from <a href="https://api.onename.com">Onename API</a>.
     */
    public Blockstack(@NonNull String appId, @NonNull String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }

    // region User operations
    /**
     * Looks up the data for one or more users by their usernames.
     *
     * @param usernames the usernames(s) to look up.
     * @return a Blockstack server response as a JSON <code>String</code>.
     */
    public String lookupUsers(@NonNull String[] usernames) {
        try {
            String lookupUsers = URLEncoder.encode(TextUtils.join(",", usernames).trim(), "UTF-8");
            String lookupUrl = String.format("%s/%s", Endpoints.USERS, lookupUsers);

            return executeGET(lookupUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Takes in a searchUsers query and returns a list of results that match the searchUsers.
     * The query is matched against +usernames, full names, and twitter handles by default.
     * It's also possible to explicitly searchUsers verified Twitter, Facebook, Github accounts,
     * and verified domains. This can be done by using searchUsers queries like twitter:itsProf,
     * facebook:g3lepage, github:shea256, domain:muneebali.com
     *
     * @param query the text to searchUsers for.
     * @return a Blockstack server response as a JSON <code>String</code>.
     */
    public String searchUsers(@NonNull String query) {
        String searchUrl = String.format("%s%s", Endpoints.SEARCH, URLEncoder.encode(query));
        return executeGET(searchUrl);
    }

    /**
     * Registers a username.
     *
     * @param username the username to be registered.
     * @param recipientAddress Bitcoin address of the new owner address.
     * @param profileData public key of the Bitcoin address that currently owns the username.
     * @return response with an object with a status that is either "success" or "error".
     */
    public String registerUser(@NonNull String username, @NonNull String recipientAddress,
                               JSONObject profileData) {
        JSONObject data = new JSONObject();

        try {
            data.put("username", username);
            data.put("recipient_address", recipientAddress);

            if (profileData != null) {
                data.put("profile", profileData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return executePOST(Endpoints.USERS, data);
    }

    /**
     * Updates a user.
     *
     * @param username the username to be updated.
     * @param profileData a <code>JSONObject</code> object with profile data that should be
     *                    associated with the username.
     * @param ownerPublicKey public key of the Bitcoin address that currently owns the username.
     * @return a response that could include an object with an unsigned transaction "unsigned_tx"
     *          in hex format.
     */
    public String updateUser(@NonNull String username, @NonNull JSONObject profileData,
                               @NonNull String ownerPublicKey) {
        String updateUrl = String.format("%s/%s/update", Endpoints.USERS, username);
        JSONObject data = new JSONObject();

        try {
            data.put("profile", profileData);
            data.put("owner_pubkey", ownerPublicKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return executePOST(updateUrl, data);
    }

    /**
     * Transfers a user to another Bitcoin address.
     *
     * @param username the username to be transferred.
     * @param transferAddress Bitcoin address of the new owner address.
     * @param ownerPublicKey public key of the Bitcoin address that currently owns the username.
     * @return a response that could include an object with an unsigned transaction "unsigned_tx"
     *          in hex format.
     */
    public String transferUser(@NonNull String username, @NonNull String transferAddress,
                         @NonNull String ownerPublicKey) {
        String transferUrl = String.format("%s/%s/update", Endpoints.USERS, username);
        JSONObject data = new JSONObject();

        try {
            data.put("transfer_address", transferAddress);
            data.put("owner_pubkey", ownerPublicKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return executePOST(transferUrl, data);
    }
    // endregion

    // region Transaction operations

    /**
     * Takes in a signed transaction (in hex format) and broadcasts it to the network.
     * If the transaction is successfully broadcast, the transaction hash is returned
     * in the response.
     *
     * @param signedTransaction a signed transaction in hex format.
     * @return a Blockstack server response as a JSON <code>String</code> with a status that is
     *          either "success" or "error".
     */
    public String broadcastTransaction(@NonNull String signedTransaction) {
        JSONObject data = new JSONObject();

        try {
            data.put("signed_hex", signedTransaction);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return executePOST(Endpoints.TRANSACTIONS, data);
    }
    // endregion

    // region Address operations
    /**
     * Retrieves the unspent outputs for a given address so they can be used
     * for building transactions.
     *
     * @param address the address to look up unspent outputs for.
     * @return a Blockstack server response as a JSON <code>String</code>.
     */
    public String getUnspentOutputs(@NonNull String address) {
        String unspentOutputsUrl = String.format("%s/%s/unspents", Endpoints.ADDRESSES, address);
        return executeGET(unspentOutputsUrl);
    }

    /**
     * Retrieves a list of names owned by the address provided.
     *
     * @param address the address to look up names owned by.
     * @return a Blockstack server response as a JSON <code>String</code>.
     */
    public String getNamesOwnedByAddress(@NonNull String address) {
        String namesOwnedUrl = String.format("%s/%s/names", Endpoints.ADDRESSES, address);
        return executeGET(namesOwnedUrl);
    }
    // endregion

    // region Domain operations
    /**
     * Retrieves a DKIM public key for given domain, using the
     * "blockchainid._domainkey" subdomain DNS record.
     *
     * @param domain the domain to look the DKIM public key up.
     * @return a Blockstack server response as a JSON <code>String</code>.
     */
    public String getDkimPublicKey(@NonNull String domain) {
        String dkimPublicKeyUrl = String.format("%s/%s/dkim", Endpoints.DOMAINS, domain);
        return executeGET(dkimPublicKeyUrl);
    }
    // endregion

    // region Networking
    /**
     * Calls a blockstack-server endpoint using the GET method.
     *
     * @param endpointUrl the Blockstack server endpoint URL.
     * @return a Blockstack server response as a JSON <code>String</code>.
     */
    private String executeGET(@NonNull String endpointUrl) {
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

            return stringBuilder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
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

    /**
     * Calls a blockstack-server endpoint using the POST method.
     *
     * @param endpointUrl the Blockstack server endpoint URL.
     * @param data the JSON data to send via POST.
     * @return a Blockstack server response as a JSON <code>String</code>.
     */
    private String executePOST(@NonNull String endpointUrl, @NonNull JSONObject data) {
        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        OutputStreamWriter writer = null;

        try {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");

            writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(data.toString());

            StringBuilder stringBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
            return  null;
        } catch (IOException e) {
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
    // endregion
}
