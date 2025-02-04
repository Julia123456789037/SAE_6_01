package ihm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PanelImport extends PanelExemple implements ActionListener
{

	private JTextArea txtDat;

	private JButton btnImporter;
	private JButton btnCopier;
	private JButton btnExporter;

	

	public PanelImport(FrameMain fm) 
	{
		super(fm);

		this.setLayout(new BorderLayout());

		this.btnImporter = FrameMain.styliserBouton("Importer");
		this.btnCopier   = FrameMain.styliserBouton("Copier");
		this.btnExporter = FrameMain.styliserBouton("Exporter");

		this.txtDat = FrameMain.styliserTextArea("Importer un fichier correspondant a la structure demander.");

		this.add(FrameMain.panelTitre("Importer le fichier Mistic", FrameMain.COULEUR), BorderLayout.NORTH);

		JPanel panelBtn = new JPanel(new GridLayout(3,1,0,3));
		panelBtn.add(this.btnImporter);
		panelBtn.add(this.btnCopier);
		panelBtn.add(this.btnExporter);
		this.add(panelBtn, BorderLayout.EAST);

		JScrollPane sp = new JScrollPane();
		sp.setViewportView(this.txtDat);

		this.add(sp     , BorderLayout.CENTER);

		this.btnImporter.addActionListener(this);
		this.btnCopier  .addActionListener(this);
		this.btnExporter.addActionListener(this);
	}
	


	/**Structure du panel. */
	public void dessinerInterface() 
	{
		this.txtDat.setText(this.frame.getTextDat());
	}


	/**Permet de passer au panel suivant. */
	public boolean peutSuivant() 
	{
		return !this.frame.getTextDat().equals("Importer un fichier correspondant a la structure demander.");
	}



	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == this.btnImporter)
		{
			File file = this.frame.getFile("Choisissez un fichier");
	
			if (file != null)
			{
				this.frame.lireDat(file);
			}
		}

		if (e.getSource() == this.btnCopier)
		{
            StringSelection stringSelection = new StringSelection(this.txtDat.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
		}


		if (e.getSource() == this.btnExporter)
		{
			this.frame.telechargerContenue(this.frame.getTextDat(), ".dat");
		}

		
	}
}
