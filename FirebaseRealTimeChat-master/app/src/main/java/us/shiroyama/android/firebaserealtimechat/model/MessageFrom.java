package us.shiroyama.android.firebaserealtimechat.model;

/**
 * Message From
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public enum MessageFrom {
    ME(1), OTHERS(1 << 1);

    private int flag;

    MessageFrom(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}
