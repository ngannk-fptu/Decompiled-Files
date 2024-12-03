/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.loader;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

public class AntClassLoader5
extends AntClassLoader {
    public AntClassLoader5(ClassLoader parent, Project project, Path classpath, boolean parentFirst) {
        super(parent, project, classpath, parentFirst);
    }

    static {
        AntClassLoader5.registerAsParallelCapable();
    }
}

