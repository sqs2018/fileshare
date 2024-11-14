package com.sqs.resourceshare_android.util.tree;

/**
 * Created by 丕汝 on 2018/10/23.
 */

/**
 * 树的参数设置类
 */
public class TreeViewSetting {

    private TreeViewAdapt.OnTreeViewCheckBoxChangeListener onTreeViewCheckBoxChangeListener;
    private TreeViewAdapt.OnItemLongClickListener onItemLongClickListener;
    private TreeViewAdapt.OnItemClickListener onItemClickListener;

    private int expandIcon;
    private int unexpandIcon;

    public TreeViewAdapt.OnTreeViewCheckBoxChangeListener getOnTreeViewCheckBoxChangeListener() {
        return onTreeViewCheckBoxChangeListener;
    }

    /**
     * 设置项复选框勾选事件
     *
     * @param onTreeViewCheckBoxChangeListener
     */
    public void setOnTreeViewCheckBoxChangeListener(TreeViewAdapt.OnTreeViewCheckBoxChangeListener onTreeViewCheckBoxChangeListener) {
        this.onTreeViewCheckBoxChangeListener = onTreeViewCheckBoxChangeListener;
    }


    public TreeViewAdapt.OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(TreeViewAdapt.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public TreeViewAdapt.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(TreeViewAdapt.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public int getExpandIcon() {
        return expandIcon;
    }

    public void setExpandIcon(int expandIcon) {
        this.expandIcon = expandIcon;
    }

    public int getUnexpandIcon() {
        return unexpandIcon;
    }

    public void setUnexpandIcon(int unexpandIcon) {
        this.unexpandIcon = unexpandIcon;
    }
}
