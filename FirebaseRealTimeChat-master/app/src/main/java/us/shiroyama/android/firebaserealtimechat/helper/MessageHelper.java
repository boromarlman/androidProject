package us.shiroyama.android.firebaserealtimechat.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import us.shiroyama.android.firebaserealtimechat.BuildConfig;
import us.shiroyama.android.firebaserealtimechat.R;
import us.shiroyama.android.firebaserealtimechat.di.scope.ActivityScope;
import us.shiroyama.android.firebaserealtimechat.fragment.MessageDeleteDialogFragment;
import us.shiroyama.android.firebaserealtimechat.model.LoginInfo;
import us.shiroyama.android.firebaserealtimechat.model.Message;
import us.shiroyama.android.firebaserealtimechat.model.MessageType;
import us.shiroyama.android.firebaserealtimechat.widget.ChatAdapter;
import us.shiroyama.android.firebaserealtimechat.widget.ScrollEdgeListener;

/**
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
@ActivityScope
public class MessageHelper {
    private static final String TAG = MessageHelper.class.getSimpleName();

    private static final int LIMIT = BuildConfig.MESSAGE_LIMIT_COUNT;

    private final DatabaseReference databaseReference;

    private final AppCompatActivity activity;

    private final Bus bus;

    @Nullable
    private List<Message> messages;

    @Nullable
    private ChatAdapter chatAdapter;

    @Nullable
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    private RecyclerView recyclerView;

    @Nullable
    private LoginInfo loginInfo;

    private long firstTimestamp = 0L;

    private long lastTimestamp = 0L;

    private ScrollEdgeListener onScrollListener;

    private ValueEventListener singleShotListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Log.d(TAG, "message: " + snapshot.toString());
                Message message = getMessageWithId(snapshot);
                if (isInitialized()) {
                    messages.add(message);
                }
                updateTimestamp(message.getTimestamp());
            }

            if (isInitialized()) {
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            databaseReference.child(Message.PATH).orderByChild(Message.KEY_TIMESTAMP).startAt(lastTimestamp + 1).addChildEventListener(childAddListener);
            databaseReference.child(Message.PATH).orderByChild(Message.KEY_TIMESTAMP).addChildEventListener(childRemoveListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            handleError(databaseError.toException(), R.string.message_fetch_error);
        }
    };

    private ChildEventListener childAddListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "onChildAdded: message: " + dataSnapshot.toString());
            Message message = getMessageWithId(dataSnapshot);
            if (isInitialized()) {
                messages.add(message);
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            // NOP
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            // NOP
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            // NOP
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            handleError(databaseError.toException(), R.string.message_fetch_error);
        }
    };

    private ChildEventListener childRemoveListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // NOP
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            // NOP
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved: message: " + dataSnapshot.toString());
            String messageId = dataSnapshot.getKey();
            synchronized (messages) {
                Stream.of(messages)
                        .filter(message -> messageId.equals(message.getMessageId()))
                        .findFirst()
                        .ifPresent(deletedMessage -> {
                            int position = messages.indexOf(deletedMessage);
                            messages.remove(position);
                            chatAdapter.notifyDataSetChanged();
                        });
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            // NOP
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            handleError(databaseError.toException(), R.string.message_fetch_error);
        }
    };

    @Inject
    public MessageHelper(DatabaseReference databaseReference, AppCompatActivity activity, Bus bus) {
        this.databaseReference = databaseReference;
        this.activity = activity;
        this.bus = bus;
    }

    public void onCreate(List<Message> messageListItems, ChatAdapter chatAdapter, SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView, LoginInfo loginInfo) {
        this.messages = messageListItems;
        this.chatAdapter = chatAdapter;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.recyclerView = recyclerView;
        this.loginInfo = loginInfo;

        databaseReference.child(Message.PATH).orderByChild(Message.KEY_TIMESTAMP).limitToLast(LIMIT).addListenerForSingleValueEvent(singleShotListener);

        onScrollListener = new ScrollEdgeListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onTop() {
                Log.d(TAG, "onTop");
                databaseReference.child(Message.PATH).orderByChild(Message.KEY_TIMESTAMP).endAt(firstTimestamp - 1).limitToLast(LIMIT).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            return;
                        }
                        List<Message> oldMessages = Stream.of(dataSnapshot.getChildren())
                                .map(snapshot -> {
                                    Message message = snapshot.getValue(Message.class);
                                    updateTimestamp(message.getTimestamp());
                                    return message;
                                })
                                .collect(Collectors.toList());

                        synchronized (messages) {
                            List<Message> newMessages = new ArrayList<>(oldMessages.size() + messages.size());
                            newMessages.addAll(oldMessages);
                            newMessages.addAll(messages);
                            messages.clear();
                            messages.addAll(newMessages);
                        }

                        int addedItemCount = (int) dataSnapshot.getChildrenCount();
                        recyclerView.scrollToPosition(addedItemCount);
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        handleError(databaseError.toException(), R.string.message_fetch_error);
                    }
                });
            }

            @Override
            public void onBottom() {
                Log.d(TAG, "onBottom");
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
    }

    public boolean isInitialized() {
        return messages != null && chatAdapter != null && swipeRefreshLayout != null && recyclerView != null && loginInfo != null;
    }

    public void onRefresh() {
        firstTimestamp = 0L;
        lastTimestamp = 0L;
        if (isInitialized()) {
            messages.clear();
            chatAdapter.notifyDataSetChanged();
        }
        databaseReference.child(Message.PATH).removeEventListener(childAddListener);
        databaseReference.child(Message.PATH).removeEventListener(childRemoveListener);
        databaseReference.child(Message.PATH).orderByChild(Message.KEY_TIMESTAMP).limitToLast(LIMIT).addListenerForSingleValueEvent(singleShotListener);
    }

    public void onDestroy() {
        databaseReference.child(Message.PATH).removeEventListener(childAddListener);
        databaseReference.child(Message.PATH).removeEventListener(childRemoveListener);
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(onScrollListener);
        }
    }

    public void send(@NonNull EditText textField) {
        String body = textField.getText().toString();
        if (TextUtils.isEmpty(body)) {
            return;
        }
        Log.d(TAG, "message body: " + body);
        Message message = new Message(MessageType.NORMAL.ordinal(), loginInfo.getUid(), body);
        DatabaseReference newMessage = databaseReference.child(Message.PATH).push();

        newMessage
                .setValue(message)
                .addOnSuccessListener(result -> {
                    Log.d(TAG, "try message send.");
                    newMessage
                            // update timestamp using server value
                            .updateChildren(new HashMap<String, Object>(1) {{
                                put(Message.KEY_TIMESTAMP, ServerValue.TIMESTAMP);
                            }})
                            .addOnSuccessListener(command -> Log.d(TAG, "timestamp update ok."))
                            .addOnFailureListener(error -> Log.e(TAG, "timestamp update failure.", error));
                    textField.setText("");
                })
                .addOnFailureListener(error -> handleError(error, R.string.message_sent_error));
    }

    private Message getMessageWithId(@NonNull DataSnapshot snapshot) {
        Message message = snapshot.getValue(Message.class);
        message.setMessageId(snapshot.getKey());
        return message;
    }

    private void updateTimestamp(long timestamp) {
        if (firstTimestamp == 0L || firstTimestamp > timestamp) {
            firstTimestamp = timestamp;
        }
        if (lastTimestamp == 0L || lastTimestamp < timestamp) {
            lastTimestamp = timestamp;
        }
    }

    private void handleError(@NonNull Throwable error, @StringRes int messageRes) {
        if (isInitialized()) {
            Snackbar.make(recyclerView, R.string.message_fetch_error, Snackbar.LENGTH_SHORT).show();
        }
        Log.e(TAG, error.getMessage(), error);
    }

    public void onClick(int position) {
        Log.d(TAG, "onClick: position: " + position);
    }

    public void onLongClick(int position) {
        Log.d(TAG, "onLongClick: position: " + position);

        if (!isInitialized()) {
            return;
        }
        Message message = messages.get(position);
        if (loginInfo.senderIsMe(message.getSenderUid())) {
            if (message.isTypeNormal()) {
                MessageDeleteDialogFragment.newInstance(position, R.string.dialog_title_delete_message, R.string.dialog_message_delete_message)
                        .show(activity.getSupportFragmentManager(), MessageDeleteDialogFragment.TAG);
            } else if (message.isTypeImage()) {
                MessageDeleteDialogFragment.newInstance(position, R.string.dialog_title_delete_image, R.string.dialog_message_delete_image)
                        .show(activity.getSupportFragmentManager(), MessageDeleteDialogFragment.TAG);
            }
        }
    }

    public void onItemDeleteYes(int position) {
        Log.d(TAG, "onItemDeleteYes: position: " + position);

        Message message = messages.get(position);
        databaseReference
                .child(Message.PATH)
                .child(message.getMessageId())
                .removeValue()
                .addOnSuccessListener(result -> {
                    Log.d(TAG, "delete OK");

                    // 画像の場合はRealtimeDatabaseからMessageレコード後にStorageの削除を要求する
                    if (message.isTypeImage()) {
                        bus.post(new ImageDeleteRequestEvent(message.getFileName()));
                    }
                })
                .addOnFailureListener(error -> handleError(error, R.string.message_remove_error));
    }

    public void onImageUploadSuccess(StorageHelper.UploadSuccessEvent event) {
        Message message = new Message(MessageType.IMAGE.ordinal(), loginInfo.getUid());
        message.setFileName(event.fileName);
        message.setDownloadUri(event.downloadUrl.toString());
        DatabaseReference newImage = databaseReference.child(Message.PATH).push();
        newImage.setValue(message)
                .addOnSuccessListener(result -> {
                    Log.d(TAG, "try image send.");
                    newImage
                            // update timestamp using server value
                            .updateChildren(new HashMap<String, Object>(1) {{
                                put(Message.KEY_TIMESTAMP, ServerValue.TIMESTAMP);
                            }})
                            .addOnSuccessListener(command -> {
                                Log.d(TAG, "timestamp update ok.");
                                bus.post(new ImageUploadResultEvent(true, null));
                                chatAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(error -> {
                                Log.e(TAG, "timestamp update failure.", error);
                                bus.post(new ImageUploadResultEvent(false, error));
                            });
                })
                .addOnFailureListener(error -> handleError(error, R.string.message_sent_error));
    }

    public static class ImageUploadResultEvent {
        public final boolean success;
        public final Throwable error;

        public ImageUploadResultEvent(boolean success, Throwable error) {
            this.success = success;
            this.error = error;
        }
    }

    public static class ImageDeleteRequestEvent {
        public final String fileName;

        public ImageDeleteRequestEvent(String fileName) {
            this.fileName = fileName;
        }
    }

}
