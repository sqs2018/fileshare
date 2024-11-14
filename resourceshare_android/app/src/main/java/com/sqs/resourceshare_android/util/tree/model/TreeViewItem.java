package com.sqs.resourceshare_android.util.tree.model;


import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 丕汝 on 2018/10/22.
 */

public class TreeViewItem {
    private ImageView expandIconView;
    private ImageView nodeIconView;
    private TextView nodeNameView;
    private TextView nodeIDView;
    private boolean isExpand;

    public ImageView getExpandIconView() {
        return expandIconView;
    }

    public void setExpandIconView(ImageView expandIconView) {
        this.expandIconView = expandIconView;
    }

    public ImageView getNodeIconView() {
        return nodeIconView;
    }

    public void setNodeIconView(ImageView nodeIconView) {
        this.nodeIconView = nodeIconView;
    }



    public TextView getNodeNameView() {
        return nodeNameView;
    }

    public void setNodeNameView(TextView nodeNameView) {
        this.nodeNameView = nodeNameView;
    }

    public TextView getNodeIDView() {
        return nodeIDView;
    }

    public void setNodeIDView(TextView nodeIDView) {
        this.nodeIDView = nodeIDView;
    }


    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
