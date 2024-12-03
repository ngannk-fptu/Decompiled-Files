/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class HasMethod
extends ProjectComponent
implements Condition {
    private String classname;
    private String method;
    private String field;
    private Path classpath;
    private AntClassLoader loader;
    private boolean ignoreSystemClasses = false;

    public void setClasspath(Path classpath) {
        this.createClasspath().append(classpath);
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        this.createClasspath().setRefid(r);
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setIgnoreSystemClasses(boolean ignoreSystemClasses) {
        this.ignoreSystemClasses = ignoreSystemClasses;
    }

    private Class<?> loadClass(String classname) {
        try {
            if (this.ignoreSystemClasses) {
                this.loader = this.getProject().createClassLoader(this.classpath);
                this.loader.setParentFirst(false);
                this.loader.addJavaLibraries();
                try {
                    return this.loader.findClass(classname);
                }
                catch (SecurityException se) {
                    throw new BuildException("class \"" + classname + "\" was found but a SecurityException has been raised while loading it", se);
                }
            }
            if (this.loader != null) {
                return this.loader.loadClass(classname);
            }
            ClassLoader l = this.getClass().getClassLoader();
            if (l != null) {
                return Class.forName(classname, true, l);
            }
            return Class.forName(classname);
        }
        catch (ClassNotFoundException e) {
            throw new BuildException("class \"" + classname + "\" was not found");
        }
        catch (NoClassDefFoundError e) {
            throw new BuildException("Could not load dependent class \"" + e.getMessage() + "\" for class \"" + classname + "\"");
        }
    }

    @Override
    public boolean eval() throws BuildException {
        if (this.classname == null) {
            throw new BuildException("No classname defined");
        }
        AntClassLoader preLoadClass = this.loader;
        try {
            Class<?> clazz = this.loadClass(this.classname);
            if (this.method != null) {
                boolean bl = this.isMethodFound(clazz);
                return bl;
            }
            if (this.field != null) {
                boolean bl = this.isFieldFound(clazz);
                return bl;
            }
            throw new BuildException("Neither method nor field defined");
        }
        finally {
            if (preLoadClass != this.loader && this.loader != null) {
                this.loader.cleanup();
                this.loader = null;
            }
        }
    }

    private boolean isFieldFound(Class<?> clazz) {
        for (Field fieldEntry : clazz.getDeclaredFields()) {
            if (!fieldEntry.getName().equals(this.field)) continue;
            return true;
        }
        return false;
    }

    private boolean isMethodFound(Class<?> clazz) {
        for (Method methodEntry : clazz.getDeclaredMethods()) {
            if (!methodEntry.getName().equals(this.method)) continue;
            return true;
        }
        return false;
    }
}

