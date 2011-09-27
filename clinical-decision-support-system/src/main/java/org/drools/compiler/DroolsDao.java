package org.drools.compiler;

import org.drools.WorkingMemory;
import org.drools.reteoo.ReteooRuleBase;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.ejb.Ejb3Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class DroolsDao {

    private EntityManagerFactory factory;
    private EntityManager em;

    private Set<String> klasses = new HashSet<String>();


    public DroolsDao() {

    }


    public void register(String className) {
        klasses.add(className);
    }


    public void initEntityManager(String persistenceUnitName, WorkingMemory wm) {

        Map<String,Object> props = new HashMap<String,Object>();
        ClassLoader drlKlassLoader = ((ReteooRuleBase) wm.getRuleBase()).getRootClassLoader();

        if (factory == null) {
            Ejb3Configuration config = new Ejb3Configuration();
                config.configure(persistenceUnitName,props);

            try {
                for (String klassName : klasses) {
                    Class klazz = drlKlassLoader.loadClass(klassName);
                    config.addAnnotatedClass(klazz);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            ClassLoader defaultLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(drlKlassLoader);
                factory = config.buildEntityManagerFactory();
                    em = factory.createEntityManager();
            Thread.currentThread().setContextClassLoader(defaultLoader);
        }

    }


    public void dispose() {
        if ( em != null ) {
            em.close();
        }
        if ( factory != null ) {
            factory.close();
        }
    }




    public void save(Object o) {
        try {
            EntityTransaction tx = em.getTransaction();

            if (!tx.isActive()) {
                tx.begin();
            }
            try {
                em.persist(o);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }










}
