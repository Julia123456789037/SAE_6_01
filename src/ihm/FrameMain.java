package ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import metier.OutilSumo;
import metier.ToSUMO;

public class FrameMain extends JFrame implements ActionListener
{
	public static Color COULEUR  = Color.decode("#2B6777");

	private OutilSumo os;
	private ToSUMO    ts;

	private PanelExemple[] panels;

	private JButton btnSuivant;
	private JButton btnPrecedent;

	private int ind;


	public FrameMain ()
	{
		this.os  = new OutilSumo();
		this.ts  = new ToSUMO   (this.os);
		
		this.panels = new PanelExemple[3];
		this.ind = 0;
		this.panels[0] = new PanelImport (this);
		this.panels[1] = new PanelResults(this);
		this.panels[2] = new PanelFichier(this);

		this.btnSuivant   = FrameMain.styliserBouton("Suivant");
		this.btnPrecedent = FrameMain.styliserBouton("Precedent");

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
		this.panels[this.ind].setBorder(new EmptyBorder(10,10,10,10));
		
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

	

	public static JButton styliserBouton(String txt)
	{
		JButton btn = new JButton(txt);

		btn.setBorder(BorderFactory.createLineBorder(FrameMain.COULEUR.darker(), 2));
		btn.setBackground(FrameMain.COULEUR);
		btn.setFocusable(false);
		btn.setForeground(Color.WHITE);

		Dimension dim = new Dimension(100, 30);
		btn.setSize(dim);
		btn.setPreferredSize(dim);

		return btn;
	}


	public static JTextArea styliserTextArea(String s)
	{
		JTextArea txt = new JTextArea(s);
		
		Border border = BorderFactory.createLineBorder(FrameMain.COULEUR);
		border        = BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// txt.setLineWrap(true);
		txt.setWrapStyleWord(true);
		txt.setBorder(border);


		return txt;
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
	
	

	public void telechargerContenue(String file, String ext) 
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
			this.os.genererFichier(file, ext, cheminFichier);
		}
	}




	public void telechargerSumo() 
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnValue = fileChooser.showDialog(null, "Sélectionner un dossier");

		if (returnValue == JFileChooser.APPROVE_OPTION) 
		{
			File selectedFolder = fileChooser.getSelectedFile();

			String nomFichier = JOptionPane.showInputDialog(null, "Entrez un nom de fichier :");

			if (nomFichier != null && !nomFichier.trim().isEmpty()) 
			{
				String nom = selectedFolder.getAbsolutePath() + "/" + nomFichier;
				this.os.genererFichier(this.ts.getRouXML()              , ToSUMO.EXTENSION_ROUTE     , nom);
				this.os.genererFichier(this.ts.getNetXML()              , ToSUMO.EXTENSION_MAP       , nom);
				this.os.genererFichier(this.ts.getSimulation(nomFichier), ToSUMO.EXTENSION_SIMULATION, nom);
				
				// Generer le fichier SUMO/sumex_view_setting.xml
			}
		}
	}

	public void telechargerImage()
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
			this.os.telechargerImage(cheminFichier);
		}
	}

	public void traiterRes(String s)
	{
		this.os.traiterRes(s);
		this.maj();
	}


	public void genererImage()
	{
		this.os.genererGraphe();
	}

	public String        getRes   () { return this.os. getRes   (); }
	public String        getNetXML() { return this.ts.getNetXML(); }
	public String        getRouXML() { return this.ts.getRouXML(); }
	public BufferedImage getImage () { return this.os. getImage (); }

	

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
