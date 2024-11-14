package com.sqs.resourceshare_android.util.tree.model;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 丕汝 on 2018/10/22.
 */

public class TreeNode implements Cloneable {
    public static int currentIDPositiion = 0;

    private boolean children;
    private boolean noCheck;
    private boolean noNodeIcon;
    private boolean select;
    private boolean parent;
    private boolean expand;
    private Long fileId;
    private String fileType;


    private int id;
    private String name;

    private int nodeIcon;
    private int expandIcon;
    private int level;

    private TreeNode parentNode;
    private List<TreeNode> nodes;


    public boolean isChildren() {
        return children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }

    public boolean isNoCheck() {
        return noCheck;
    }

    public void setNoCheck(boolean noCheck) {
        this.noCheck = noCheck;
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setData(String name,Long fileId,String fileType) {
        this.name = name;
        this.fileId=fileId;
        this.fileType=fileType;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public int getNodeIcon() {
        return nodeIcon;
    }

    public void setNodeIcon(int nodeIcon) {
        this.nodeIcon = nodeIcon;
    }

    public int getExpandIcon() {
        return expandIcon;
    }

    public void setExpandIcon(int expandIcon) {
        this.expandIcon = expandIcon;
    }

    public List<TreeNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TreeNode> nodes) {
        this.nodes = nodes;
    }

    public TreeNode() {
        this.nodes = new ArrayList<>();
        this.id = getCurrnetID();
        this.noCheck = false;
        this.expand = true;
        this.noNodeIcon = false;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isNoNodeIcon() {
        return noNodeIcon;
    }

    public void setNoNodeIcon(boolean noNodeIcon) {
        this.noNodeIcon = noNodeIcon;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    private int getCurrnetID() {
        int id = TreeNode.currentIDPositiion;
        TreeNode.currentIDPositiion += 1;
        return id;
    }

    public TreeNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(TreeNode parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Object o;
        o = super.clone();
        return o;
    }

    public static TreeNode removeChildByParentNodeID(TreeNode node, int id) {

        int curID = node.getId();
        if (curID == id) {
            node.getNodes().clear();
            return node;
        }
        for (TreeNode cN : node.getNodes()) {
            removeChildByParentNodeID(cN, id);
        }
        return null;
    }
    public void removeNode(){
        this.nodes.clear();
    }

    /**
     * 添加节点使用此方法
     *
     * @param treeNode
     */
    public void addNode(TreeNode treeNode) {
        treeNode.parentNode = this;
        this.nodes.add(treeNode);
    }

}
