/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.component;

import java.io.FileWriter;
import java.io.Writer;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class FileNoticeLifeCycleListener
implements LifeCycle.Listener {
    private static final Logger LOG = LoggerFactory.getLogger(FileNoticeLifeCycleListener.class);
    private final String _filename;

    public FileNoticeLifeCycleListener(String filename) {
        this._filename = filename;
    }

    private void writeState(String action, LifeCycle lifecycle) {
        try (FileWriter out = new FileWriter(this._filename, true);){
            ((Writer)out).append(action).append(" ").append(lifecycle.toString()).append("\n");
        }
        catch (Exception e) {
            LOG.warn("Unable to write state", (Throwable)e);
        }
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        this.writeState("STARTING", event);
    }

    @Override
    public void lifeCycleStarted(LifeCycle event) {
        this.writeState("STARTED", event);
    }

    @Override
    public void lifeCycleFailure(LifeCycle event, Throwable cause) {
        this.writeState("FAILED", event);
    }

    @Override
    public void lifeCycleStopping(LifeCycle event) {
        this.writeState("STOPPING", event);
    }

    @Override
    public void lifeCycleStopped(LifeCycle event) {
        this.writeState("STOPPED", event);
    }
}

