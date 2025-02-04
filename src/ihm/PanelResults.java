package ihm;

import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PanelResults extends PanelExemple implements ActionListener
{
	private JTextArea txtRes;
	private JLabel    lblRes;
	private JButton   btnRes;

	public PanelResults (FrameMain frame)
	{
		super(frame);
		this.txtRes = FrameMain.styliserTextArea("Remplacer ce texte par les resultats de la simulation.");
		this.lblRes = new JLabel("Pas de donnée ");
		this.btnRes = FrameMain.styliserBouton("Analyser");
		
		this.setLayout(new BorderLayout());


		JScrollPane sp = new JScrollPane();
		sp.setViewportView(this.txtRes);

		this.add(sp     , BorderLayout.CENTER);
		this.add(lblRes , BorderLayout.SOUTH);
		this.add(btnRes , BorderLayout.EAST);
		this.add(FrameMain.panelTitre("Saisissez la trace d'execution de CPLEX", FrameMain.COULEUR), BorderLayout.NORTH);

		this.btnRes.addActionListener(this);
	}

	public void dessinerInterface() 
	{
		this.lblRes.setText(this.frame.getRes());
	}

	public boolean peutSuivant() 
	{
		return !this.frame.getRes().equals("Données incorrect ou non saisie");
	}


	public void actionPerformed(ActionEvent e) 
	{
		// Appeller frame avec le texte pour decomposer les resultats.
		this.frame.traiterRes(this.txtRes.getText());
	}
	
}
