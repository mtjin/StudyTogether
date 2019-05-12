package com.mtjin.studdytogether.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.cities_view.DetailCityActivity;
import com.mtjin.studdytogether.view.CommentActivity;
import com.mtjin.studdytogether.rtdb_model.StudyMessage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>  {
    Context context;
    ArrayList<StudyMessage> items = new ArrayList<StudyMessage>();

    public  MessageAdapter(ArrayList<StudyMessage> items, Context context){
        this.context =  context;
        addItems(items);
    }

    @Override //어댑터에서 관리하는 아이템의 개수를 반환
    public int getItemCount() {
        return items.size();
    }

    //아이템을 추가해주고싶을때 이거쓰면됨
    public  void addItem(StudyMessage item){
        items.add(item);
    }

    //한꺼번에 추가해주고싶을떄
    public void addItems(ArrayList<StudyMessage> items){
        this.items = items;
    }

    //아이템 전부 삭제
    public void clear(){
        items.clear();
    }


    @NonNull
    @Override //뷰를 담을 수 있는 뷰홀더를 생성해줍니다.
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_message, viewGroup, false); //우리가쓸려는 chatMessage아이템의 뷰객체 생성
        return new MessageViewHolder(view); //각각의 chatMessage아이템을 위한 뷰를 담고있는 뷰홀더객체를 반환한다.
    }

    @Override //홀더가 갖고있는 뷰에 데이터들을 세팅해줍니다.
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int i) {
        final StudyMessage model = items.get(i);
        holder.titleTextView.setText(model.getTitle());
        holder.nickNameTextView.setText(model.getNickName());
        holder.ageTextView.setText(model.getAge());
        holder.messageTextView.setText(model.getContent());
        if (model.getImage().equals("basic")) { //프로필사진이 없는경우
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/studdytogether.appspot.com/o/Basisc%2FbasicProfile.png?alt=media&token=dd0e0e17-a057-40a4-ae7f-364fa529e2a3").into(holder.photoImageView);
        } else {
            Glide.with(context).load(model.getImage()).into(holder.photoImageView);
        }
        if (model.getPhoto() != "basic") { //분명 게시글사진을 업로드안하면 basic이란 이름으로 model에 저장되서 !model.getPhoto().equals("basic") 로 비교했는데 계속 사진이안떠야하는데 기본사진이뜬다.지금한건 이상한거같지만 잘되서 이부분은 이러한 조건문으로 했다.
            Glide.with(context).load(model.getPhoto()).into(holder.messageImageView);
        } else {
            //사진첨부안했으니 안올림
        }
        holder.datesTextView.setText(model.getDates());



        holder.commentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.getId();
                Intent intent = new Intent(context, CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", model.getId());
                bundle.putString("city", model.getCity());
                Log.d("TEST11", model.getId());
                Log.d("TEST11", model.getCity());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        //게시물 자세히보기 (내용물눌럿을때)
        holder.messageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailCityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", model.getId());
                bundle.putString("city", model.getCity());
                bundle.putString("messageTitle", model.getTitle());
                bundle.putString("messageDate", model.getDates());
                bundle.putString("messageNIckName", model.getNickName());
                bundle.putString("messageAge", model.getAge());
                bundle.putString("messagePhoto", model.getPhoto()); //업로드하는사진
                bundle.putString("messageImage", model.getImage()); //내 프로필사진
                bundle.putString("messageContent", model.getContent()); //내용
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });

        //게시물 자세히보기 (업로드한사진눌럿을때 위와동일코드)
        holder.messageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailCityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", model.getId());
                bundle.putString("city", model.getCity());
                bundle.putString("messageTitle", model.getTitle());
                bundle.putString("messageDate", model.getDates());
                bundle.putString("messageNIckName", model.getNickName());
                bundle.putString("messageAge", model.getAge());
                bundle.putString("messagePhoto", model.getPhoto()); //업로드하는사진
                bundle.putString("messageImage", model.getImage()); //내 프로필사진
                bundle.putString("messageContent", model.getContent()); //내용
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    //뷰들을 바인딩 해줍니다.
    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView nickNameTextView;
        TextView ageTextView;
        ImageView messageImageView;
        TextView messageTextView;
        CircleImageView photoImageView;
        TextView datesTextView;
        TextView commentTextView; //댓글

        public MessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.message_tv_title);
            nickNameTextView = itemView.findViewById(R.id.message_tv_name);
            ageTextView = itemView.findViewById(R.id.message_tv_age);
            messageImageView = itemView.findViewById(R.id.message_iv_photo); //업로드한사진
            messageTextView = itemView.findViewById(R.id.message_tv_message);
            photoImageView = itemView.findViewById(R.id.message_iv_profile); //내 프로필사진
            datesTextView = itemView.findViewById(R.id.message_tv_date); //글쓴 날짜
            commentTextView = itemView.findViewById(R.id.message_tv_comment); //댓글부분 (클릭시 댓글창으로 이동)

        }
    }
}
