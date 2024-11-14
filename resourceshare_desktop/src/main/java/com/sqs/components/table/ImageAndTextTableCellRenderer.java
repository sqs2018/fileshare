package com.sqs.components.table;

import com.sqs.util.IconUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ImageAndTextTableCellRenderer extends DefaultTableCellRenderer {


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(value!=null){
            String temp[]=value.toString().split(":");
            // 设置图标和文本
            if (temp[0].equals("folder")) {
                Icon icon= IconUtil.getIcon(this, "/fileico/folder.png", 20, 20, true);
                setIcon(icon);
            }else{
                Icon icon= IconUtil.getIcon(this, "/fileico/file.png", 20, 20, true);
                setIcon(icon);
            }
            setText(temp[1]);
        }


        // 根据需要调整其他属性，如字体、对齐方式等
        return this;
    }
}


