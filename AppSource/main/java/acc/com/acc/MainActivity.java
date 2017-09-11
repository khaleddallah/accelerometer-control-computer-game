package acc.com.acc;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MainActivity extends AppCompatActivity  {

    public int PORT = 15000;
    private Button connectPhones;
    private String serverIpAddress = "192.168.0.0";
    private boolean connected = false;
    TextView text;
    EditText port;
    EditText ipAdr;
    private float x,y,z;
    private SensorManager sensorManager;
    private Sensor sensor;
    boolean acc_disp = false;
    boolean isStreaming = false;
    PrintWriter out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectPhones = (Button)findViewById(R.id.send);
        connectPhones.setOnClickListener(connectListener);
        text=(TextView)findViewById(R.id.textin);
        port=(EditText)findViewById(R.id.port);
        ipAdr=(EditText)findViewById(R.id.ipadr);
        text.setText("Press send to stream acceleration measurement");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        port.setText("15000");
        ipAdr.setText(serverIpAddress);
        acc_disp =false;
    }

    private Button.OnClickListener connectListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!connected) {
                if (!serverIpAddress.equals("")) {
                    connectPhones.setText("send");
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }
            }
            else{
                connectPhones.setText("stop");
                connected=false;
                acc_disp=false;
            }
        }
    };

    public class ClientThread implements Runnable {
        DatagramSocket udpsocket = null;
        public void run() {
            try {
                acc_disp=true;

                PORT = Integer.parseInt(port.getText().toString());

                byte[] buf = new byte[15];
                String bufff;

                udpsocket = new DatagramSocket(PORT);
                InetAddress serverAddr = InetAddress.getByName(ipAdr.getText().toString());
                buf = ("===============").getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length , serverAddr, PORT);
                udpsocket.send(packet);


                connected = true;


                while (connected) {
                    bufff = String.format("Y:%4.2f|Z:%4.2f", y, z);
                    buf=bufff.getBytes();
                    packet= new DatagramPacket(buf , buf.length ,serverAddr , PORT);
                    udpsocket.send(packet);
                    Thread.sleep(2);
                }
            }
            catch (Exception e) {
                acc_disp=false;
                connected=false;
                connectPhones.setText("start again");
                udpsocket.close();

            }
            finally{
                try{
                    acc_disp=false;
                    connected=false;
                    connectPhones.setText("start again");
                    //out.close();
                    udpsocket.close();
                }
                catch(Exception a){
                }
            }
        }
    };

    private void init_perif(){
        // smthing
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationListener, sensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(accelerationListener);
        super.onStop();
    }

    private SensorEventListener accelerationListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            refreshDisplay();
        }
    };

    private void refreshDisplay() {
        if(acc_disp == true){
            String output = String.format("X:%3.2f m/s^2  |  Y:%3.2f m/s^2  |   Z:%3.2f m/s^2", x, y, z);
            text.setText(output);
        }
    }
}
