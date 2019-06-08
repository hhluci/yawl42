package org.yawlfoundation.yawl.editor.ui.specification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class GitInfoDialog extends JDialog implements ActionListener {

	private JTextField uriTxt;
	private JTextField nameTxt;
	private JTextField pwdTxt;
	private JTextField localPathTxt;
	private String localPath;
	private String uri;
	private String name;
	private String pwd;
	private JButton button;
	
	public GitInfoDialog(JFrame f,String s) {
		super(f,s);
		/*GridLayout gridLayout = new GridLayout(3,2,3,3);
		JPanel jPane = new JPanel();
		jPane.setLayout(gridLayout);*/
		localPathTxt=new JTextField(25);
		localPathTxt.setText(GitParas.getLocalPath());
		localPathTxt.addActionListener(this);
		uriTxt=new JTextField(25);
		uriTxt.addActionListener(this);
		uriTxt.setText(GitParas.getUri());
		nameTxt=new JTextField(25);
		nameTxt.addActionListener(this);
		nameTxt.setText(GitParas.getName());
		pwdTxt=new JTextField(25);
		pwdTxt.addActionListener(this);
		pwdTxt.setText(GitParas.getPwd());
		
		
		
		
		Box hBox00 = Box.createHorizontalBox();
		hBox00.add(new JLabel("localpath"));
		hBox00.add(localPathTxt);
		Box hBox01 = Box.createHorizontalBox();
		hBox01.add(new JLabel("         URI"));
		hBox01.add(uriTxt);
		Box hBox02 = Box.createHorizontalBox();
		hBox02.add(new JLabel("username"));
		hBox02.add(nameTxt);
		Box hBox03 = Box.createHorizontalBox();
		hBox03.add(new JLabel("password"));
		hBox03.add(pwdTxt);
		
		Box hBox04 = Box.createHorizontalBox();
		button=new JButton("ok");
		button.setSize(60, 40);
		button.addActionListener(this);
		hBox04.add(new JLabel(""));
		hBox04.add(button);
		Box vBox = Box.createVerticalBox();
		vBox.add(hBox00);
        vBox.add(hBox01);
        vBox.add(hBox02);
        vBox.add(hBox03);
        vBox.add(hBox04);
        this.setContentPane(vBox);
		
		
		
		
		setBounds(600,260,380,300);
		this.pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Get Git information from user		
		uri = uriTxt.getText();
		name = nameTxt.getText();
		pwd = pwdTxt.getText();
		localPath= localPathTxt.getText();
		setVisible(false);
	}
	public String getUri() {
		return uri;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPwd() {
		return pwd;
	}
	public String getLocalPath() {
		return localPath;
	}
	

}
