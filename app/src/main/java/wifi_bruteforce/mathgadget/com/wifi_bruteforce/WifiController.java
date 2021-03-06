package wifi_bruteforce.mathgadget.com.wifi_bruteforce;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernan Programer on 23/12/2017.
 */

public class WifiController {
    private WifiManager wifiManager;
    private RecyclerView myList;
    private Context Cont;
    private Adaptador_Wifi Adapter;
    private FragmentManager Fr;
    protected int Position = -1;
    protected ArrayList<Wifi_List_Model> WifiInfo;
    private WifiConfiguration Wifi;
    private boolean Conectada = false;

    public WifiController(Context Cont, RecyclerView myList, FragmentManager Fr) {
        this.Cont = Cont;
        this.wifiManager = (WifiManager) Cont.getSystemService(Context.WIFI_SERVICE);
        this.myList = myList;
        this.Fr = Fr;
        Wifi();
    }

    public void Wifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        this.wifiManager.startScan();
        List<ScanResult> scanResults = this.wifiManager.getScanResults();
        WifiInfo = new ArrayList<>();
        for (ScanResult Scan : scanResults) {
            WifiInfo.add(new Wifi_List_Model(Scan.SSID, Scan.BSSID, Scan.level + "", Scan.capabilities));
        }
        myList.setLayoutManager(new LinearLayoutManager(Cont));
        Adapter = new Adaptador_Wifi(WifiInfo);
        Adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiManager.reconnect();
                Position = myList.getChildAdapterPosition(view);
                if (Integer.parseInt(WifiInfo.get(Position).getsLevel()) >= -75) {
                    if(wifiManager.getConnectionInfo().getSSID().toString().equals(String.format("\"%s\"",WifiInfo.get(Position).getsNombre()))) Toast.makeText(Cont, "La Red Seleccionada Ya Esta Conectada", Toast.LENGTH_LONG).show();
                    else new Dialog_Wifi(WifiInfo.get(Position).getsNombre()).show(Fr, "Dialog");
                } else {
                    Toast.makeText(Cont, "La Intensidad De La Señal Es Muy Baja Refresque La Lista", Toast.LENGTH_LONG).show();
                }
            }
        });
        myList.setAdapter(Adapter);
    }

    protected void Connect(final String networkPass, String networkSSID) {
        Wifi = new WifiConfiguration();
        Wifi.status = WifiConfiguration.Status.ENABLED;
        Wifi.priority = 40;
        Wifi.SSID = String.format("\"%s\"", networkSSID);
        Wifi.preSharedKey = String.format("\"%s\"", networkPass);
        Log.i("Nombre", Wifi.SSID);
        wifiManager.addNetwork(Wifi);
        wifiManager.disconnect();
        wifiManager.enableNetwork(Wifi.networkId, true);
        wifiManager.reconnect();
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) Cont.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting() && netInfo.getType() != ConnectivityManager.TYPE_MOBILE);
    }

    public void Remove() {
        wifiManager.removeNetwork(Wifi.networkId);
    }

    public void Reconnect() {
        wifiManager.setWifiEnabled(false);
        wifiManager.setWifiEnabled(true);
    }
    public void DisableNetwork(){
        if(isOnline()){
            wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
            Conectada = true;
        }
    }
    public void EnableNetwork(){
        if(Conectada) wifiManager.enableNetwork(wifiManager.getConnectionInfo().getNetworkId(), true);
    }

}
