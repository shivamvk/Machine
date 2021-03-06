package com.example.shivamvk.machinemindful;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerActivityFragment extends Fragment {

    String name,id;

    ImageView ivCollectPayment;
    List<Logs> logsList;

    ProgressBar pbLogs;
    RecyclerView rvLogs;
    TextView tvEmptyLogs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        name = getArguments().getString("name");
        id = getArguments().getString("id");
        return inflater.inflate(R.layout.fragment_customer_activity, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivCollectPayment = view.findViewById(R.id.iv_customer_details_collect_payment);
        ivCollectPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddPaymentActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        tvEmptyLogs = view.findViewById(R.id.tv_empty_logs);
        pbLogs = view.findViewById(R.id.pb_logs);

        rvLogs = view.findViewById(R.id.rv_customer_details_log);
        rvLogs.setHasFixedSize(true);
        rvLogs.setLayoutManager(new LinearLayoutManager(getContext()));

        logsList = new ArrayList<>();

        loadLogs();
    }

    private void loadLogs() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                API.ALL_LOGS + "?customer=" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONObject customer = jsonObject.getJSONObject("customer");
                                String name = customer.getString("name");
                                Logs logs = new Logs(jsonObject.getString("id"),
                                        jsonObject.getString("message"),
                                        name,
                                        jsonObject.getString("created_at"));
                                logsList.add(logs);
                            }
                            pbLogs.setVisibility(View.GONE);
                            LogsAdapter adapter = new LogsAdapter(logsList, getContext());
                            rvLogs.setAdapter(adapter);
                            if (logsList.isEmpty()){
                                tvEmptyLogs.setText("No logs yet!");
                            } else {
                                tvEmptyLogs.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "token 0ee1248c5a84e8b1e36a8a15da48c0bb7580926c");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}
