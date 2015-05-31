package nonocopter.fr.nonocopter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Pilot extends Activity {

    Navigator nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilot);
        registerReceiver( onBroadcastReceived, new IntentFilter( "EVENT_DISCONNECTED"));
        registerReceiver( onBroadcastReceived, new IntentFilter( "EVENT_CONNECTED"));
        ImageButton btnEmergency = ( ImageButton) findViewById( R.id.btnEmergency);
        ImageButton btnConnect   = ( ImageButton) findViewById( R.id.btnConnect);
        ImageButton btnVideo     = ( ImageButton) findViewById( R.id.btnVideo);
        ImageButton btnPhoto     = ( ImageButton) findViewById( R.id.btnPhoto);
        ImageButton btnMoveUp    = ( ImageButton) findViewById( R.id.btnMoveUp);
        ImageButton btnMoveDown  = ( ImageButton) findViewById( R.id.btnMoveDown);
        ImageButton btnMoveLeft  = ( ImageButton) findViewById( R.id.btnMoveLeft);
        ImageButton btnMoveRight = ( ImageButton) findViewById( R.id.btnMoveRight);
        ImageButton btnNavigate  = ( ImageButton) findViewById( R.id.btnNavigate);
        VideoView videoView      = ( VideoView)   findViewById( R.id.videoView);
        setButtonColor( R.id.btnEmergency, Color.RED);
        setButtonColor(R.id.btnConnect, Color.RED);
        btnEmergency.setOnClickListener(onClickListener);
        btnConnect  .setOnClickListener(onClickListener);
        btnVideo    .setOnClickListener(onClickListener);
        btnPhoto    .setOnClickListener(onClickListener);
        btnMoveUp   .setOnTouchListener(onTouchListener);
        btnMoveDown .setOnTouchListener(onTouchListener);
        btnMoveLeft .setOnTouchListener(onTouchListener);
        btnMoveRight.setOnTouchListener(onTouchListener);
        btnNavigate .setOnTouchListener(onTouchListener);
        videoView   .setOnTouchListener(onTouchListener);
        if ( new ConnexionManager().isConnectedToCopter(this)) showConnected();
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int e = event.getAction();
            switch( v.getId()) {
                case R.id.videoView:
                    PlayVideo();
                    break;
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

    public void moveDrone( String action){ // TODO
        switch( action.toLowerCase()){
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

    private void PlayVideo() { // TODO
        final VideoView videoView = (VideoView) findViewById(R.id.videoView);
        try {
            Uri video = Uri.parse( "http://192.168.0.11:8160" );
            videoView.setVideoURI(video);
            videoView.requestFocus();
            Toast.makeText(this, getString(R.string.stream_loading), Toast.LENGTH_LONG).show();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                public void onPrepared(MediaPlayer mp)
                {
                    videoView.start();
                }
            });
        }
        catch(Exception e) {
            Toast.makeText(this, getString(R.string.stream_error), Toast.LENGTH_LONG).show();
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
        setButtonColor( R.id.btnConnect, Color.GREEN);
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

    public void setButtonColor( int btnId, int color) {
        ImageButton btn = ( ImageButton) findViewById( btnId);
        btn.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
