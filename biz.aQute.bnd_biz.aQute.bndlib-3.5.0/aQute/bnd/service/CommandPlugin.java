/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.bnd.build.Project;

public interface CommandPlugin {
    public void before(Project var1, String var2);

    public void after(Project var1, String var2, Throwable var3);
}

