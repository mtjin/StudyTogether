package com.mtjin.studdytogether.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.cities.SeoulActivity;
import com.mtjin.studdytogether.comments.CommentActivity;
import com.mtjin.studdytogether.realtime_database.StudyMessage;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends FirebaseRecyclerAdapter<StudyMessage, MessageAdapter.MessageViewHolder> {
    Context context;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MessageAdapter(@NonNull FirebaseRecyclerOptions<StudyMessage> options, Context context) {
        super(options); //options는 파이어베이스 리사이클러뷰에 DB의 쿼리문옵션을 넣어 해당 쿼리문에 맞는 데이터들을 자동 세팅해주기 위해서 사용합니다.
        this.context = context; //Glide 서울액티비티쪽에 사용하기위해 필요
    }

    @Override //홀더가 갖고있는 뷰에 데이터들을 세팅해줍니다.
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, final int position, @NonNull StudyMessage model) {

        holder.titleTextView.setText(model.getTitle());
        holder.nickNameTextView.setText(model.getNickName());
        holder.ageTextView.setText(model.getAge());
        holder.messageTextView.setText(model.getContent());
        if (model.getImage().equals("basic")) { //프로필사진이 없는경우
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/studdytogether.appspot.com/o/Basisc%2FbasicProfile.png?alt=media&token=dd0e0e17-a057-40a4-ae7f-364fa529e2a3").into(holder.photoImageView);
        } else {
            Glide.with(context).load(model.getImage()).into(holder.photoImageView);
        }
        if (model.getPhoto() != "basic") {
            Glide.with(context).load(model.getPhoto()).into(holder.messageImageView);
        } else {
            //사진첨부안했으니 안올림
        }
        holder.datesTextView.setText(model.getDates());

    }

    @NonNull
    @Override //뷰를 담을 수 있는 뷰홀더를 생성해줍니다.
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_message, viewGroup, false); //우리가쓸려는 chatMessage아이템의 뷰객체 생성
        return new MessageViewHolder(view); //각각의 chatMessage아이템을 위한 뷰를 담고있는 뷰홀더객체를 반환한다.
    }

    //뷰들을 바인딩 해줍니다.
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
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
            commentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "ADASDd", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), CommentActivity.class);

                }
            });
        }

    }
}
