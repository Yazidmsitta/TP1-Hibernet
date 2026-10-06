package org.example.repository;

import org.example.model.Produit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.function.Function;

public final class CatalogueRepository {
    private final EntityManagerFactory factory;

    public CatalogueRepository(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void enregistrer(List<Produit> produits) {
        avecSession(manager -> {
            EntityTransaction transaction = manager.getTransaction();
            try {
                transaction.begin();
                produits.forEach(manager::persist);
                transaction.commit();
            } catch (RuntimeException failure) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw failure;
            }
            return null;
        });
    }

    public List<Produit> lister() {
        return avecSession(manager -> manager.createQuery(
                "SELECT produit FROM Produit produit ORDER BY produit.id", Produit.class)
                .getResultList());
    }

    public Produit trouver(Long identifiant) {
        return avecSession(manager -> manager.find(Produit.class, identifiant));
    }

    private <T> T avecSession(Function<EntityManager, T> operation) {
        EntityManager manager = factory.createEntityManager();
        try {
            return operation.apply(manager);
        } finally {
            manager.close();
        }
    }
}
