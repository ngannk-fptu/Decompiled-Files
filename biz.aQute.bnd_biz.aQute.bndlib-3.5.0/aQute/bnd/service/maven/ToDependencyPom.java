/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.maven;

import aQute.bnd.service.maven.PomOptions;
import java.io.OutputStream;

public interface ToDependencyPom {
    public void toPom(OutputStream var1, PomOptions var2) throws Exception;
}

