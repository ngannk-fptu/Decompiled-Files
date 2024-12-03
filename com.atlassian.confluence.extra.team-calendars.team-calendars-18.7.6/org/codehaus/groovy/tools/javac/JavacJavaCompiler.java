/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.javac;

import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.messages.ExceptionMessage;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.tools.javac.JavaCompiler;

public class JavacJavaCompiler
implements JavaCompiler {
    private CompilerConfiguration config;

    public JavacJavaCompiler(CompilerConfiguration config) {
        this.config = config;
    }

    @Override
    public void compile(List<String> files, CompilationUnit cu) {
        String[] javacParameters = this.makeParameters(files, cu.getClassLoader());
        StringWriter javacOutput = null;
        int javacReturnValue = 0;
        try {
            Class javac = this.findJavac(cu);
            Method method = null;
            try {
                method = javac.getMethod("compile", String[].class, PrintWriter.class);
                javacOutput = new StringWriter();
                PrintWriter writer = new PrintWriter(javacOutput);
                Object ret = method.invoke(null, javacParameters, writer);
                javacReturnValue = (Integer)ret;
            }
            catch (NoSuchMethodException writer) {
                // empty catch block
            }
            if (method == null) {
                method = javac.getMethod("compile", String[].class);
                Object ret = method.invoke(null, new Object[]{javacParameters});
                javacReturnValue = (Integer)ret;
            }
        }
        catch (InvocationTargetException ite) {
            cu.getErrorCollector().addFatalError(new ExceptionMessage((Exception)ite.getCause(), true, cu));
        }
        catch (Exception e) {
            cu.getErrorCollector().addFatalError(new ExceptionMessage(e, true, cu));
        }
        if (javacReturnValue != 0) {
            switch (javacReturnValue) {
                case 1: {
                    JavacJavaCompiler.addJavacError("Compile error during compilation with javac.", cu, javacOutput);
                    break;
                }
                case 2: {
                    JavacJavaCompiler.addJavacError("Invalid commandline usage for javac.", cu, javacOutput);
                    break;
                }
                case 3: {
                    JavacJavaCompiler.addJavacError("System error during compilation with javac.", cu, javacOutput);
                    break;
                }
                case 4: {
                    JavacJavaCompiler.addJavacError("Abnormal termination of javac.", cu, javacOutput);
                    break;
                }
                default: {
                    JavacJavaCompiler.addJavacError("unexpected return value by javac.", cu, javacOutput);
                    break;
                }
            }
        } else {
            System.out.print(javacOutput);
        }
    }

    private static void addJavacError(String header, CompilationUnit cu, StringWriter msg) {
        header = msg != null ? header + "\n" + msg.getBuffer().toString() : header + "\nThis javac version does not support compile(String[],PrintWriter), so no further details of the error are available. The message error text should be found on System.err.\n";
        cu.getErrorCollector().addFatalError(new SimpleMessage(header, cu));
    }

    private String[] makeParameters(List<String> files, GroovyClassLoader parentClassLoader) {
        Map<String, Object> options = this.config.getJointCompilationOptions();
        LinkedList<String> paras = new LinkedList<String>();
        File target = this.config.getTargetDirectory();
        if (target == null) {
            target = new File(".");
        }
        paras.add("-d");
        paras.add(target.getAbsolutePath());
        paras.add("-sourcepath");
        paras.add(((File)options.get("stubDir")).getAbsolutePath());
        String[] flags = (String[])options.get("flags");
        if (flags != null) {
            for (String flag : flags) {
                paras.add('-' + flag);
            }
        }
        boolean hadClasspath = false;
        String[] namedValues = (String[])options.get("namedValues");
        if (namedValues != null) {
            for (int i = 0; i < namedValues.length; i += 2) {
                String name = namedValues[i];
                if (name.equals("classpath")) {
                    hadClasspath = true;
                }
                paras.add('-' + name);
                paras.add(namedValues[i + 1]);
            }
        }
        if (!hadClasspath) {
            StringBuilder resultPath = new StringBuilder(DefaultGroovyMethods.join(this.config.getClasspath(), File.pathSeparator));
            for (ClassLoader cl = parentClassLoader; cl != null; cl = cl.getParent()) {
                if (!(cl instanceof URLClassLoader)) continue;
                for (URL u : ((URLClassLoader)cl).getURLs()) {
                    try {
                        resultPath.append(File.pathSeparator);
                        resultPath.append(new File(u.toURI()).getPath());
                    }
                    catch (URISyntaxException uRISyntaxException) {
                        // empty catch block
                    }
                }
            }
            paras.add("-classpath");
            paras.add(resultPath.toString());
        }
        paras.addAll(files);
        return paras.toArray(new String[paras.size()]);
    }

    private Class findJavac(CompilationUnit cu) throws ClassNotFoundException {
        String main = "com.sun.tools.javac.Main";
        try {
            return Class.forName(main);
        }
        catch (ClassNotFoundException classNotFoundException) {
            try {
                ClassLoader cl22 = this.getClass().getClassLoader();
                return cl22.loadClass(main);
            }
            catch (ClassNotFoundException cl22) {
                try {
                    return ClassLoader.getSystemClassLoader().loadClass(main);
                }
                catch (ClassNotFoundException cl22) {
                    try {
                        return cu.getClassLoader().getParent().loadClass(main);
                    }
                    catch (ClassNotFoundException cl22) {
                        File toolsJar;
                        String javaHome = System.getProperty("java.home");
                        if (javaHome.toLowerCase(Locale.US).endsWith("jre")) {
                            javaHome = javaHome.substring(0, javaHome.length() - 4);
                        }
                        if ((toolsJar = new File(javaHome + "/lib/tools.jar")).exists()) {
                            GroovyClassLoader loader = cu.getClassLoader();
                            loader.addClasspath(toolsJar.getAbsolutePath());
                            return loader.loadClass(main);
                        }
                        throw new ClassNotFoundException("unable to locate the java compiler com.sun.tools.javac.Main, please change your classloader settings");
                    }
                }
            }
        }
    }
}

