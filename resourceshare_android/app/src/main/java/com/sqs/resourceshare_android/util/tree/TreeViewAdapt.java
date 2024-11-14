package com.sqs.resourceshare_android.util.tree;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.sqs.resourceshare_android.R;
import com.sqs.resourceshare_android.util.tree.model.TreeNode;
import com.sqs.resourceshare_android.util.tree.model.TreeViewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 丕汝 on 2018/10/22.
 */


public class TreeViewAdapt extends BaseAdapter {
    private final TreeNode treeNode;
    private final ListView treeView_lv;
    private List<TreeNode> treeNodeArray;//树中的元素
    private final Context context;
    private String treeViewItemClassName;//树项对象全名（包名.类名）


    private int currnetDep = 0;
    private int currnetPosition = 0;

    /**
     * item的行首缩进基数
     */
    private int indentionBase;

    /**
     * 每一个item的行高 默认100；
     */
    private int singleLineHeight;

    private boolean isRegisterLongTouchListener = false;

    //事件侦听
    private OnItemLongClickListener onItemLongClickListener = null;
    private static OnItemClickListener onItemClickListenerTemp = null;
    private int expandIconResID;
    private int unexpandIconResID;

    public TreeViewAdapt(Context context, TreeNode treeNode, ListView listView) {
        this.treeViewItemClassName = "com.sqs.resourceshare_android.util.tree.model.TreeViewItem";
        this.treeNode = treeNode;
        this.treeView_lv = listView;
        this.context = context;
        treeNodeArray = new ArrayList<>();
        indentionBase = 90;
        singleLineHeight = 100;
        expandIconResID = R.drawable.tree_item_expand_icon;
        unexpandIconResID = R.drawable.tree_item_unexpand_icon;
        paraseTreeNodeData(treeNode);
    }

    private void paraseTreeNodeData(TreeNode node) {
        node.setLevel(currnetDep);
        if (node.getNodes().size() > 0) {
            node.setParent(true);
            node.setChildren(false);
        } else {
            node.setParent(false);
            node.setChildren(true);
        }
        this.treeNodeArray.add(node);
        List<TreeNode> cNode = node.getNodes();
        if (node.isExpand()) {//展开则继续获取它的字节点
            for (TreeNode n : cNode) {
                currnetDep++;
                //设置其父节点对象
                n.setParentNode(node);
                paraseTreeNodeData(n);
                currnetDep--;
            }
        }
    }

    @Override
    public int getCount() {
        return treeNodeArray.size();
    }

    @Override
    public Object getItem(int i) {
        return treeNodeArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        TreeViewItem item = null;
        Class<TreeViewItem> treeViewItemClass;
        if (view == null) {
            try {
                treeViewItemClass = (Class<TreeViewItem>) Class.forName(this.treeViewItemClassName);
                item = treeViewItemClass.newInstance();
            } catch (ClassNotFoundException e) {
                System.out.println("class没找到，检查：" + this.treeViewItemClassName);
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                System.out.println("反射创建对象失败，确保对象：" + this.treeViewItemClassName + "继承了:TreeViewItem");
                e.printStackTrace();
                return null;
            } catch (InstantiationException e) {
                System.out.println("反射创建对象失败，确保对象：" + this.treeViewItemClassName + "继承了:TreeViewItem");
                e.printStackTrace();
                return null;
            }

            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.treeview_item, null);
            item.setExpandIconView((ImageView) view.findViewById(R.id.tree_item_expand_icon));
            item.setNodeIconView((ImageView) view.findViewById(R.id.tree_item_node_icon));
            item.setNodeIDView((TextView) view.findViewById(R.id.tree_item_node_id));
            item.setNodeNameView((TextView) view.findViewById(R.id.tree_item_name));


            view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, singleLineHeight));
            view.setTag(item);
        } else {
            //返回已经缓存的视图
            item = (TreeViewItem) view.getTag();
        }

        final TreeNode node = treeNodeArray.get(position);
        int level = node.getLevel();
        LinearLayout.LayoutParams layout =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //如果当前节点的父节点不显示复选框 则其子节点缩进等级需要增加一个复选框的大小
        int marginLeft = indentionBase * level;

        layout.setMargins(marginLeft, 5, 5, 5);
        item.getExpandIconView().setLayoutParams(layout);
        item.getNodeNameView().setText(node.getName());

        Log.i("t", node.getName());

        //设置图标
      /*  if (node.getExpandIcon() != -1) {
            item.getExpandIconView().setImageResource(node.getExpandIcon());
        }*/
        if (node.getFileType()!=null&&"folder".equals(node.getFileType())) {
            item.getNodeIconView().setBackgroundResource(R.drawable.folder);
        } else {
            item.getNodeIconView().setImageResource(R.drawable.file);
        }

        if (node.getNodeIcon() > 0) {
            item.getNodeIconView().setImageResource(node.getNodeIcon());
        }

        //默认属性是指

        //控制显隐
        setViewVisibleStatus(item.getNodeIconView(), !node.isNoNodeIcon());
        setViewVisibleStatus(item.getExpandIconView(), node.isParent());

      /*  //绑定事件
        item.getCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                node.setChecked(b);
                if (onTreeViewCheckBoxChangeListener != null) {
                    onTreeViewCheckBoxChangeListener.setOnChangeListener(compoundButton, node, b);
                }
            }
        });*/

        //设置长按事件
        if (!this.isRegisterLongTouchListener) {
            this.treeView_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (onItemLongClickListener != null) {
                        TreeNode n = treeNodeArray.get(position);
                        onItemLongClickListener.setOnItemLongClickListener(view, position, n);
                    }
                    return false;
                }
            });

            this.treeView_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (onItemClickListenerTemp != null) {
                        onItemClickListenerTemp.setOnItemClickListener(view, position, treeNodeArray.get(position));
                    }
                }
            });
            isRegisterLongTouchListener = true;
        }

        //展开关闭事件
        item.getExpandIconView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (node.isParent()) {
                    int id = node.getId();
                    //清空待显示的数据
                    treeNodeArray.clear();
                    //初始化层级计数器为0
                    currnetDep = 0;

                    if (node.isExpand()) {
                        node.setExpand(false);
                        //折叠
                        fillterVisibleNode(treeNode, id, false);
                        notifyDataSetChanged();
                    } else {
                        node.setExpand(true);
                        //展开
                        fillterVisibleNode(treeNode, id, true);

                        notifyDataSetChanged();
                    }
                }
            }
        });


        return view;
    }

    /**
     * 从原始数据中过滤出可见数据(折叠/展开指定位置)
     *
     * @param node        包含所有数据的树
     * @param clickNodeID 点击节点的ID
     * @param isExpand    true 表示要展开当前节点，false表示折叠
     */
    private void fillterVisibleNode(TreeNode node, int clickNodeID, boolean isExpand) {
        node.setLevel(currnetDep);
        if (node.getNodes().size() > 0) {
            node.setParent(true);
            node.setChildren(false);
        } else {
            node.setParent(false);
            node.setChildren(true);
        }
        this.treeNodeArray.add(node);

        List<TreeNode> cNode = node.getNodes();

        if (clickNodeID == node.getId() && !isExpand) {
            //如果当前的节点为点击的节点，并且需要执行折叠，则不继续遍历其子节点
            node.setExpandIcon(unexpandIconResID);
        } else {
            //展开
            //读取原有展开状态
            if (node.isExpand()) {
                node.setExpandIcon(expandIconResID);
                for (TreeNode n : cNode) {
                    currnetDep++;
                    fillterVisibleNode(n, clickNodeID, isExpand);
                    currnetDep--;
                }
            }
        }
    }

//    region 事件

    /**
     * 设置项的checkbox状态改变事件
     *
     * @param listener
     */


    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListenerTemp = onItemClickListener;
    }

    public interface OnTreeViewCheckBoxChangeListener {
        void setOnChangeListener(CompoundButton view, TreeNode node, boolean status);
    }

    public interface OnItemLongClickListener {
        void setOnItemLongClickListener(View view, int position, TreeNode node);
    }

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position, TreeNode node);
    }

    public interface OnItemExpandChangeListener {
        void setOnItemExpandChangeListener(View view, TreeNode node, boolean status);
    }

    private void setViewVisibleStatus(View v, boolean status) {
        if (status) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.INVISIBLE);
        }
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public ListView getTreeView_lv() {
        return treeView_lv;
    }

    public List<TreeNode> getTreeNodeArray() {
        return treeNodeArray;
    }

    /**
     * 设置树的项对象 此对象必须继承 com.wpr.treeviewcore.model.TreeViewItem
     *
     * @param treeViewItemClassName 包名.类名
     */
    public void setTreeViewItemClassName(String treeViewItemClassName) {
        this.treeViewItemClassName = treeViewItemClassName;
    }

    public int getExpandIconResID() {
        return expandIconResID;
    }

    public void setExpandIconResID(int expandIconResID) {
        this.expandIconResID = expandIconResID;
    }

    public int getUnexpandIconResID() {
        return unexpandIconResID;
    }

    public void setUnexpandIconResID(int unexpandIconResID) {
        this.unexpandIconResID = unexpandIconResID;
    }

    //endregion
}