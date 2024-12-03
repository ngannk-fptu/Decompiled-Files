/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor.exec;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import java.io.Serializable;
import org.apache.struts2.interceptor.exec.BackgroundProcess;

public class StrutsBackgroundProcess
implements BackgroundProcess,
Serializable {
    private static final long serialVersionUID = 3884464776311686443L;
    private final String threadName;
    private final int threadPriority;
    private transient Thread processThread;
    protected transient ActionInvocation invocation;
    protected transient Exception exception;
    protected String result;
    protected boolean done;

    public StrutsBackgroundProcess(ActionInvocation invocation, String threadName, int threadPriority) {
        this.invocation = invocation;
        this.threadName = threadName;
        this.threadPriority = threadPriority;
    }

    @Override
    public BackgroundProcess prepare() {
        try {
            this.processThread = new Thread(() -> {
                try {
                    this.beforeInvocation();
                    this.result = this.invocation.invokeActionOnly();
                    this.afterInvocation();
                }
                catch (Exception e) {
                    this.exception = e;
                }
                finally {
                    this.done = true;
                }
            });
            this.processThread.setName(this.threadName);
            this.processThread.setPriority(this.threadPriority);
        }
        catch (Exception e) {
            this.done = true;
            this.exception = e;
        }
        return this;
    }

    @Override
    public void run() {
        if (this.processThread == null) {
            this.done = true;
            this.exception = new IllegalStateException("Background thread " + this.threadName + " has not been prepared!");
            return;
        }
        this.processThread.start();
    }

    protected void beforeInvocation() throws Exception {
        ActionContext.bind(this.invocation.getInvocationContext());
    }

    protected void afterInvocation() throws Exception {
        ActionContext.clear();
    }

    @Override
    public Object getAction() {
        return this.invocation.getAction();
    }

    @Override
    public ActionInvocation getInvocation() {
        return this.invocation;
    }

    @Override
    public String getResult() {
        return this.result;
    }

    @Override
    public Exception getException() {
        return this.exception;
    }

    @Override
    public boolean isDone() {
        return this.done;
    }

    public String toString() {
        return "StrutsBackgroundProcess { name = " + this.processThread.getName() + " }";
    }
}

