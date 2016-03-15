package br.ufrj.nce.ubicomp.ecodifcoap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import br.ufrj.nce.ubicomp.sensors.Orientation;
import br.ufrj.nce.ubicomp.utils.ConnectionDetector;
import br.ufrj.nce.ubicomp.utils.Definitions;
import br.ufrj.nce.ubicomp.utils.NewActivity;

public class OrientationActivity extends NewActivity {

    private TextView xResultTV, yResultTV, zResultTV;
    private EditText xhttpUrlET, xdataStreamET, yhttpUrlET, ydataStreamET,
            zhttpUrlET, zdataStreamET, intervalET;

    private SharedPreferences app = null;

    private StrictMode.ThreadPolicy policy;

    private MyReceiver myReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onNewCreate(savedInstanceState);

        policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_orientation);

        xhttpUrlET = (EditText) findViewById(R.id.httpUrl_x_ET);
        xdataStreamET = (EditText) findViewById(R.id.datastream_x_ET);
        yhttpUrlET = (EditText) findViewById(R.id.httpUrl_y_ET);
        ydataStreamET = (EditText) findViewById(R.id.datastream_y_ET);
        zhttpUrlET = (EditText) findViewById(R.id.httpUrl_z_ET);
        zdataStreamET = (EditText) findViewById(R.id.datastream_z_ET);
        intervalET = (EditText) findViewById(R.id.intervalTimeET);
        xResultTV = (TextView) findViewById(R.id.xResultTV);
        yResultTV = (TextView) findViewById(R.id.yResultTV);
        zResultTV = (TextView) findViewById(R.id.zResultTV);

        app = PreferenceManager.getDefaultSharedPreferences(this);

        xhttpUrlET.setText(app.getString("Orientation_xhttpUrlET", ""),
                TextView.BufferType.EDITABLE);
        xdataStreamET.setText(app.getString("Orientation_xdataStreamET", ""),
                TextView.BufferType.EDITABLE);
        yhttpUrlET.setText(app.getString("Orientation_yhttpUrlET", ""),
                TextView.BufferType.EDITABLE);
        ydataStreamET.setText(app.getString("Orientation_ydataStreamET", ""),
                TextView.BufferType.EDITABLE);
        zhttpUrlET.setText(app.getString("Orientation_zhttpUrlET", ""),
                TextView.BufferType.EDITABLE);
        zdataStreamET.setText(app.getString("Orientation_zdataStreamET", ""),
                TextView.BufferType.EDITABLE);
        intervalET.setText(app.getString("Orientation_intervalET", ""),
                TextView.BufferType.EDITABLE);

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Definitions.ORIENTATION_TAG);
        registerReceiver(myReceiver, intentFilter);

    }

    @Override
    protected void onStop() {
        unregisterReceiver(myReceiver);
        super.onStop();
    }

    @Override
    public void onMessageReceived(Message msg) {
        // TODO Auto-generated method stub

    }

    public void onClickStartService(View v) {
        String temp1 = xhttpUrlET.getText().toString().trim();
        if (!temp1.startsWith("http://")) {
            temp1 = "http://" + temp1;
        }
        String temp2 = yhttpUrlET.getText().toString().trim();
        if (!temp2.startsWith("http://")) {
            temp2 = "http://" + temp2;
        }
        String temp3 = zhttpUrlET.getText().toString().trim();
        if (!temp3.startsWith("http://")) {
            temp3 = "http://" + temp3;
        }

        if ((ishttpUrlValid(temp1, "X"))
                && (ishttpUrlValid(temp2, "Y"))
                && (ishttpUrlValid(temp3, "Z"))
                && (isDataStreamValid(
                xdataStreamET.getText().toString().trim(), "X"))
                && (isDataStreamValid(
                ydataStreamET.getText().toString().trim(), "Y"))
                && (isDataStreamValid(
                zdataStreamET.getText().toString().trim(), "Z"))
                && (isIntervalValid(intervalET.getText().toString().trim()))) {

            SharedPreferences.Editor editor = app.edit();
            editor.putString("Orientation_xhttpUrlET", temp1);
            editor.putString("Orientation_yhttpUrlET", temp2);
            editor.putString("Orientation_zhttpUrlET", temp3);
            editor.putString("Orientation_xdataStreamET", xdataStreamET
                    .getText().toString().trim());
            editor.putString("Orientation_ydataStreamET", ydataStreamET
                    .getText().toString().trim());
            editor.putString("Orientation_zdataStreamET", zdataStreamET
                    .getText().toString().trim());
            editor.putString("Orientation_intervalET", intervalET.getText()
                    .toString().trim());
            editor.commit();
            ConnectionDetector connectionDetector = new ConnectionDetector(
                    getApplicationContext());
            if (connectionDetector.haveConnection()) {
                startService(new Intent(this, Orientation.class));
            } else {
                showAlertDialog("Servi�o n�o inicializado",
                        "N�o h� conectividade com a Internet.", false);
            }
        }
    }

    public void onClickStopService(View V) {
        stopService(new Intent(this, Orientation.class));
    }

    public void onClickSendMessage(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context content, Intent intent) {

            float datapassed1 = (float) intent.getDoubleExtra(
                    "Orientation_result1", 0.0);
            float datapassed2 = (float) intent.getDoubleExtra(
                    "Orientation_result2", 0.0);
            float datapassed3 = (float) intent.getDoubleExtra(
                    "Orientation_result3", 0.0);
            xResultTV.setText(String.valueOf(datapassed1) + "�");
            yResultTV.setText(String.valueOf(datapassed2) + "�");
            zResultTV.setText(String.valueOf(datapassed3) + "�");

        }

    }
}