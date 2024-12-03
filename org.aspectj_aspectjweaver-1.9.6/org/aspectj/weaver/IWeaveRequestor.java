/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.IUnwovenClassFile;

public interface IWeaveRequestor {
    public void acceptResult(IUnwovenClassFile var1);

    public void processingReweavableState();

    public void addingTypeMungers();

    public void weavingAspects();

    public void weavingClasses();

    public void weaveCompleted();
}

