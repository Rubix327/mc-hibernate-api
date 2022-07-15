package me.rubix327.mchibernateapi.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class McHibernateApi {

    private static final McHibernateApi instance = new McHibernateApi();

    public static McHibernateApi get(){
        return instance;
    }

    private final Map<String, String> props = new HashMap<>();

    private String database;
    private String host = "localhost";
    private String port = "3306";
    private String username = "root";
    private String password = "";
    private String driver = "com.mysql.cj.jdbc.Driver";
    private boolean useSSL = false;
    private String append = "";

    private boolean showSQL = false;
    private boolean formatSQL = true;
    private boolean autocommit = false;
    private Action hbm2ddlAuto = Action.UPDATE;
    private String dialect = "org.hibernate.dialect.MySQLDialect";
    private String packagesToScan = null;
    private String encoding = "utf8";
    private String defaultSchema = null;
    private String currentSessionContextClass = "thread";

    public String getUrl(){
        return "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL + append;
    }

    public void setProperty(String key, String value){
        props.put(key, value);
    }

    public void setProperty(AvailableSettings prop, String value){
        setProperty(prop.toString(), value);
    }

    public void run(){
        if (database == null){
            throw new IllegalArgumentException("Parameter 'database' is not set in McHibernateApi.");
        }
        if (packagesToScan == null){
            throw new IllegalArgumentException("Parameter 'packagesToScan' is not set in McHibernateApi.");
        }
        HibernateUtil.get().run();
    }

    public SessionFactory getSessionFactory(){
        return HibernateUtil.get().getSessionFactory();
    }

}
