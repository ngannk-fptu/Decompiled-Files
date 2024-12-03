/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.log.LogChute
 */
package org.apache.velocity.tools.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

@Deprecated
public class ServletLogChute
implements LogChute {
    public static final String RUNTIME_LOG_LEVEL_KEY = "runtime.log.logsystem.servlet.level";
    private int enabled = -1;
    protected ServletContext servletContext = null;
    public static final String PREFIX = " Velocity ";

    public void init(RuntimeServices rs) throws Exception {
        Object obj = rs.getApplicationAttribute((Object)ServletContext.class.getName());
        if (obj == null) {
            throw new IllegalStateException("Could not retrieve ServletContext from application attributes!");
        }
        this.servletContext = (ServletContext)obj;
        String level = (String)rs.getProperty(RUNTIME_LOG_LEVEL_KEY);
        if (level != null) {
            this.setEnabledLevel(this.toLevel(level));
        }
    }

    protected int toLevel(String level) {
        if (level.equalsIgnoreCase("debug")) {
            return 0;
        }
        if (level.equalsIgnoreCase("info")) {
            return 1;
        }
        if (level.equalsIgnoreCase("warn")) {
            return 2;
        }
        if (level.equalsIgnoreCase("error")) {
            return 3;
        }
        return -1;
    }

    public void setEnabledLevel(int level) {
        this.enabled = level;
    }

    public int getEnabledLevel() {
        return this.enabled;
    }

    public boolean isLevelEnabled(int level) {
        return level >= this.enabled;
    }

    public void log(int level, String message) {
        if (!this.isLevelEnabled(level)) {
            return;
        }
        switch (level) {
            case 2: {
                this.servletContext.log(" Velocity   [warn] " + message);
                break;
            }
            case 1: {
                this.servletContext.log(" Velocity   [info] " + message);
                break;
            }
            case 0: {
                this.servletContext.log(" Velocity  [debug] " + message);
                break;
            }
            case -1: {
                this.servletContext.log(" Velocity  [trace] " + message);
                break;
            }
            case 3: {
                this.servletContext.log(" Velocity  [error] " + message);
                break;
            }
            default: {
                this.servletContext.log(" Velocity  : " + message);
            }
        }
    }

    public void log(int level, String message, Throwable t) {
        if (!this.isLevelEnabled(level)) {
            return;
        }
        message = message + " - " + t.toString();
        if (level >= 3) {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            message = message + "\n" + sw.toString();
        }
        this.log(level, message);
    }
}

