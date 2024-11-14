package com.sqs.panel;

import com.sqs.components.InitComponent;
import com.sqs.components.RCButton;
import com.sqs.components.Toast;
import com.sqs.frames.LoginFrame;
import com.sqs.frames.MainFrame;
import com.sqs.res.Colors;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author shiqs
 * @date 2024-08
 * @description
 * @since
 */
public class LeftPanel extends JPanel {

	// 公共文件夹
	private RCButton puf_button;
	// 私人文件夹
	private RCButton pri_button;
	// 文件上传
	private RCButton fu_button;
	// 文件下载
	private RCButton fd_button;
	// 文件下载列表
	private RCButton fdl_button;
	private MainFrame mainFrame;
	private LoginFrame loginFrame;

	private Toast messageToast;

	public LeftPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		initComponents();
		initView();
		setListeners();
	}

	public void initComponents() {
		fd_button = new RCButton("文件下载", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
		fu_button = new RCButton("文件上传", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);

		puf_button = new RCButton("公共文件", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
		pri_button = new RCButton("私人文件", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
		fdl_button = new RCButton("下载列表", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
	}

	public void initView() {
		this.setPreferredSize(new Dimension(210, MainFrame.DEFAULT_HEIGHT));
		this.setLayout(null);
		int startY = 5;
		// 文件下载
		add(fd_button);
		fd_button.setBounds(5, startY, 200, 30);
		// 默认显示文件下载
		fd_button.setSelect(true);

		startY += 35;
		// 文件上传
		add(fu_button);
		fu_button.setBounds(5, startY, 200, 30);
		startY += 35;
		// 公共文件
		add(puf_button);
		puf_button.setBounds(5, startY, 200, 30);
		startY += 35;
		// 私人文件
		add(pri_button);
		pri_button.setBounds(5, startY, 200, 30);
		//pri_button.setForeground(Colors.DARKER);
		// pri_button.setEnabled(false);
		startY += 35;
		// 下载列表
		add(fdl_button);
		fdl_button.setBounds(5, startY, 200, 30);
		setBorder(BorderFactory.createLineBorder(Colors.FONT_GRAY));

		// setBackground(Color.BLACK);
		/*
		 * messageToast = new Toast(MainFrame.getContext(), "提示信息");
		 * messageToast.addMouseListener(new MouseAdapter() {
		 * 
		 * @Override public void mouseClicked(MouseEvent e) {
		 * 
		 * 
		 * super.mouseClicked(e); } });
		 */
	}

	class LeftPanelMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getComponent() == puf_button) {
				clickMenu("puf_button");
			} else if (e.getComponent() == pri_button) {
				clickMenu("pri_button");
			} else if (e.getComponent() == fu_button) {
				clickMenu("fu_button");
			} else if (e.getComponent() == fd_button) {
				clickMenu("fd_button");
			} else if (e.getComponent() == fdl_button) {
				clickMenu("fdl_button");
			}

			super.mouseClicked(e);
		}
	}

	/***
	 * 点击按钮事件
	 * @param button
	 */
	public void clickMenu(String button){
		if (button.equals("puf_button")) {
			resetButton();
			puf_button.setSelect(true);
			mainFrame.getRightPanel().showPanel("publicPanel");
		} else if (button.equals("pri_button")) {
			/*
			 * messageToast.setMessage("请登录系统"); messageToast.setVisible(true);
			 */
			boolean result = LoginFrame.validateLogin();
			// 登录成功
			if (result) {
				// 逻辑待加
				resetButton();
				pri_button.setSelect(true);
				mainFrame.getRightPanel().showPanel("privatePersonPanel");
			}
		}
		//文件上传
		else  if (button.equals("fu_button")) {
			resetButton();
			fu_button.setSelect(true);
			mainFrame.getRightPanel().showPanel("uploadPanel");
		} else  if (button.equals("fd_button")) {
			resetButton();
			mainFrame.getRightPanel().showPanel("downloadPanel");
			fd_button.setSelect(true);
			fd_button.setForeground(Colors.SCROLL_BAR_TRACK_LIGHT);
		} else  if (button.equals("fdl_button")) {
			resetButton();
			mainFrame.getRightPanel().showPanel("downLoadListPanel");
			fdl_button.setForeground(Colors.SCROLL_BAR_TRACK_LIGHT);
			fdl_button.setSelect(true);
		}
	}

	private void resetButton() {
		// 公共文件夹
		if (puf_button.isSelected()) {
			puf_button.setSelect(false);
			puf_button.repaint();
		}
		// 私人文件夹
		if (pri_button.isSelected()) {
			pri_button.setSelect(false);
			pri_button.repaint();
		}
		// 文件上传
		if (fu_button.isSelected()) {
			fu_button.setSelect(false);
			fu_button.repaint();
		}
		// 文件下载
		if (fd_button.isSelected()) {
			fd_button.setSelect(false);
			fd_button.repaint();
		}
		// 文件下载列表

		if (fdl_button.isSelected()) {
			fdl_button.setSelect(false);
			fdl_button.repaint();
		}
	}

	public void setListeners() {
		LeftPanelMouseAdapter leftPanelMouseAdapter = new LeftPanelMouseAdapter();
		puf_button.addMouseListener(leftPanelMouseAdapter);
		pri_button.addMouseListener(leftPanelMouseAdapter);
		fu_button.addMouseListener(leftPanelMouseAdapter);
		fd_button.addMouseListener(leftPanelMouseAdapter);
		fdl_button.addMouseListener(leftPanelMouseAdapter);
	}

}
