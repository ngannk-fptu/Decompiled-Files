/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Appender
 *  org.apache.log4j.Category
 *  org.apache.log4j.Layout
 *  org.apache.log4j.Level
 *  org.apache.log4j.PatternLayout
 *  org.apache.log4j.RollingFileAppender
 */
package org.apache.velocity.runtime.log;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

public class SimpleLog4JLogSystem
implements LogSystem {
    private RuntimeServices rsvc = null;
    private RollingFileAppender appender = null;
    protected Category logger = null;

    public void init(RuntimeServices rs) {
        this.rsvc = rs;
        String categoryname = (String)this.rsvc.getProperty("runtime.log.logsystem.log4j.category");
        if (categoryname != null) {
            this.logger = Category.getInstance((String)categoryname);
            this.logVelocityMessage(0, "SimpleLog4JLogSystem using category '" + categoryname + "'");
            return;
        }
        String logfile = this.rsvc.getString("runtime.log");
        try {
            this.internalInit(logfile);
            this.logVelocityMessage(0, "SimpleLog4JLogSystem initialized using logfile '" + logfile + "'");
        }
        catch (Exception e) {
            System.err.println("PANIC : error configuring SimpleLog4JLogSystem : " + e);
        }
    }

    private void internalInit(String logfile) throws Exception {
        this.logger = Category.getInstance((String)this.getClass().getName());
        this.logger.setAdditivity(false);
        this.logger.setLevel(Level.DEBUG);
        this.appender = new RollingFileAppender((Layout)new PatternLayout("%d - %m%n"), logfile, true);
        this.appender.setMaxBackupIndex(1);
        this.appender.setMaximumFileSize(100000L);
        this.logger.addAppender((Appender)this.appender);
    }

    public void logVelocityMessage(int level, String message) {
        switch (level) {
            case 2: {
                this.logger.warn((Object)message);
                break;
            }
            case 1: {
                this.logger.info((Object)message);
                break;
            }
            case 3: {
                this.logger.error((Object)message);
                break;
            }
            default: {
                this.logger.debug((Object)message);
            }
        }
    }

    protected void finalize() throws Throwable {
        this.shutdown();
    }

    public void shutdown() {
        if (this.appender != null) {
            this.logger.removeAppender((Appender)this.appender);
            this.appender.close();
            this.appender = null;
        }
    }
}

