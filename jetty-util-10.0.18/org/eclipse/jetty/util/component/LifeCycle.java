/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.component;

import java.util.EventListener;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;

@ManagedObject(value="Lifecycle Interface for startable components")
public interface LifeCycle {
    @ManagedOperation(value="Starts the instance", impact="ACTION")
    public void start() throws Exception;

    public static void start(Object object) {
        if (object instanceof LifeCycle) {
            try {
                ((LifeCycle)object).start();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @ManagedOperation(value="Stops the instance", impact="ACTION")
    public void stop() throws Exception;

    public static void stop(Object object) {
        if (object instanceof LifeCycle) {
            try {
                ((LifeCycle)object).stop();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isRunning();

    public boolean isStarted();

    public boolean isStarting();

    public boolean isStopping();

    public boolean isStopped();

    public boolean isFailed();

    public boolean addEventListener(EventListener var1);

    public boolean removeEventListener(EventListener var1);

    public static interface Listener
    extends EventListener {
        default public void lifeCycleStarting(LifeCycle event) {
        }

        default public void lifeCycleStarted(LifeCycle event) {
        }

        default public void lifeCycleFailure(LifeCycle event, Throwable cause) {
        }

        default public void lifeCycleStopping(LifeCycle event) {
        }

        default public void lifeCycleStopped(LifeCycle event) {
        }
    }
}

