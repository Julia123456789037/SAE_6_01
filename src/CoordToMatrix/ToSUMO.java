package CoordToMatrix;

public class ToSUMO
{
    public static final String EXTENSION_GRAPH = ".net.xml";
    public static final String EXTENSION_ROUTE = ".rou.xml";

    private CoordToMatrix ctm;
    // private Resultat resultat;

    public ToSUMO(CoordToMatrix ctm)
    {
        this.ctm = ctm;
        // TODO :this.resultat = ...;
    }

    public String getNetXML()
    {
		String retStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n"                                                                                                               +
        "<!-- generated on "+ CoordToMatrix.getDate() +" by MAJ for SUMO netedit Version 1.15.0\r\n"                                                                       +
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
        
        // Itération sur les routes entre les clients
        for (int i = 0; i < this.ctm.matrix.length; i++) {
            for (int j = 0; j < this.ctm.matrix[i].length; j++) {
                if (j == i) continue;

                int index = i * this.ctm.matrix.length + j;
                retStr += "\t<edge id=\"E" + index + "\" from=\"" + i + "\" to=\"" + j + "\" priority=\"-1\">\r\n" +
                        "\t\t<lane id=\"E" + index + "_\" index=\"0\" speed=\"13.89\" width=\"0.50\" length=\""+ this.ctm.points[i].getDistance(this.ctm.points[j]) *10 +"\" shape=\"-32.83,65.57 32.11,60.58\"/>\r\n" +
                        "\t</edge>\r\n\r\n";
            }
        }

        retStr += "\r\n";

        // Itération sur tous les clients
        for (int i = 0; i < this.ctm.points.length; i++) {
            Point  p = this.ctm.points[i];
            String c = (i < 26) ? (char) ('A' + i -1) + "" : ((char) ('A' + ((i -1) / 26) -1)) + "" + ((char) ('A' + ((i -1) % 26)));

            retStr += "\t<junction id=\"" + i +"\" type=\"priority\" x=\""+ p.x() *10 +"\" y=\""+ p.y() *10 +"\" incLanes=\"\" intLanes=\"\" shape=\"142.73,46.58\"/>\r\n";
        }

        retStr += "\r\n</net>"; 
        return retStr;
    }

    public String getRouXML()
    {
		String retStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n"                                         +
        "<!-- generated on "+ CoordToMatrix.getDate() +" by MAJ for SUMO netedit Version 1.15.0\r\n" +
        "-->\r\n\r\n";
        
        retStr += "<routes xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://sumo.dlr.de/xsd/routes_file.xsd\">\r\n";
        retStr += "\t<!-- Vehicles, persons and containers (sorted by depart) -->\r\n"; 
        
        // Itération sur les routes prisent par les véhicules
        // TODO : Une route est composé de plusieurs arrêtes

        /*
        for (int i = 0; i < this.resultat.getRoutes().length; i++) {
            Route route = this.resultat.getRoute(i);
            retStr += 
            "\t<trip id=\"t_"+ i +"\" depart=\"0.00\" from=\"" + route.getLstArrete().get(0) + "\" to=\"" + route.getLstArrete().get(route.getLstArrete().size() -1) +
            "\" via=\"" + String.join(" ", route.getLstArrete()) + "\"/>";

            retStr += "\r\n";
        } */

        retStr += "</routes>"; 
        return retStr;
    }
}
