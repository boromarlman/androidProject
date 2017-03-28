package us.shiroyama.android.firebaserealtimechat.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * RecyclerViewのtop/bottomに到達したことを知らせるリスナ
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class ScrollEdgeListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager linearLayoutManager;

    private int firstVisibleBackUp = 0;

    private int lastVisibleBackUp = 0;

    public ScrollEdgeListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

        if (totalItemCount <= visibleItemCount) {
            return;
        }

        // swipe down（つまり上スクロール）
        if (dy < 0) {
            if (firstVisibleBackUp != firstVisibleItemPosition) {
                if (firstVisibleItemPosition == 0) {
                    onTop();
                }
                firstVisibleBackUp = firstVisibleItemPosition;
            }
            return;
        }

        // swipe up（つまり下スクロール）
        if (dy > 0) {
            if (lastVisibleBackUp != lastVisibleItemPosition) {
                if (lastVisibleItemPosition == (totalItemCount - 1)) {
                    onBottom();
                }
                lastVisibleBackUp = lastVisibleItemPosition;
            }
        }
    }

    public void onTop() {

    }

    public void onBottom() {

    }
}
