package br.ufrj.nce.ubicomp.connection;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import br.ufrj.nce.ubicomp.utils.Definitions;
import br.ufrj.nce.ubicomp.utils.MyXML;

import android.util.Log;

public class SendData extends Thread {

    private boolean isStarted = false;
    private int interval;
    private long time;
    private Double result = null;
    private String tag, httpUrl, datastream;
    private String registrationId = null;

    public SendData(String httpUrl, String datastream, String tag, long time,
                    int interval, boolean isStarted) {
        this.httpUrl = httpUrl;
        this.datastream = datastream;
        this.tag = tag;
        this.time = time;
        this.interval = interval;
        this.isStarted = isStarted;
        this.registrationId = null;
    }

    public SendData(String httpUrl, String datastream, String tag, long time,
                    int interval) {
        this.httpUrl = httpUrl;
        this.datastream = datastream;
        this.tag = tag;
        this.time = time;
        this.interval = interval;
        this.isStarted = false;
        this.registrationId = null;
    }

    public SendData(String httpUrl, String registrationId) {
        this.httpUrl = httpUrl;
        this.registrationId = registrationId;
        this.isStarted = false;
    }

    public void run() {
        while (isStarted) {

            long currentTime;

            currentTime = (java.lang.System.currentTimeMillis() / 1000L);
            if ((currentTime - time) > (long) interval) {
                time = currentTime;
            }

            if (result != null) {
                Log.d(tag, "###Enviando Result: " + result.doubleValue());
                Log.d(tag, "###Time: " + java.lang.System.currentTimeMillis()
                        / 1000L);
                sendData(result.doubleValue());
            }

            try {
                Thread.sleep(interval * 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if ((!isStarted) && (registrationId != null)) {
            sendData();
        }
        // return null;

    }

    public void sendData(double result) {
        String temp = MyXML.myToXML(datastream, result);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpput = new HttpPut(httpUrl);

        try {
            StringEntity se = new StringEntity(temp, HTTP.UTF_8);
            se.setContentType("application/xml");
            httpput.setEntity(se);

            HttpResponse httpresponse = httpclient.execute(httpput);
            HttpEntity resEntity = httpresponse.getEntity();
            Log.d("SEND_DATA", "Resultado:" + resEntity.getContent().toString());

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendData() {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpput = new HttpPut(httpUrl+"/"+registrationId+"/");

        Log.d(Definitions.NOTIFICATION_TAG, "URL FORMADA: "+httpUrl+"/"+registrationId+"/");

        try {
            StringEntity se = new StringEntity("", HTTP.UTF_8);
            httpput.setEntity(se);
            HttpResponse httpresponse = httpclient.execute(httpput);
            HttpEntity resEntity = httpresponse.getEntity();

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getDatastream() {
        return datastream;
    }

    public void setDatastream(String datastream) {
        this.datastream = datastream;
    }

}
