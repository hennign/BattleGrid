package com.test.battlegrid;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class settings extends ActionBarActivity {

    public boolean is_server = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(connector);

        CheckBox c = (CheckBox) findViewById(R.id.checkBox);
        c.setOnClickListener(server);
    }

    View.OnClickListener connector = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText ip = (EditText) findViewById(R.id.editText);
            Log.i("TEST", "Connect button clicked with value " + ip.getText().toString());
        }
    };

    View.OnClickListener server = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox c = (CheckBox) v;
            is_server = c.isChecked();

            if (is_server) {

                Log.i("TEST", "Set to server mode: true");
            } else {
                Log.i("TEST", "Set to server mode: false");
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
