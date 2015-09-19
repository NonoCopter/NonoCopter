package nonocopter.fr.nonocopter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

public class Pilot extends Activity {

    Navigator nav;
    SocketIO socket;
    ProgressDialog progressCalibrage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilot);

        String[] items      = getResources().getStringArray(R.array.nav_drawer_items);

        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, items));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawers();
                switch ( position){
                    case 0 : startCalibration();  break;
                    case 1 : stopOrRestartNonoCopter( false); break;
                    case 2 : stopOrRestartNonoCopter(true);    break;
                }
            }
        });

        registerReceiver(onBroadcastReceived, new IntentFilter("EVENT_DISCONNECTED"));
        registerReceiver(onBroadcastReceived, new IntentFilter("EVENT_CONNECTED"));
        ImageButton btnEmergency = (ImageButton) findViewById(R.id.btnEmergency);
        ImageButton btnConnect   = (ImageButton) findViewById(R.id.btnConnect);
        ImageButton btnVideo     = (ImageButton) findViewById(R.id.btnVideo);
        ImageButton btnPhoto     = (ImageButton) findViewById(R.id.btnPhoto);
        ImageButton btnMoveUp    = (ImageButton) findViewById(R.id.btnMoveUp);
        ImageButton btnMoveDown  = (ImageButton) findViewById(R.id.btnMoveDown);
        ImageButton btnMoveLeft  = (ImageButton) findViewById(R.id.btnMoveLeft);
        ImageButton btnMoveRight = (ImageButton) findViewById(R.id.btnMoveRight);
        ImageButton btnNavigate  = (ImageButton) findViewById(R.id.btnNavigate);
        ImageButton btnCalib     = (ImageButton) findViewById(R.id.btnCalib);
        ImageButton btnDrawer    = (ImageButton) findViewById(R.id.btnDrawer);
        setButtonColor(R.id.btnEmergency, Color.RED);
        setButtonColor(R.id.btnConnect,   Color.RED);
        btnEmergency.setOnClickListener(onClickListener);
        btnConnect.setOnClickListener(onClickListener);
        btnVideo.setOnClickListener(onClickListener);
        btnPhoto.setOnClickListener(onClickListener);
        btnMoveUp.setOnTouchListener(onTouchListener);
        btnMoveDown.setOnTouchListener(onTouchListener);
        btnMoveLeft.setOnTouchListener(onTouchListener);
        btnMoveRight.setOnTouchListener(onTouchListener);
        btnNavigate.setOnTouchListener(onTouchListener);
        btnDrawer.setOnClickListener(onClickListener);
        //if (new ConnexionManager().isConnectedToCopter(this)) showConnected();
    }

    private void toast( String message){
        Toast.makeText(Pilot.this, message, Toast.LENGTH_LONG).show();
    }

    private void sendSocket( String event){
        sendSocket(event, null, null);
    }

    private void sendSocket( String event, JSONException data){
        sendSocket( event, data, null);
    }

    private void sendSocket( String event, IOAcknowledge ack){
        sendSocket( event, null, ack);
    }

    private void sendSocket( String event, JSONException data, IOAcknowledge ack){
        if ( socket == null ){
            try {
                socket = new SocketIO();
                socket.connect("http://192.168.0.11:8080/", new IOCallback() {
                    @Override
                    public void onMessage(JSONObject json, IOAcknowledge ack) {
                        try {
                            System.out.println("################Server said Json:" + json.toString(2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMessage(String data, IOAcknowledge ack) {
                        System.out.println("###################Server said String: " + data);
                    }

                    @Override
                    public void onError(SocketIOException socketIOException) {
                        System.out.println("###############an Error occured");
                        if ( progressCalibrage != null ) progressCalibrage.dismiss();
                        socketIOException.printStackTrace();
                    }

                    @Override
                    public void onDisconnect() {
                        System.out.println("#################Connection terminated.");
                    }

                    @Override
                    public void onConnect() {

                        Pilot.this.runOnUiThread( new Runnable(){
                            @Override
                            public void run() {
                                //toast( "Yahou");
                            }
                        });
                        System.out.println("##############Connection established");
                    }

                    @Override
                    public void on(String event, IOAcknowledge ack, Object... args) {
                        System.out.println("############Server triggered event '" + event + "'");
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace(); // Todo
            }
        }
        if ( socket != null) socket.emit( event, ack, data);
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int e = event.getAction();
            switch( v.getId()) {
                case R.id.btnMoveUp:
                    if ( e == MotionEvent.ACTION_DOWN || e == MotionEvent.ACTION_MOVE ) moveDrone( "up");
                    break;
                case R.id.btnMoveDown:
                    if ( e == MotionEvent.ACTION_DOWN || e == MotionEvent.ACTION_MOVE ) moveDrone( "down");
                    break;
                case R.id.btnMoveLeft:
                    if ( e == MotionEvent.ACTION_DOWN || e == MotionEvent.ACTION_MOVE ) moveDrone( "left");
                    break;
                case R.id.btnMoveRight:
                    if ( e == MotionEvent.ACTION_DOWN || e == MotionEvent.ACTION_MOVE ) moveDrone( "right");
                    break;
                case R.id.btnNavigate:
                    navigate( e);
                    break;
            }
            return true;
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick( final View v) {
            switch( v.getId()){
                case R.id.btnEmergency :
                    emergency();
                    break;
                case R.id.btnConnect:
                    startConnexion();
                    break;
                case R.id.btnPhoto:
                    takePhoto();
                    break;
                case R.id.btnCalib:
                    startCalibration();
                    break;
                case R.id.btnDrawer:
                    DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    mDrawerLayout.openDrawer( Gravity.START);
                    break;
            }
        }
    };

    public void navigate( int e){
        if ( nav == null ) nav = new Navigator( this);
        TextView txt = (TextView) findViewById( R.id.textView);

        if ( e == MotionEvent.ACTION_DOWN ) nav.start();
        if ( e == MotionEvent.ACTION_MOVE ) txt.setText( nav.navigate());
        if ( e == MotionEvent.ACTION_UP || e == MotionEvent.ACTION_CANCEL ) nav.stop();
    }

    public void moveDrone( String action) { // TODO
        switch (action.toLowerCase()) {
            case "up":
                Toast.makeText(this, "BTN UP", Toast.LENGTH_LONG).show();
                break;
            case "down":
                Toast.makeText(this, "btnMoveDown", Toast.LENGTH_LONG).show();
                break;
            case "left":
                Toast.makeText(this, "btnMoveLeft", Toast.LENGTH_LONG).show();
                break;
            case "right":
                Toast.makeText(this, "btnMoveRight", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onBroadcastReceived);
    }

    public void startConnexion() {
        final Context c = this;
        final ProgressDialog progress = ProgressDialog.show( this, getString( R.string.loadT_connecting), getString( R.string.load_connecting), true, false);
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return new ConnexionManager().connectToCopter(c);
            }
            @Override
            protected void onPostExecute( Boolean connected) {
                super.onPostExecute( connected);
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
        setButtonColor(R.id.btnConnect, Color.YELLOW);
        Toast.makeText(this, getString(R.string.connected), Toast.LENGTH_LONG).show();
    }

    public void showDisconnected(){
        setButtonColor( R.id.btnConnect, Color.RED);
        Toast.makeText(this, getString(R.string.notConnected), Toast.LENGTH_LONG).show();
        //emergency(); // TODO
    }

    public void emergency(){ // TODO
        Toast.makeText(this, getString(R.string.emergency), Toast.LENGTH_LONG).show();
    }

    public void takePhoto(){ // TODO
        Toast.makeText(this, getString(R.string.take_photo), Toast.LENGTH_LONG).show();
    }

    public void stopOrRestartNonoCopter( Boolean stop){ // TODO
        String txt;
        txt = stop ? "STOP" : "RESTART";
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }

    public void startCalibration(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ( which == DialogInterface.BUTTON_POSITIVE ){
                    progressCalibrage = ProgressDialog.show( Pilot.this, getString( R.string.calibrageT_load), getString( R.string.calibrage_load), true, false);
                    //final ProgressDialog progress = ProgressDialog.show( Pilot.this, getString( R.string.calibrageT_load), getString( R.string.calibrage_load), true, false);
                    IOAcknowledge callback = new IOAcknowledge() {
                        @Override
                        public void ack(final Object... objects) {
                            Pilot.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressCalibrage.dismiss();
                                    //progress.dismiss();
                                    if ( objects.length <= 0 ) return;
                                    switch ( (Integer) objects[0]) {
                                        case 0:
                                            toast( getString( R.string.calibrage_ko));
                                            break;
                                        case 1:
                                            toast( getString( R.string.calibrage_ok));
                                            break;
                                        case 2:
                                            toast( getString( R.string.calibrage_again));
                                            break;
                                        default:
                                    }
                                }
                            });
                        }
                    };
                    sendSocket( "startCalibration", callback);
                }
            }
        };
        AlertDialog.Builder yesNo = new AlertDialog.Builder( Pilot.this);
        yesNo.setMessage( getString( R.string.startCalibrage));
        yesNo.setPositiveButton(getString(R.string.yes), dialogClickListener);
        yesNo.setNegativeButton(getString(R.string.no),  dialogClickListener);
        yesNo.show();
    }

    public void setButtonColor( int btnId, int color) {
        ImageButton btn = ( ImageButton) findViewById( btnId);
        btn.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
