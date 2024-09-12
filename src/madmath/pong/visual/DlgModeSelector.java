package madmath.pong.visual;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import madmath.pong.logic.Mode;
import java.awt.Dialog;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;


public class DlgModeSelector extends JDialog {
	private ButtonGroup choice;
	private boolean setted;
	private JButton conferma;
	private HashMap<JRadioButton,Mode> map=new HashMap<>();
	public DlgModeSelector(JFrame parent) {
		super(parent);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setModal(true);
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setted=false;
		try {
			this.setTitle("Seleziona modalità di gioco");
			JRadioButton rbtLocal=new JRadioButton("Gioco locale");
			map.put(rbtLocal, Mode.LOCAL);
			JRadioButton rbtClient=new JRadioButton("Gioco online");
			map.put(rbtClient, Mode.CLIENT);
			JRadioButton rbtServer=new JRadioButton("Host per partite online");
			map.put(rbtServer,Mode.SERVER);
			choice=new ButtonGroup();
			choice.add(rbtLocal);
			choice.add(rbtClient);
			choice.add(rbtServer);
			JPanel pnl=new JPanel();
			pnl.add(rbtLocal);
			pnl.add(rbtClient);
			pnl.add(rbtServer);
			conferma=new JButton("Conferma");
			conferma.addActionListener((e)->{
				if(choice.getSelection()!=null) {
					this.setted=true;
					this.dispose();
				}
			});
			this.setBounds(200,200,200,200);
			conferma.setFocusable(false);
			pnl.add(conferma);
			this.getContentPane().add(pnl);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static final long serialVersionUID = 1L;

	public Mode getMode() {
		while(!setted);
		for(JRadioButton b:map.keySet()) {
			if(b.isSelected()) {
				return map.get(b);
			}
		}
		return Mode.LOCAL;
	}

	public void unset() {
		choice.clearSelection();
		setted=false;
	}

}
