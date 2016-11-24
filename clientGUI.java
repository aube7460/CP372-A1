// Client side GUI
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.ButtonGroup;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class clientGUI extends JFrame implements ActionListener
{
  String userQuery = null;
  Socket kSocket = null;
  BufferedReader readIn = null;
  PrintWriter outputToServer = null;
 
  JPanel pnl = new JPanel();
  
  //Declare connect/disconnect buttons
  JButton btnConnect = new JButton("Connect");
  JButton btnDisconnect = new JButton("Disconnect");
  JButton btnSubmit = new JButton("Submit");
  
  //Declare all the textfields needed
  JTextField ipAddress = new JTextField(16);
  JTextField portNum = new JTextField(5);
  JTextField keyValue = new JTextField(20);
  JTextField synonymValue = new JTextField(20);
  
  //declare all the labels needed
  JLabel ipLabel = new JLabel("IP Address");
  JLabel pNumLabel = new JLabel("Port Number");
  JLabel keyLabel = new JLabel("Key");
  JLabel synonymLabel = new JLabel("Synonym");
  JTextArea outputTextarea = new JTextArea(10,20);
  
  //declare radiobuttons
  JRadioButton getRadioButton = new JRadioButton("GET");
  JRadioButton setRadioButton = new JRadioButton("SET");
  JRadioButton removeRadioButton = new JRadioButton("REMOVE");
  
  ButtonGroup choiceGroup = new ButtonGroup();
   
  clientGUI() // the frame constructor method
  {
    super("Client Side Interface"); 

	pnl.setLayout(null);	
	setBounds(100,100,500,300);
	
	choiceGroup.add(getRadioButton);
	choiceGroup.add(setRadioButton);
	choiceGroup.add(removeRadioButton);
	
	removeRadioButton.setVisible(false);
	getRadioButton.setVisible(false);
	setRadioButton.setVisible(false);
	
	outputTextarea.setEditable(false);
	
	//Put everything in its place in the panel
	ipLabel.setBounds(10,10,100,20);
	pNumLabel.setBounds(120,10,100,20);
	btnConnect.setBounds(250,20,125,30);
	btnDisconnect.setBounds(250,55,125,30);
	ipAddress.setBounds(10,50,100,20);
	portNum.setBounds(120,50,50,20);
	
	getRadioButton.setBounds(10,75,50,20);
	setRadioButton.setBounds(70,75,50,20);
	removeRadioButton.setBounds(130,75,75,20);
	
	keyLabel.setBounds(10,125,100,20);
	synonymLabel.setBounds(10,150,100,20);
	keyValue.setBounds(120,125,100,20);
	synonymValue.setBounds(120,150,100,20);
	btnSubmit.setBounds(120,175,100,20);
	outputTextarea.setBounds(10,200,400,150);
	
	outputTextarea.setVisible(false);
	btnSubmit.setVisible(false);
	synonymValue.setVisible(false);
	keyLabel.setVisible(false);
	synonymLabel.setVisible(false);
	keyValue.setVisible(false);
	
	
	//add everything to the panel
	pnl.add(ipLabel);
	pnl.add(pNumLabel);
	pnl.add(btnConnect);
	pnl.add(btnDisconnect);
	pnl.add(ipAddress);
	pnl.add(portNum);
	pnl.add(setRadioButton);
	pnl.add(removeRadioButton);
	pnl.add(getRadioButton);
	pnl.add(keyLabel);
	pnl.add(keyValue);
	pnl.add(synonymLabel);
	pnl.add(synonymValue);
	pnl.add(btnSubmit);
	pnl.add(outputTextarea);
	
	getRadioButton.addActionListener(this);
	setRadioButton.addActionListener(this);
	removeRadioButton.addActionListener(this);
	btnConnect.addActionListener(this);
	btnDisconnect.addActionListener(this);
	btnSubmit.addActionListener(this);
	
	//add the panel to the frame
    Container c = this.getContentPane(); // inherit main frame
    c.add(pnl); // add the panel to frame

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true); // display this frame
  }
  
  public void actionPerformed(ActionEvent e) {
	String ipAd = ipAddress.getText();
	int pNum = Integer.parseInt(portNum.getText());
	
	
	if (e.getSource() == btnConnect){
		
		
		btnConnect.setEnabled(false);
		ipAddress.setEnabled(false);
		portNum.setEnabled(false);
		btnDisconnect.setEnabled(true);
		
		try{
			kSocket = new Socket(ipAd,pNum);
			outputToServer = new PrintWriter(kSocket.getOutputStream(), true);
			readIn = new BufferedReader(new InputStreamReader(kSocket.getInputStream()));
			System.out.println("Connection made");
			getRadioButton.setVisible(true);
			setRadioButton.setVisible(true);
			removeRadioButton.setVisible(true);
		} catch (UnknownHostException ex){
			System.err.println("Don't know about host");
			System.exit(1);
		} catch (IOException ex){
			System.err.println("Couldn't get I/O connection");
			System.exit(1);
		}
	}
	
	if (e.getSource() == btnDisconnect){
		btnConnect.setEnabled(true);
		ipAddress.setEnabled(true);
		portNum.setEnabled(true);
		btnDisconnect.setEnabled(false);
		outputTextarea.setVisible(false);
		btnSubmit.setVisible(false);
		synonymValue.setVisible(false);
		keyLabel.setVisible(false);
		synonymLabel.setVisible(false);
		keyValue.setVisible(false);
		getRadioButton.setVisible(false);
		setRadioButton.setVisible(false);
		removeRadioButton.setVisible(false);
		choiceGroup.clearSelection();
		
		try{
			kSocket.close();
			System.out.println("Connection terminated successfully");
		}catch (IOException ex){
			System.out.println("ERROR");
		}
	}
	
	if (e.getSource() == btnSubmit){
		if (removeRadioButton.isSelected()){
			String temp = keyValue.getText();
			userQuery = "REMOVE " + temp;
			outputToServer.println(userQuery);
			userQuery = "";
		}
		else if (getRadioButton.isSelected()){
			String temp = keyValue.getText();
			userQuery = "GET " + temp;
			outputToServer.println(userQuery);
			userQuery = "";
		}
		else if (setRadioButton.isSelected()){
			String temp = keyValue.getText();
			String temp1 = synonymValue.getText();
			userQuery = "SET " + temp + " " + temp1;
			outputToServer.println(userQuery);
			userQuery = "";
		}
		try{
			Thread.sleep(1000);
			if (setRadioButton.isSelected()){
				outputTextarea.setText("Word added as synonym");
			}
			else{
				outputTextarea.setText(readIn.readLine());
			}
		}
		catch(InterruptedException exx){
			System.out.println("ERROR");
		}
		catch(IOException ee){
			System.out.println("ERROR");
		}
	}
	
	if (e.getSource() == getRadioButton){
		outputTextarea.setVisible(true);
		btnSubmit.setVisible(true);
		synonymValue.setVisible(false);
		keyLabel.setVisible(true);
		synonymLabel.setVisible(false);
		keyValue.setVisible(true);
	}
	else if (e.getSource() == setRadioButton){
		outputTextarea.setVisible(true);
		btnSubmit.setVisible(true);
		synonymValue.setVisible(true);
		keyLabel.setVisible(true);
		synonymLabel.setVisible(true);
		keyValue.setVisible(true);
	}
	else if (e.getSource() == removeRadioButton){
		outputTextarea.setVisible(true);
		btnSubmit.setVisible(true);
		synonymValue.setVisible(false);
		keyLabel.setVisible(true);
		synonymLabel.setVisible(false);
		keyValue.setVisible(true);
	}
	
	
    }
 

	public static void main(String args[]) {
		new clientGUI();
}

}