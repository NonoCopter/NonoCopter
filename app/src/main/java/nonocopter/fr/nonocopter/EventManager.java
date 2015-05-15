package nonocopter.fr.nonocopter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class EventManager extends BroadcastReceiver {
    public EventManager() {
    }

    @Override
    public void onReceive(Context c, Intent intent) {
        switch ( intent.getAction() ){
            case "android.net.conn.CONNECTIVITY_CHANGE" :
                NetworkInfo networkInfo = intent.getParcelableExtra( WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State state = networkInfo.getState();
                if ( state == NetworkInfo.State.CONNECTED && ConnexionManager.isOnGoodWifi( c) ) {
                    c.sendBroadcast(new Intent("EVENT_CONNECTED"));
                }
                if ( state == NetworkInfo.State.DISCONNECTED ){
                    c.sendBroadcast(new Intent( "EVENT_DISCONNECTED"));
                }
                break;
        }
    }
}
