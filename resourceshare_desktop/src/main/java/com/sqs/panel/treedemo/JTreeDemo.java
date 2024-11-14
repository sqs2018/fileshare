/*
package com.sqs.panel.treedemo;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
public class JTreeDemo extends JFrame{
    JTree tree;  //树
    DefaultTreeModel treeModel;//树的数据模型
    DefaultMutableTreeNode selectedNode;//当前被选择的树节点
    JButton addButton=new JButton("增加");
    JButton insertButton=new JButton("插入");
    JButton updateButton=new JButton("修改");
    JButton deleteButton=new JButton("删除");
    JTextField textField=new JTextField();
    public JTreeDemo() {
        JFrame frame=this;
        this.setTitle("树的示例");
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Container contentPane=this.getContentPane();
        //创建树的根节点
        DefaultMutableTreeNode root=new DefaultMutableTreeNode(
                new Profession("00","专业"));
        treeModel=new DefaultTreeModel(root);//创建树的数据模型
        tree=new JTree(treeModel);//创建树
        JPanel panel1=new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel1.add(addButton);
        panel1.add(insertButton);
        panel1.add(updateButton);
        panel1.add(deleteButton);
        contentPane.add(panel1,BorderLayout.NORTH);
        contentPane.add(new JScrollPane(tree),BorderLayout.CENTER);
        contentPane.add(textField,BorderLayout.SOUTH);
        //注册树的选择事件监听器
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                //获取当前被选择的节点
                selectedNode=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                if(selectedNode!=null) {
                    //获取当前被选择节点的用户对象路径
                    Object userObjects[]=selectedNode.getUserObjectPath();
                    String str=userObjects[0].toString();
                    for(int i=1;i<userObjects.length;i++) {
                        String str1="->"+userObjects[i].toString();
                        str+=str1;
                    }
                    textField.setText(str);
                }
            }
        });
        //增加按钮注册动作事件监听器
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //获取当前被选择的树节点
                selectedNode=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                if(selectedNode!=null) {
                    new TreeNodeDialog(frame,"增加节点",selectedNode);
                }
            }
        });
        //修改按钮注册动作事件监听器
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedNode=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                if(selectedNode!=null) {
                    new TreeNodeDialog(frame,"修改节点",selectedNode);
                }
            }
        });
        //插入按钮注册动作事件监听器
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedNode=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                if(selectedNode!=null) {
                    new TreeNodeDialog(frame,"插入节点",selectedNode);
                }
            }
        });
        //删除按钮注册动作事件监听器
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedNode=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                if(selectedNode==root) return;//不能删除根节点
                if(selectedNode!=root && selectedNode!=null) {
                    int select=JOptionPane.showConfirmDialog(null,
                            "是否确定删除？", "删除节点",JOptionPane.YES_NO_OPTION);
                    if(select==JOptionPane.YES_OPTION) {
                        //获取当前被选择节点的父节点
                        DefaultMutableTreeNode parent=
                                (DefaultMutableTreeNode)selectedNode.getParent();
                        //将当前被选择节点从其父节点中删除
                        parent.remove(selectedNode);
                        treeModel.reload(parent);//重新加载父节点
                    }
                }
            }
        });
        this.setVisible(true);
    }
    //增加和修改树的节点对话框（内部类）
    class TreeNodeDialog extends JDialog{
        JTextField codeField=new JTextField(10);
        JTextField nameField=new JTextField(10);
        JButton saveButton=new JButton("保存");
        Profession userObject;
        DefaultMutableTreeNode parentNode;
        public TreeNodeDialog(JFrame owner,String title,
                              DefaultMutableTreeNode selectedNode) {
            super(owner,true);
            this.setTitle(title);
            setSize(300,150);
            setLocationRelativeTo(null);
            setResizable(false);
            JPanel panel1=new JPanel();
            panel1.setLayout(new GridLayout(2,1));
            JPanel panel11=new JPanel();
            JPanel panel12=new JPanel();
            panel11.add(new JLabel("专业代码："));panel11.add(codeField);
            panel12.add(new JLabel("专业名称："));panel12.add(nameField);
            panel1.add(panel11);panel1.add(panel12);
            JPanel panel2=new JPanel();
            panel2.add(saveButton);
            add(panel1,BorderLayout.CENTER);
            add(panel2,BorderLayout.SOUTH);
            switch(title) {
                case "增加节点":
                case "插入节点":
                    codeField.setText("");
                    nameField.setText("");
                    break;
                case "修改节点":
                    userObject=(Profession)selectedNode.getUserObject();
                    codeField.setText(userObject.code);
                    nameField.setText(userObject.name);
                    break;
            }
            //保存按钮注册动作事件监听器
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(codeField.getText()!="" && nameField.getText()!="") {
                        switch(title) {
                            case "增加节点":
                                //创建新的用户对象
                                userObject=new Profession(codeField.getText(),
                                        nameField.getText());
                                //创建一个新的节点加入到当前被选择节点的子节点数组中
                                selectedNode.add(new DefaultMutableTreeNode(userObject));
                                break;
                            case "插入节点":
                                //创建新的用户对象
                                userObject=new Profession(codeField.getText(),
                                        nameField.getText());
                                //获取当前被选择节点的父节点
                                parentNode=(DefaultMutableTreeNode)selectedNode.getParent();
                                //获取当前被选择节点在其父节点数组中的索引
                                int i=parentNode.getIndex(selectedNode);
                                //创建一个新的节点插入到当前被选择节点的后面
                                parentNode.insert(new
                                        DefaultMutableTreeNode(userObject), i+1);
                                //重新加载parentNode节点
                                JTreeDemo.this.treeModel.reload(parentNode);
                                break;
                            case "修改节点":
                                //获取当前被选择节点的用户对象
                                userObject=(Profession)selectedNode.getUserObject();
                                userObject.code=codeField.getText();
                                userObject.name=nameField.getText();
                                break;

                        }
                        //更新加载selectedNode节点
                        JTreeDemo.this.treeModel.reload(selectedNode);
                    }
                }
            });
            setVisible(true);
        }
    }
    public static void main(String[] args) {
        JTreeDemo frame=new JTreeDemo();
    }
}

*/
