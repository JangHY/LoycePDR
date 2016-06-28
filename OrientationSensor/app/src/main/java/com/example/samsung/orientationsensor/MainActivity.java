package com.example.samsung.orientationsensor;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    SensorManager sm;
    SensorEventListener accL;
    SensorEventListener oriL;
    SensorEventListener pressL;
    Sensor oriSensor;
    Sensor accSensor;
    Sensor pressSensor;
    TextView ax, ay, az;
    TextView ox, oy, oz;
    TextView px, py, pz, pAltitude;

    FileTest mTextFileManager;

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextFileManager = new FileTest();

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);    // SensorManager 인스턴스를 가져옴
        oriSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);    // 방향 센서
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);    // 가속도 센서
        pressSensor = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);    //기압센서
        oriL = new oriListener();        // 방향 센서 리스너 인스턴스
        accL = new accListener();       // 가속도 센서 리스너 인스턴스
        pressL = new pressListener();       // 기압 센서 리스너 인스턴스
        ax = (TextView)findViewById(R.id.acc_x);
        ay = (TextView)findViewById(R.id.acc_y);
        az = (TextView)findViewById(R.id.acc_z);
        ox = (TextView)findViewById(R.id.ori_x);
        oy = (TextView)findViewById(R.id.ori_y);
        oz = (TextView)findViewById(R.id.ori_z);
        px = (TextView)findViewById(R.id.press_x);
        py = (TextView)findViewById(R.id.press_y);
        pz = (TextView)findViewById(R.id.press_z);
        pAltitude = (TextView)findViewById(R.id.acc_altitude);
        //mTextFileManager.save("짱이당");
        checkPermission();
    }

    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        }
        else {
            // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
            //mTextFileManager.save("20000");
        }
    }



    @Override
    public void onResume() {
        super.onResume();

        sm.registerListener(accL, accSensor, SensorManager.SENSOR_DELAY_NORMAL);    // 가속도 센서 리스너 오브젝트를 등록
        sm.registerListener(oriL, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);    // 방향 센서 리스너 오브젝트를 등록
        sm.registerListener(pressL, pressSensor, SensorManager.SENSOR_DELAY_NORMAL);    // 기압 센서 리스너 오브젝트를 등록
    }

    @Override
    public void onPause() {
        super.onPause();

        sm.unregisterListener(oriL);    // unregister acceleration listener
        sm.unregisterListener(accL);    // unregister orientation listener
        sm.unregisterListener(pressL);    // unregister press listener
    }


    private class accListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {  // 가속도 센서 값이 바뀔때마다 호출됨
            ax.setText(Float.toString(event.values[0]));
            ay.setText(Float.toString(event.values[1]));
            az.setText(Float.toString(event.values[2]));
            Log.i("SENSOR", "Acceleration changed.");
            Log.i("SENSOR", "  Acceleration X: " + event.values[0]
                    + ", Acceleration Y: " + event.values[1]
                    + ", Acceleration Z: " + event.values[2]);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    private class oriListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {  // 방향 센서 값이 바뀔때마다 호출됨
            ox.setText(Float.toString(event.values[0]));    //방위각   0=북쪽, 90=동쪽, 180=남쪽, 270=서쪽
            //기기를 수평으로 두었을 때 기기의 머리부분이 어느방향을 가리키고 있는가.
            oy.setText(Float.toString(event.values[1]));    //경사도 : 기기의 수직 기울기
            //기기의 머리부분과 아래부분이 수평을 이룰 때 0값을 가지며 머리부분의 높이가 높아지면 수치값이 감소한다
            //머리부분의 높이가 낮아지면 수치값이 증가한다.
            oz.setText(Float.toString(event.values[2]));    //좌우회전 : 기기의 수평 기울기
            //화면이 하늘을 향하고 있을 때 기기의 좌, 우 부분이 수평이면 0
            //기기의 좌측위치가 높아지면 증가, 우측 위치가 높아지면 감소
            Log.i("SENSOR", "Orientation changed.");
            Log.i("SENSOR", "  Orientation X: " + event.values[0]
                    + ", Orientation Y: " + event.values[1]
                    + ", Orientation Z: " + event.values[2]);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class pressListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {  //기압 센서 값이 바뀔때마다 호출됨
            px.setText(Float.toString(event.values[0]));    //values[0] : 대기압(Atmospheric pressure)
            py.setText(Float.toString(event.values[1]));    //values[1] : 고도(Altitude)
            pz.setText(Float.toString(event.values[2]));

            Log.i("SENSOR", "Pressure changed.");
            Log.i("SENSOR", "Pressure X: " + event.values[0] + ", Pressure Y: " + event.values[1] + ", Pressure Z: " + event.values[2]);

            float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, event.values[0]);
            System.out.println("altitude = " + altitude);
            pAltitude.setText(Float.toString(altitude));
            mTextFileManager.save(Float.toString(altitude));
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}
