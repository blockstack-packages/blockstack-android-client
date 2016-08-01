package org.blockstack.client;

/**
 * The Endpoints <code>class</code> defines all valid blockstack-server endpoints.
 *
 * @author  Jorge Tapia (@itsProf)
 * @version 1.0
 */
// TODO: add missing endpoints
public class Endpoints {
    // region User endpoints
    public static final String USERS = "https://api.onename.com/v1/users";
    public static final String SEARCH = "https://api.onename.com/v1/search?query=";
    // endregion

    // region Transaction endpoints
    public static final String TRANSACTIONS = "https://api.onename.com/v1/transactions";
    // endregion

    // region Address endpoints
    public static final String ADDRESSES = "https://api.onename.com/v1/addresses";
    // endregion

    // region Domain endpoints
    public static final String DOMAINS = "https://api.onename.com/v1/domains";
    // endregion
}
