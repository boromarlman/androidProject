package us.shiroyama.android.firebaserealtimechat.model;

import com.google.firebase.database.Exclude;

/**
 * Chat Message Entity
 *
 * @author Fumihiko Shiroyama (fu.shiroyamagmail.com)
 */
public class Message {
    public static final String PATH = "messages";

    public static final String KEY_TIMESTAMP = "timestamp";

    private int type;

    private String senderUid;

    private String body;

    private String fileName;

    private String downloadUri;

    @Exclude
    private long timestamp;

    @Exclude
    private String messageId;

    public Message() {
    }

    public Message(int type, String senderUid) {
        this.type = type;
        this.senderUid = senderUid;
    }

    public Message(int type, String senderUid, String body) {
        this.type = type;
        this.senderUid = senderUid;
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isTypeNormal() {
        return getType() == MessageType.NORMAL.ordinal();
    }

    public boolean isTypeImage() {
        return getType() == MessageType.IMAGE.ordinal();
    }

}
