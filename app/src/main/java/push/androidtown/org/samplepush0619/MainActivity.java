package push.androidtown.org.samplepush0619;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText messageInput;
    TextView messageOutput;
    TextView log;
    String regId;

    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageInput = (EditText) findViewById(R.id.messageInput);
        messageOutput = (TextView) findViewById(R.id.messageOutput);
        log = (TextView) findViewById(R.id.log);

        getRegistrationId();

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = messageInput.getText().toString();
                send(input);
            }
        });

        queue = Volley.newRequestQueue(getApplicationContext());

        //getRegistrationId();

        Intent intent = getIntent();
        // if (intent != null) {
        processIntent(intent);
        //}
    }

    public void getRegistrationId() {
        regId = FirebaseInstanceId.getInstance().getToken();
        Log.d("Main", "등록 아이디 ->" + regId.toString());
    }

    public void send(String input) {

        JSONObject requestData = new JSONObject();

        try {
            requestData.put("priority", "high");

            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", input);
            requestData.put("data", dataObj);

            JSONArray idArray = new JSONArray();
            idArray.put(0, regId);
            requestData.put("registration_ids", idArray);

        } catch(Exception e) {
            e.printStackTrace();
        }

        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestCompleted() {
                println("onRequestCompleted() .");
            }

            @Override
            public void onRequestStarted() {
                println("onRequestStarted() .");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                println("onRequestWithError() .");
            }
        });

    }

    public interface SendResponseListener {
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }

    public void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Authorization","key=AAAA4FcV2z4:APA91bFSiISY7BtiHP7AjDXG9vxybtTrvzUZtToU0p9WDYxRhs1MTJD5Q2ZiuxCERfCUCLq1uKzV7WfwAcugB75ly2UdTiBUnNpAzRkkWPTAARx9WPkI_2m7sSWMPI9d8OUHtQ5Nus30wNf-f8kNOpppjzmEpKK_5w");

                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        println("onNewIntent() called.");
        super.onNewIntent(intent);
        // if (intent != null) {
        processIntent(intent);
        //}
    }

    private void processIntent(Intent intent) {
        String from = intent.getStringExtra("from");
        //if (from == null) {
        //    println("from is null.");
        //    return;
        //}

        Log.d("aaaaaaaaaaa".toString(), "aaaaaaaaaaa".toString());

        String contents = intent.getStringExtra("contents");

        println("DATA LAST POINT : " + from + ", " + contents);
        messageOutput.setText("[" + from + "] : " + contents);


        Log.d("aaaaaaaaaaa".toString(), "[" + from + "] : " + contents);
    }

    public void println(String data) {
        log.append(data + "\n");
    }

}