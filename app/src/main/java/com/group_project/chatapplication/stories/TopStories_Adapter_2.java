package com.group_project.chatapplication.stories;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlomi.circularstatusview.CircularStatusView;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStories_Adapter_2 extends RecyclerView.Adapter<TopStories_Adapter_2.TopStatusViewHolder> {
    Context context;
    ArrayList<UserStories_Model> userStatuses;

    public TopStories_Adapter_2(Context context, ArrayList<UserStories_Model> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stories_gridview, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {
        try {
            UserStories_Model userStories_model3 = userStatuses.get(position);
            Stories_Model lastStory = userStories_model3.getStatuses().get(userStories_model3.getStatuses().size() - 1);
            byte[] data = Base64.decode(lastStory.getImageUrl(), Base64.DEFAULT);
            String text = new String(data, StandardCharsets.UTF_8);
            Glide.with(context).load(text).into(holder.square_img_show_story);
            Glide.with(holder.status_outer_img_2.getContext()).load(userStories_model3.getProfileImage()).into(holder.status_outer_img_2);
            holder.txt_set_story_user_name.setText(userStories_model3.getName());
            holder.circular_status_view_2.setPortionsCount(userStories_model3.getStatuses().size());
            holder.square_img_show_story.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<MyStory> myStories = new ArrayList<>();
                    for (Stories_Model story : userStories_model3.getStatuses()) {
                        byte[] data = Base64.decode(story.getImageUrl(), Base64.DEFAULT);
                        String text = new String(data, StandardCharsets.UTF_8);
                        myStories.add(new MyStory(text, new Date(story.getTimestamp())));
                    }
                    new StoryView.Builder(((MainActivity) context).getSupportFragmentManager()).setStoriesList(myStories) // Required
                            .setStoryDuration(5000L) // Default is 2000 Millis (2 Seconds)
                            .setTitleText(userStories_model3.getName()) // Default is Hidden
//                            .setSubtitleText(longToDateString(lastStory.getTimestamp(), "dd-MM-yyyy hh:mm a")) // Default is Hidden
                            .setTitleLogoUrl(userStories_model3.getProfileImage()) // Default is Hidden
                            .setStoryClickListeners(new StoryClickListeners() {
                                @Override
                                public void onDescriptionClickListener(int position) {
                                    //your action
                                }

                                @Override
                                public void onTitleIconClickListener(int position) {
                                    //your action
                                }
                            }) // Optional Listeners
                            .build() // Must be called before calling show method
                            .show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public static class TopStatusViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView square_img_show_story;
        CircularStatusView circular_status_view_2;
        CircleImageView status_outer_img_2;
        TextView txt_set_story_user_name;

        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            square_img_show_story = itemView.findViewById(R.id.square_img_show_story);
            circular_status_view_2 = itemView.findViewById(R.id.circular_status_view_2);
            status_outer_img_2 = itemView.findViewById(R.id.status_outer_img_2);
            txt_set_story_user_name = itemView.findViewById(R.id.txt_set_story_user_name);
        }
    }

    public static String longToDateString(long timestamp, String format) {
        return DateFormat.format(format, new Date(timestamp)).toString();
    }

}
