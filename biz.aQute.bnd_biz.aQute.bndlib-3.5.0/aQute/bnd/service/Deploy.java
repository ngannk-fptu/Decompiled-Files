/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.bnd.build.Project;
import java.io.InputStream;

public interface Deploy {
    public boolean deploy(Project var1, String var2, InputStream var3) throws Exception;
}

