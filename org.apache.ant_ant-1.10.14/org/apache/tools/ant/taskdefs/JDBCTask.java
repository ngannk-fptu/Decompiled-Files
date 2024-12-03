/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public abstract class JDBCTask
extends Task {
    private static final int HASH_TABLE_SIZE = 3;
    private static final Hashtable<String, AntClassLoader> LOADER_MAP = new Hashtable(3);
    private boolean caching = true;
    private Path classpath;
    private AntClassLoader loader;
    private boolean autocommit = false;
    private String driver = null;
    private String url = null;
    private String userId = null;
    private String password = null;
    private String rdbms = null;
    private String version = null;
    private boolean failOnConnectionError = true;
    private List<Property> connectionProperties = new ArrayList<Property>();

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    public void setCaching(boolean enable) {
        this.caching = enable;
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        this.createClasspath().setRefid(r);
    }

    public void setDriver(String driver) {
        this.driver = driver.trim();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAutocommit(boolean autocommit) {
        this.autocommit = autocommit;
    }

    public void setRdbms(String rdbms) {
        this.rdbms = rdbms;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setFailOnConnectionError(boolean b) {
        this.failOnConnectionError = b;
    }

    protected boolean isValidRdbms(Connection conn) {
        if (this.rdbms == null && this.version == null) {
            return true;
        }
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            if (this.rdbms != null) {
                String theVendor = dmd.getDatabaseProductName().toLowerCase();
                this.log("RDBMS = " + theVendor, 3);
                if (theVendor == null || !theVendor.contains(this.rdbms)) {
                    this.log("Not the required RDBMS: " + this.rdbms, 3);
                    return false;
                }
            }
            if (this.version != null) {
                String theVersion = dmd.getDatabaseProductVersion().toLowerCase(Locale.ENGLISH);
                this.log("Version = " + theVersion, 3);
                if (theVersion == null || !theVersion.startsWith(this.version) && !theVersion.contains(" " + this.version)) {
                    this.log("Not the required version: \"" + this.version + "\"", 3);
                    return false;
                }
            }
        }
        catch (SQLException e) {
            this.log("Failed to obtain required RDBMS information", 0);
            return false;
        }
        return true;
    }

    protected static Hashtable<String, AntClassLoader> getLoaderMap() {
        return LOADER_MAP;
    }

    protected AntClassLoader getLoader() {
        return this.loader;
    }

    public void addConnectionProperty(Property var) {
        this.connectionProperties.add(var);
    }

    protected Connection getConnection() throws BuildException {
        if (this.userId == null) {
            throw new BuildException("UserId attribute must be set!", this.getLocation());
        }
        if (this.password == null) {
            throw new BuildException("Password attribute must be set!", this.getLocation());
        }
        if (this.url == null) {
            throw new BuildException("Url attribute must be set!", this.getLocation());
        }
        try {
            this.log("connecting to " + this.getUrl(), 3);
            Properties info = new Properties();
            info.put("user", this.getUserId());
            info.put("password", this.getPassword());
            for (Property p : this.connectionProperties) {
                String name = p.getName();
                String value = p.getValue();
                if (name == null || value == null) {
                    this.log("Only name/value pairs are supported as connection properties.", 1);
                    continue;
                }
                this.log("Setting connection property " + name + " to " + value, 3);
                info.put(name, value);
            }
            Connection conn = this.getDriver().connect(this.getUrl(), info);
            if (conn == null) {
                throw new SQLException("No suitable Driver for " + this.url);
            }
            conn.setAutoCommit(this.autocommit);
            return conn;
        }
        catch (SQLException e) {
            if (this.failOnConnectionError) {
                throw new BuildException(e, this.getLocation());
            }
            this.log("Failed to connect: " + e.getMessage(), 1);
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Driver getDriver() throws BuildException {
        Driver driverInstance;
        if (this.driver == null) {
            throw new BuildException("Driver attribute must be set!", this.getLocation());
        }
        try {
            Class<Driver> dc;
            if (this.classpath != null) {
                Hashtable<String, AntClassLoader> hashtable = LOADER_MAP;
                synchronized (hashtable) {
                    if (this.caching) {
                        this.loader = LOADER_MAP.get(this.driver);
                    }
                    if (this.loader == null) {
                        this.log("Loading " + this.driver + " using AntClassLoader with classpath " + this.classpath, 3);
                        this.loader = this.getProject().createClassLoader(this.classpath);
                        if (this.caching) {
                            LOADER_MAP.put(this.driver, this.loader);
                        }
                    } else {
                        this.log("Loading " + this.driver + " using a cached AntClassLoader.", 3);
                    }
                }
                dc = this.loader.loadClass(this.driver).asSubclass(Driver.class);
            } else {
                this.log("Loading " + this.driver + " using system loader.", 3);
                dc = Class.forName(this.driver).asSubclass(Driver.class);
            }
            driverInstance = dc.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException e) {
            throw new BuildException("Class Not Found: JDBC driver " + this.driver + " could not be loaded", e, this.getLocation());
        }
        catch (IllegalAccessException e) {
            throw new BuildException("Illegal Access: JDBC driver " + this.driver + " could not be loaded", e, this.getLocation());
        }
        catch (InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new BuildException(e.getClass().getSimpleName() + ": JDBC driver " + this.driver + " could not be loaded", e, this.getLocation());
        }
        return driverInstance;
    }

    public void isCaching(boolean value) {
        this.caching = value;
    }

    public Path getClasspath() {
        return this.classpath;
    }

    public boolean isAutocommit() {
        return this.autocommit;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserid(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRdbms() {
        return this.rdbms;
    }

    public String getVersion() {
        return this.version;
    }
}

