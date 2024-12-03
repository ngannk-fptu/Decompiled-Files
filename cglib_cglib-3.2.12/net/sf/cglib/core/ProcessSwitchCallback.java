/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.Label
 */
package net.sf.cglib.core;

import org.objectweb.asm.Label;

public interface ProcessSwitchCallback {
    public void processCase(int var1, Label var2) throws Exception;

    public void processDefault() throws Exception;
}

