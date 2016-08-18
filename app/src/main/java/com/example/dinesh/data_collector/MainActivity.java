package com.example.dinesh.data_collector;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public TextView text,battery;
    public Button start, stop, restart;
    public double lat;
    public double lon;
    public double acc;
    public String cellid;
    public int rssi=0;
    public String operatorName;
    private LocationManager locationManager;
    public LocationManager GPSmgr, GSMmgr;
    public Location s;
    public int flagstrt = 0, flagstop = 0, flagrestrt = 0;
    public TelephonyManager tm;
    public MyPhoneStateListener MyListener;
    public String sfile="";
    public EditText fname;
    public File file;
//    Intent batteryStatus;
//    IntentFilter ifilter;
//    float ich=0 , ech=0;
    //public SignalStrength ss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        restart = (Button) findViewById(R.id.restart);
        fname = (EditText) findViewById(R.id.fname);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        restart.setOnClickListener(this);

//        battery = (TextView) findViewById(R.id.battery);
//        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        batteryStatus = getApplicationContext().registerReceiver(null, ifilter);


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            return;
//        }
        //GPSmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, GPSlistener);




    }






    LocationListener GPSlistener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {

            // TODO Auto-generated method stub
            lat = location.getLatitude();
            lon = location.getLongitude();
            acc = location.getAccuracy();

            GsmCellLocation loc=(GsmCellLocation)tm.getCellLocation();
            cellid = String.valueOf(loc.getCid() & 0xffff);
            operatorName = tm.getSimOperatorName();

            Calendar c = Calendar.getInstance();
            int hr = c.get(Calendar.HOUR);
            int mn = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);

            text.setText(hr+"::"+mn+"::"+sec+" || "+lat + " || " + lon + " || " + acc + " || "+ operatorName + " || " + cellid + " || " + rssi + " || ");
            // Toast.makeText(MainActivity.this, "latitude:" + lat + " longitude:" + lon, Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onClick(View v) {
        //text.setText(String.valueOf(v.getId()));
        // Toast.makeText(MainActivity.this, "ddfdfs", Toast.LENGTH_SHORT).show();
        //Log.d("tag","asdasa");
        if (v.getId() == R.id.start && flagstrt==0) {

//            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//            ich = level / (float)scale;



            //Toast.makeText(MainActivity.this, "ddfdfs", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);

                return;

            }



            AlarmManager am=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, Alarm.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),20*1000,
//                pendingIntent);


            //AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int ALARM_TYPE = AlarmManager.RTC_WAKEUP;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                am.setExactAndAllowWhileIdle(ALARM_TYPE, System.currentTimeMillis()+30000, pendingIntent);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                am.setExact(ALARM_TYPE,System.currentTimeMillis()+30000, pendingIntent);
            else
                am.set(ALARM_TYPE, System.currentTimeMillis()+30000, pendingIntent);



            flagstrt=1;
            Toast.makeText(getApplicationContext(), "Data Collection Started !!!", Toast.LENGTH_SHORT).show();

            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            MyListener = new MyPhoneStateListener();
            tm.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            GPSmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            GPSmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, GPSlistener);

            //background.start(); // for waking every 10 seconds
            // dataread.start();



        }

        else if (v.getId() == R.id.stop && flagstrt==1) {

//            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//            ech = level / (float)scale;

            // battery.setText(ich + " || " + ech);

            flagstop=1;flagstrt = 1;
            GPSmgr.removeUpdates(GPSlistener);
            GPSmgr = null;
            tm.listen(MyListener, PhoneStateListener.LISTEN_NONE);
            text.setText("DONE!!!");
            writeToFile(sfile);
            //Toast.makeText(getApplicationContext(), "Data Collection Stoppped !!!"+sfile, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Alarm.class);
            final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
                    intent, 0);
            AlarmManager alarm = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
            alarm.cancel(pIntent);

        }

        else  if (v.getId() == R.id.restart ) {

            if(GPSmgr!=null){
                GPSmgr.removeUpdates(GPSlistener);
                GPSmgr = null;
                Intent intent = new Intent(this, Alarm.class);
                final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
                        intent, 0);
                AlarmManager alarm = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
                alarm.cancel(pIntent);
            }

            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(i);

        }

    }
    private class MyPhoneStateListener extends PhoneStateListener
    {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength)
        {
            super.onSignalStrengthsChanged(signalStrength);
            int val=-113+2*signalStrength.getGsmSignalStrength();
            rssi=val;
            GsmCellLocation loc=(GsmCellLocation)tm.getCellLocation();
            cellid = String.valueOf(loc.getCid()& 0xffff);
            operatorName = tm.getSimOperatorName();

            Calendar c = Calendar.getInstance();
            int hr = c.get(Calendar.HOUR);
            int mn = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);

            sfile = sfile+hr+"::"+mn+"::"+sec+" || "+lat + " || " +  lon + " || " + acc + " || "+ operatorName + " || "  + cellid + " || " + rssi + "\n";
            text.setText(hr+"::"+mn+"::"+sec+" || "+lat + " || " + lon + " || " + acc + " || "+ operatorName + " || " + cellid + " || " + rssi + " || ");



        }
    };

    public boolean writeToFile(String data)
    {
        try
        {    Toast.makeText(getApplicationContext(), "writing to file", Toast.LENGTH_SHORT).show();
            File file=new File(getExternalFilesDir(null).toString());
            file.mkdirs();
            File f=new File(file, fname.getText().toString()+".txt");
            //Log.d("nkn", String.valueOf(fname));
            FileWriter fw=new FileWriter(f,true);
            BufferedWriter out=new BufferedWriter(fw);
            out.append(data);
            out.close();
            return true;
        }
        catch(FileNotFoundException f)
        {
            return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }
}
