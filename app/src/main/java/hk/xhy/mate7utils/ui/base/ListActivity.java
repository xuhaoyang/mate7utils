package hk.xhy.mate7utils.ui.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hk.xhy.android.common.bind.Bind;
import hk.xhy.android.common.ui.RecyclerActivity;
import hk.xhy.android.common.ui.vh.ViewHolder;
import hk.xhy.android.common.utils.ActivityUtils;
import hk.xhy.android.common.utils.ViewUtils;
import hk.xhy.android.common.widget.PullToRefreshMode;
import hk.xhy.mate7utils.R;
import hk.xhy.mate7utils.ui.interfaces.OnDialogClickListener;


/**
 * Created by xuhaoyang on 16/9/8.
 */
public abstract class ListActivity<VH extends ViewHolder, Item, Result>
        extends hk.xhy.android.common.ui.ListActivity<VH, Item, Result>
        implements RecyclerActivity.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = this.getClass().getSimpleName();

//    protected final ApiClientImpl API = ApiClient.getApi();

    protected ProgressDialog mProgressDialog;

    private View mLoadingView;
    private View mEmptyView;
    private View mErrorView;

    private boolean isLoadMore = false;
    private boolean mFirstLoaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.addActivity(this);
        Bind.inject(this);
        // 禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Setting the mode of refresh list
     *
     * @param mode
     */
    public void setMode(PullToRefreshMode mode) {
        if (getPullToRefreshLayout() == null) {
            return;
        }
        if (mode == PullToRefreshMode.PULL_FROM_START) {
            getPullToRefreshLayout().setEnabled(true);
            getPullToRefreshLayout().setOnRefreshListener(this);
            setOnLoadMoreListener(null);
        } else if (mode == PullToRefreshMode.PULL_FROM_END) {
            getPullToRefreshLayout().setEnabled(false);
            getPullToRefreshLayout().setOnRefreshListener(null);
            setOnLoadMoreListener(this);
        } else if (mode == PullToRefreshMode.BOTH) {
            getPullToRefreshLayout().setEnabled(true);
            getPullToRefreshLayout().setOnRefreshListener(this);
            setOnLoadMoreListener(this);
        } else {
            getPullToRefreshLayout().setEnabled(false);
        }
    }

    @Override
    public void onRefresh() {
        Log.e(TAG, ">>>onRefresh");

        //显示加载效果
        if (!isLoadMore()) {
            getPullToRefreshLayout().setRefreshing(true);

        }

        isLoadMore = false;
        restartLoader();

        // 首次加载处理
        if (!mFirstLoaded) {
            ensureView();
            if (mLoadingView != null) {
                mLoadingView.setVisibility(View.VISIBLE);
            }
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.INVISIBLE);
            }
            if (mErrorView != null) {
                mErrorView.setVisibility(View.INVISIBLE);
            }
            getRecyclerView().setVisibility(View.INVISIBLE);

        }


    }

    public void retryRefresh() {
        getItemsSource().clear();
        getAdapter().notifyDataSetChanged();
        mFirstLoaded = false;
        onRefresh();
    }

    public boolean isLoadMore() {
        return isLoadMore;
    }

    public void ensureView() {
        View view = getWindow().getDecorView().getRootView();
        if (view == null) {
            return;
        }
        if (mLoadingView == null) {
            mLoadingView = view.findViewById(R.id.loading);
        }
        if (mEmptyView == null) {
            mEmptyView = view.findViewById(R.id.empty);
        }
        if (mErrorView == null) {
            mErrorView = view.findViewById(R.id.error);
        }
    }

    protected void showProgressDialog(int resId) {
        showProgressDialog(getString(resId));
    }

    protected void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onLoadStart();

    @Override
    public abstract Result onLoadInBackground() throws Exception;

    @Override
    public abstract void onLoadComplete(Result data);

    @Override
    public void onLoadError(Exception e) {
        onRefreshComplete();
        if (!isEmpty()) {
            if (ismFooterShow) {
                showLoadFailView();
            }
        } else {
            ensureView();
            if (mLoadingView != null) {
                mLoadingView.setVisibility(View.INVISIBLE);
            }

            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.INVISIBLE);
            }

            if (mErrorView != null) {
                mErrorView.setVisibility(View.VISIBLE);
                mErrorView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        retryRefresh();
                    }
                });
            }
        }
    }

    @Override
    public void onLoadMore() {
        isLoadMore = true;

        /**
         * 上拉加载更多
         */
        if (ismFooterShow) {
            showLoadingView();
        }

        forceLoad();
    }

    //该方法需放在onLoadComplete的最后调用
    @Override
    public void onRefreshComplete() {
        super.onRefreshComplete();
        isLoadMore = false;
        if (!mFirstLoaded) {
            ensureView();
            if (mLoadingView != null) {
                mLoadingView.setVisibility(View.INVISIBLE);
            }
            if (mEmptyView != null) {
                if (getItemsSource().size() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.INVISIBLE);
                }
            }
            if (mErrorView != null) {
                mErrorView.setVisibility(View.INVISIBLE);
            }
            getRecyclerView().setVisibility(View.VISIBLE);
        } else {
            if (getItemsSource().size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.INVISIBLE);
            }

        }
        mFirstLoaded = true;

        /**
         * 完成加载显示完成加载Item
         */
        if (ismFooterShow) {
            showLoadEndView();
        }

    }

    protected void setLoadingShow(boolean isShow) {
        if (mLoadingView != null) {
            if (mLoadingView instanceof TextView) {
                mLoadingView.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        }
    }

    protected void setEmptyShow(boolean isShow) {
        if (mEmptyView != null) {
            if (mEmptyView instanceof TextView) {
                mEmptyView.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        }
    }

    protected void setEmptyText(String text) {
        if (mEmptyView != null) {
            if (mEmptyView instanceof TextView) {
                ((TextView) mEmptyView).setText(text);
            }
        }
    }

    private boolean ismFooterShow = false;
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_FOOTER = 1;

    public RelativeLayout mFooterLayout;//footer view
    private View mFooterLoadingView; //分页加载中view
    private View mFooterLoadFailedView; //分页加载失败view
    private View mFooterLoadEndView; //分页加载结束view

    @Override
    public int getItemViewType(int position) {

        if (isFooterView(position) && ismFooterShow) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }

    }

    @Override
    public int getItemCount() {
        if (getItemsSource().isEmpty()) {
            return 0;
        }
        return getItemsSource().size() + getFooterViewCount();
    }

    /**
     * 返回 footer view数量
     *
     * @return
     */
    public int getFooterViewCount() {
        return ismFooterShow && !getItemsSource().isEmpty() ? 1 : 0;
    }

    /**
     * 是否是FooterView
     *
     * @param position
     * @return
     */
    private boolean isFooterView(int position) {
        return position >= getItemCount() - 1;
    }

    /**
     * 是否显示Footer
     *
     * @param isShow
     */
    public void setFooterShowEnable(boolean isShow) {
        ismFooterShow = isShow;
    }

    /**
     * 清空footer view
     */
    private void removeFooterView() {
        mFooterLayout.removeAllViews();
    }


    /**
     * 添加新的footer view
     *
     * @param footerView
     */
    private void addFooterView(View footerView) {
        if (footerView == null) {
            return;
        }

        if (mFooterLayout == null) {
            mFooterLayout = new RelativeLayout(this);
        }
        removeFooterView();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mFooterLayout.addView(footerView, params);

    }

    /**
     * 初始化加载中布局
     *
     * @param loadingView
     */
    public void setLoadingView(View loadingView) {
        mFooterLoadingView = loadingView;
//        addFooterView(loadingView);
    }

    public void setLoadingView(int loadingId) {
        setLoadingView(ViewUtils.inflate(this, loadingId));
    }

    /**
     * 显示Footer加载布局
     */
    public void showLoadingView() {
        if (mFooterLoadingView == null) {
            return;
        }
        addFooterView(mFooterLoadingView);
    }

    /**
     * 初始化全部加载完成布局
     *
     * @param loadEndView
     */
    public void setLoadEndView(View loadEndView) {
        mFooterLoadEndView = loadEndView;
//        addFooterView(mFooterLoadEndView);
    }

    public void setLoadEndView(int loadEndId) {
        setLoadEndView(ViewUtils.inflate(this, loadEndId));
    }

    public void showLoadEndView() {
        if (mFooterLoadEndView == null) {
            return;
        }
        addFooterView(mFooterLoadEndView);
    }

    /**
     * 初始加载失败布局
     *
     * @param loadFailView
     */
    public void setLoadFailedView(View loadFailView) {
        mFooterLoadFailedView = loadFailView;
        mFooterLoadFailedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadMore();
            }
        });
    }

    public void setLoadFailedView(int loadFailId) {
        setLoadFailedView(ViewUtils.inflate(this, loadFailId));
    }

    public void showLoadFailView() {
        if (mFooterLoadFailedView == null) {
            return;
        }
        addFooterView(mFooterLoadFailedView);
    }

    /**
     * 显示AlertDialog
     * @param title 标题
     * @param message 内容信息
     */
    public void showDialog(String title, String message) {
        showDialog(title, message, null);
    }

    /**
     * 显示AlertDialog
     * @param title 标题
     * @param message 内容信息
     * @param callback 对确定取消进行回调操作
     */
    public void showDialog(String title, String message, final OnDialogClickListener callback) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message);

        if (callback != null) {
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    callback.SuccessClick();

                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    callback.CancelClick();
                }
            });
        } else {
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        final AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    /**
     * 显示AlertDialog
     * @param titleId 标题resId
     * @param messageId 内容信息resId
     * @param callback 对确定取消进行回调操作
     */
    public void showDialog(int titleId, int messageId, final OnDialogClickListener callback) {
        showDialog(getString(titleId), getString(messageId), callback);
    }
}
