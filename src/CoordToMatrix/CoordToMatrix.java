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

    private int[][] matrix;
    private Point[] points;
    private int     nbClient;

    private int   capacite;

    private float resultatOpti;

    public void chargerFichier(String nameFile) 
	{
        try
		{
            // Lire le fichier
            File file = new File(nameFile);
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
                int x = input.nextInt();
                int y = input.nextInt();

                points[i] = new Point(num, demande, x, y);
            }

            // Créer la matrice de distances
            this.matrix = new int[nbClient + 1][nbClient + 1];
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
            e.printStackTrace();
        }
    }



	public void generDat(String nameFile)
	{
		// Crée un fichier de données
		try {
			PrintWriter pw = new PrintWriter( new OutputStreamWriter(new FileOutputStream(nameFile+".dat"), "UTF8" ));


			// Génération du commentaire au dessus 
			pw.println("/*********************************************\r\n"  +
			           " * OPL 22.1.1.0 Data\r\n"                            + 
			           " * Author: Ottirate\r\n"                             + 
			           " * Creation Date: "+ CoordToMatrix.getDate() +"\r\n" + 
			           " *********************************************/\r\n");


			// Génération du nombre de sommets : dépôt + clients
			pw.println("// Nombre de sommets : dépôt + clients");
			pw.println("nbClientDep = " + (nbClient + 1) + ";\n");


			// Génération du nombre de véhicule
			pw.println("// Nombre de véhicules");
			pw.println("nbVehicules = " + CoordToMatrix.DEFAULT_NB_VEHICULE + ";\n");

			
			// Génération des capacité maximale des véhicules
			pw.println("// Capacité maximale des véhiculess");
			pw.println("Qmax = " + this.capacite + ";\n");
			
			
			// Demandes des clients (le premier élément correspond au dépôt)
			pw.println("// Demandes des clients (le premier élément correspond au dépôt)");
			String demande = "[";
			for (Point p : this.points) {
				demande += ", " + p.demande;
			}
			demande = demande.replaceFirst(", ", "");
			pw.println("Demande = " + demande + "];\n");

			
			
			// Matrice des distances
			pw.println("// Matrice des distances");
			String distance = "[\n";
			for (int i = 0; i < matrix.length; i++) 
			{
				distance += "\t[";
				for (int j = 0; j < matrix[i].length; j++) 
				{
					distance += String.format("%3d", matrix[i][j]);
					if (j != matrix[i].length - 1) distance += ", ";
				}
				distance += "]";

				if (i != matrix.length - 1) distance += ",\n";
			}
			pw.println("Distance = " + distance + "\n];");




			pw.close();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String getDate ()
	{
		Date now = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy 'at' HH:mm:ss", Locale.FRANCE);

        return formatter.format(now);
	}  

    private record Point(int num, int demande, int x, int y) {
        public int getDistance(Point c) {
            return Math.abs(this.x - c.x) + Math.abs(this.y - c.y);
        }

        public String toString() {
            return "Point [num=" + num + ", demande=" + demande + ", x=" + x + ", y=" + y + "]";
        }
    }


	


    public static void main(String[] args) {
        CoordToMatrix ctm = new CoordToMatrix();
        ctm.chargerFichier("./src/CoordToMatrix/Test.txt");
		ctm.generDat("Hey");
    }

}
