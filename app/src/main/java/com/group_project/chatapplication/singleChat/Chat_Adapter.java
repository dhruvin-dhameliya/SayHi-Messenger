package com.group_project.chatapplication.singleChat;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group_project.chatapplication.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Chat_Adapter extends RecyclerView.Adapter {

    ArrayList<Chatmodel> chatmodels;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public Chat_Adapter(ArrayList<Chatmodel> chatmodels, Context context) {
        this.chatmodels = chatmodels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message_ui, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_message_ui, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String myMobileNo;

    {
        assert user != null;
        myMobileNo = Objects.requireNonNull(user.getPhoneNumber()).replace("+91", "");
    }

    @Override
    public int getItemViewType(int position) {
        if (chatmodels.get(position).getSender().equals(myMobileNo)) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chatmodel chatmodel = chatmodels.get(position);
        String timestamp = chatmodel.getTimestamp();
        String message = chatmodel.getMessage();//text,image
        String sender = chatmodel.getSender();
        String messageType = chatmodel.getType();

        if (messageType.equals("text")) {
            if (holder.getClass() == SenderViewHolder.class) {
                ((SenderViewHolder) holder).senderMsg.setText(chatmodel.getMessage());
                String senderMsgTime = longToDateString(Long.parseLong(chatmodel.getTimestamp()), "dd-MM-yyyy hh:mm");
                ((SenderViewHolder) holder).senderTime.setText(senderMsgTime);

                ((SenderViewHolder) holder).senderMsg.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).user_doc_msg_layout.setVisibility(View.GONE);
                ((SenderViewHolder) holder).user_img_msg_layout.setVisibility(View.GONE);

            } else {
                ((RecieverViewHolder) holder).receiverMsg.setText(chatmodel.getMessage());
                String reciverMsgTime = longToDateString(Long.parseLong(chatmodel.getTimestamp()), "dd-MM-yyyy hh:mm");
                ((RecieverViewHolder) holder).receiverTime.setText(reciverMsgTime);

                ((RecieverViewHolder) holder).receiverMsg.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).user_doc_msg_layout.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).user_img_msg_layout.setVisibility(View.GONE);
            }

        } else if (messageType.equals("image")) {
            if (holder.getClass() == SenderViewHolder.class) {
                try {
                    Glide.with(((SenderViewHolder) holder).senderImage).load(message).into(((SenderViewHolder) holder).senderImage);
                } catch (Exception e) {
                    ((SenderViewHolder) holder).senderImage.setImageResource(R.drawable.default_image_for_chat);
                }
                String senderMsgTime = longToDateString(Long.parseLong(chatmodel.getTimestamp()), "dd-MM-yyyy hh:mm");
                ((SenderViewHolder) holder).senderTime.setText(senderMsgTime);

                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).user_doc_msg_layout.setVisibility(View.GONE);


                ((SenderViewHolder) holder).senderImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Single_Chat_full_screen_photo_Activity.class);
                        intent.putExtra("image", message);
                        intent.putExtra("sender", sender);
                        context.startActivity(intent);
                    }
                });
            } else {
                try {
                    Glide.with(((RecieverViewHolder) holder).receiverImage).load(message).into(((RecieverViewHolder) holder).receiverImage);
                } catch (Exception e) {
                    ((RecieverViewHolder) holder).receiverImage.setImageResource(R.drawable.default_image_for_chat);
                }
                String receiverMsgTime = longToDateString(Long.parseLong(chatmodel.getTimestamp()), "dd-MM-yyyy hh:mm");
                ((RecieverViewHolder) holder).receiverTime.setText(receiverMsgTime);

                ((RecieverViewHolder) holder).receiverMsg.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).user_doc_msg_layout.setVisibility(View.GONE);

                ((RecieverViewHolder) holder).receiverImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Single_Chat_full_screen_photo_Activity.class);
                        intent.putExtra("image", message);
                        intent.putExtra("sender", sender);
                        context.startActivity(intent);
                    }
                });
            }
        } else { //document message
            if (holder.getClass() == SenderViewHolder.class) {
                String senderMsgTime = longToDateString(Long.parseLong(chatmodel.getTimestamp()), "dd-MM-yyyy hh:mm");
                ((SenderViewHolder) holder).senderTime.setText(senderMsgTime);

                ((SenderViewHolder) holder).user_doc_msg_layout.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderImage.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Single_Chat_Doc_WebView_Activity.class);
                        intent.putExtra("pass_pdf_url", message);
                        intent.putExtra("sender", sender);
                        context.startActivity(intent);
                    }
                });
            } else {
                String receiverMsgTime = longToDateString(Long.parseLong(chatmodel.getTimestamp()), "dd-MM-yyyy hh:mm");
                ((RecieverViewHolder) holder).receiverTime.setText(receiverMsgTime);

                ((RecieverViewHolder) holder).user_doc_msg_layout.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).receiverMsg.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).receiverImage.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).receiverFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Single_Chat_Doc_WebView_Activity.class);
                        intent.putExtra("pass_pdf_url", message);
                        intent.putExtra("sender", sender);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatmodels.size();
    }


    public class RecieverViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout user_img_msg_layout, user_doc_msg_layout;
        TextView receiverMsg, receiverTime;
        RoundedImageView receiverImage, receiverFile;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.single_user_txt_msg);
            receiverTime = itemView.findViewById(R.id.single_msg_time);
            user_img_msg_layout = itemView.findViewById(R.id.single_user_img_msg_layout);
            user_doc_msg_layout = itemView.findViewById(R.id.single_user_doc_msg_layout);
            receiverImage = itemView.findViewById(R.id.single_user_img_msg);
            receiverFile = itemView.findViewById(R.id.single_user_doc_msg);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout user_img_msg_layout, user_doc_msg_layout;
        TextView senderMsg, senderTime;
        RoundedImageView senderImage, senderFile;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.single_user_txt_msg);
            senderTime = itemView.findViewById(R.id.single_msg_time);
            user_img_msg_layout = itemView.findViewById(R.id.single_user_img_msg_layout);
            user_doc_msg_layout = itemView.findViewById(R.id.single_user_doc_msg_layout);
            senderImage = itemView.findViewById(R.id.single_user_img_msg);
            senderFile = itemView.findViewById(R.id.single_user_doc_msg);
        }
    }

    public static String longToDateString(long timestamp, String format) {
        return DateFormat.format(format, new Date(timestamp)).toString();
    }

}
