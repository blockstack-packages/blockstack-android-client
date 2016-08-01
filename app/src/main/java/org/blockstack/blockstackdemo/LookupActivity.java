package org.blockstack.blockstackdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.blockstack.client.Blockstack;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LookupActivity extends AppCompatActivity implements TextWatcher {
    private static final String TAG = LookupActivity.class.getSimpleName();

    EditText usersEditText;
    ImageButton searchButton;
    ListView listView;
    HashMap<String, String> data = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);
        setupUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        usersEditText = (EditText) findViewById(R.id.usersEditText);
        usersEditText.addTextChangedListener(this);

        searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setEnabled(false);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] users = usersEditText.getText().toString().split(",");
                lookup(users);
            }
        });

        listView = (ListView) findViewById(R.id.userListView);
    }

    private void dismissKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(usersEditText.getWindowToken(), 0);
    }

    // region Blockstack
    private void lookup(@NonNull  String[] users) {
        LookupTask task = new LookupTask(this);
        task.executeOnExecutor(LookupTask.THREAD_POOL_EXECUTOR, users);
    }
    // endregion

    // region Text watcher
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        if (usersEditText.getText().length() > 0) {
            searchButton.setAlpha(1.0f);
            searchButton.setEnabled(true);
        } else {
            searchButton.setAlpha(0.5f);
            searchButton.setEnabled(false);
        }
    }
    // endregion

    // region Lookup task
    private class LookupTask extends AsyncTask<String[], Void, String> {
        Blockstack client = new Blockstack("YOUR_APP_ID", "YOUR_APP_SECRET");
        Context context;
        ProgressDialog dialog;
        String[] users;

        public LookupTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            LookupActivity.this.dismissKeyboard();

            searchButton.setAlpha(0.5f);
            searchButton.setEnabled(false);

            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Looking up users...");
            dialog.show();

            data.clear();
        }

        @Override
        protected String doInBackground(String[]... params) {
            users = TextUtils.join(",", params[0]).replaceAll(" ", "").split(",");
            return client.lookupUsers(users);
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject json = new JSONObject(response);

                    for (String user: users) {
                        JSONObject jsonUser = json.getJSONObject(user);

                        if (jsonUser.has("error")) {
                            continue;
                        }

                        data.put(user, jsonUser.toString());
                    }

                    if (data.keySet().toArray().length > 0) {
                        String[] keys = data.keySet().toArray(new String[data.keySet().size()]);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_list_item_1, keys);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i,
                                                    long l) {
                                String username = adapterView.getItemAtPosition(i).toString();
                                Toast.makeText(LookupActivity.this, data.get(username),
                                        Toast.LENGTH_LONG).show();

                            }
                        });
                    } else {
                        Toast.makeText(LookupActivity.this, "No users matching " + "\""
                                + usersEditText.getText() + "\" were found.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(LookupActivity.this, "There was error executing your request...",
                        Toast.LENGTH_LONG).show();
            }

            searchButton.setAlpha(1.0f);
            searchButton.setEnabled(true);

            dialog.cancel();
        }
    }
    // endregion
}
