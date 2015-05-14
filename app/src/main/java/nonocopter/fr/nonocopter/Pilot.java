package nonocopter.fr.nonocopter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
        btnEmergency = (ImageButton) findViewById(R.id.btnEmergency);
        btnConnect   = (ImageButton) findViewById(R.id.btnConnect);
        btnVideo     = (ImageButton) findViewById(R.id.btnVideo);
        btnPhoto     = (ImageButton) findViewById(R.id.btnPhoto);
        setButtonColor( btnEmergency, Color.RED);
        setButtonColor( btnConnect,   Color.RED);

        btnConnect.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                startConnexion();
            }
        });
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
                setButtonColor( btnConnect, connected ? Color.GREEN : Color.RED);
                String text = connected ? getString(R.string.connected) : getString(R.string.notConnected);
                Toast.makeText( Pilot.this, text, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

    private void setButtonColor( ImageButton btn, int color) {
        btn.getDrawable().setColorFilter( color, PorterDuff.Mode.SRC_IN);
    }
}
