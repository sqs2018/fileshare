/*
package com.sqs.frames;

import com.sqs.components.*;
import com.sqs.res.Colors;
import com.sqs.util.FontUtil;
import com.sqs.util.IconUtil;
import com.sqs.util.OSUtil;
//import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

*/
/**
 * Created by shiqs 显示二维码和文件共享码
 *//*

public class ShowShareCodeFrame extends JFrame {
	private static final int windowWidth = 300;
	private static final int windowHeight = 300;

	private static ShowShareCodeFrame context;

	private JPanel controlPanel;
	private JLabel closeLabel;
	private JPanel editPanel;
	private RCTextField usernameField;
	// 二维码图片
	private JLabel codeImage;
	private JLabel titleLabel;
	//二维码
	private String code;

	private static Point origin = new Point();

	public ShowShareCodeFrame() {
		context = this;
		initComponents();
		initView();
		centerScreen();
		setListeners();
	}

	public ShowShareCodeFrame(String username) {
		this();
	}

	private void initComponents() {
		Dimension windowSize = new Dimension(windowWidth, windowHeight);
		setMinimumSize(windowSize);
		setMaximumSize(windowSize);

		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		// controlPanel.setBounds(0,5, windowWidth, 30);

		closeLabel = new JLabel();
		closeLabel.setIcon(IconUtil.getIcon(this, "/image/close.png", false));
		closeLabel.setHorizontalAlignment(JLabel.CENTER);
		// closeLabel.setPreferredSize(new Dimension(30,30));
		closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		titleLabel = new JLabel();
		titleLabel.setText("文件共享成功！");
		titleLabel.setFont(FontUtil.getDefaultFont(16));

		editPanel = new JPanel();
		editPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 5, true, false));

		Dimension textFieldDimension = new Dimension(200, 35);
		usernameField = new RCTextField();
		usernameField.setPlaceholder("文件共享码");
		usernameField.setPreferredSize(textFieldDimension);
		usernameField.setFont(FontUtil.getDefaultFont(14));
		usernameField.setForeground(Colors.FONT_BLACK);

		codeImage = new JLabel();
		codeImage.setPreferredSize(new Dimension(150, 150));
		codeImage.setIcon(IconUtil.getIcon(this, "/image/ic_launcher_dark.png", 20, 20, false));


		setIconImage(IconUtil.getIcon(this, "/image/ic_launcher.png", true).getImage());
	}

	private void initView() {
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new LineBorder(Colors.LIGHT_GRAY));
		contentPanel.setLayout(new GridBagLayout());

		controlPanel.add(closeLabel);

		if (OSUtil.getOsType() != OSUtil.MacOS) {
			setUndecorated(true);
			contentPanel.add(controlPanel, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1).setInsets(5, 0, 0, 0));
		}

		JPanel titlePanel = new JPanel();
		titlePanel.add(titleLabel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());

		editPanel.add(usernameField);
		editPanel.add(codeImage);

		editPanel.add(buttonPanel);

		add(contentPanel);
		contentPanel.add(titlePanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 1).setInsets(10, 10, 0, 10));
		contentPanel.add(editPanel, new GBC(0, 2).setFill(GBC.BOTH).setWeight(1, 10).setInsets(10, 10, 0, 10));
	}

	*/
/**
	 * 使窗口在屏幕中央显示
	 *//*

	private void centerScreen() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		this.setLocation((tk.getScreenSize().width - windowWidth) / 2, (tk.getScreenSize().height - windowHeight) / 2);
	}

	private void setListeners() {
		closeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// System.exit(1);
				ShowShareCodeFrame.this.setVisible(false);
				super.mouseClicked(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				closeLabel.setBackground(Colors.LIGHT_GRAY);
				super.mouseEntered(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				closeLabel.setBackground(Colors.WINDOW_BACKGROUND);
				super.mouseExited(e);
			}
		});

		if (OSUtil.getOsType() != OSUtil.MacOS) {
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					// 当鼠标按下的时候获得窗口当前的位置
					origin.x = e.getX();
					origin.y = e.getY();
				}
			});

			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					// 当鼠标拖动时获取窗口当前位置
					Point p = ShowShareCodeFrame.this.getLocation();
					// 设置窗口的位置
					ShowShareCodeFrame.this.setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
				}
			});
		}


	}
}
*/
