/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import java.io.File;
import java.util.Collection;

public interface Compiler {
    public boolean compile(Project var1, Collection<File> var2, Collection<Container> var3, File var4) throws Exception;
}

