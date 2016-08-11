package com.example.mcn_lab_no1.accelerometer;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowId;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import android.os.Handler;
import java.io.BufferedReader;
import android.os.Vibrator; // 記得匯入震動類別
import android.widget.ListView;

/*1.sit",
2."stand",
3."walk",
4."ride",
5."jog",
6."Ascending Stairs",
7."Descending Stairs"*/

public class MainActivity extends AppCompatActivity  {

    private TextView text_x;
    private TextView text_y;
    private TextView text_z;
    private TextView Time;
    private SensorManager aSensorManager;
    private Sensor aSensor;
    static TextView result;
    private Button btn_str, btn_pause;
    private Handler handler = new Handler();
    private static ListView listview;


    BufferedReader myFile_train;
    FileReader br;
    File SDcarPath;
    boolean flag=false;
    private double gravity[] = new double[3];
    static double  train[][]=new double[70][44];
    static double test[]=new double[43];
    double BinnedX[]=new double[10],BinnedY[]=new double[10],BinnedZ[]=new double[10];
    static double[] distance=new double[70];
    static String[] type=new String[70];
    static int count=0,count1=0;
    ArrayList <Double>temX=new ArrayList<>();
    ArrayList <Double>temY=new ArrayList<>();
    ArrayList <Double>temZ=new ArrayList<>();
    String act, s;//s存10次動作名稱
    int t = 0;//一個動作有10次
    private MediaPlayer mePlayer_start,mePlayer_end;
    private AudioManager aManager;
    /**
     * Called when the activity is first created.
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.d("DU", "1");
        text_x = (TextView) findViewById(R.id.X);
        text_y = (TextView) findViewById(R.id.Y);
        text_z = (TextView) findViewById(R.id.Z);



        result = (TextView) findViewById(R.id.result);
        btn_str = (Button) findViewById(R.id.btn_str);
        btn_pause = (Button) findViewById(R.id.btn_pause);

        aSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//連結手機的的感測器
        aSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//開啟三軸加速器
        btn_str.setOnClickListener(listener1);
        btn_pause.setOnClickListener(listener1);

        aManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        //初始化mediaplayer對象,這裏播放的是raw文件中的mp3資源
        mePlayer_start = MediaPlayer.create(MainActivity.this, R.raw.one);
        mePlayer_end = MediaPlayer.create(MainActivity.this, R.raw.two);

        String str="";
        String txt[]=null;
        int i=0;
        try
        {
             if (!(Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)))
             {
                 SDcarPath = Environment.getExternalStorageDirectory();//抓目前的外部儲存位置
                 br = new FileReader(SDcarPath + "/myData/trainxx" + ".csv");
                 myFile_train = new BufferedReader(br);
                 while((str=myFile_train.readLine())!=null)
                 {
                     txt=str.split(",");
                     for(int j=0;j<44;j++)
                         train[i][j]=Double.valueOf(txt[j]);
                     i++;
                 }
             }
        }
        catch (IOException e)
        {
            result.setText(e.toString());
        }
    }

    private android.view.View.OnClickListener listener1 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_str:
                    Log.d("DU","2");
                    flag=true;
                    text_x.setText("X =");//X軸
                    text_y.setText("Y =");//Y軸
                    text_z.setText("Z =");//Z軸
                    handler.postDelayed(runnable, 100);
                    aSensorManager.registerListener(listener, aSensor, SensorManager.SENSOR_DELAY_GAME);//註冊感測器，偵測手機的感測器值。
                    result.setText("偵測中.....");
                    break;
                case R.id.btn_pause:
                    Log.d("DU","3");
                    flag=false;
                    temX.clear();
                    temY.clear();
                    temZ.clear();
                    text_x.setText("X =");//X軸
                    text_y.setText("Y =");//Y軸
                    text_z.setText("Z =");//Z軸
                    t = 0;
                    act = s = "";
                    aSensorManager.unregisterListener(listener);
                    result.setText("暫停");
                    break;
            }
        }
    };


    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
// TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            double X[] = new double[100];
            double Y[] = new double[100];
            double Z[] = new double[100];


// TODO Auto-generated method stub
            Log.d("DU", "4");

            if (flag) {
                gravity[0] = event.values[0];
                gravity[1] = event.values[1];
                gravity[2] = event.values[2];
                text_x.setText("X = " + String.format("%.2f", gravity[0]));//X軸
                text_y.setText("Y = " + String.format("%.2f", gravity[1]));//Y軸
                text_z.setText("Z = " + String.format("%.2f", gravity[2]));//Z軸
                temX.add(gravity[0]);
                temY.add(gravity[1]);
                temZ.add(gravity[2]);


                if (temX.size() == 100) {
                    aSensorManager.unregisterListener(listener);
                    for (int i = 0; i < temX.size(); i++) {
                        X[i] = temX.get(i);
                        Y[i] = temY.get(i);
                        Z[i] = temZ.get(i);
                    }
                    Log.d("DU", "5");
                    /*********平均*********/
                    test[0] = getAverage(X);
                    test[1] = getAverage(Y);
                    test[2] = getAverage(Z);
                    /*********平均*********/
                    Log.d("DU", "6");
                    /*********標準差*********/
                    test[3] = getStandardDiviation(X);
                    test[4] = getStandardDiviation(Y);
                    test[5] = getStandardDiviation(Z);
                    /*********標準差*********/
                    Log.d("DU", "7");

                    /*********Average Absolute Difference*********/
                    test[6] = getAverageAbsoluteDifference(X);
                    test[7] = getAverageAbsoluteDifference(Y);
                    test[8] = getAverageAbsoluteDifference(Z);
                    /*********Average Absolute Difference*********/
                    Log.d("DU", "8");

                    /*********Average Resultant Acceleration*********/
                    test[9] = getAverageResultantAcceleration(X, Y, Z);
                    /*********Average Resultant Acceleration*********/
                    Log.d("DU", "9");

                    /*********Time Between Peaks*********/
                    test[10] = getpeak(X);
                    test[11] = getpeak(Y);
                    test[12] = getpeak(Z);
                    /*********Time Between Peaks*********/
                    Log.d("DU", "10");

                    /*********Binned Distribution*********/
                    BinnedX = getBinnedDistribution(X, BinnedX);
                    BinnedY = getBinnedDistribution(Y, BinnedY);
                    BinnedZ = getBinnedDistribution(Z, BinnedZ);
                    /*********Binned Distribution*********/
                    int coun = 0;
                    for (int i = 13; i < 23; i++) {
                        test[i] = BinnedX[coun];
                        coun++;
                    }
                    Log.d("DU", "11");
                    coun = 0;
                    for (int i = 23; i < 33; i++) {
                        test[i] = BinnedY[coun];
                        coun++;
                    }
                    Log.d("DU", "12");
                    coun = 0;
                    for (int i = 33; i < 43; i++) {
                        test[i] = BinnedZ[coun];
                        coun++;
                    }
                    Log.d("DU", "13");
                    for (int a = 0; a < BinnedX.length; a++) {
                        BinnedX[a] = 0;
                        BinnedY[a] = 0;
                        BinnedZ[a] = 0;
                    }
                    Log.d("DU", "14");
                    Noremal(test);//正規化
                    Log.d("DU", "15");
                    getNeighbors(train, test);
                    s = getResponse(distance, type, 3);
                    act += s;
                    Log.d("DU", "16");
                    temX.clear();
                    temY.clear();
                    temZ.clear();
                    t++;
                    if (t == 10)
                    {
                        Log.d("DU", "FINISH");
                        aSensorManager.unregisterListener(listener);
                        setVibrate(3000);//震動3秒
                        mePlayer_end.start();
                        result.setText(act);
                        act="";
                        flag=false;
                    }
                    aSensorManager.registerListener(listener, aSensor, SensorManager.SENSOR_DELAY_GAME);
                }
            }
            //aSensorManager.unregisterListener(listener);
        }
    };
    public void setVibrate(int time)
    {
        Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(time);
    }
    public static double getSum(double[]inputData)//加總
    {
        if (inputData == null || inputData.length == 0)
            return -1;
        int len = inputData.length;
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum = sum + inputData[i];
        }
        return sum;
    }


    public static double getAverage(double[] inputData) //取平均
    {
        if (inputData == null || inputData.length == 0)
            return -1;
        int len = inputData.length;
        double result;
        result = getSum(inputData) / len;

        return result;
    }

    public static int getCount(double[] inputData)//取個數
    {
        if (inputData == null)
            return -1;
        return inputData.length;
    }

    public static double getSquareSum(double[] inputData)//取平方和
    {
        if(inputData==null||inputData.length==0)
            return -1;
        int len=inputData.length;
        double sqrsum = 0;
        for (int i = 0; i <len; i++) {
            sqrsum = sqrsum + inputData[i] * inputData[i];
        }
        return sqrsum;
    }

    public static double getVariance(double[] inputData) //取變異數
    {
        int count = getCount(inputData);
        double sqrsum = getSquareSum(inputData);
        double average = getAverage(inputData);
        double result;
        result = (sqrsum - count * average * average) / count;
        return result;
    }

    public static double getStandardDiviation(double[] inputData)//取標準差
    {
        double result;

        //絕對值
        result = Math.sqrt(Math.abs( getVariance(inputData)));

        return result;
    }

    public static double getAverageAbsoluteDifference(double[] inputData)
    //取平均絕對離差
    {

        double result=0,mean=getAverage(inputData),sum=0;
        for(int i=0;i<inputData.length;i++)
        {
            sum+=Math.abs(inputData[i]-mean);
        }
        result=sum/getCount(inputData);
        return result;
    }

    public static double getAverageResultantAcceleration(double[] inputDataX,double[] inputDataY,double[] inputDataZ)
    {
        double result=0;
        for(int i=0;i<inputDataX.length;i++)
            result=Math.sqrt((Math.pow(inputDataX[i],2))
                    +(Math.pow(inputDataY[i], 2))
                    +(Math.pow(inputDataZ[i], 2)))/getCount(inputDataX);
        return result;
    }

    public static double Sort(double[] inputData,String type)
    {
        int min;
        double []test1=new double[inputData.length];

        for(int i=0;i<inputData.length;i++)
        {
            test1[i]=inputData[i];
        }

        Arrays.sort( test1);//排序
        if(type=="max")
            return test1[test1.length-1];//最後一個為最大

        else if(type=="min")
            return test1[0];//第一個為最小

        return 0;
    }


    public static double getpeak(double[] inputData)
    {
        double max,min,val,Th,result=0,value=0.9;//Th門檻值,Time1是第一波峰時間,Time2是第二波峰時間
        double []test=new double[inputData.length];
        int count=0;
        ArrayList <Integer>Position=new ArrayList<>();
        for(int i=0;i<inputData.length;i++)
            test[i]=inputData[i];
        max=Sort(inputData, "max");
        min=Sort(inputData, "min");
        Th=max*value;
        val=Math.abs(min);
        for(int i=0;i<inputData.length;i++)
            inputData[i]=inputData[i]+val;

        while(true)
        {
            for(int i=0;i<inputData.length;i++)
            {
                if(inputData[i]>Th)
                {
                    Position.add(i);
                    count++;
                }
            }
            if(count<2)
            {
                value-=0.1;
                Th*=value;
            }

            else
                break;
        }

        for(int i=0;i<Position.size();i++)
            result+=Math.abs(Position.get(i));
        return (result/count)*0.02;
    }

    public static double[] getBinnedDistribution(double []inputData,double []bin)
    {
        double max,min,range[]=new double[10],result=0,test[]=new double[inputData.length];

        int sum=0,inval=0;

        for(int i=0;i<inputData.length;i++)
            test[i]=inputData[i];


        for(int i=0;i<inputData.length;i++)
        {
            for(int j=i+1;j<inputData.length;j++)
            {
                if(inputData[i]<inputData[j])
                {
                    double temp=inputData[i];
                    inputData[i]=inputData[j];
                    inputData[j]=temp;
                }
            }
        }


        for(int i=0;i<range.length;i++)
        {
            inval+=10;
            range[i]=inputData[inval-1];//各區間的上限值

        }


        for(int i=0;i<test.length;i++)//計算落在各區間的個數
        {
            if(  test[i]>=range[0] )
                bin[0]++;
            else if( test[i]>=range[1])
                bin[1]++;
            else if(test[i]>=range[2])
                bin[2]++;
            else if( test[i]>=range[3])
                bin[3]++;
            else if( test[i]>=range[4])
                bin[4]++;
            else if( test[i]>=range[5])
                bin[5]++;
            else if( test[i]>=range[6])
                bin[6]++;
            else if( test[i]>=range[7])
                bin[7]++;
            else if( test[i]>=range[8])
                bin[8]++;
            else if( test[i]>=range[9])
                bin[9]++;

        }

        return bin;
    }

    public static void Noremal(double test[]) {
        int k, i = 0, j = 0;
        double max = 0, min = 0;


        max = Sort(test, "max");
        min = Sort(test, "min");

        for (j = 0; j < test.length; j++) {
            Math.abs(test[j] = (test[j] - min) / (max - min));

        }
    }


    public  static double euclideanDistance (double []instance1,double [][]instance2,int len)
    {
        double result=0;

        for(int j=0;j<instance1.length;j++)
        {
            result+=Math.sqrt(Math.pow(instance1[j]-instance2[len][j],2));
        }

        return result;
    }

    public static void sort(double[]distance,String[] type)
    {
        int min;
        String tempType;
        for(int i=0;i<distance.length-1;i++)
        {
            min=i;
            for(int j=i+1;j<distance.length;j++)
            {
                if(distance[min]>distance[j])
                    min=j;
            }
            if(i!=min)
            {
                //距離排序//
                double temp = distance[i];
                distance[i]=distance[min];
                distance[min]=temp;

                //類別排序//
                tempType=type[i];
                type[i]=type[min];
                type[min]=tempType;
            }
        }
    }

    public  static void getNeighbors(double[][] train,double []test)
    {
        for(int i=0;i<train.length;i++)
        {
            distance[i]=euclideanDistance(test, train,i);
            type[i]=String.valueOf(train[i][43]);
        }
        Log.d("DU", "17");
        sort(distance, type);
    }
    public String getResponse(double[] distance, String[] type, int k)
    {
        Log.d("DU", "18");
        int vote[]=new int [7],max;

        double respone[]=new double[k];


        for(int i=0;i<k;i++)
        {
            respone[i]=(Double.valueOf(type[i]));
        }


        for(int i=0;i<respone.length;i++)
        {
            if(respone[i]==1.0)
                vote[0]++;
            else if(respone[i]==2.0)
                vote[1]++;
            else if(respone[i]==3.0)
                vote[2]++;
            else if(respone[i]==4.0)
                vote[3]++;
            else if(respone[i]==5.0)
                vote[4]++;
            else if(respone[i]==6.0)
                vote[5]++;
            else if(respone[i]==7.0)
                vote[6]++;
        }

        max=0;
        for(int i=1;i<vote.length;i++)
        {
            if(vote[max]<vote[i])
                max=i;
        }
        
        /*1.sit",
              2."stand",
              3."walk",
              4."ride",
              5."jog",
              6."Ascending Stairs",
              7."Descending Stairs"*/
        switch (max) {
            case 0:
                s="sit\n";
                break;
            case 1:
                s="stand\n";
                break;
            case 2:
                s="walk\n";
                break;
            case 3:
                s="ride\n";
                break;
            case 4:
                s="jog\n";
                break;
            case 5:
                s="Ascending Stairs\n";
                break;
            case 6:
                s="Descending Stairs\n";
                break;
        }
        return s;
    }

    @Override
        protected void onResume() {
            Log.d("DU","5");
            super.onResume();
            Log.d("DU", "6");

            //aSensorManager.registerListener(listener, aSensor, SensorManager.SENSOR_DELAY_GAME);//註冊感測器，偵測手機的感測器值。
            Log.d("DU", "7");
        }


        @Override
        protected void onPause() {
// TODO Auto-generated method stub
/* 取消註冊SensorEventListener */
            super.onPause();
            aSensorManager.unregisterListener(listener);
            Toast.makeText(this, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();

        }
            Runnable runnable = new Runnable() {
                public void run() {
                    // TODO Auto-generated method stub
                    // 需要背景作的事
                    try {
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(1000);
                        }
                        //setVibrate(1000);//震動1秒
                        mePlayer_start.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.removeCallbacks(runnable);
                }
            };
    }




