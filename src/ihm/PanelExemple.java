package ihm;

import javax.swing.JPanel;

public abstract class PanelExemple extends JPanel
{
	protected FrameMain frame;

	public PanelExemple (FrameMain fm)
	{
		super();
		this.frame = fm;
	}


	public abstract void    dessinerInterface ();
	public abstract boolean peutSuivant ();
}
