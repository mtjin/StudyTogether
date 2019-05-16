package com.mtjin.studdytogether.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.rtdb_model.Comment;
import com.mtjin.studdytogether.activity.PhotoZoomActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    Context context;
    ArrayList<Comment> items = new ArrayList<Comment>();


    public  CommentAdapter(ArrayList<Comment> items, Context context){
        this.context =  context;
        addItems(items);
    }

    @Override //어댑터에서 관리하는 아이템의 개수를 반환
    public int getItemCount() {
        return items.size();
    }

    //아이템을 추가해주고싶을때 이거쓰면됨
    public  void addItem(Comment item){
        items.add(item);
    }

    //한꺼번에 추가해주고싶을떄
    public void addItems(ArrayList<Comment> items){
        this.items = items;
    }

    //아이템 전부 삭제
    public void clear(){
        items.clear();
    }

    @NonNull
    @Override //뷰를 담을 수 있는 뷰홀더를 생성해줍니다.
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_comment, viewGroup, false); //우리가쓸려는 chatMessage아이템의 뷰객체 생성
        return new CommentViewHolder(view); //각각의 chatMessage아이템을 위한 뷰를 담고있는 뷰홀더객체를 반환한다.
    }

    @Override //홀더가 갖고있는 뷰에 데이터들을 세팅해줍니다.
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int i) {
        final Comment model = items.get(i);
        holder.nickNameTextView.setText(model.getNickName());
        holder.ageTextView.setText(model.getAge());
        holder.dateTextView.setText(model.getDate());
        holder.messageTextView.setText(model.getMessage());
        if (model.getImage().equals("basic")) { //프로필사진이 없는경우
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/studdytogether.appspot.com/o/Basisc%2FbasicProfile.png?alt=media&token=dd0e0e17-a057-40a4-ae7f-364fa529e2a3").into(holder.profileCircleImageView);
        } else {
            Glide.with(context).load(model.getImage()).into(holder.profileCircleImageView);
        }

        holder.profileCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , PhotoZoomActivity.class);
                intent.putExtra("zoomProfilePhoto", model.getImage());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    //뷰들을 바인딩 해줍니다.
    public class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileCircleImageView;
        TextView nickNameTextView;
        TextView ageTextView;
        TextView dateTextView;
        TextView messageTextView;

        public CommentViewHolder(@NonNull final View itemView) {
            super(itemView);
            profileCircleImageView = itemView.findViewById(R.id.comment_iv_profile);
            nickNameTextView = itemView.findViewById(R.id.comment_tv_name);
            ageTextView = itemView.findViewById(R.id.comment_tv_age);
            dateTextView = itemView.findViewById(R.id.comment_tv_date);
            messageTextView = itemView.findViewById(R.id.comment_tv_message);
        }



    }
}
