/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.aspectj.weaver.tools.AbstractTrace;

public class Jdk14Trace
extends AbstractTrace {
    private Logger logger;
    private String name;

    public Jdk14Trace(Class clazz) {
        super(clazz);
        this.name = clazz.getName();
        this.logger = Logger.getLogger(this.name);
    }

    @Override
    public void enter(String methodName, Object thiz, Object[] args) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.entering(this.name, methodName, this.formatObj(thiz));
            if (args != null && this.logger.isLoggable(Level.FINER)) {
                this.logger.entering(this.name, methodName, this.formatObjects(args));
            }
        }
    }

    @Override
    public void enter(String methodName, Object thiz) {
        this.enter(methodName, thiz, null);
    }

    @Override
    public void exit(String methodName, Object ret) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.exiting(this.name, methodName, this.formatObj(ret));
        }
    }

    @Override
    public void exit(String methodName, Throwable th) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.exiting(this.name, methodName, th);
        }
    }

    @Override
    public void exit(String methodName) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.exiting(this.name, methodName);
        }
    }

    @Override
    public void event(String methodName, Object thiz, Object[] args) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.logp(Level.FINER, this.name, methodName, "EVENT", this.formatObj(thiz));
            if (args != null && this.logger.isLoggable(Level.FINER)) {
                this.logger.logp(Level.FINER, this.name, methodName, "EVENT", this.formatObjects(args));
            }
        }
    }

    @Override
    public void event(String methodName) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.logp(Level.FINER, this.name, methodName, "EVENT");
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isLoggable(Level.FINER);
    }

    @Override
    public void setTraceEnabled(boolean b) {
        if (b) {
            Logger parent;
            this.logger.setLevel(Level.FINER);
            Handler[] handlers = this.logger.getHandlers();
            if (handlers.length == 0 && (parent = this.logger.getParent()) != null) {
                handlers = parent.getHandlers();
            }
            for (int i = 0; i < handlers.length; ++i) {
                Handler handler = handlers[i];
                handler.setLevel(Level.FINER);
            }
        } else {
            this.logger.setLevel(Level.INFO);
        }
    }

    @Override
    public void debug(String message) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine(message);
        }
    }

    @Override
    public void info(String message) {
        if (this.logger.isLoggable(Level.INFO)) {
            this.logger.info(message);
        }
    }

    @Override
    public void warn(String message, Throwable th) {
        if (this.logger.isLoggable(Level.WARNING)) {
            this.logger.log(Level.WARNING, message, th);
        }
    }

    @Override
    public void error(String message, Throwable th) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            this.logger.log(Level.SEVERE, message, th);
        }
    }

    @Override
    public void fatal(String message, Throwable th) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            this.logger.log(Level.SEVERE, message, th);
        }
    }
}

