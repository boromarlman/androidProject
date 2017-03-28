package us.shiroyama.android.firebaserealtimechat.widget;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import us.shiroyama.android.firebaserealtimechat.R;
import us.shiroyama.android.firebaserealtimechat.model.LoginInfo;
import us.shiroyama.android.firebaserealtimechat.model.Message;
import us.shiroyama.android.firebaserealtimechat.model.MessageFrom;
import us.shiroyama.android.firebaserealtimechat.model.MessageType;

/**
 * Adapter
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private static final String TAG = ChatAdapter.class.getSimpleName();

    private final AppCompatActivity activity;
    private final LayoutInflater layoutInflater;
    private final DatabaseReference databaseReference;
    private final Bus bus;

    private List<Message> messages;
    private LoginInfo loginInfo;

    @Inject
    public ChatAdapter(AppCompatActivity activity, LayoutInflater layoutInflater, DatabaseReference databaseReference, Bus bus) {
        this.activity = activity;
        this.layoutInflater = layoutInflater;
        this.databaseReference = databaseReference;
        this.bus = bus;
    }

    public void onCreate(List<Message> messages, LoginInfo loginInfo) {
        this.messages = messages;
        this.loginInfo = loginInfo;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChatViewHolder viewHolder;

        if (viewType == (MessageType.NORMAL.getFlag() | MessageFrom.ME.getFlag())) {
            View itemView = layoutInflater.inflate(R.layout.list_item_chat_me, parent, false);
            viewHolder = new ChatViewHolder.MyMessage(itemView, activity, databaseReference);
        } else if (viewType == (MessageType.NORMAL.getFlag() | MessageFrom.OTHERS.getFlag())) {
            View itemView = layoutInflater.inflate(R.layout.list_item_chat_others, parent, false);
            viewHolder = new ChatViewHolder.OthersMessage(itemView, activity, databaseReference);
        } else if (viewType == (MessageType.IMAGE.getFlag() | MessageFrom.ME.getFlag())) {
            View itemView = layoutInflater.inflate(R.layout.list_item_image_me, parent, false);
            viewHolder = new ChatViewHolder.MyImage(itemView, activity, databaseReference);
        } else if (viewType == (MessageType.IMAGE.getFlag() | MessageFrom.OTHERS.getFlag())) {
            View itemView = layoutInflater.inflate(R.layout.list_item_image_others, parent, false);
            viewHolder = new ChatViewHolder.OthersImage(itemView, activity, databaseReference);
        } else {
            throw new IllegalStateException("no such view type");
        }

        viewHolder.itemView.setOnClickListener(view -> {
            int position = viewHolder.getAdapterPosition();
            bus.post(new OnClickEvent(position));
        });
        viewHolder.itemView.setOnLongClickListener(view -> {
            int position = viewHolder.getAdapterPosition();
            bus.post(new OnLongClickEvent(position));
            return true;
        });
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (message.isTypeNormal()) {
            if (loginInfo.senderIsMe(message.getSenderUid())) {
                return MessageType.NORMAL.getFlag() | MessageFrom.ME.getFlag();
            } else {
                return MessageType.NORMAL.getFlag() | MessageFrom.OTHERS.getFlag();
            }
        } else if (message.isTypeImage()) {
            if (loginInfo.senderIsMe(message.getSenderUid())) {
                return MessageType.IMAGE.getFlag() | MessageFrom.ME.getFlag();
            } else {
                return MessageType.IMAGE.getFlag() | MessageFrom.OTHERS.getFlag();
            }
        } else {
            throw new IllegalStateException("no such message type");
        }

    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class OnClickEvent {
        public final int position;

        public OnClickEvent(int position) {
            this.position = position;
        }
    }

    public static class OnLongClickEvent {
        public final int position;

        public OnLongClickEvent(int position) {
            this.position = position;
        }
    }

}
