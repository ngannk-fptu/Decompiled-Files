/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

public final class SplitClassLoader
extends AntClassLoader {
    private final String[] splitClasses;

    public SplitClassLoader(ClassLoader parent, Path path, Project project, String[] splitClasses) {
        super(parent, project, path, true);
        this.splitClasses = splitClasses;
    }

    @Override
    protected synchronized Class<?> loadClass(String classname, boolean resolve) throws ClassNotFoundException {
        Class<?> theClass = this.findLoadedClass(classname);
        if (theClass != null) {
            return theClass;
        }
        if (this.isSplit(classname)) {
            theClass = this.findClass(classname);
            if (resolve) {
                this.resolveClass(theClass);
            }
            return theClass;
        }
        return super.loadClass(classname, resolve);
    }

    private boolean isSplit(String classname) {
        String simplename = classname.substring(classname.lastIndexOf(46) + 1);
        for (String splitClass : this.splitClasses) {
            if (!simplename.equals(splitClass) && !simplename.startsWith(splitClass + '$')) continue;
            return true;
        }
        return false;
    }
}

