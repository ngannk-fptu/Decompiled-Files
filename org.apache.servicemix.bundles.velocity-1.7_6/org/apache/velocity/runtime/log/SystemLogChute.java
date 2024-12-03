/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import java.io.PrintStream;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

public class SystemLogChute
implements LogChute {
    public static final String RUNTIME_LOG_LEVEL_KEY = "runtime.log.logsystem.system.level";
    public static final String RUNTIME_LOG_SYSTEM_ERR_LEVEL_KEY = "runtime.log.logsystem.system.err.level";
    private int enabled = 2;
    private int errLevel = -1;

    public void init(RuntimeServices rs) throws Exception {
        String errLevel;
        String level = (String)rs.getProperty(RUNTIME_LOG_LEVEL_KEY);
        if (level != null) {
            this.setEnabledLevel(this.toLevel(level));
        }
        if ((errLevel = (String)rs.getProperty(RUNTIME_LOG_SYSTEM_ERR_LEVEL_KEY)) != null) {
            this.setSystemErrLevel(this.toLevel(errLevel));
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

    protected String getPrefix(int level) {
        switch (level) {
            case 2: {
                return "  [warn] ";
            }
            case 0: {
                return " [debug] ";
            }
            case -1: {
                return " [trace] ";
            }
            case 3: {
                return " [error] ";
            }
        }
        return "  [info] ";
    }

    public void log(int level, String message) {
        this.log(level, message, null);
    }

    public void log(int level, String message, Throwable t) {
        if (!this.isLevelEnabled(level)) {
            return;
        }
        String prefix = this.getPrefix(level);
        if (level >= this.errLevel) {
            this.write(System.err, prefix, message, t);
        } else {
            this.write(System.out, prefix, message, t);
        }
    }

    protected void write(PrintStream stream, String prefix, String message, Throwable t) {
        stream.print(prefix);
        stream.println(message);
        if (t != null) {
            stream.println(t.getMessage());
            t.printStackTrace(stream);
        }
    }

    public void setEnabledLevel(int level) {
        this.enabled = level;
    }

    public int getEnabledLevel() {
        return this.enabled;
    }

    public void setSystemErrLevel(int level) {
        this.errLevel = level;
    }

    public int getSystemErrLevel() {
        return this.errLevel;
    }

    public boolean isLevelEnabled(int level) {
        return level >= this.enabled;
    }
}

