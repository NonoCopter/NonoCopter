package nonocopter.fr.nonocopter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Pilot extends Activity {

    ImageButton btnEmergency;
    ImageButton btnConnect;
    ImageButton btnVideo;
    ImageButton btnPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilot);
        registerReceiver(onBroadcastReceived, new IntentFilter("EVENT_DISCONNECTED"));
        registerReceiver( onBroadcastReceived, new IntentFilter( "EVENT_CONNECTED"));
        btnEmergency = ( ImageButton) findViewById( R.id.btnEmergency);
        btnConnect   = ( ImageButton) findViewById( R.id.btnConnect);
        btnVideo     = ( ImageButton) findViewById( R.id.btnVideo);
        btnPhoto     = ( ImageButton) findViewById( R.id.btnPhoto);
        setButtonColor( btnEmergency, Color.RED);
        setButtonColor( btnConnect,   Color.RED);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startConnexion();
            }
        });

        btnEmergency.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                emergency();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver( onBroadcastReceived);
    }

    public void startConnexion() {
        final Context c = this;
        final ProgressDialog progress = ProgressDialog.show( this, getString( R.string.loadT_connecting), getString( R.string.load_connecting), true, false);
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return ConnexionManager.connectToCopter( c);
            }
            @Override
            protected void onPostExecute( Boolean connected) {
                super.onPostExecute(connected);
                progress.dismiss();
                if ( connected ) showConnected();
                else showDisconnected();
            }
        }.execute(null, null, null);
    }

    BroadcastReceiver onBroadcastReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            switch( intent.getAction() ){
                case "EVENT_CONNECTED"    : showConnected();    break;
                case "EVENT_DISCONNECTED" : showDisconnected(); break;
            }

        }
    };

    public void showConnected(){
        setButtonColor(btnConnect, Color.GREEN);
        Toast.makeText( this, getString(R.string.connected), Toast.LENGTH_LONG).show();
    }

    public void showDisconnected(){
        setButtonColor( btnConnect, Color.RED);
        Toast.makeText( this, getString(R.string.notConnected), Toast.LENGTH_LONG).show();
        if ( false ) emergency();
    }

    public void emergency(){
        Toast.makeText(this, getString(R.string.emergency), Toast.LENGTH_LONG).show();
    }

    public void setButtonColor( ImageButton btn, int color) {
        btn.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
