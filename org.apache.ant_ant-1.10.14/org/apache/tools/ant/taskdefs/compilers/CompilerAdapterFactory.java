/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.compilers;

import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
import org.apache.tools.ant.taskdefs.compilers.Gcj;
import org.apache.tools.ant.taskdefs.compilers.Javac13;
import org.apache.tools.ant.taskdefs.compilers.JavacExternal;
import org.apache.tools.ant.taskdefs.compilers.Jikes;
import org.apache.tools.ant.taskdefs.compilers.Jvc;
import org.apache.tools.ant.taskdefs.compilers.Kjc;
import org.apache.tools.ant.taskdefs.compilers.Sj;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public final class CompilerAdapterFactory {
    private static final String MODERN_COMPILER = "com.sun.tools.javac.Main";
    public static final String COMPILER_JIKES = "jikes";
    public static final String COMPILER_GCJ = "gcj";
    public static final String COMPILER_SYMANTEC_ALIAS = "sj";
    public static final String COMPILER_SYMANTEC = "symantec";
    public static final String COMPILER_JVC_ALIAS = "microsoft";
    public static final String COMPILER_JVC = "jvc";
    public static final String COMPILER_KJC = "kjc";
    public static final String COMPILER_JAVAC_1_1 = "javac1.1";
    public static final String COMPILER_JAVAC_1_2 = "javac1.2";
    public static final String COMPILER_JAVAC_1_3 = "javac1.3";
    public static final String COMPILER_JAVAC_1_4 = "javac1.4";
    public static final String COMPILER_JAVAC_1_5 = "javac1.5";
    public static final String COMPILER_JAVAC_1_6 = "javac1.6";
    public static final String COMPILER_JAVAC_1_7 = "javac1.7";
    public static final String COMPILER_JAVAC_1_8 = "javac1.8";
    public static final String COMPILER_JAVAC_9_ALIAS = "javac1.9";
    public static final String COMPILER_JAVAC_9 = "javac9";
    public static final String COMPILER_JAVAC_10_PLUS = "javac10+";
    public static final String COMPILER_CLASSIC = "classic";
    public static final String COMPILER_MODERN = "modern";
    public static final String COMPILER_EXTJAVAC = "extJavac";
    public static final String COMPILER_MODERN_CLASSNAME = Javac13.class.getName();
    public static final String COMPILER_EXTJAVAC_CLASSNAME = JavacExternal.class.getName();
    private static final List<String> JDK_COMPILERS = Arrays.asList("javac1.1", "javac1.2", "javac1.3", "javac1.4", "javac1.5", "javac1.6", "javac1.7", "javac1.8", "javac1.9", "javac9", "javac10+", "classic", "modern", "extJavac", COMPILER_MODERN_CLASSNAME, COMPILER_EXTJAVAC_CLASSNAME);
    private static final List<String> FORKED_JDK_COMPILERS = Arrays.asList("extJavac", COMPILER_EXTJAVAC_CLASSNAME);
    private static final List<String> JDK_COMPILER_NICKNAMES = Arrays.asList("classic", "modern", "extJavac", COMPILER_MODERN_CLASSNAME, COMPILER_EXTJAVAC_CLASSNAME);
    private static final List<String> CLASSIC_JDK_COMPILERS = Arrays.asList("javac1.1", "javac1.2");
    private static final List<String> MODERN_JDK_COMPILERS = Arrays.asList("javac1.3", "javac1.4", "javac1.5", "javac1.6", "javac1.7", "javac1.8", "javac1.9", "javac9", "javac10+", COMPILER_MODERN_CLASSNAME);

    private CompilerAdapterFactory() {
    }

    public static CompilerAdapter getCompiler(String compilerType, Task task) throws BuildException {
        return CompilerAdapterFactory.getCompiler(compilerType, task, null);
    }

    public static CompilerAdapter getCompiler(String compilerType, Task task, Path classpath) throws BuildException {
        if (COMPILER_JIKES.equalsIgnoreCase(compilerType)) {
            return new Jikes();
        }
        if (CompilerAdapterFactory.isForkedJavac(compilerType)) {
            return new JavacExternal();
        }
        if (COMPILER_CLASSIC.equalsIgnoreCase(compilerType) || CompilerAdapterFactory.isClassicJdkCompiler(compilerType)) {
            task.log("This version of java does not support the classic compiler; upgrading to modern", 1);
            compilerType = COMPILER_MODERN;
        }
        if (COMPILER_MODERN.equalsIgnoreCase(compilerType) || CompilerAdapterFactory.isModernJdkCompiler(compilerType)) {
            if (CompilerAdapterFactory.doesModernCompilerExist()) {
                return new Javac13();
            }
            throw new BuildException("Unable to find a javac compiler;\n%s is not on the classpath.\nPerhaps JAVA_HOME does not point to the JDK.\nIt is currently set to \"%s\"", MODERN_COMPILER, JavaEnvUtils.getJavaHome());
        }
        if (COMPILER_JVC.equalsIgnoreCase(compilerType) || COMPILER_JVC_ALIAS.equalsIgnoreCase(compilerType)) {
            return new Jvc();
        }
        if (COMPILER_KJC.equalsIgnoreCase(compilerType)) {
            return new Kjc();
        }
        if (COMPILER_GCJ.equalsIgnoreCase(compilerType)) {
            return new Gcj();
        }
        if (COMPILER_SYMANTEC_ALIAS.equalsIgnoreCase(compilerType) || COMPILER_SYMANTEC.equalsIgnoreCase(compilerType)) {
            return new Sj();
        }
        return CompilerAdapterFactory.resolveClassName(compilerType, task.getProject().createClassLoader(classpath));
    }

    private static boolean doesModernCompilerExist() {
        try {
            Class.forName(MODERN_COMPILER);
            return true;
        }
        catch (ClassNotFoundException cnfe) {
            try {
                ClassLoader cl = CompilerAdapterFactory.class.getClassLoader();
                if (cl != null) {
                    cl.loadClass(MODERN_COMPILER);
                    return true;
                }
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
            return false;
        }
    }

    private static CompilerAdapter resolveClassName(String className, ClassLoader loader) throws BuildException {
        return ClasspathUtils.newInstance(className, loader != null ? loader : CompilerAdapterFactory.class.getClassLoader(), CompilerAdapter.class);
    }

    public static boolean isForkedJavac(String compilerName) {
        return CompilerAdapterFactory.containsIgnoreCase(FORKED_JDK_COMPILERS, compilerName);
    }

    public static boolean isJdkCompiler(String compilerName) {
        return CompilerAdapterFactory.containsIgnoreCase(JDK_COMPILERS, compilerName);
    }

    public static boolean isJdkCompilerNickname(String compilerName) {
        return CompilerAdapterFactory.containsIgnoreCase(JDK_COMPILER_NICKNAMES, compilerName);
    }

    public static boolean isClassicJdkCompiler(String compilerName) {
        return CompilerAdapterFactory.containsIgnoreCase(CLASSIC_JDK_COMPILERS, compilerName);
    }

    public static boolean isModernJdkCompiler(String compilerName) {
        return CompilerAdapterFactory.containsIgnoreCase(MODERN_JDK_COMPILERS, compilerName);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean containsIgnoreCase(List<String> compilers, String compilerName) {
        if (compilerName == null) return false;
        if (!compilers.stream().anyMatch(compilerName::equalsIgnoreCase)) return false;
        return true;
    }
}

