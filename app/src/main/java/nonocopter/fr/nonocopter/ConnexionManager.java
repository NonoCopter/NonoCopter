package nonocopter.fr.nonocopter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import java.util.Date;
import java.util.List;

public class ConnexionManager {

    final private static String COPTER_WIFI_NAME = "NonoCopter";
    final private static String COPTER_WIFI_PASS = "0123456789A";

    public static Boolean connectToCopter( final Context c){
        _lockWifi( c);
        if ( !_turnOnWifi( c, 5) ) return false;
        return _connectToWifi( c, _getCopterWifiId( c), 10);
    }

    private static void _lockWifi( Context c){
        WifiManager.WifiLock lock = _getWifiManager(c).createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "NonoCopterWifiLock");
        lock.acquire();
    }

    private static Integer _getCopterWifiId( Context c){
        WifiManager wm = _getWifiManager(c);
        List<WifiConfiguration> list = wm.getConfiguredNetworks();
        for( WifiConfiguration wifi : list ) {
            if( wifi.SSID != null && wifi.SSID.equals("\"" + COPTER_WIFI_NAME + "\"") ) {
                return wifi.networkId;
            }
        }
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID         = "\"" + COPTER_WIFI_NAME + "\"";
        wifiConfig.preSharedKey = "\""+ COPTER_WIFI_PASS +"\"";
        return wm.addNetwork( wifiConfig);
    }

    private static Boolean _connectToWifi( Context c, Integer wifiId, Integer timeout){
        Long start = new Date().getTime();
        _getWifiManager(c).enableNetwork( wifiId, true);
        while( !isConnectedToCopter( c) ){
            if ( ( new Date().getTime() - start) > timeout*1000 ) return false;
        }
        return true;
    }

    public static Boolean isConnectedToCopter( Context c){
        return isOnGoodWifi( c) && _isWifiConnected( c);
    }

    private static Boolean _isWifiEnabled( Context c){
        WifiManager wm = _getWifiManager(c);
        return ( wm != null && wm.isWifiEnabled());
    }

    private static Boolean _isWifiConnected( Context c){
        ConnectivityManager cm  = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return netInfoWifi.isConnected();
    }

    public static boolean isOnGoodWifi( Context c){
        return _getWifiInfo(c).getSSID().equals( "\"" + COPTER_WIFI_NAME + "\"");
    }

    private static WifiManager _getWifiManager( Context c){
        return ( WifiManager) c.getSystemService( Context.WIFI_SERVICE);
    }

    private static WifiInfo _getWifiInfo( Context c){
        return _getWifiManager(c).getConnectionInfo();
    }

    private static Boolean _turnOnWifi( Context c, int timeout){
        Long start = new Date().getTime();
        _getWifiManager( c).setWifiEnabled(true);
        while( !_isWifiEnabled( c) ){
            if ( ( new Date().getTime() - start) > timeout*1000 ) return false;
        }
        return true;
    }
}
