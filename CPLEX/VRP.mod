/*********************************************
 * OPL 22.1.1.0 Model
 * Author: bj220781
 * Creation Date: 27 janv. 2025 at 10:07:01
 *********************************************/

// Définition des ensembles et paramètres
int nbClientDep = ...; // Nombre total de noeuds (dépôt + clients)
int nbVehicules = ...; // Nombre de véhicules
range Noeuds = 1..nbClientDep; // Inclut le dépôt
range Vehicules = 1..nbVehicules;

// Données du problème
float Distance[Noeuds][Noeuds] = ...; // Matrice des distances
int Demande[Noeuds] = ...;            // Demande des clients
int Qmax = ...;                       // Capacité maximale des véhicules
int idDepot = 1;                      // l'indice du dépôt

// Variables de décision
dvar boolean x[Noeuds][Noeuds][Vehicules]; // 1 si le véhicule v passe de i à j, 0 sinon
dvar int+ Qrestant[Noeuds][Vehicules];     // Capacité restante du véhicule v au noeud i
dvar float+ u[Noeuds][Vehicules];          // Variable MTZ pour éviter les sous-tours

// Fonction objectif : Minimiser la distance totale
minimize sum(v in Vehicules, i in Noeuds, j in Noeuds : i != j) Distance[i][j] * x[i][j][v];

// Contraintes
subject to {
  // Tous les clients doivent être visités une fois
  forall(i in Noeuds : i != idDepot)
    sum(v in Vehicules, j in Noeuds : j != i) x[i][j][v] == 1;
  
  // Chaque véhicule doit revenir au dépôt s'il sort
  forall(v in Vehicules) {
    sum(i in Noeuds : i != idDepot) x[idDepot][i][v] == sum(j in Noeuds : j != idDepot) x[j][idDepot][v];
  }
  
  forall(v in Vehicules, i in Noeuds : i != idDepot) {
    sum(j in Noeuds : j != i) x[j][i][v] == sum(j in Noeuds : j != i) x[i][j][v];
	}

  // Limitation de la capacité des véhicules
  forall(v in Vehicules, i in Noeuds : i != idDepot) {
    // La capacité restante au nœud i est mise à jour en fonction de la demande servie
    sum(j in Noeuds : j != idDepot) Demande[i] * x[i][j][v] <= Qmax;
  }

  // Mise à jour de la capacité restante pour chaque transition
  forall(v in Vehicules, i in Noeuds : i != idDepot, j in Noeuds : j != i) {
    Qrestant[j][v] >= Qrestant[i][v] - Demande[j] * x[i][j][v];
  }

  // Initialisation de la capacité au dépôt
  forall(v in Vehicules) {
    Qrestant[idDepot][v] == Qmax;
  }

  // Capacité restante doit rester dans les limites [0, Qmax]
  forall(v in Vehicules) {
    sum(i in Noeuds, j in Noeuds) Demande[i] * x[i][j][v] <= Qmax;
  }
  
  // Contraintes MTZ pour éviter les sous-tours
  forall(v in Vehicules, i in Noeuds : i != idDepot, j in Noeuds : j != idDepot && i != j) {
    u[j][v] >= u[i][v] + Demande[j] - Qmax * (1 - x[i][j][v]);
  }

  // Initialisation de MTZ au dépôt
  forall(v in Vehicules) {
    u[idDepot][v] == 0;
  }

  // Limites sur les variables MTZ
  forall(v in Vehicules, i in Noeuds) {
    u[i][v] >= 0;
    u[i][v] <= Qmax;
  }
  
  
}

// Section d'exécution pour afficher les résultats
execute {
    var totalDistance = 0;
    var nbVehiculeSorti = 0;

    writeln("Process:");
    writeln("==============================");

    // Parcours des véhicules pour afficher leurs tournées
    for (var v in Vehicules) {
        var distV = 0; // Distance parcourue par le véhicule
        var clientVisiter = "";
        var vehiculeSorti = false;
        var capaciteUtilisee = 0;  // Variable pour suivre la capacité utilisée par le véhicule

        // Trouver l'ordre de visite des clients pour chaque véhicule
        var currentNode = idDepot; // On commence toujours par le dépôt
        var route = ""; // Cette variable contiendra l'ordre des arcs visités

        // Afficher les arcs dans l'ordre de la tournée
        while (true) {
            // Trouver le prochain client visité par le véhicule
            var nextNode = -1;
            var found = false;

            // On cherche quel est le prochain client à visiter
            for (var j in Noeuds) {
                if (j != currentNode && x[currentNode][j][v].solutionValue > 0.99) {
                    nextNode = j;
                    found = true;
                    route += currentNode.toString() + " -> "; // Ajout de l'arc actuel
                    distV += Distance[currentNode][nextNode];
                    // Calcul de la capacité utilisée pour chaque client visité
                    if (nextNode != idDepot) {
                        capaciteUtilisee += Demande[nextNode];  // Ajoute la demande du client
                    }
                    break;
                }
            }

            if (!found) {
                // Si aucun autre client n'est trouvé, on revient au dépôt
                route += currentNode.toString() + " -> " + idDepot.toString();
                distV += Distance[currentNode][idDepot]; // Distance du dernier arc vers le dépôt
                break; // Fin de la tournée
            }

            // Met à jour le noeud actuel
            currentNode = nextNode;
        }

        // Afficher la tournée et les statistiques du véhicule
        if (distV > 0) {
            nbVehiculeSorti += 1;
            writeln("Résultats de l'optimisation:");
            writeln("==============================");
            writeln("Véhicule ", v, ":");
            writeln("  Tournée : ", route);
            writeln("  Distance parcourue : ", distV);

            // Affichage de la capacité restante et de la capacité utilisée
            var capaciteRestante = Qmax - capaciteUtilisee;
            writeln("  Capacité utilisée : ", capaciteUtilisee);
            writeln("  Capacité restante au dépôt : ", capaciteRestante);
            totalDistance += distV;
        }
    }

    writeln("==============================");
    writeln("Statistiques globales :");
    writeln("  Nombre total de véhicules utilisés : ", nbVehiculeSorti, " / ", nbVehicules);
    writeln("  Distance totale parcourue : ", totalDistance);
};
