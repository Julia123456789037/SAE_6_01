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
dvar int+ u[Noeuds][Vehicules];          // Variable MTZ pour éviter les sous-tours

// Fonction objectif : Minimiser la distance totale
minimize sum(v in Vehicules, i in Noeuds, j in Noeuds : i != j) Distance[i][j] * x[i][j][v];

// Contraintes
subject to {
  // Tous les clients doivent être visités une fois
  forall(i in Noeuds : i != idDepot)
    sum(v in Vehicules, j in Noeuds : j != i) x[i][j][v] == 1;
  
  // Chaque véhicule doit revenir au dépôt s'il sort
  forall(v in Vehicules, i in Noeuds : i != idDepot) {
    sum(j in Noeuds : j != i) x[j][i][v] == sum(j in Noeuds : j != i) x[i][j][v];
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

    writeln("Résultats de l'optimisation:");

    // Parcours des véhicules pour afficher leurs tournées
    for (var v in Vehicules) {
        var distV = 0; // Distance parcourue par le véhicule
        var cumulSoustraction = Qmax;
        
        // Chaines de caractères à l'affichage
        var clientVisiter = "";
        var qteDepose     = Qmax + " -> ";
        
        var vehiculeSorti = false;
        var capaciteUtilisee = 0;  // Variable pour suivre la capacité utilisée par le véhicule

        for (var i in Noeuds) {
            for (var j in Noeuds) {
                if (i != j && x[i][j][v].solutionValue > 0.99) { // Vérification améliorée
                    vehiculeSorti = true;
                    clientVisiter = j + " -> " + clientVisiter; // Ajout du client visité
                    
                    cumulSoustraction -= Demande[j] * x[i][j][v]; // Calcul de la quantité de marchandises restantes
                    qteDepose += cumulSoustraction + " -> ";
                    
                    // Ajouter la distance uniquement pour les arcs actifs
                    distV += Distance[i][j];

                    // Calcul de la capacité utilisée pour chaque client visité
                    if (i != idDepot) {
                        capaciteUtilisee += Demande[i];  // Ajoute la demande du client i
                    }
                }
            }
        }

		clientVisiter = "Dépôt -> " + clientVisiter;
		
        // Afficher la tournée et les statistiques du véhicule
        if (vehiculeSorti) {
            nbVehiculeSorti += 1;
            writeln("==============================");
            writeln(" Véhicule ", v, ":");
            writeln(" ͞ ͞ ͞ ͞ ͞ ͞ ͞ ͞ ͞ ͞ ͞ ͞ ");
            writeln("  Tournée      : ", clientVisiter, "Dépôt");
            
            var capaciteRestante = Qmax - capaciteUtilisee;
            writeln("  Qté déposées : ", qteDepose, capaciteRestante);
            writeln("  Distance parcourue : ", distV);
            
            // Affichage de la capacité restante et de la capacité utilisée
            writeln("  Capacité utilisée : ", capaciteUtilisee);
            totalDistance += distV;
        }
    }
    writeln("");
    writeln("==============================");
    writeln("==============================");
    writeln("Statistiques globales :");
    writeln("  Nombre total de véhicules utilisés : ", nbVehiculeSorti, " / ", nbVehicules);
    writeln("  Distance totale parcourue : ", totalDistance);
};