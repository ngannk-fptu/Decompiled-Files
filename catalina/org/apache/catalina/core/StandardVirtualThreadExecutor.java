/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.compat.JreCompat
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.threads.VirtualThreadExecutor
 */
package org.apache.catalina.core;

import java.util.concurrent.TimeUnit;
import org.apache.catalina.Executor;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;

public class StandardVirtualThreadExecutor
extends LifecycleMBeanBase
implements Executor {
    private static final StringManager sm = StringManager.getManager(StandardVirtualThreadExecutor.class);
    private String name;
    private java.util.concurrent.Executor executor;
    private String namePrefix = "tomcat-virt-";

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getNamePrefix() {
        return this.namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public void execute(Runnable command) {
        if (this.executor == null) {
            throw new IllegalStateException(sm.getString("standardVirtualThreadExecutor.notStarted"));
        }
        this.executor.execute(command);
    }

    @Override
    public void execute(Runnable command, long timeout, TimeUnit unit) {
        this.execute(command);
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (!JreCompat.isJre21Available()) {
            throw new LifecycleException(sm.getString("standardVirtualThreadExecutor.noVirtualThreads"));
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {
        this.executor = new VirtualThreadExecutor(this.getNamePrefix());
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        this.executor = null;
        this.setState(LifecycleState.STOPPING);
    }

    @Override
    protected String getDomainInternal() {
        return null;
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return "type=Executor,name=" + this.getName();
    }
}

