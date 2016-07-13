package org.blockstack.blockstackdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    private void setupUI() {
        Button lookupButton = (Button) findViewById(R.id.lookupButton);
        lookupButton.setOnClickListener(this);
    }

    private void startLookupActivity() {
        Intent intent = new Intent(this, LookupActivity.class);
        startActivity(intent);
    }

    // region On click listener
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.lookupButton:
                startLookupActivity();
                break;
            default:
                break;
        }
    }
    // endregion
}
