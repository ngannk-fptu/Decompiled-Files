/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.jsp.Jasper41Mangler;
import org.apache.tools.ant.taskdefs.optional.jsp.JspNameMangler;
import org.apache.tools.ant.taskdefs.optional.jsp.compilers.JasperC;
import org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapter;

public final class JspCompilerAdapterFactory {
    private JspCompilerAdapterFactory() {
    }

    public static JspCompilerAdapter getCompiler(String compilerType, Task task) throws BuildException {
        return JspCompilerAdapterFactory.getCompiler(compilerType, task, task.getProject().createClassLoader(null));
    }

    public static JspCompilerAdapter getCompiler(String compilerType, Task task, AntClassLoader loader) throws BuildException {
        if ("jasper".equalsIgnoreCase(compilerType)) {
            return new JasperC(new JspNameMangler());
        }
        if ("jasper41".equalsIgnoreCase(compilerType)) {
            return new JasperC(new Jasper41Mangler());
        }
        return JspCompilerAdapterFactory.resolveClassName(compilerType, loader);
    }

    private static JspCompilerAdapter resolveClassName(String className, AntClassLoader classloader) throws BuildException {
        try {
            Class<JspCompilerAdapter> c = classloader.findClass(className).asSubclass(JspCompilerAdapter.class);
            return c.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException cnfe) {
            throw new BuildException(className + " can't be found.", cnfe);
        }
        catch (ClassCastException cce) {
            throw new BuildException(className + " isn't the classname of a compiler adapter.", cce);
        }
        catch (Throwable t) {
            throw new BuildException(className + " caused an interesting exception.", t);
        }
    }
}

