package com.example.hyon.loyce;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Hyon on 2016-04-09.
 */
public class MapActivity extends Activity {

    PhotoViewAttacher mAttacher;//오픈소스 사용
    PhotoViewAttacher mAttacher1;//오픈소스 사용
    BitmapView bitmapview;
    ImageView map;
    String mapName;// 지도 이름 ex) map5_1
    Button bottomButton;//현재 위치를 지정해 주세요(완료) 버튼
    Button.OnClickListener bottomButtonListener;

    ImageView currentMarker;//현재 위치 지정 마커

    float mapX, mapY,mapHeight, mapWidth;// 현재위치 등록 할 때 지도의 센터값, 크기
    float viewCenterX, viewCenterY, viewMapHeight, viewMapWidth;//그냥 핸드폰 화면의 중심 point

    /* 기압센서변수*/
    SensorManager sm;
    SensorEventListener pressL;
    Sensor pressSensor;
    TextView px, py, pz, pAltitude;

    String path;
    String fileName;
    FileOutputStream fos = null;
    FileWriter write;
    PrintWriter out;
    File file;
    RandomAccessFile raf;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = (ImageView)findViewById(R.id.map);//지도 이미지
        bottomButton = (Button)findViewById(R.id.bottomButton);//"현재 위치를 지정해 주세요" 버튼

        Intent intent = getIntent();
        mapName = intent.getStringExtra("mapName");//지도 이름 FloorActivity에서 받기

        Toast.makeText(MapActivity.this, mapName, Toast.LENGTH_SHORT).show();

        Context context = map.getContext();
        int id = context.getResources().getIdentifier(mapName, "drawable", context.getPackageName());//만약 없는 그림이면 id가 0이 됨
        if(id == 0){
            mapName = "map5_4";
            id = context.getResources().getIdentifier(mapName, "drawable", context.getPackageName());//지도가 없는 층은 우선 5호관 4층 지도로 나오게 함
        }
        map.setImageResource(id);

        currentMarker = (ImageView) findViewById(R.id.currentMarker);
        viewMapHeight = (float)2464;
        viewMapWidth = (float)1069.582;




        bottomButtonListener = new View.OnClickListener() {
            public void onClick(View v) {//현재위치를 지정해 주세요 -> 완료 버튼
                //Toast.makeText(getApplicationContext(),"클릭 성공",Toast.LENGTH_LONG).show();
                bottomButton.setVisibility(View.INVISIBLE);//버튼 안보이게
                //currentMarker.setVisibility(View.INVISIBLE);
                //mAttacher.resetMatrix();

                mapX = mAttacher.getMapCenterX();
                mapY = mAttacher.getMapCenterY();
                mapHeight = mAttacher.getMapHeight();
                mapWidth = mAttacher.getMapWidth();//2614.5757

                float[] values = new float[9];
                mAttacher.getDisplayMatrix().getValues(values);

                //mapWidth = values[0]*viewMapWidth;
                //mapHeight = values[4]*viewMapHeight;

                mapX = values[2];
                mapY = values[5];

                //Log.e("Value", "value X : "+value[2]+"    value Y : "+value[5]);

                mAttacher = new PhotoViewAttacher(map);//이미지 핀치 줌 기능
                mAttacher.setZoomable(false);//지도를 더이상 확대할 수 없도록
                // mAttacher1 = new PhotoViewAttacher(currentMarker);

                viewMapHeight =  mAttacher.getMapHeight();
                viewMapWidth = mAttacher.getMapWidth();
                //mAttacher.update();//지도 크기 원래대로 복구
//                mAttacher.setZoomable(false);//지도를 더이상 확대할 수 없도록
 //               mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
   //             mAttacher.update();//지도 크기 원래대로 복구

                //View layoutMainView = (View)findViewById(R.id.mainLayout);

                getCurrentPoint();
            }
        };

        bottomButton.setOnClickListener(bottomButtonListener);
        bottomButton.setClickable(false);


       // map.setImageBitmap(rotateImage(BitmapFactory.decodeResource(getResources(),context.getResources().getIdentifier(mapName, "drawable", context.getPackageName())), 90));//지도 90도 회전
        //map.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.map5_5));//지도 이미지 보여주기
        mAttacher = new PhotoViewAttacher(map, bottomButton);//이미지 핀치 줌 기능
       // mAttacher1 = new PhotoViewAttacher(currentMarker);
        mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);//여기 옵션으로 지도가 화면에 어떤 크기로 보여주는지 결정 가능 ex)FIT_XY, CENTER_CROP



        //////////////////////////////////////////////////////////////////////////////////////////////////////
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);    // SensorManager 인스턴스를 가져옴
        pressSensor = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);    //기압센서
        pressL = new pressListener();       // 기압 센서 리스너 인스턴스
        //////////////////////////////////////////////////////////////////////////////////////////////////////

        //px = (TextView)findViewById(R.id.press_altitude);

        //fileSave();
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyyMMdd_HHMM", java.util.Locale.getDefault());
        Date date = new Date();
        String strDate = dateFormat.format(date);
        String path = Environment.getExternalStorageDirectory()+"/";
        String fileName = "sensorTest"+strDate+".txt";

        file = new File(path + fileName);
    } // end onCreate()

    public void getCurrentPoint(){//현재위치 계산 함수

        viewCenterX =  (float) 720;
        viewCenterY = (float)1232;
        viewMapHeight = (float)2464;
        viewMapWidth = (float)1069.582;

        float ratio = (viewCenterX - mapX)/(float)mapWidth;
        float ratio1 = (viewCenterY - mapY)/(float)mapHeight;

        float ypoint = viewMapHeight * ratio1;//3727.8638
        float xpoitn = viewMapWidth * ratio;//2122.1372

        //currentMarker.setVisibility(View.VISIBLE);
        currentMarker.setX(xpoitn);
        currentMarker.setY(ypoint);

       /*
        float maxX = mapCenterX + (mapWidth/(float) 2);//큰 지도 가장 오른쪽 x
        float minY = mapCenterY - (mapHeight/(float)2);//큰 지도 맨 밑 y

        float ratioX = (maxX - viewCenterX)/(float)mapWidth;
        float ratioY = (viewCenterY - minY)/(float)mapHeight;

        float currentX = (viewCenterX + viewMapWidth/(float)2) - (viewMapWidth * ratioX);
        float currentY = (viewCenterY - viewMapHeight/(float)2) + (viewMapHeight * ratioY);

        currentMarker.setVisibility(View.VISIBLE);
        currentMarker.setX(currentX);
        currentMarker.setY(currentY);
        */
    }


    /**
     * Bitmap이미지의 가로, 세로 사이즈를 리사이징 한다.
     *
     * @param source 원본 Bitmap 객체
     * @param maxResolution 제한 해상도
     * @return 리사이즈된 이미지 Bitmap 객체
     */
    public Bitmap resizeBitmapImage(Bitmap source, int maxResolution)
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if(width > height)
        {
            if(maxResolution < width)
            {
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        }
        else
        {
            if(maxResolution < height)
            {
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    protected class myDragEventListener implements View.OnDragListener {

        // This is the method that the system calls when it dispatches a drag event to the
        // listener.
        public boolean onDrag(View v, DragEvent event) {

            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();

            // Handles each of the expected events
            switch(action) {

                case DragEvent.ACTION_DRAG_STARTED:

                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        // As an example of what your application might do,
                        // applies a blue color tint to the View to indicate that it can accept
                        // data.
                       // v.setColorFilter(Color.BLUE);

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();

                        // returns true to indicate that the View can accept the dragged data.
                        return true;

                    }

                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:

                    // Applies a green tint to the View. Return true; the return value is ignored.

                    //v.setColorFilter(Color.GREEN);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    // Ignore the event
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:

                    // Re-sets the color tint to blue. Returns true; the return value is ignored.
                    //v.setColorFilter(Color.BLUE);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DROP:

                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // Gets the text data from the item.
                    //dragData = item.getText();

                    // Displays a message containing the dragged data.
                    //Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_LONG);

                    // Turns off any color tints
                    //v.clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    // Turns off any color tinting
                    //v.clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Does a getResult(), and displays what happened.
                    if (event.getResult()) {
                       // Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG);

                    } else {
                       // Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG);

                    }

                    // returns true; the value is ignored.
                    return true;

                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example","Unknown action type received by OnDragListener.");
                    break;
            }

            return false;
        }
    };


    public void onResume() {
        super.onResume();

        sm.registerListener(pressL, pressSensor, SensorManager.SENSOR_DELAY_NORMAL);    // 기압 센서 리스너 오브젝트를 등록
    }

    public void onPause() {
        super.onPause();

        sm.unregisterListener(pressL);    // unregister press listener
    }

    private class pressListener implements SensorEventListener {
        private Handler mHandler;
        private Runnable mRunnable;

        public void onSensorChanged(SensorEvent event) {  //기압 센서 값이 바뀔때마다 호출됨
            //px.setText(Float.toString(event.values[0]));    //values[0] : 대기압(Atmospheric pressure)
            //py.setText(Float.toString(event.values[1]));    //values[1] : 고도(Altitude)
            //pz.setText(Float.toString(event.values[2]));

            Log.i("SENSOR", "Pressure changed.");
            Log.i("SENSOR", "Pressure X: " + event.values[0] + ", Pressure Y: " + event.values[1] + ", Pressure Z: " + event.values[2]);

            /*
            float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, event.values[0]);
            px.setText(Float.toString(altitude));
            */
            try{
                float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, event.values[0]);
                System.out.println("altitude = " + altitude);
               // px.setText(Float.toString(altitude));
                /*
                if(!file.exists()){
                    path = Environment.getExternalStorageDirectory() + "/Sensor/";
                    fileName =  "altutude.txt";

                    file = new File(path+fileName);

                    raf = new RandomAccessFile(path, "rw");
                    fos = new FileOutputStream(file, true);
                }
                */
                write = new FileWriter(file,true);
                out = new PrintWriter(write);
                out.print(Float.toString(altitude));
                out.print(",");
                out.close();
                Log.d("finish","저장완료");
                /*
                fos.write((Float.toString(altitude)).getBytes());
                fos.write(",".getBytes());
                fos.close();
                */
            }
            catch(IOException e){
                e.printStackTrace();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }


}


