package uk.ac.lincoln.jackduffy.alfred;

import android.app.Notification;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

public class ListenerServiceWear extends WearableListenerService
{
    @Override
    public void onDataChanged(DataEventBuffer dataEvents)
    {
        String tempString = "";
        for (DataEvent event : dataEvents)
        {
            //http://stackoverflow.com/questions/42254858/converting-a-datamap-to-a-string-or-string-array/42255079#42255079
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);
            System.out.println(DataMapItem.fromDataItem(event.getDataItem()).getDataMap());
            System.out.flush();
            System.setOut(old);
            tempString = baos.toString();
        }

        try
        {
            String[] tempData = tempString.split(",");
            for(int i=0; i < tempData.length; i++)
            {
                tempData[i] = tempData[i].replaceAll(" ","");
                if(tempData[i].contains("{"))
                {
                    tempData[i] = tempData[i].replaceAll("\\{","");
                }

                else if(tempData[i].contains("}"))
                {
                    tempData[i] = tempData[i].replaceAll("\\}","");
                }
            }

            Arrays.sort(tempData);
            String apiService = null;
            switch(tempData[0].substring(11))
            {
                case "0":
                {
                    apiService = "weather";
                    break;
                }
            }

            if(apiService != null)
            {
                String[] sortedData = new String[(tempData.length - 2)];
                for(int i = 2; i < tempData.length; i++)
                {
                    sortedData[(i-2)] = tempData[i];
                }

                //System.out.println("Preparing to write to Shared Preferences");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                editor.putInt("contentArray_size", sortedData.length);

                //System.out.println("Writing to Shared Preferences");
                int i;
                for(i = 0; i < sortedData.length; i++)
                {
                    editor.putString(Integer.toString(i), sortedData[i]);
                    //System.out.println(sortedData[i]);
                }

                editor.putString(Integer.toString(i), "##-WEATHER");
                editor.apply();

                System.out.println("Data Transfer Complete");
                Alfred.sharedPreferencesReady = true;

            }

            else
            {
                System.out.println("Error, do not pass data");
            }
        }

        catch(Exception e)
        {
            System.out.println("The data packet is not properly configured. Did not send");
        }
    }
}