package org.example;

import org.example.model.Produit;
import org.example.repository.CatalogueRepository;
import org.h2.tools.Server;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class App {
    private static final String PERSISTENCE_UNIT = "hibernate-demo";
    private static final String CONSOLE_URL = "http://localhost:8082";

    private App() {
    }

    public static void main(String[] args) {
        Server console = null;
        EntityManagerFactory factory = null;
        try {
            console = Server.createWebServer("-web", "-webPort", "8082").start();
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            executerDemo(new CatalogueRepository(factory));
            attendreFermeture();
        } catch (Exception failure) {
            System.err.println("Impossible de terminer la démonstration du catalogue.");
            failure.printStackTrace();
        } finally {
            try {
                if (factory != null && factory.isOpen()) {
                    factory.close();
                }
            } finally {
                if (console != null) {
                    console.stop();
                }
            }
        }
    }

    private static void executerDemo(CatalogueRepository catalogue) {
        catalogue.enregistrer(produitsExemple());
        System.out.println("Les produits ont été ajoutés au catalogue.");
        System.out.println("\nProduits disponibles :");
        catalogue.lister().forEach(System.out::println);

        Long identifiant = 2L;
        Produit resultat = catalogue.trouver(identifiant);
        System.out.println("\nRésultat de la recherche pour l'identifiant " + identifiant + " :");
        System.out.println(resultat == null ? "Aucun produit correspondant." : resultat);
    }

    private static List<Produit> produitsExemple() {
        return Arrays.asList(
                new Produit("Clavier mécanique", new BigDecimal("649.90")),
                new Produit("Souris sans fil", new BigDecimal("279.50")),
                new Produit("Écran 27 pouces", new BigDecimal("2199.00")));
    }

    private static void attendreFermeture() {
        System.out.println("\nConsole H2 : " + CONSOLE_URL);
        System.out.println("JDBC URL   : jdbc:h2:mem:tp1db");
        System.out.println("Utilisateur: sa");
        System.out.println("Appuyez sur Entrée pour quitter.");
        Scanner input = new Scanner(System.in);
        if (input.hasNextLine()) {
            input.nextLine();
        }
    }
}
