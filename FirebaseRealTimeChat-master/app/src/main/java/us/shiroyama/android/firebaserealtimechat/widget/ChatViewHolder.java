package us.shiroyama.android.firebaserealtimechat.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.shiroyama.android.firebaserealtimechat.R;
import us.shiroyama.android.firebaserealtimechat.model.Message;
import us.shiroyama.android.firebaserealtimechat.model.User;
import us.shiroyama.android.firebaserealtimechat.widget.transformation.CircleTransformation;

/**
 * ViewHolder
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public abstract class ChatViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = ChatViewHolder.class.getSimpleName();

    protected final Context context;

    protected final DatabaseReference databaseReference;

    protected abstract void bind(Message message);

    public ChatViewHolder(View itemView, Context context, DatabaseReference databaseReference) {
        super(itemView);
        this.context = context;
        this.databaseReference = databaseReference;
    }

    /**
     * ViewHolder for my message
     */
    static class MyMessage extends ChatViewHolder {
        @BindView(R.id.thumbnail)
        ImageView thumbnailView;
        @BindView(R.id.message)
        TextView messageView;
        @BindView(R.id.timestamp)
        RelativeTimeTextView timestampView;

        public MyMessage(View itemView, Context context, DatabaseReference databaseReference) {
            super(itemView, context, databaseReference);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void bind(Message message) {
            if (message.isTypeNormal()) {
                // TODO set default icon
                thumbnailView.setVisibility(View.INVISIBLE);
                messageView.setText(message.getBody());
                timestampView.setReferenceTime(message.getTimestamp());

                databaseReference
                        .child(User.PATH)
                        .child(message.getSenderUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "user: " + dataSnapshot.toString());
                                User user = dataSnapshot.getValue(User.class);
                                if (!TextUtils.isEmpty(user.getThumbnail())) {
                                    Picasso.with(context)
                                            .load(user.getThumbnail())
                                            .transform(new CircleTransformation())
                                            .into(thumbnailView);
                                    thumbnailView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                            }
                        });
            }
        }
    }

    /**
     * ViewHolder for others message
     */
    static class OthersMessage extends ChatViewHolder {
        @BindView(R.id.thumbnail)
        ImageView thumbnailView;
        @BindView(R.id.message)
        TextView messageView;
        @BindView(R.id.timestamp)
        RelativeTimeTextView timestampView;

        public OthersMessage(View itemView, Context context, DatabaseReference databaseReference) {
            super(itemView, context, databaseReference);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void bind(Message message) {
            if (message.isTypeNormal()) {
                // TODO set default icon
                thumbnailView.setVisibility(View.INVISIBLE);
                messageView.setText(message.getBody());
                timestampView.setReferenceTime(message.getTimestamp());

                databaseReference
                        .child(User.PATH)
                        .child(message.getSenderUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "user: " + dataSnapshot.toString());
                                User user = dataSnapshot.getValue(User.class);
                                if (!TextUtils.isEmpty(user.getThumbnail())) {
                                    Picasso.with(context)
                                            .load(user.getThumbnail())
                                            .transform(new CircleTransformation())
                                            .into(thumbnailView);
                                    thumbnailView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                            }
                        });
            }
        }
    }

    /**
     * ViewHolder for my image
     */
    static class MyImage extends ChatViewHolder {
        @BindView(R.id.thumbnail)
        ImageView thumbnailView;
        @BindView(R.id.image)
        ImageView myImage;
        @BindView(R.id.timestamp)
        RelativeTimeTextView timestampView;

        public MyImage(View itemView, Context context, DatabaseReference databaseReference) {
            super(itemView, context, databaseReference);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void bind(Message message) {
            if (message.isTypeImage()) {
                // TODO set default icon
                thumbnailView.setVisibility(View.INVISIBLE);

                timestampView.setReferenceTime(message.getTimestamp());
                Picasso.with(context)
                        .load(message.getDownloadUri())
                        .into(myImage);

                databaseReference
                        .child(User.PATH)
                        .child(message.getSenderUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "user: " + dataSnapshot.toString());
                                User user = dataSnapshot.getValue(User.class);
                                if (!TextUtils.isEmpty(user.getThumbnail())) {
                                    Picasso.with(context)
                                            .load(user.getThumbnail())
                                            .transform(new CircleTransformation())
                                            .into(thumbnailView);
                                    thumbnailView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                            }
                        });
            }
        }
    }


    /**
     * ViewHolder for others image
     */
    static class OthersImage extends ChatViewHolder {
        @BindView(R.id.thumbnail)
        ImageView thumbnailView;
        @BindView(R.id.image)
        ImageView myImage;
        @BindView(R.id.timestamp)
        RelativeTimeTextView timestampView;

        public OthersImage(View itemView, Context context, DatabaseReference databaseReference) {
            super(itemView, context, databaseReference);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void bind(Message message) {
            if (message.isTypeImage()) {
                // TODO set default icon
                thumbnailView.setVisibility(View.INVISIBLE);

                timestampView.setReferenceTime(message.getTimestamp());
                Picasso.with(context)
                        .load(message.getDownloadUri())
                        .into(myImage);

                databaseReference
                        .child(User.PATH)
                        .child(message.getSenderUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "user: " + dataSnapshot.toString());
                                User user = dataSnapshot.getValue(User.class);
                                if (!TextUtils.isEmpty(user.getThumbnail())) {
                                    Picasso.with(context)
                                            .load(user.getThumbnail())
                                            .transform(new CircleTransformation())
                                            .into(thumbnailView);
                                    thumbnailView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                            }
                        });
            }
        }
    }
}
