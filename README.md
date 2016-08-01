# blockstack-android-client
Android client for blockstack-server.

### Installation

Installation is done through [Gradle](https://gradle.org/), a modern open source polyglot build automation system built into Android Studio. Visit their site for more information about [Gradle](https://gradle.org/).

Add `blockstack-client` to your app module `gradle.build` file `dependencies` section:

```Java
dependencies {
  compile 'org.blockstack.client:blockstack-client:0.0.13'
}
```

The Blockstack client is now ready to be used.

### Usage Example

Blockstack client methods are not asynchronous. This provides developers the flexibility to implement the appropriate mechanism to make method calls based on their apps' needs. You can call Blockstack methods using `AsyncTask` objects, services or even with classic `Thread` and `Runnable` objects. It all depends on what you are building.

This is how to perform a user lookup using a basic `AsyncTask` inside of an `Activity`:

```Java
final Blockstack blockstack = new Blockstack("YOUR_APP_ID", "YOUR_APP_SECRET");

new AsyncTask<Void, Void, String>() {
    protected String doInBackground(Void... params) {
        return blockstack.lookupUsers(new String[] {"itsProf"});
    }
    protected void onPostExecute(String response) {
        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
    }
}.execute();
```

A sample Android application is included in the sources to demonstrate client usage and interaction with Android UI.
