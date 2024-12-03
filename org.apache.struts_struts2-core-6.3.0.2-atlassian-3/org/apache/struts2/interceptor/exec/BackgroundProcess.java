/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor.exec;

import com.opensymphony.xwork2.ActionInvocation;

public interface BackgroundProcess
extends Runnable {
    public BackgroundProcess prepare();

    public Object getAction();

    public ActionInvocation getInvocation();

    public String getResult();

    public Exception getException();

    public boolean isDone();
}

