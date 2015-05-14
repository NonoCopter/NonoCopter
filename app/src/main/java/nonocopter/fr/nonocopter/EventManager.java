package nonocopter.fr.nonocopter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class EventManager extends BroadcastReceiver {
    public EventManager() {
    }

    @Override
    public void onReceive(Context c, Intent intent) {
        String action = intent.getAction();

        switch ( action ){
            case "android.net.conn.CONNECTIVITY_CHANGE" :
                WifiManager manager = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State state = networkInfo.getState();

                if( state == NetworkInfo.State.CONNECTED){
                    System.out.println( "Connected");
                }
                if ( state == NetworkInfo.State.CONNECTING){
                    System.out.println( "Connecting");
                }
                if ( state == NetworkInfo.State.DISCONNECTING){
                    System.out.println( "Disconnecting");
                }
                if ( state == NetworkInfo.State.DISCONNECTED){
                    System.out.println( "Disconnected");
                }
                break;
        }
    }
}
