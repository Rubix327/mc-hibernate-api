package me.rubix327.mchibernateapi.api;

import jakarta.persistence.Entity;
import org.bukkit.Bukkit;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public final class HibernateUtil {

    private static final HibernateUtil instance = new HibernateUtil();
    private static final McHibernateApi api = McHibernateApi.get();
    private static SessionFactory sessionFactory;
    private static final Logger logger = Bukkit.getLogger();

    static HibernateUtil get(){
        return instance;
    }

    void run(){
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                Properties settings = new Properties();

                settings.put(Environment.DRIVER, api.getDriver());
                settings.put(Environment.URL, api.getUrl());
                settings.put(Environment.USER, api.getUsername());
                settings.put(Environment.PASS, api.getPassword());

                settings.put(Environment.HBM2DDL_AUTO, api.getHbm2ddlAuto().getExternalHbm2ddlName());
                settings.put(Environment.SHOW_SQL, api.isShowSQL());
                settings.put(Environment.FORMAT_SQL, api.isFormatSQL());
                settings.put(Environment.DIALECT, api.getDialect());
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, api.getCurrentSessionContextClass());
                settings.put(Environment.AUTOCOMMIT, api.isAutocommit());
                settings.put("hibernate.connection.characterEncoding", api.getEncoding());
                if (api.getDefaultSchema() != null){
                    settings.put(Environment.DEFAULT_SCHEMA, api.getDefaultSchema());
                }

                settings.putAll(api.getProps());
                configuration.setProperties(settings);
                registerAnnotatedClasses(configuration);
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);

                logger.info("Connected to " + api.getDatabase() + " database.");
                logger.info("The following settings are applied:");
                logger.info("- HBM2DDL_AUTO: " + api.getHbm2ddlAuto().getExternalHbm2ddlName());
                logger.info("- SHOW_SQL: " + api.isShowSQL());
                logger.info("- FORMAT_SQL: " + api.isFormatSQL());
                logger.info("- DIALECT: " + api.getDialect().substring(api.getDialect().indexOf("dialect.") + 8));
                logger.info("- SCHEMA: " + api.getDefaultSchema());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    SessionFactory getSessionFactory() {
        if (sessionFactory == null){
            throw new IllegalStateException("Session Factory is not initialized. Use McHibernateApi.run().");
        }
        return sessionFactory;
    }

    private void registerAnnotatedClasses(Configuration configuration) {
        for (Class<?> clazz : findAllClassesUsingReflectionsLibrary(api.getPackagesToScan())){
            if (clazz.isAnnotationPresent(Entity.class)){
                configuration.addAnnotatedClass(clazz);
            }
        }
    }

    private Set<Class<?>> findAllClassesUsingReflectionsLibrary(String packageName) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        return new HashSet<>(reflections.getSubTypesOf(Object.class));
    }

}
