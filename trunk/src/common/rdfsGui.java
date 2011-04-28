package common;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.*;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class rdfsGui extends javax.swing.JFrame  {

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	private JMenuItem helpMenuItem;
	private JButton downloadFileButton;
	private JButton uploadFileButton;
	private JList fileList;
	private JPanel jPanel1;
	private JMenu helpMenu;
	private JMenuItem exitMenuItem;
	private JSeparator jSeparator2;
	private JMenuItem joinSystemMenuItem;
	private JMenuItem connectUserMenuItem;
	private JMenuItem disconnectMenuItem;
	private JMenu connectionMenu;
	private JLabel statusLabel;
	private JMenuBar jMenuBar1;
	private JScrollPane scrollPane;
	
	private rdfsController rdfsControl;

	
	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				rdfsGui inst = new rdfsGui();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public rdfsGui() {
		super();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Redundant Distributed File System");
		rdfsControl = new rdfsController(this);
		
		initGUI();		
		
		rdfsControl.setGuiDefaults();
	}
	
	private void initGUI() {
		try {
			{
				jPanel1 = new JPanel();
				getContentPane().add(jPanel1, BorderLayout.CENTER);
				{
					statusLabel = new JLabel();
					jPanel1.add(statusLabel);
					GridLayout statusLabelLayout = new GridLayout(1, 1);
					statusLabelLayout.setHgap(5);
					statusLabelLayout.setVgap(5);
					statusLabelLayout.setColumns(1);
					statusLabel.setLayout(null);
					statusLabel.setText("Status: Not Connected");
				}
				{
					ListModel fileListModel = 
						new DefaultComboBoxModel(
								new String[] { 
										
										});
					fileList = new JList();
					scrollPane = new JScrollPane(fileList);
					jPanel1.add(scrollPane);
					scrollPane.setPreferredSize(new java.awt.Dimension(376, 250));
					fileList.setModel(fileListModel);
					fileList.setAutoscrolls(true);
				}
				{
					uploadFileButton = new JButton();
					FlowLayout uploadFileButtonLayout = new FlowLayout();
					uploadFileButton.setLayout(uploadFileButtonLayout);
					jPanel1.add(uploadFileButton);
					uploadFileButton.setText("Upload File");
				}
				{
					downloadFileButton = new JButton();
					FlowLayout downloadFileButtonLayout = new FlowLayout();
					downloadFileButton.setLayout(downloadFileButtonLayout);
					jPanel1.add(downloadFileButton);
					downloadFileButton.setText("Download File");
				}
			}
			this.setSize(419, 385);
			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(jMenuBar1);
				{
					connectionMenu = new JMenu();
					jMenuBar1.add(connectionMenu);
					connectionMenu.setText("Connection");
					{
						joinSystemMenuItem = new JMenuItem();
						connectionMenu.add(joinSystemMenuItem);
						joinSystemMenuItem.setText("Join the file system as server...");
						joinSystemMenuItem.addActionListener(rdfsControl);
						joinSystemMenuItem.setActionCommand(rdfsController.JOIN_SYSTEM_ITEM_ACTION_COMMAND);
						
						connectUserMenuItem = new JMenuItem();
						connectionMenu.add(connectUserMenuItem);
						connectUserMenuItem.setText("Connect in user mode...");
						connectUserMenuItem.addActionListener(rdfsControl);
						connectUserMenuItem.setActionCommand(rdfsController.CONNECT_USER_ITEM_ACTION_COMMAND);
						
						disconnectMenuItem = new JMenuItem();
						connectionMenu.add(disconnectMenuItem);
						disconnectMenuItem.setText("Disconnect");
						disconnectMenuItem.addActionListener(rdfsControl);
						disconnectMenuItem.setEnabled(false);
						disconnectMenuItem.setActionCommand(rdfsController.DISCONNECT_ITEM_ACTION_COMMAND);
					}
					{
						jSeparator2 = new JSeparator();
						connectionMenu.add(jSeparator2);
					}
					{
						exitMenuItem = new JMenuItem();
						connectionMenu.add(exitMenuItem);
						exitMenuItem.setText("Exit");
						exitMenuItem.addActionListener(rdfsControl);
						exitMenuItem.setActionCommand(rdfsController.EXIT_ITEM_ACTION_COMMAND);
					}
				}
				{
					helpMenu = new JMenu();
					jMenuBar1.add(helpMenu);
					helpMenu.setText("Help");
					{
						helpMenuItem = new JMenuItem();
						helpMenu.add(helpMenuItem);
						helpMenuItem.setText("Help");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deactivateAll()
	{
		toggleList(false);
		toggleButtons(false);
	}

	public void activateAll()
	{
		toggleList(true);
		toggleButtons(true);
	}
	
	public void disconnect()
	{
		toggleConnection(false);
	}
	
	public void connect()
	{
		toggleConnection(true);
	}
	
	public void changeStatus(String status)
	{
		statusLabel.setText("Status: "+status);
	}
	
	private void toggleConnection(boolean connected)
	{
		disconnectMenuItem.setEnabled(connected);
		connectUserMenuItem.setEnabled(!connected);
		joinSystemMenuItem.setEnabled(!connected);
	}
	
	private void toggleList(boolean enabled)
	{
		scrollPane.setEnabled(enabled);
	}
	
	private void toggleButtons(boolean enabled)
	{
		uploadFileButton.setEnabled(enabled);
		downloadFileButton.setEnabled(enabled);
	}

	

}
