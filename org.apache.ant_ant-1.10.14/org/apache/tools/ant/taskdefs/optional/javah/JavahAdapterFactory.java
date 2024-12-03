/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.javah;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.optional.javah.ForkingJavah;
import org.apache.tools.ant.taskdefs.optional.javah.Gcjh;
import org.apache.tools.ant.taskdefs.optional.javah.JavahAdapter;
import org.apache.tools.ant.taskdefs.optional.javah.Kaffeh;
import org.apache.tools.ant.taskdefs.optional.javah.SunJavah;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public class JavahAdapterFactory {
    public static String getDefault() {
        if (JavaEnvUtils.isKaffe()) {
            return "kaffeh";
        }
        if (JavaEnvUtils.isGij()) {
            return "gcjh";
        }
        return "forking";
    }

    public static JavahAdapter getAdapter(String choice, ProjectComponent log) throws BuildException {
        return JavahAdapterFactory.getAdapter(choice, log, null);
    }

    public static JavahAdapter getAdapter(String choice, ProjectComponent log, Path classpath) throws BuildException {
        if (JavaEnvUtils.isKaffe() && choice == null || "kaffeh".equals(choice)) {
            return new Kaffeh();
        }
        if (JavaEnvUtils.isGij() && choice == null || "gcjh".equals(choice)) {
            return new Gcjh();
        }
        if (JavaEnvUtils.isAtLeastJavaVersion("10") && (choice == null || "forking".equals(choice))) {
            throw new BuildException("javah does not exist under Java 10 and higher, use the javac task with nativeHeaderDir instead");
        }
        if ("forking".equals(choice)) {
            return new ForkingJavah();
        }
        if ("sun".equals(choice)) {
            return new SunJavah();
        }
        if (choice != null) {
            return JavahAdapterFactory.resolveClassName(choice, log.getProject().createClassLoader(classpath));
        }
        return new ForkingJavah();
    }

    private static JavahAdapter resolveClassName(String className, ClassLoader loader) throws BuildException {
        return ClasspathUtils.newInstance(className, loader != null ? loader : JavahAdapterFactory.class.getClassLoader(), JavahAdapter.class);
    }
}

