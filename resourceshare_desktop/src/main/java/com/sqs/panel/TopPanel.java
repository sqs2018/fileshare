package com.sqs.panel;

import com.sqs.frames.LoginFrame;
import com.sqs.frames.MainFrame;
import com.sqs.res.Colors;
import com.sqs.util.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author shiqs
 * @date 2024-08
 * @description 窗口顶端显示内容
 * @since
 */
public class TopPanel extends JPanel {

	// 头像
	private JLabel icon_label;
	// 登录用户
	private JLabel name_label;

	public TopPanel() {
		initComponents();
		initView();
	}
    public void setName(String username){
		name_label.setText(username);
	}
	private void initComponents() {
		icon_label = new JLabel(IconUtil.getIcon(MainFrame.getContext(), "/image/default_head.png", 20, 20, false));
		name_label = new JLabel("未登录");

	}

	private void initView() {
		this.setPreferredSize(new Dimension(210, 40));
		this.setLayout(new FlowLayout(FlowLayout.RIGHT));

		add(icon_label);

		add(name_label);

		setBorder(BorderFactory.createLineBorder(Colors.FONT_GRAY));
		name_label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				boolean result = LoginFrame.validateLogin();
				// 登录成功
				if (result) {
					// 更新显示信息
				}

			}
		});
		// setBackground(Color.BLACK);
	}

}
