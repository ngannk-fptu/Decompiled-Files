/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.rmic.ForkingSunRmic;
import org.apache.tools.ant.taskdefs.rmic.KaffeRmic;
import org.apache.tools.ant.taskdefs.rmic.RmicAdapter;
import org.apache.tools.ant.taskdefs.rmic.SunRmic;
import org.apache.tools.ant.taskdefs.rmic.WLRmic;
import org.apache.tools.ant.taskdefs.rmic.XNewRmic;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public final class RmicAdapterFactory {
    public static final String ERROR_UNKNOWN_COMPILER = "Class not found: ";
    public static final String ERROR_NOT_RMIC_ADAPTER = "Class of unexpected Type: ";
    public static final String DEFAULT_COMPILER = "default";

    private RmicAdapterFactory() {
    }

    public static RmicAdapter getRmic(String rmicType, Task task) throws BuildException {
        return RmicAdapterFactory.getRmic(rmicType, task, null);
    }

    public static RmicAdapter getRmic(String rmicType, Task task, Path classpath) throws BuildException {
        if (DEFAULT_COMPILER.equalsIgnoreCase(rmicType) || rmicType.isEmpty()) {
            rmicType = KaffeRmic.isAvailable() ? "kaffe" : (JavaEnvUtils.isAtLeastJavaVersion("9") ? "forking" : "sun");
        }
        if ("sun".equalsIgnoreCase(rmicType)) {
            return new SunRmic();
        }
        if ("kaffe".equalsIgnoreCase(rmicType)) {
            return new KaffeRmic();
        }
        if ("weblogic".equalsIgnoreCase(rmicType)) {
            return new WLRmic();
        }
        if ("forking".equalsIgnoreCase(rmicType)) {
            return new ForkingSunRmic();
        }
        if ("xnew".equalsIgnoreCase(rmicType)) {
            return new XNewRmic();
        }
        return RmicAdapterFactory.resolveClassName(rmicType, task.getProject().createClassLoader(classpath));
    }

    private static RmicAdapter resolveClassName(String className, ClassLoader loader) throws BuildException {
        return ClasspathUtils.newInstance(className, loader != null ? loader : RmicAdapterFactory.class.getClassLoader(), RmicAdapter.class);
    }
}

