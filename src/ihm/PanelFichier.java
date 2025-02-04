package ihm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import metier.ToSUMO;


public class PanelFichier extends PanelExemple implements ActionListener
{
	private JButton btnFicMap;
	private JButton btnFicRoute;
	
	public PanelFichier (FrameMain frame)
	{
		super(frame);
		this.setLayout(new BorderLayout());
		this.add(FrameMain.panelTitre("Exporter les fichiers", FrameMain.COULEUR), BorderLayout.NORTH);


		this.btnFicMap    = new JButton("Fichier Map");
		this.btnFicRoute  = new JButton("Fichier Route");

		JPanel panelCentre = new JPanel();

		panelCentre.add(this.btnFicMap);
		panelCentre.add(this.btnFicRoute);

		this.add(panelCentre, BorderLayout.CENTER);	


		this.btnFicMap.addActionListener(this);
		this.btnFicRoute.addActionListener(this);	
	}

	public void dessinerInterface() {
	}

	public boolean peutSuivant() {
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.btnFicMap) {
			this.frame.telechargerContenue(this.frame.getNetXML(), ToSUMO.EXTENSION_MAP);
		}
		else if (e.getSource() == this.btnFicRoute) {
			this.frame.telechargerContenue(this.frame.getRouXML(), ToSUMO.EXTENSION_ROUTE);
		}
	}
}
