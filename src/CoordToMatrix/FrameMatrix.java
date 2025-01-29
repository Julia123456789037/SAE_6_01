package CoordToMatrix;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FrameMatrix extends JFrame implements ActionListener
{
	private static Color COULEUR_BLEU  = Color.decode("#1D69AA");

	private File lastFileOpened;

	private JButton btnImporter;
	private JButton btnCopier; 
	private JButton btnExporter;

	private JTextArea txtDat;

	private CoordToMatrix coordToMatrix;
	

	public FrameMatrix ()
	{
		this.coordToMatrix  = new CoordToMatrix();
		
		this.lastFileOpened = null;

		this.btnImporter    = new JButton("Importer");
		this.btnCopier      = new JButton("Copier");
		this.btnExporter    = new JButton("Exporter");

		this.txtDat = new JTextArea("Importer un fichier correspondant a la structure demander.");


		JPanel panelImporter = new JPanel( new BorderLayout()); 
		panelImporter.add(this.panelTitre("Génération fichier de selon Mistic", COULEUR_BLEU),BorderLayout.CENTER);

		JPanel panelBtn = new JPanel();
		panelBtn.add(this.btnImporter);
		panelImporter.add(panelBtn, BorderLayout.SOUTH);

		
		JPanel panelExporter = new JPanel( new BorderLayout(0,20  ) ); 
		JPanel panelSouth    = new JPanel( new FlowLayout  (FlowLayout.CENTER) );
		
		panelSouth.add(btnCopier);
		panelSouth.add(btnExporter);


		JScrollPane sp = new JScrollPane();
		sp.setViewportView(txtDat);

		panelExporter.add(sp    , BorderLayout.CENTER);
		panelExporter.add(panelSouth, BorderLayout.SOUTH );


		this.styliserBouton(this.btnImporter, FrameMatrix.COULEUR_BLEU);
		this.styliserBouton(this.btnCopier  , FrameMatrix.COULEUR_BLEU);
		this.styliserBouton(this.btnExporter, FrameMatrix.COULEUR_BLEU);

		Border border = BorderFactory.createLineBorder(FrameMatrix.COULEUR_BLEU);
		border        = BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10));

		txtDat.setEditable(false);
		txtDat.setWrapStyleWord(true);
		
		txtDat.setBorder(border);


		this.setLayout(new BorderLayout(10,30));
		this.add(panelImporter, BorderLayout.NORTH);
		this.add(panelExporter, BorderLayout.CENTER);

		// Activer les bouttons
		this.btnImporter.addActionListener(this);
		this.btnCopier  .addActionListener(this);
		this.btnExporter.addActionListener(this);

		this.btnExporter.setEnabled(false);
		this.btnCopier  .setEnabled(false);

		this.txtDat.setEditable(false);

		this.setVisible(true);
		this.setSize(600, 400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		new FrameMatrix();
	}

	public void actionPerformed(ActionEvent e) 
	{
		// Importer le fichier
		if (e.getSource() == btnImporter) 
		{
			// Récuperer le fichier choisi 
			File fileChosed = this.selectionnerFichier("Choissisez le fichier a importer", this.lastFileOpened);

			// Si un fichier a été choisi
			if (fileChosed != null)
			{
				this.btnExporter.setEnabled(false);
				this.btnCopier  .setEnabled(false);

				if (this.coordToMatrix.chargerFichier(fileChosed)) 
				{
					this.txtDat.setText(coordToMatrix.getText());
					this.btnExporter.setEnabled(true);
					this.btnCopier  .setEnabled(true);
				} else {
					this.txtDat.setText("Erreur lors de l'importation du fichier.");
				}

				this.lastFileOpened = fileChosed;
			}
		}

		// Copier le contenu du JTextArea dans le presse papier
		if (e.getSource() == btnCopier) 
		{
            StringSelection stringSelection = new StringSelection(this.txtDat.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
		}

		// Exporter le contenu du JTextArea dans un fichier
		if (e.getSource() == btnExporter) 
		{
			this.telechargerDat(lastFileOpened);
		}

	}

	
	
	private File selectionnerFichier(String dialogue, File cheminOrigine)
	{
		JFileChooser selectionFichier = new JFileChooser();

		if (cheminOrigine != null)
		{
			selectionFichier.setCurrentDirectory(cheminOrigine);
		}

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


	private JPanel panelTitre(String titre, Color coul)
	{
		JPanel panelTitre = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel titreLbl = new JLabel(titre);
		titreLbl.setFont(new Font("Montserrat", Font.BOLD, 24));

		titreLbl  .setForeground(coul   );	// Couleur du texte
		panelTitre.setBackground(null);	// Couleur du fond

		panelTitre.add(titreLbl);
		
		return panelTitre;
	}

	private void styliserBouton(JButton btn, Color coul)
	{
		btn.setBorder(BorderFactory.createLineBorder(coul.darker(), 2));
		btn.setBackground(coul);
		btn.setFocusable(false);
		btn.setForeground(Color.WHITE);

		Dimension dim = new Dimension(100, 30);
		btn.setSize(dim);
		btn.setPreferredSize(dim);
	}

	

	private void telechargerDat(File repertoireBase) 
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
			this.coordToMatrix.generDat(fichierAEnregistrer.getAbsolutePath());
			this.lastFileOpened = fichierAEnregistrer;
		}

		
	}
	
}
