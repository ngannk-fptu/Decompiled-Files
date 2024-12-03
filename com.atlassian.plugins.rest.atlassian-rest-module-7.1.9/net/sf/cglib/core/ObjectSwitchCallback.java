/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.core;

import org.objectweb.asm.Label;

public interface ObjectSwitchCallback {
    public void processCase(Object var1, Label var2) throws Exception;

    public void processDefault() throws Exception;
}

