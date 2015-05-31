package nonocopter.fr.nonocopter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Navigator implements SensorEventListener {
    private float         gyroX,  gyroY,  gyroZ  = 0;
    private static float  maxValue = 5;
    private SensorManager sensorManager;
    private Sensor        accelerometer;

    public Navigator( Context context){
        sensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void start(){
        resetValues();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop(){
        sensorManager.unregisterListener(this);
        resetValues();
    }

    public String navigate(){ // TODO
        return ""+this.gyroX;
    }

    private void resetValues(){
        this.gyroX = this.gyroY = this.gyroZ = 0;
    }

    private float getGoodValue( float value){
        if ( value == 0 ) return 0;
        if ( value < 0 && value < -maxValue ) return -maxValue;
        if ( value > 0 && value > maxValue  ) return maxValue;
        float temp = Math.round( value * 10);
        return temp / 10;
    }

    public void onSensorChanged(SensorEvent event) {
        this.gyroX = getGoodValue( event.values[0]);
        this.gyroY = getGoodValue( event.values[1]);
        this.gyroZ = getGoodValue( event.values[2]);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        System.out.println( "Accuracy : " + accuracy);
    }
}
