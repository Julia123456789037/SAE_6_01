package CoordToMatrix;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FrameMatrix extends JFrame implements ActionListener
{
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


		JPanel panelImporter = new JPanel( new FlowLayout(FlowLayout.LEFT) ); 
		panelImporter.add(new JLabel("Selectionnez un fichier:"));
		panelImporter.add(btnImporter);

		
		JPanel panelExporter = new JPanel( new BorderLayout(0,20  ) ); 
		JPanel panelSouth    = new JPanel( new FlowLayout  (FlowLayout.CENTER) );
		
		panelSouth.add(btnCopier);
		panelSouth.add(btnExporter);


		panelExporter.add(txtDat    , BorderLayout.CENTER);
		panelExporter.add(panelSouth, BorderLayout.SOUTH );

		this.setLayout(new BorderLayout(30,20));
		this.add(panelImporter, BorderLayout.NORTH);
		this.add(panelExporter, BorderLayout.CENTER);


		// Activer les bouttons
		this.btnImporter.addActionListener(this);
		this.btnCopier  .addActionListener(this);
		this.btnExporter.addActionListener(this);

		this.btnExporter.setEnabled(false);
		this.btnCopier  .setEnabled(false);

		this.setVisible(true);
		this.pack();
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
			// Copier le contenu du JTextArea dans le presse papier
		}

		// Exporter le contenu du JTextArea dans un fichier
		if (e.getSource() == btnExporter) 
		{
			// Ouvrir une boite de dialogue pour selectionner un fichier
			// Sauvegarder le contenu du JTextArea dans le fichier
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
	
}
