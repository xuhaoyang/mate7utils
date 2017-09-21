package hk.xhy.mate7utils.ui.base;


import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import hk.xhy.android.common.ui.fragment.RecyclerFragment;
import hk.xhy.android.common.utils.ViewUtils;
import hk.xhy.android.common.widget.PullToRefreshMode;
import hk.xhy.mate7utils.R;

/**
 * Created by xuhaoyang on 6/2/16.
 */
public abstract class ListFragment<VH extends RecyclerView.ViewHolder, Item, Result, T extends ViewGroup>
        extends hk.xhy.android.common.ui.fragment.ListFragment<VH, Item, Result> implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerFragment.OnLoadMoreListener {
    private final String TAG = this.getClass().getSimpleName();

//    protected final ApiClientImpl API = ApiClient.getApi();

    protected ProgressDialog mProgressDialog;

    private View mLoadingView;
    private View mEmptyView;
    private View mErrorView;

    private boolean isLoadMore = false;
    private boolean mFirstLoaded = false;

    public Context mParentContext;


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


    @Override
    public void onLoadStart() {

    }

    @Override
    public Result onLoadInBackground() throws Exception {
        return null;
    }

    @Override
    public void onLoadComplete(Result data) {
    }

    public void retryRefresh() {
        getItemsSource().clear();
        getAdapter().notifyDataSetChanged();
        mFirstLoaded = false;
        onRefresh();
    }

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
                    public void onClick(View v) {
                        retryRefresh();
                    }
                });
            }
        }
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

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
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

    public boolean isLoadMore() {
        return isLoadMore;
    }

    public void ensureView() {
        View view = getView();
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
            mProgressDialog = new ProgressDialog(getActivity());
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


    private boolean ismFooterShow = false;
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_FOOTER = 1;

    public T mFooterLayout;//footer view
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
    public void removeFooterView() {
        mFooterLayout.removeAllViews();
    }


    /**
     * 添加新的footer view
     *
     * @param footerView
     */
    public void addFooterView(View footerView) {
        Context mContext = mParentContext == null ? getContext() : mParentContext;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (footerView == null) {
            return;
        }

        if (mFooterLayout == null) {
            mFooterLayout = (T) new RelativeLayout(mContext);
        }
        removeFooterView();

        mFooterLayout.addView(footerView, params);
        mFooterLayout.requestLayout();
    }


    /**
     * 初始化加载中布局
     *
     * @param loadingView
     */
    public void setLoadingView(View loadingView) {
        mFooterLoadingView = loadingView;
        addFooterView(loadingView);
    }

    public void setLoadingView(int loadingId) {
        Context mContext = mParentContext == null ? getContext() : mParentContext;
        setLoadingView(ViewUtils.inflate(mContext, loadingId));
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
        addFooterView(mFooterLoadEndView);
    }

    public void setLoadEndView(int loadEndId) {
        Context mContext = mParentContext == null ? getContext() : mParentContext;
        setLoadEndView(ViewUtils.inflate(mContext, loadEndId));
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
        Context mContext = mParentContext == null ? getContext() : mParentContext;

        setLoadFailedView(ViewUtils.inflate(mContext, loadFailId));
    }

    public void showLoadFailView() {
        if (mFooterLoadFailedView == null) {
            return;
        }
        addFooterView(mFooterLoadFailedView);
    }
}

