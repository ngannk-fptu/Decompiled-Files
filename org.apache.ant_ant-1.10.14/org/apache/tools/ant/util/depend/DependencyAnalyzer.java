/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.depend;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import org.apache.tools.ant.types.Path;

public interface DependencyAnalyzer {
    public void addSourcePath(Path var1);

    public void addClassPath(Path var1);

    public void addRootClass(String var1);

    public Enumeration<File> getFileDependencies();

    public Enumeration<String> getClassDependencies();

    public void reset();

    public void config(String var1, Object var2);

    public void setClosure(boolean var1);

    public File getClassContainer(String var1) throws IOException;

    public File getSourceContainer(String var1) throws IOException;
}

