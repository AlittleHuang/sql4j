package github.alittlehuang.sql4j.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagers {

    private static final EntityManager ENTITY_MANAGER = doGetEntityManager();

    private EntityManagers() {
    }

    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER;
    }

    private static EntityManager doGetEntityManager() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("org.hibernate.jpa");
        return factory.createEntityManager();
    }


}
