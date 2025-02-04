package metier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToSUMO
{
    public static final String EXTENSION_MAP   = ".net.xml";
    public static final String EXTENSION_ROUTE = ".rou.xml";

    private OutilSumo utilSumo;

    public ToSUMO(OutilSumo utilSumo)
    {
        this.utilSumo = utilSumo;
    }

    public String getNetXML()
    {
		String retStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n"                                                                                                               +
        "<!-- generated on "+ OutilSumo.getDate() +" by MAJ for SUMO netedit Version 1.15.0\r\n"                                                                       +
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
                    
                    retStr += "\t<edge id=\"E" + edgeIndex + "\" from=\"J" + i + "\" to=\"J" + j + "\" priority=\"1\">\r\n" +
                              "\t\t<lane id=\"E" + edgeIndex + "_0\" index=\"0\" speed=\"13.89\" width=\"0.8\" length=\"" + distance + 
                              "\" shape=\"" + shape + "\"/>\r\n" +  // Ajout du shape ici
                              "\t</edge>\r\n\r\n";
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
					  "\t\tshape=\"142.73,46.58\"/>\r\n";
		}

        retStr += "\r\n</net>"; 
        return retStr;
    }

    public String getRouXML()
    {
		String retStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n"                                     +
        "<!-- generated on "+ OutilSumo.getDate() +" by MAJ for SUMO netedit Version 1.15.0\r\n" +
        "-->\r\n\r\n";
        
        retStr += "<routes xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://sumo.dlr.de/xsd/routes_file.xsd\">\r\n";
        retStr += "\t<!-- Vehicles, persons and containers (sorted by depart) -->\r\n"; 
        
        // Itération sur les tournées des véhicules
        for (Map.Entry<Integer, List<Point>> set : this.utilSumo.tournees.entrySet())
        {
            String numVehicule = "V_" + set.getKey();
            ArrayList<Point> points = new ArrayList<Point>(set.getValue());

            retStr += 
            "\t<trip id=\"t_"+ numVehicule +"\" depart=\"0.00\" from=\"J" + points.get(0) + "\" to=\"J" + points.get(points.size() -1) +
            "\" via=\"";

            for (int j = 1; j < points.size() -1; j++)
            {
                retStr += "E" + points.get(j).num() + " ";
            }

            retStr += "\"/>\r\n";
        }

        retStr += "</routes>"; 
        return retStr;
    }

	public static void main(String[] args) {
		OutilSumo outilsumo = new OutilSumo();
		ToSUMO ts = new ToSUMO(outilsumo);

		outilsumo.chargerFichier(new File("c50.txt"));
		outilsumo.genererFichier(outilsumo.getTextDat(), ".dat", "sortieSUMO" + EXTENSION_MAP);
		outilsumo.genererFichier(ts.getNetXML(), EXTENSION_MAP, "sortieSUMO" + EXTENSION_MAP);
	}
}
