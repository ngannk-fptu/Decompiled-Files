/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

public interface IProgressListener {
    public void setText(String var1);

    public void setProgress(double var1);

    public void setCancelledRequested(boolean var1);

    public boolean isCancelledRequested();
}

