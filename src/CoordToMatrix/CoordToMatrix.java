package CoordToMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class CoordToMatrix 
{
	public static final int DEFAULT_NB_VEHICULE = 4;

    private float[][] matrix;
    private Point[]   points;
    private int       nbClient;

    private int   capacite;

    private float resultatOpti;

    public boolean chargerFichier(File file) 
	{
        try
		{
            // Lire le fichier
            Scanner input = new Scanner(file);
            input.useLocale(Locale.US); 

            // Lire le nombre de clients et le résultat optimal
            this.nbClient = input.nextInt();
            this.resultatOpti = input.nextFloat();

            // Lire la capacité
            this.capacite = input.nextInt();

            // Lire le point de dépôt
            int xDepot = input.nextInt();
            int yDepot = input.nextInt();
            this.points = new Point[nbClient + 1];
            points[0] = new Point(0, 0, xDepot, yDepot);

            // Lire les clients
            for (int i = 1; i <= nbClient; i++) 
			{
                int num = input.nextInt();
                int demande = input.nextInt();
                float x = input.nextFloat();
                float y = input.nextFloat();

                points[i] = new Point(num, demande, x, y);
            }

            // Créer la matrice de distances
            this.matrix = new float[nbClient + 1][nbClient + 1];
            for (int i = 0; i <= nbClient; i++) 
			{
                for (int j = 0; j <= nbClient; j++) 
				{
                    if (i == j) matrix[i][j] = 0;
                    else        matrix[i][j] = points[i].getDistance(points[j]);
                    
                }
            }
			
            input.close();
        } catch (Exception e) {
			return false;
        }

		return true;
    }



	public void generDat(String nameFile)
	{
		// Crée un fichier de données
		try {

			
			
			// Ajouter l'extension .dat si elle n'est pas présente
			if (!nameFile.toLowerCase().endsWith(".dat")) {
				nameFile += ".dat";
			}
			PrintWriter pw = new PrintWriter( new OutputStreamWriter(new FileOutputStream(nameFile), "UTF8" ));


			// Génération du commentaire au dessus 
			pw.println(this.getText());


			pw.close();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}





	public String getText()
	{
		String text ="/*********************************************\r\n"                 +
					 " * OPL 22.1.1.0 Data\r\n"                                           + 
					 " * Author: Groupe MAJ\r\n"                                          + 
					 " * Creation Date: "+ CoordToMatrix.getDate() +"\r\n"                + 
					 " *********************************************/\r\n"                +
		             "// Nombre de sommets : dépôt + clients\n"                           +
		             "nbClientDep = " + (nbClient + 1) + ";\n\n"                          +
                     "// Nombre de véhicules\n"                                           +
		             "nbVehicules = " + CoordToMatrix.DEFAULT_NB_VEHICULE + ";\n\n"       +
		             "// Capacité maximale des véhiculess\n"                              +
		             "Qmax = " + this.capacite + ";\n\n"                                  +
		             "// Demandes des clients (le premier élément correspond au dépôt)\n" ;


		String demande = "[";
		for (Point p : this.points) {
			demande += ", " + p.demande;
		}
		demande = demande.replaceFirst(", ", "");
		
		text += "Demande = " + demande + "];\n\n" +
		        "// Matrice des distances\n"      ;

		String distance = "[\n";
		for (int i = 0; i < matrix.length; i++) 
		{
			distance += "\t[";
			for (int j = 0; j < matrix[i].length; j++) 
			{
				distance += String.format("%,6.2f", matrix[i][j]).replace(',', '.');
				if (j != matrix[i].length - 1) distance += ", ";
			}
			distance += "]";

			if (i != matrix.length - 1) distance += ",\n";
		}
		text += "Distance = " + distance + "\n];";

		return text;
	}


	private static String getDate ()
	{
		Date now = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy 'at' HH:mm:ss", Locale.FRANCE);

        return formatter.format(now);
	}  

    private record Point(int num, int demande, float x, float y) {

        public float getDistance(Point c) {
            return Math.abs(this.x - c.x) + Math.abs(this.y - c.y);
        }

        public String toString() {
            return "Point [num=" + num + ", demande=" + demande + ", x=" + x + ", y=" + y + "]";
        }
    }

}
