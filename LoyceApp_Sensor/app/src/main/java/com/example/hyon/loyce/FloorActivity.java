package com.example.hyon.loyce;

import android.app.Activity;
import android.content.Intent;

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

/**
 * Created by Hyon on 2016-04-09.
 */
public class FloorActivity extends Activity  {

    private LinearLayout layout;
    private LinearLayout.LayoutParams params;
    private int floor;
    private int bFloor;
    private Button[] floorButtons;
    private Button.OnClickListener floorListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_floor);

        layout = new LinearLayout(this);

        // RelativeLayout width, height 설정
        params = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setPadding(0, 0, 0, 0);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);

        Intent intent = getIntent();
        final Building building = (Building) intent.getParcelableExtra("building");
        floor = building.get_floor();
        bFloor = building.get_bFloor();

        floorButtons = new Button[floor];
        floorListener = new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),
                        MapActivity.class
                );
                // 데이터를 intent 에 실어 보내기
                //intent.putExtra("year", 2015);
               // intent.putExtra("building",nearBuildings[v.getId()]);//선택된 버튼의 객체를 인텐트로 보냄
                Log.e("이름전",building.get_map()[v.getId()]);
                Toast.makeText(FloorActivity.this, building.get_map()[v.getId()], Toast.LENGTH_SHORT).show();
                intent.putExtra("mapName",building.get_map()[v.getId()]);//선택된 버튼의 객체를 인텐트로 보냄

                startActivity(intent);
            }
        };

        for (int i = 0; i < floor; i++) {

            floorButtons[i] = new Button(this);
            floorButtons[i].setOnClickListener(floorListener);
            floorButtons[i].setId(i);//어떤 층이 선택되었는지 구별하기 위해 id값 지정

            if (i < bFloor) {
                floorButtons[i].setText("지하 "+(bFloor-i)+"층");
            }
            else{
                floorButtons[i].setText((i-bFloor+1)+"층");
            }
            layout.addView(floorButtons[i], params);
        }
        setContentView(layout);

    } // end onCreate()

}
