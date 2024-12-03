/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.core.runtime;

public interface IStatus {
    public static final int OK = 0;
    public static final int INFO = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 4;
    public static final int CANCEL = 8;

    public IStatus[] getChildren();

    public int getCode();

    public Throwable getException();

    public String getMessage();

    public String getPlugin();

    public int getSeverity();

    public boolean isMultiStatus();

    public boolean isOK();

    public boolean matches(int var1);
}

