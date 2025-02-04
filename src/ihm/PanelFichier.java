package ihm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import metier.ToSUMO;


public class PanelFichier extends PanelExemple implements ActionListener
{
	private JButton btnFicMap;
	private JButton btnImage;

	private JLabel lblImage;
	
	public PanelFichier (FrameMain frame)
	{
		super(frame);
		this.setLayout(new BorderLayout());
		this.add(FrameMain.panelTitre("Exporter les fichiers", FrameMain.COULEUR), BorderLayout.NORTH);


		this.btnFicMap    = FrameMain.styliserBouton("Fichier Sumo");
		this.btnImage     = FrameMain.styliserBouton("Image");
		this.lblImage	  = new JLabel();

		JPanel panelEast = new JPanel(new GridLayout(2,1, 0, 3));

		panelEast.add(this.btnFicMap);
		panelEast.add(this.btnImage);

		this.add(panelEast, BorderLayout.EAST);	

		JScrollPane sp = new JScrollPane();
		sp.setViewportView(this.lblImage);
		this.add(sp, BorderLayout.CENTER);


		this.btnFicMap.addActionListener(this);
		this.btnImage.addActionListener(this);	
	}

	public void dessinerInterface() 
	{
		this.frame.genererImage();

		ImageIcon img = new ImageIcon(this.frame.getImage());
		this.lblImage.setIcon(img);
	}

	public boolean peutSuivant() {
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.btnFicMap) {
			this.frame.telechargerSumo();
		}

		if (e.getSource() == this.btnImage) {
			this.frame.telechargerImage();
		}
	}
}
