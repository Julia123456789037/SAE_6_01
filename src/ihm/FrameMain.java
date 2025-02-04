package ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import metier.OutilSumo;

public class FrameMain extends JFrame implements ActionListener
{
	public static Color COULEUR  = Color.decode("#1D69AA");

	private OutilSumo os;

	private PanelExemple[] panels;

	private JButton btnSuivant;
	private JButton btnPrecedent;

	private int ind;


	public FrameMain ()
	{
		this.os = new OutilSumo();
		
		this.panels = new PanelExemple[3];
		this.ind = 0;
		this.panels[0] = new PanelImport (this);
		this.panels[1] = new PanelResults(this);
		this.panels[2] = new PanelFichier(this);

		this.btnSuivant   = new JButton("Suivant");
		this.btnPrecedent = new JButton("Precedent");

		this.setLayout(new BorderLayout());
		this.addPanelCorrespondant();
		
		
		this.setTitle("Fichier Sumo");
		this.setVisible(true);

		this.btnSuivant  .addActionListener(this);
		this.btnPrecedent.addActionListener(this);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
	}
		
		
	private void addPanelCorrespondant() 
	{
		this.getContentPane().removeAll();


		this.add(this.panels[this.ind], BorderLayout.CENTER);
		
		JPanel panelSouth = new JPanel();
		panelSouth.add(this.btnPrecedent);
		panelSouth.add(this.btnSuivant);

		this.add(panelSouth, BorderLayout.SOUTH);
		
		this.panels[this.ind].dessinerInterface();
		this.maj();
	}

	


	public static JPanel panelTitre(String titre, Color coul)
	{
		JPanel panelTitre = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel titreLbl = new JLabel(titre);
		titreLbl.setFont(new Font("Montserrat", Font.BOLD, 24));

		titreLbl  .setForeground(coul   );	// Couleur du texte
		panelTitre.setBackground(null);	// Couleur du fond

		panelTitre.add(titreLbl);
		
		return panelTitre;
	}

	public void peutSuivant (boolean b) {this.btnSuivant.setEnabled(b && ind < this.panels.length - 1);}

	public String getTextDat () {return this.os.getTextDat();}


	
	public File getFile(String dialogue)
	{
		JFileChooser selectionFichier = new JFileChooser();

		selectionFichier.setDialogTitle(dialogue);
		selectionFichier.setApproveButtonText("Sélectionner");
		selectionFichier.setApproveButtonToolTipText("Sélectionner un fichier");
		selectionFichier.setFileFilter(new FileNameExtensionFilter("Fichier Texte", "txt"));
		selectionFichier.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int resultat = selectionFichier.showOpenDialog(this);
		if ( resultat == JFileChooser.APPROVE_OPTION)
		{
			return selectionFichier.getSelectedFile();
		}
		else
		{
			return null;
		}
	}


	public void lireDat(File file) 
	{
		this.os.chargerFichier(file);
		this.maj();
	}

	private void maj()
	{
		this.btnSuivant.setEnabled(this.panels[this.ind].peutSuivant() && ind < this.panels.length - 1);
		this.btnPrecedent.setEnabled(ind > 0);
		this.panels[this.ind].dessinerInterface();

		this.revalidate();
		this.repaint();
	}
	
	

	public void telechargerDat() 
	{
		// Demande le dossier d'enregistrement à l'utilisateur
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Spécifiez un fichier à enregistrer");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int userSelection = fileChooser.showSaveDialog(this);
		
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fichierAEnregistrer = fileChooser.getSelectedFile();
			String cheminFichier = fichierAEnregistrer.getAbsolutePath();
			
			// Enregistré le fichier 
			this.os.genererFichier(this.os.getTextDat(), ".dat", cheminFichier);
		}
	}

	public void traiterRes(String s)
	{
		this.os.traiterRes(s);
		this.maj();
	}

	public String getRes() { return this.os.getRes(); }

	

	public static void main(String[] args) {
		new FrameMain();
	}


	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == this.btnSuivant)
			this.ind++;
		else
			this.ind--;

		this.addPanelCorrespondant();
	}
}
