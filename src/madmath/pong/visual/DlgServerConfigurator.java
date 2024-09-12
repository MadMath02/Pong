package madmath.pong.visual;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DlgServerConfigurator  extends JDialog implements KeyListener{
	private boolean setted;
	private int porta;
	private JLabel invitoServer;
	private JTextField server;
	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	private JButton conferma;

	public void setup() {
		this.setFocusable(true);
		this.contentPanel= new JPanel();
		this.setted=false;
		contentPanel.setLayout(new FlowLayout());
		this.addKeyListener(this);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setTitle("Inserisci la porta di ascolto");
		this.setBounds(100, 100, 200, 200);
		invitoServer=new JLabel("Inserisci la porta su cui ascoltare");
		server=new JTextField(5);
		contentPanel.add(invitoServer);
		contentPanel.add(server);
		conferma=new JButton("Conferma");
		conferma.addActionListener((e)->{
			this.setted=true;
			this.dispose();
		});
		conferma.setFocusable(false);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getContentPane().add(conferma,BorderLayout.SOUTH);
		//this.setVisible(true);
	}
	
	public DlgServerConfigurator (JFrame parent) {
		super(parent);
		this.setModal(true);
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setResizable(false);
		try{
			setup();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean hasBeenSet() {
		return this.setted;
	}

	public int sendResult() {
		String aux=server.getText();
		try{
			if(aux.equals("")||aux==null) {
				porta=1618;
			}else {
				porta=Integer.parseInt(aux);
			}
		}catch(Exception e) {
			porta=-1;
		}
		this.dispose();
		return porta;
	}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_ENTER) {
			this.setted=true;
			this.dispose();
		}
		//System.out.println(e);
	}
	public void keyReleased(KeyEvent e) {
		//System.out.println(e);
	}
	public void keyTyped(KeyEvent e) {
		//System.out.println(e);
	}
	public void unset() {
		porta=-1;
		setted=false;
	}
}
