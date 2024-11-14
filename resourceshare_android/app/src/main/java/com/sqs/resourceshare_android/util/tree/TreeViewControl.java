package com.sqs.resourceshare_android.util.tree;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.sqs.resourceshare_android.util.tree.model.TreeNode;


public class TreeViewControl extends ListView {

    private TreeViewAdapt treeViewAdapt;
    private TreeViewSetting setting;
    private boolean isInited = false;

    public TreeViewControl(Context context) {
        super(context);
    }

    public TreeViewControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TreeViewControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 给当前树设置数据
     *
     * @param node         节点数据
     * @param isForceRefsh false:当树已经初始化，择不执行刷新，否则重新初始化该树，
     */
    public void setTreeData(TreeNode node, boolean isForceRefsh) {
        if (!isInited) {
            treeViewAdapt = new TreeViewAdapt(getContext(), node, this);
            this.setAdapter(treeViewAdapt);
            this.isInited = true;
        } else {
            if (isForceRefsh) {
                treeViewAdapt = new TreeViewAdapt(getContext(), node, this);
                this.setAdapter(treeViewAdapt);
                this.isInited = true;
            }
        }
    }

    /**
     * 设置树的参数
     *
     * @param setting
     */
    public void setTreeViewSetting(TreeViewSetting setting) {
        this.setting = setting;

        //添加配置
        if (setting != null) {

            if (setting.getOnItemLongClickListener() != null) {
                treeViewAdapt.setOnItemLongClickListener(setting.getOnItemLongClickListener());
            }

            if (setting.getOnItemClickListener() != null) {
                treeViewAdapt.setOnItemClickListener(setting.getOnItemClickListener());
            }
        }
    }

    public TreeViewAdapt getTreeViewAdapt() {
        return treeViewAdapt;
    }

    public TreeViewSetting getSetting() {
        return setting;
    }

    public boolean isInited() {
        return isInited;
    }
}
