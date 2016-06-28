package com.example.hyon.loyce;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layout;
    private LinearLayout.LayoutParams params;
    private LocationManager mLM;
    private LocationListener mLocationListener;
    private Building[] buildings;//빌딩 전체 데이터
    private Building[] nearBuildings;//현재 위치에서 범위 내에 있는 빌딩 데이터
    private int nearBuildingsCnt;//범위 내 빌딩 개수
    private Location currentLocation;//현재 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        addBuilding();//건물 데이터 넣기

        layout = new LinearLayout(this);

        // RelativeLayout width, height 설정
        params = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setPadding(0, 0, 0, 0);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);


        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        }

        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //여기서 위치값이 갱신되면 이벤트가 발생한다.
                //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
                Log.i("로그", "위치정보 들어옴" + location.getProvider());

                double longitude = location.getLongitude();    //경도
                double latitude = location.getLatitude();         //위도
                float accuracy = location.getAccuracy();        //신뢰도

                currentLocation = location;

                findBuilding();
                addBuildingButtons();
                Toast.makeText(MainActivity.this, "GPS", Toast.LENGTH_SHORT).show();
            }

            public void onProviderDisabled(String provider) {

            }

            public void onProviderEnabled(String provider) {

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

        };
        mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
    }

    public void findBuilding() {//전체 빌딩 배열을 돌면서 거리 내에 있는 빌딩들을 구별해 낸다
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mLM.removeUpdates(mLocationListener);
        nearBuildingsCnt = 0;
        nearBuildings = new Building[buildings.length];
        for (int i = 0; i < buildings.length; i++){
            if(buildings[i].checkInside(currentLocation)){
                nearBuildings[nearBuildingsCnt] = buildings[i];
                nearBuildingsCnt++;
            }
        }
    }

    public  void addBuildingButtons(){

        Button[] buildingButtons;
        Button.OnClickListener buildingListener;

        buildingButtons = new Button[nearBuildingsCnt];
        buildingListener = new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),
                        FloorActivity.class
                );
                // 데이터를 intent 에 실어 보내기
                //intent.putExtra("year", 2015);
                Building b = nearBuildings[v.getId()];
                intent.putExtra("building", b);//선택된 버튼의 객체를 인텐트로 보냄
                startActivity(intent);

            }
        };


        for (int i = 0; i < nearBuildingsCnt; i++) {
            buildingButtons[i] = new Button(this);
            buildingButtons[i].setText(nearBuildings[i].get_name());
            buildingButtons[i].setOnClickListener(buildingListener);
            buildingButtons[i].setId(i);//어떤 빌딩이 선택되었는지 구별하기 위해 id값 지정
            layout.addView(buildingButtons[i], params);
        }
        setContentView(layout);
    }

    public void addBuilding() {
        buildings = new Building[5];
        String map[];

        buildings[0] = new Building("공대 1호관", 3, 0, 36.3680228, 127.3446833);
        buildings[1] = new Building("공대 2호관", 4, 4, 36.3643547, 127.3462813);
        buildings[2] = new Building("공대 3호관", 2, 1, 36.3652377, 127.3465816);
        buildings[3] = new Building("공대 4호관", 3, 0, 36.3649379, 127.3475821);
        buildings[4] = new Building("공대 5호관", 7, 1, 36.3665547, 127.3443752);

        for(int j = 0; j < buildings.length; j++) {//building에 지도 이름 매칭 시킴
            map = new String[buildings[j].get_floor()];
            int f = buildings[j].get_floor();
            for (int i = 0; i < buildings[j].get_floor(); i++) {
                if (i <  buildings[j].get_bFloor()) {//지하 map5_B1 (5호관 지하 1층)이런식으로
                    map[i] = new String("map"+(j+1)+"_B"+( buildings[j].get_bFloor() - i));
                    Log.e("층",map[i]);
                } else {//지상 map5_1 (5호관 1층)
                    map[i] = new String("map"+(j+1)+"_"+(i - buildings[j].get_bFloor() + 1));
                    Log.e("층",map[i]);
                }
            }
            buildings[j].setBuilding(map);
        }


/*공대 1호관 : lat=36.3680228&lng=127.3446833
*공대 2호관 : lat=36.3643547&lng=127.3462813
*공대 3호관 : lat=36.3652377&lng=127.3465816
*공대 4호관 : lat=36.3649379&lng=127.3475821
*공대 5호관 : lat=36.3665547&lng=127.3443752
* */
    }
}
