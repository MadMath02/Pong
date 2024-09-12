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

public class DlgNomiClient extends JDialog implements KeyListener{

	private boolean setted;
	private String[] nomi;
	private JTextField nome;
	private JLabel invitoNome;
	private JLabel invitoServer;
	private JTextField server;
	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	private JButton conferma;

	public void setup() {
		this.setFocusable(true);
		this.contentPanel= new JPanel();
		this.setted=false;
		this.nomi=new String[2];
		nomi[0]="";
		nomi[1]="";
		contentPanel.setLayout(new FlowLayout());
		this.addKeyListener(this);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setTitle("Inserisci i dati di gioco");
		this.setBounds(100, 100, 200, 200);
		invitoNome=new JLabel("Inserisci il nome del giocatore");
		nome=new JTextField(10);
		invitoServer=new JLabel("Inserisci l'indirizzo IP o nome di dominio del server");
		server=new JTextField(10);
		contentPanel.add(invitoNome);
		contentPanel.add(nome);
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

	public DlgNomiClient(JFrame parent) {
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

	public String[] sendResult() {
		nomi[0]=nome.getText();
		nomi[1]=server.getText();
		this.dispose();
		return nomi;
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
		nomi[0]="";
		nomi[1]="";
		setted=false;
	}

}
