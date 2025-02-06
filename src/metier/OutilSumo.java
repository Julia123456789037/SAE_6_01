package metier;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
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
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ihm.FrameMain;

public class OutilSumo 
{
    float[][] matrix;
    Point[]   points;
    int       nbClient;

    int   capacite;

    float resultatOpti;
	HashMap<Integer, List<Point>> tournees;	
    Color[]                       coulVehic;


    BufferedImage bi;



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
            somme += p.demande();  

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




    public void genererGraphe()
    {
        int d = 20; 
    
        float maxX, minX;
        float maxY, minY;
    
        maxX = minX = this.points[0].x();
        maxY = minY = this.points[0].y();
    
        for (Point p : this.points)
        {
            if (p.x() < minX) minX = p.x();
            if (p.y() < minY) minY = p.y();
    
            if (p.x() > maxX) maxX = p.x();
            if (p.y() > maxY) maxY = p.y();
        }

        float scaleFactor = 650 / Math.max((int) ((Math.abs(maxX - minX))),(int) ((Math.abs(maxY - minY)))); 
        scaleFactor = Math.max(scaleFactor, 40);

        int width  = (int) ((Math.abs(minX) + Math.abs(maxX)) * scaleFactor) + d * 2 + 40;
        int height = (int) ((Math.abs(minY) + Math.abs(maxY)) * scaleFactor) + d * 2 + 40;
    
        this.bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        Font font = new Font("Arial", Font.BOLD, d/2); 
        FontMetrics metrics = g2.getFontMetrics(font);

    
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
        int offsetX = (int) (-minX * scaleFactor) + 20;
        int offsetY = (int) (-minY * scaleFactor) + 20;

        // Fond blanc
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
    
        // Dessine les chemins
        this.coulVehic = new Color[this.tournees.size()];
        int ind = 0;
        
        for (Integer num : this.tournees.keySet())
        {
            int r = 50 + (int) (Math.random() * 156);
            int g = 50 + (int) (Math.random() * 156);
            int b = 50 + (int) (Math.random() * 156);

            Color coul = new Color(r, g, b);
            g2.setColor(coul);
            this.coulVehic[ind++] = coul;
    
            List<Point> list = this.tournees.get(num);
            for (int i = 0; i < list.size() - 1; i++)
            {
                Point p1 = list.get(i);
                Point p2 = list.get(i + 1);
    
                this.drawArrow(g2,
                    (int) (p1.x() * scaleFactor) + offsetX, 
                    (int) (p1.y() * scaleFactor) + offsetY,
                    (int) (p2.x() * scaleFactor) + offsetX, 
                    (int) (p2.y() * scaleFactor) + offsetY
                );
            }
        }
    
        // Dessiner les points
        for (Point p : this.points)
        {
            int px = (int) (p.x() * scaleFactor) + offsetX;
            int py = (int) (p.y() * scaleFactor) + offsetY;

            g2.setColor(FrameMain.COULEUR);
            g2.fillOval(
                px - d / 2, 
                py - d / 2, 
                d, d
            );


            String label = p.num() +"";
            int textWidth = metrics.stringWidth(label);
            int textHeight = metrics.getHeight();
            
            g2.setColor(FrameMain.COULEUR.darker().darker());
            g2.drawString(label, px - textWidth / 2 - 1, py + textHeight / 4 + 1); 
        }
    }


    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) 
    {
        double arrowLength = 10; 

        int midX = (x1 + x2) / 2;
        int midY = (y1 + y2) / 2;

        double angle = Math.atan2(y2 - y1, x2 - x1);

        int xArrow1 = (int) (midX - arrowLength * Math.cos(angle - Math.PI / 6));
        int yArrow1 = (int) (midY - arrowLength * Math.sin(angle - Math.PI / 6));

        int xArrow2 = (int) (midX - arrowLength * Math.cos(angle + Math.PI / 6));
        int yArrow2 = (int) (midY - arrowLength * Math.sin(angle + Math.PI / 6));

        g2.drawLine(x1, y1, x2, y2);

        g2.drawLine(midX, midY, xArrow1, yArrow1);
        g2.drawLine(midX, midY, xArrow2, yArrow2);
    }

    

    public BufferedImage getImage() { return this.bi; }

    public void telechargerImage(String nameFile)
    {
        try {
            File outputfile = new File(nameFile + ".png");
            javax.imageio.ImageIO.write(this.bi, "png", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
