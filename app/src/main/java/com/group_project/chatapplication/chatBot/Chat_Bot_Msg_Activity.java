package com.group_project.chatapplication.chatBot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.group_project.chatapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chat_Bot_Msg_Activity extends AppCompatActivity {

    RelativeLayout chatbot_msg_layout;
    RecyclerView chatBotRecyclerView;
    ImageView back_btn;
    LottieAnimationView welcome_bot_animation;
    EditText editMsg;
    CardView btnSendMsg;
    List<ChatBot_Msg_Model> msg_modelList;
    ChatBot_Msg_Adapter chatBot_msg_adapter;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot_msg);

        msg_modelList = new ArrayList<>();

        chatbot_msg_layout = findViewById(R.id.chatbot_msg_layout);
        chatBotRecyclerView = findViewById(R.id.chatBotRecyclerView);
        welcome_bot_animation = findViewById(R.id.welcome_bot_animation);
        back_btn = findViewById(R.id.back_btn);
        editMsg = findViewById(R.id.editMsg);
        btnSendMsg = findViewById(R.id.btnSendMsg);

        chatBot_msg_adapter = new ChatBot_Msg_Adapter(msg_modelList);
        chatBotRecyclerView.setAdapter(chatBot_msg_adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        chatBotRecyclerView.setLayoutManager(llm);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = editMsg.getText().toString().trim();
                if (question.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(chatbot_msg_layout, "Type a question...", Snackbar.LENGTH_SHORT);
                    snackbar.setAnchorView(editMsg);
                    snackbar.show();
                } else {
                    addToChat(question, ChatBot_Msg_Model.SENT_BY_ME);
                    editMsg.setText("");
                    callAPI(question);
                    welcome_bot_animation.setVisibility(View.GONE);
                }
            }
        });

        if (msg_modelList.isEmpty()) {
            msg_modelList.add(new ChatBot_Msg_Model("How can I help you?", ChatBot_Msg_Model.SENT_BY_BOT));
        }
    }

    public void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msg_modelList.add(new ChatBot_Msg_Model(message, sentBy));
                chatBot_msg_adapter.notifyDataSetChanged();
                chatBotRecyclerView.smoothScrollToPosition(chatBot_msg_adapter.getItemCount());
            }
        });
    }

    public void addResponse(String response) {
        msg_modelList.remove(msg_modelList.size() - 1);
        addToChat(response, ChatBot_Msg_Model.SENT_BY_BOT);
    }

    public void callAPI(String question) {
//         okhttp
        msg_modelList.add(new ChatBot_Msg_Model("Typing...", ChatBot_Msg_Model.SENT_BY_BOT));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "text-davinci-003");
            jsonBody.put("prompt", question.trim());
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Fail to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response.body().string());
                        JSONArray jsonArray = object.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    addResponse("Fail to load response due to " + response.body().toString());

                }
            }
        });
    }

}