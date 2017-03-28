package us.shiroyama.android.firebaserealtimechat.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import us.shiroyama.android.firebaserealtimechat.R;
import us.shiroyama.android.firebaserealtimechat.util.BusHolder;

/**
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class MessageDeleteDialogFragment extends DialogFragment {
    public static final String TAG = MessageDeleteDialogFragment.class.getSimpleName();

    private static final String KEY_POSITION = "key_position";

    private static final String KEY_TITLE_RES = "key_title_res";

    private static final String KEY_MESSAGE_RES = "key_message_res";

    private int position;

    @StringRes
    private int titleRes;

    @StringRes
    private int messageRes;

    public static MessageDeleteDialogFragment newInstance(int position, @StringRes int titleRes, @StringRes int messageRes) {
        Bundle args = new Bundle(3);
        args.putInt(KEY_POSITION, position);
        args.putInt(KEY_TITLE_RES, titleRes);
        args.putInt(KEY_MESSAGE_RES, messageRes);
        MessageDeleteDialogFragment fragment = new MessageDeleteDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !getArguments().containsKey(KEY_POSITION) && !getArguments().containsKey(KEY_TITLE_RES) && !getArguments().containsKey(KEY_MESSAGE_RES)) {
            throw new IllegalArgumentException("position must be supplied.");
        }
        position = getArguments().getInt(KEY_POSITION);
        titleRes = getArguments().getInt(KEY_TITLE_RES);
        messageRes = getArguments().getInt(KEY_MESSAGE_RES);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(titleRes)
                .setMessage(messageRes)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    BusHolder.EVENT_BUS.post(new OnYesEvent(position));
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    // NOP
                })
                .create();
    }

    public static class OnYesEvent {
        public final int position;

        public OnYesEvent(int position) {
            this.position = position;
        }
    }
}
