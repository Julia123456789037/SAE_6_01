package metier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutilSumo 
{
    float[][] matrix;
    Point[]   points;
    int       nbClient;

    int   capacite;

    float resultatOpti;
	HashMap<Integer, List<Point>> tournees;	



    /**
     * Charger le fichier avec les info matix.
     * @param file
     * @return
     */
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
            float xDepot = input.nextFloat();
            float yDepot = input.nextFloat();
            this.points = new Point[nbClient + 1];
            points[0] = new Point(0, 0, xDepot, yDepot);

            // Lire les clients
            for (int i = 1; i <= nbClient; i++) 
			{
                int num = input.nextInt();
                float x = input.nextFloat();
                float y = input.nextFloat();
                int demande = input.nextInt();

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
			System.out.println("Matrice des distances :");
            for (int i = 0; i <= nbClient; i++) {
                for (int j = 0; j <= nbClient; j++) {
                    System.out.printf("%.2f ", matrix[i][j]);
                }
                System.out.println();
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
			return false;
        }

		return true;
    }

    
    /**
     * Récupérer le text .dat
     * @return
     */
	public String getTextDat()
	{
        try {
            String text ="/*********************************************\r\n"                 +
                        " * OPL 22.1.1.0 Data\r\n"                                           + 
                        " * Author: Groupe MAJ\r\n"                                          + 
                        " * Creation Date: "+ OutilSumo.getDate() +"\r\n"                + 
                        " *********************************************/\r\n"                +
                        "// Nombre de sommets : dépôt + clients\n"                           +
                        "nbClientDep = " + (nbClient + 1) + ";\n\n"                          +
                        "// Nombre de véhicules\n"                                           +
                        "nbVehicules = " + this.getSommeDemande() / capacite + ";\n\n"       +
                        "// Capacité maximale des véhiculess\n"                              +
                        "Qmax = " + this.capacite + ";\n\n"                                  +
                        "// Demandes des clients (le premier élément correspond au dépôt)\n" ;


            String demande = "[";
            for (Point p : this.points) {
                demande += ", " + p.demande();
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
                    distance += String.format(Locale.US, "%.2f", matrix[i][j]);
                    if (j != matrix[i].length - 1) distance += ", ";
                }
                distance += "]";

                if (i != matrix.length - 1) distance += ",\n";
            }
            text += "Distance = " + distance + "\n];";

            return text;
            
        } catch (Exception e) {
            return "Importer un fichier correspondant a la structure demander.";
        }
	}


    public int getSommeDemande() 
    {
        int somme=0;

        for (Point p : points)
            if (p.demande() > somme) somme += p.demande();  

        return somme;
    }

	public static String getDate ()
	{
		Date now = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy 'at' HH:mm:ss", Locale.FRANCE);

        return formatter.format(now);
	}


    

	public void genererFichier(String contenue,String ext, String nameFile)
	{
		// Crée un fichier de données
		try {

			// Ajouter l'extension .dat si elle n'est pas présente
			if (!nameFile.toLowerCase().endsWith(ext)) {
				nameFile += ext;
			}
			PrintWriter pw = new PrintWriter( new OutputStreamWriter(new FileOutputStream(nameFile), "UTF8" ));


			// Génération du commentaire au dessus 
			pw.println(contenue);


			pw.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}





    public boolean traiterRes(String resultats) {
        this.tournees = new HashMap<>();

        Pattern vehiclePattern = Pattern.compile("Véhicule (\\d+):");
        Pattern nodesPattern = Pattern.compile("Tournée.*?: Dépôt -> (.*?) -> Dépôt");

        Matcher vehicleMatcher = vehiclePattern.matcher(resultats);
        Matcher nodesMatcher = nodesPattern.matcher(resultats);

        while (vehicleMatcher.find() && nodesMatcher.find()) 
        {
            String vehicleNumber = vehicleMatcher.group(1);
            String nodes = nodesMatcher.group(1);

            // Crée une noivelle tournée avec le véhicule
            tournees.put(Integer.parseInt(vehicleNumber), new ArrayList<>());

            
            // Ajouter les points à la tournée
            for (String node : nodes.split(" -> ")) 
            tournees.get(Integer.parseInt(vehicleNumber)).add(points[Integer.parseInt(node)]);
            
            // Ajouter le depot en début et fin de tournée
            tournees.get(Integer.parseInt(vehicleNumber)).add(0, points[0]);
            tournees.get(Integer.parseInt(vehicleNumber)).add(points[0]);
        }

        return !tournees.isEmpty();
    }

    public String getRes()
    {
        System.out.println("hjhh");
        if (this.tournees == null || this.tournees.isEmpty()) return "Données incorrect ou non saisie";

        String res = "";
        
        for (Integer tournee : tournees.keySet()) {
           res += "Vehicule " + tournee + " : [";
            for(Point p : this.tournees.get(tournee))
               res += " " + p.num() ;
            res += "]\n";
        }

        return res;
    }
}
