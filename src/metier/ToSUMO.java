package metier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToSUMO
{
    public static final String EXTENSION_MAP          = ".net.xml";
    public static final String EXTENSION_ROUTE        = ".rou.xml";
    public static final String EXTENSION_SIMULATION   = ".sumocfg";

    private OutilSumo utilSumo;

    public ToSUMO(OutilSumo utilSumo)
    {
        this.utilSumo = utilSumo;
    }

    public String getNetXML()
    {
		String retStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n"                                                                                                               +
        "<!-- generated on "+ OutilSumo.getDate() +" by SUMEX for SUMO netedit Version 1.15.0\r\n"                                                                       +
        "<configuration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://sumo.dlr.de/xsd/netconvertConfiguration.xsd\">\r\n" +

        "\t<input>\r\n"                                                                                  +
        "\t\t<sumo-net-file value=\"/home/etudiant/na222180/TP/s6/sae/netedit/test/test.net.xml\"/>\r\n" +
        "\t</input>\r\n\r\n"                                                                             +

        "\t<output>\r\n"                                                                                 +
        "\t\t<output-file value=\"/home/etudiant/na222180/TP/s6/sae/netedit/test/test.net.xml\"/>\r\n"   +
        "\t</output>\r\n\r\n"                                                                            +

        "\t<processing>\r\n" +
        "\t\t<geometry.min-radius.fix.railways value=\"false\"/>\r\n"                                    +
        "\t\t<geometry.max-grade.fix value=\"false\"/>\r\n"                                              +
        "\t\t<offset.disable-normalization value=\"true\"/>\r\n"                                         +
        "\t\t<lefthand value=\"0\"/>\r\n"                                                                +
        "\t</processing>\r\n\r\n"                                                                        +

        "\t<junctions>\r\n"                                                                              +
        "\t\t<no-turnarounds value=\"true\"/>\r\n"                                                       +
        "\t\t<junctions.corner-detail value=\"5\"/>\r\n"                                                 +
        "\t\t<junctions.limit-turn-speed value=\"5.50\"/>\r\n"                                           +
        "\t\t<rectangular-lane-cut value=\"0\"/>\r\n"                                                    +
        "\t</junctions>\r\n\r\n"                                                                         +

        "\t<pedestrian>\r\n"                                                                             +
        "\t\t<walkingareas value=\"0\"/>\r\n"                                                            +
        "\t\t<walkingareas value=\"0\"/>\r\n"                                                            +
        "\t</pedestrian>\r\n\r\n"                                                                        +
                      
        "</configuration>\r\n"                                                                           +
        "-->\r\n\r\n";
        
        retStr += "<net version=\"1.9\" junctionCornerDetail=\"5\" limitTurnSpeed=\"5.50\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://sumo.dlr.de/xsd/net_file.xsd\">\r\n\r\n"; 
        // Itération sur les arrêtes entre les clients
        for (int i = 0; i < this.utilSumo.matrix.length; i++) {
            for (int j = 0; j < this.utilSumo.matrix[i].length; j++) {
                if (i != j && this.utilSumo.matrix[i][j] > 0) { // Vérifie qu'il y a bien une connexion
        
                    int edgeIndex = i * this.utilSumo.matrix.length + j;

                    double distance = this.utilSumo.points[i].getDistance(this.utilSumo.points[j]) * 10;
                    double xFrom = this.utilSumo.points[i].x() * 10;
                    double yFrom = this.utilSumo.points[i].y() * 10;
                    double xTo   = this.utilSumo.points[j].x() * 10;
                    double yTo   = this.utilSumo.points[j].y() * 10;
                    
                    String shape = xFrom + "," + yFrom + " " + xTo + "," + yTo; // Génération du shape
                    
                    retStr += "\t<edge id=\"E-J" + i + "J" + j + "\" from=\"J" + i + "\" to=\"J" + j + "\" priority=\"1\">\r\n" +
                              "\t\t<lane id=\"E" + edgeIndex + "_0\" index=\"0\" speed=\"13.89\" width=\"0.8\" length=\"" + distance + 
                              "\" shape=\"" + shape + "\"/>\r\n" +  // Ajout du shape ici
                              "\t</edge>\r\n";
                }
            }
        }
        
        retStr += "\r\n";

        for (int i = 0; i < this.utilSumo.points.length; i++) {
			Point p = this.utilSumo.points[i];
		
			// Récupérer les voies entrantes dans la jonction J[i]
			StringBuilder incLanes = new StringBuilder();
			for (int j = 0; j < this.utilSumo.matrix.length; j++) {
				if (this.utilSumo.matrix[j][i] > 0) { // Si une route mène vers i
					int edgeIndex = j * this.utilSumo.matrix.length + i;
					incLanes.append("E").append(edgeIndex).append("_0 "); // Ajoute la voie
				}
			}
		
			// Création de la jonction avec incLanes renseigné
			retStr += "\t<junction id=\"J" + i + "\" type=\"unregulated\" x=\"" + (p.x() * 10) + "\" y=\"" + (p.y() * 10) + "\"\r\n" +
					  "\t\tincLanes=\"" + incLanes.toString().trim() + "\"\r\n" +
					  "\t\tintLanes=\"\"\r\n" +
					  "\t\tshape=\"142.73,46.58\"/>\r\n\r\n";
		}

        retStr += "\r\n";

        for (int i = 0; i < this.utilSumo.matrix.length; i++) {
            for (int j = 0; j < this.utilSumo.matrix[i].length; j++) {
                if (i != j && this.utilSumo.matrix[i][j] > 0) { // Vérifie si la distance i → j existe
                    for (int k = 0; k < this.utilSumo.matrix[j].length; k++) {
                        if (j != k && this.utilSumo.matrix[j][k] > 0) { // Vérifie si la distance j → k existe
                            retStr += "\t<connection from=\"E-J" + i + "J" + j + "\" to=\"E-J" + j + "J" + k + 
                                    "\" fromLane=\"0\" toLane=\"0\" dir=\"s\" state=\"O\"/>\r\n";
                        }
                    }
                }
            }
        }


        retStr += "\r\n</net>"; 
        return retStr;
    }

    public String getRouXML()
    {
		String retStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n"                                     +
        "<!-- generated on "+ OutilSumo.getDate() +" by SUMEX for SUMO netedit Version 1.15.0\r\n" +
        "-->\r\n\r\n";
        
        retStr += "<routes xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://sumo.dlr.de/xsd/routes_file.xsd\">\r\n\r\n" +
                  "\t<!-- Routes -->\r\n";

        // Itération sur les tournées
        int i = 0;
        for (List<Point> values : this.utilSumo.tournees.values())
        {
            ArrayList<Point> points = new ArrayList<Point>(values);

            retStr +=  
            "\t<route id=\"r_"+ i +"\" edges=\"";
            
            int j;
            for (j = 1; j < points.size() -1; j++)
            {
                retStr += "E-J" + points.get(j -1).num() + "J" + points.get(j).num() + " ";
            }

            retStr += "E-J" + points.get(j -1).num() + "J" + points.get(j).num() + "\"/>\r\n";
            i++;
        }

        retStr +="\r\n\t<!-- Vehicles, persons and containers (sorted by depart) -->\r\n";

        i = 0;
        for (Integer numVehicule : this.utilSumo.tournees.keySet())
        {
            retStr +=  
            "\t<vehicle id=\"v_" + numVehicule + "\" depart=\"0.00\" route=\"r_" + i + "\"/>\r\n";

            i++;
        }

        retStr += "\r\n</routes>"; 
        return retStr;
    }

    public String getSimulation(String nomFichier)
    {
		String retStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
        "<!-- generated on "+ OutilSumo.getDate() +" by MAP SUMO GUI Version 1.21.0\r\n" +
        "-->\r\n" +
        "<sumoConfiguration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://sumo.dlr.de/xsd/sumoConfiguration.xsd\">\r\n\r\n" +

            "\t<input>\r\n" +
            "\t\t<net-file value=\""   + nomFichier + ".net.xml\"/>\r\n" +
            "\t\t<route-files value=\"" + nomFichier + ".rou.xml\"/>\r\n" +
            "\t</input>\r\n\r\n" +

        "</sumoConfiguration>\r\n";
        
        return retStr;
    }
}
