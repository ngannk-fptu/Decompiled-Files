/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.exolab.castor.jdo.Database
 *  org.exolab.castor.jdo.JDO
 *  org.exolab.castor.jdo.PersistenceException
 *  org.exolab.castor.util.Configuration
 *  org.exolab.castor.util.Logger
 */
package com.opensymphony.user.provider.castor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Properties;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDO;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.util.Configuration;
import org.exolab.castor.util.Logger;

public class CastorDataProvider {
    public static final String DatabaseFile = "/META-INF/database.xml";
    private static CastorDataProvider instance = null;
    protected JDO _jdo = new JDO();

    public CastorDataProvider(Properties properties) {
        if (Configuration.debug()) {
            PrintStream out;
            if (properties.containsKey("log.file")) {
                try {
                    out = new PrintStream(new FileOutputStream(properties.getProperty("log.file")));
                }
                catch (FileNotFoundException e) {
                    out = System.out;
                }
            } else {
                out = System.out;
            }
            Logger writer = new Logger((OutputStream)out).setLogTime(true);
            writer = new Logger((OutputStream)out).setPrefix("OS:GROUPS");
            this._jdo.setLogWriter((PrintWriter)writer);
        }
        this._jdo.setConfiguration(this.getClass().getResource(DatabaseFile).toString());
        this._jdo.setDatabaseName(properties.getProperty("database", "quiz"));
        this._jdo.setDatabasePooling(true);
    }

    public static CastorDataProvider getInstance(Properties properties) {
        if (instance == null) {
            instance = new CastorDataProvider(properties);
        }
        return instance;
    }

    public Database getDatabase() throws PersistenceException {
        return this._jdo.getDatabase();
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this._jdo = null;
    }
}

