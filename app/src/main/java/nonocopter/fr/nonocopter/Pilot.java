package nonocopter.fr.nonocopter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class Pilot extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilot);
        ImageButton btnEmergency = (ImageButton) findViewById(R.id.btnEmergency);
        ImageButton btnConnect   = (ImageButton) findViewById(R.id.btnConnect);
        ImageButton btnVideo     = (ImageButton) findViewById(R.id.btnVideo);
        ImageButton btnPhoto     = (ImageButton) findViewById(R.id.btnPhoto);
        setButtonColor(btnEmergency, Color.RED);
        setButtonColor( btnConnect,   Color.RED);

        btnConnect.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                startConnexion();
            }
        });
    }

    public void startConnexion() { Toast.makeText( this, "Coucou", Toast.LENGTH_LONG).show();
        /*final ProgressDialog progress = ProgressDialog.show( Connexion.this, "Connexion", "Connexion au NonoCopter...", true, false);

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return ConnexionManager.connectToCopter( getApplicationContext());
            }

            @Override
            protected void onPostExecute( Boolean connected) {
                super.onPostExecute(connected);
                progress.dismiss();
                String text = connected ? "Connecté" : "Non connecté";
                Toast.makeText(Connexion.this, text, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);*/
    }

    public void setButtonColor( ImageButton btn, int color) {
        btn.getDrawable().setColorFilter( color, PorterDuff.Mode.SRC_IN);
    }
}
