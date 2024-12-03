/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import java.io.IOException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;

public class ConfigurationException
extends IOException {
    private Exception containedException = null;
    private String stackTrace = "";
    protected static boolean copyStackByDefault = true;
    protected static Log log = LogFactory.getLog((class$org$apache$axis$ConfigurationException == null ? (class$org$apache$axis$ConfigurationException = ConfigurationException.class$("org.apache.axis.ConfigurationException")) : class$org$apache$axis$ConfigurationException).getName());
    static /* synthetic */ Class class$org$apache$axis$ConfigurationException;

    public ConfigurationException(String message) {
        super(message);
        if (copyStackByDefault) {
            this.stackTrace = JavaUtils.stackToString(this);
        }
        this.logException(this);
    }

    public ConfigurationException(Exception exception) {
        this(exception, copyStackByDefault);
    }

    public String toString() {
        String stack = this.stackTrace.length() == 0 ? "" : "\n" + this.stackTrace;
        return super.toString() + stack;
    }

    public ConfigurationException(Exception exception, boolean copyStack) {
        super(exception.toString() + (copyStack ? "\n" + JavaUtils.stackToString(exception) : ""));
        this.containedException = exception;
        if (copyStack) {
            this.stackTrace = JavaUtils.stackToString(this);
        }
        if (!(exception instanceof ConfigurationException)) {
            this.logException(exception);
        }
    }

    private void logException(Exception exception) {
        log.debug((Object)"Exception: ", (Throwable)exception);
    }

    public Exception getContainedException() {
        return this.containedException;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

