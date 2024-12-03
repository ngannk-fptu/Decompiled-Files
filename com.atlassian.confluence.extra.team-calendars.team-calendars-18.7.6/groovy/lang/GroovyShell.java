/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovySystem;
import groovy.lang.Script;
import groovy.security.GroovyCodeSourcePermission;
import groovy.ui.GroovyMain;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.plugin.GroovyRunner;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;

public class GroovyShell
extends GroovyObjectSupport {
    public static final String DEFAULT_CODE_BASE = "/groovy/shell";
    private Binding context;
    private int counter;
    private CompilerConfiguration config;
    private GroovyClassLoader loader;

    public static void main(String[] args) {
        GroovyMain.main(args);
    }

    public GroovyShell() {
        this(null, new Binding());
    }

    public GroovyShell(Binding binding) {
        this(null, binding);
    }

    public GroovyShell(ClassLoader parent, CompilerConfiguration config) {
        this(parent, new Binding(), config);
    }

    public GroovyShell(CompilerConfiguration config) {
        this(new Binding(), config);
    }

    public GroovyShell(Binding binding, CompilerConfiguration config) {
        this(null, binding, config);
    }

    public GroovyShell(ClassLoader parent, Binding binding) {
        this(parent, binding, CompilerConfiguration.DEFAULT);
    }

    public GroovyShell(ClassLoader parent) {
        this(parent, new Binding(), CompilerConfiguration.DEFAULT);
    }

    public GroovyShell(ClassLoader parent, Binding binding, final CompilerConfiguration config) {
        if (binding == null) {
            throw new IllegalArgumentException("Binding must not be null.");
        }
        if (config == null) {
            throw new IllegalArgumentException("Compiler configuration must not be null.");
        }
        final ClassLoader parentLoader = parent != null ? parent : GroovyShell.class.getClassLoader();
        this.loader = AccessController.doPrivileged(new PrivilegedAction<GroovyClassLoader>(){

            @Override
            public GroovyClassLoader run() {
                return new GroovyClassLoader(parentLoader, config);
            }
        });
        this.context = binding;
        this.config = config;
    }

    public void resetLoadedClasses() {
        this.loader.clearCache();
    }

    public GroovyShell(GroovyShell shell) {
        this((ClassLoader)shell.loader, shell.context);
    }

    public Binding getContext() {
        return this.context;
    }

    public GroovyClassLoader getClassLoader() {
        return this.loader;
    }

    @Override
    public Object getProperty(String property) {
        Object answer = this.getVariable(property);
        if (answer == null) {
            answer = super.getProperty(property);
        }
        return answer;
    }

    @Override
    public void setProperty(String property, Object newValue) {
        this.setVariable(property, newValue);
        try {
            super.setProperty(property, newValue);
        }
        catch (GroovyRuntimeException groovyRuntimeException) {
            // empty catch block
        }
    }

    public Object run(File scriptFile, List list) throws CompilationFailedException, IOException {
        String[] args = new String[list.size()];
        return this.run(scriptFile, list.toArray(args));
    }

    public Object run(String scriptText, String fileName, List list) throws CompilationFailedException {
        String[] args = new String[list.size()];
        list.toArray(args);
        return this.run(scriptText, fileName, args);
    }

    public Object run(final File scriptFile, String[] args) throws CompilationFailedException, IOException {
        Class scriptClass;
        String scriptName = scriptFile.getName();
        int p = scriptName.lastIndexOf(".");
        if (p++ >= 0 && scriptName.substring(p).equals("java")) {
            throw new CompilationFailedException(0, null);
        }
        final Thread thread = Thread.currentThread();
        class DoSetContext
        implements PrivilegedAction {
            ClassLoader classLoader;

            public DoSetContext(ClassLoader loader) {
                this.classLoader = loader;
            }

            public Object run() {
                thread.setContextClassLoader(this.classLoader);
                return null;
            }
        }
        AccessController.doPrivileged(new DoSetContext(this.loader));
        try {
            scriptClass = AccessController.doPrivileged(new PrivilegedExceptionAction<Class>(){

                @Override
                public Class run() throws CompilationFailedException, IOException {
                    return GroovyShell.this.loader.parseClass(scriptFile);
                }
            });
        }
        catch (PrivilegedActionException pae) {
            Exception e = pae.getException();
            if (e instanceof CompilationFailedException) {
                throw (CompilationFailedException)e;
            }
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            throw (RuntimeException)pae.getException();
        }
        return this.runScriptOrMainOrTestOrRunnable(scriptClass, args);
    }

    private Object runScriptOrMainOrTestOrRunnable(Class scriptClass, String[] args) {
        this.context.setProperty("args", args);
        if (scriptClass == null) {
            return null;
        }
        if (Script.class.isAssignableFrom(scriptClass)) {
            try {
                Script script = InvokerHelper.newScript(scriptClass, this.context);
                return script.run();
            }
            catch (InstantiationException script) {
            }
            catch (IllegalAccessException script) {
            }
            catch (InvocationTargetException script) {
                // empty catch block
            }
        }
        try {
            scriptClass.getMethod("main", String[].class);
            return InvokerHelper.invokeMethod(scriptClass, "main", new Object[]{args});
        }
        catch (NoSuchMethodException e) {
            if (Runnable.class.isAssignableFrom(scriptClass)) {
                return GroovyShell.runRunnable(scriptClass, args);
            }
            if (this.isJUnit3Test(scriptClass)) {
                return GroovyShell.runJUnit3Test(scriptClass);
            }
            if (this.isJUnit3TestSuite(scriptClass)) {
                return GroovyShell.runJUnit3TestSuite(scriptClass);
            }
            if (this.isJUnit4Test(scriptClass)) {
                return this.runJUnit4Test(scriptClass);
            }
            for (Map.Entry<String, GroovyRunner> entry : GroovySystem.RUNNER_REGISTRY.entrySet()) {
                GroovyRunner runner = entry.getValue();
                if (runner == null || !runner.canRun(scriptClass, this.loader)) continue;
                return runner.run(scriptClass, this.loader);
            }
            String message = "This script or class could not be run.\nIt should either:\n- have a main method,\n- be a JUnit test or extend GroovyTestCase,\n- implement the Runnable interface,\n- or be compatible with a registered script runner. Known runners:\n";
            if (GroovySystem.RUNNER_REGISTRY.isEmpty()) {
                message = message + "  * <none>";
            }
            for (Map.Entry<String, GroovyRunner> entry : GroovySystem.RUNNER_REGISTRY.entrySet()) {
                message = message + "  * " + entry.getKey() + "\n";
            }
            throw new GroovyRuntimeException(message);
        }
    }

    private static Object runRunnable(Class scriptClass, String[] args) {
        Constructor constructor = null;
        Runnable runnable = null;
        Throwable reason = null;
        try {
            constructor = scriptClass.getConstructor(new String[0].getClass());
            try {
                runnable = (Runnable)constructor.newInstance(new Object[]{args});
            }
            catch (Throwable t) {
                reason = t;
            }
        }
        catch (NoSuchMethodException e1) {
            try {
                constructor = scriptClass.getConstructor(new Class[0]);
                try {
                    runnable = (Runnable)constructor.newInstance(new Object[0]);
                }
                catch (InvocationTargetException ite) {
                    throw new InvokerInvocationException(ite.getTargetException());
                }
                catch (Throwable t) {
                    reason = t;
                }
            }
            catch (NoSuchMethodException nsme) {
                reason = nsme;
            }
        }
        if (constructor == null || runnable == null) {
            throw new GroovyRuntimeException("This script or class was runnable but could not be run. ", reason);
        }
        runnable.run();
        return null;
    }

    private static Object runJUnit3Test(Class scriptClass) {
        try {
            Object testSuite = InvokerHelper.invokeConstructorOf("junit.framework.TestSuite", (Object)new Object[]{scriptClass});
            return InvokerHelper.invokeStaticMethod("junit.textui.TestRunner", "run", (Object)new Object[]{testSuite});
        }
        catch (ClassNotFoundException e) {
            throw new GroovyRuntimeException("Failed to run the unit test. JUnit is not on the Classpath.", e);
        }
    }

    private static Object runJUnit3TestSuite(Class scriptClass) {
        try {
            Object testSuite = InvokerHelper.invokeStaticMethod(scriptClass, "suite", (Object)new Object[0]);
            return InvokerHelper.invokeStaticMethod("junit.textui.TestRunner", "run", (Object)new Object[]{testSuite});
        }
        catch (ClassNotFoundException e) {
            throw new GroovyRuntimeException("Failed to run the unit test. JUnit is not on the Classpath.", e);
        }
    }

    private Object runJUnit4Test(Class scriptClass) {
        try {
            return InvokerHelper.invokeStaticMethod("org.codehaus.groovy.vmplugin.v5.JUnit4Utils", "realRunJUnit4Test", (Object)new Object[]{scriptClass, this.loader});
        }
        catch (ClassNotFoundException e) {
            throw new GroovyRuntimeException("Failed to run the JUnit 4 test.", e);
        }
    }

    private boolean isJUnit3Test(Class scriptClass) {
        boolean isUnitTestCase = false;
        try {
            try {
                Class<?> testCaseClass = this.loader.loadClass("junit.framework.TestCase");
                if (testCaseClass.isAssignableFrom(scriptClass)) {
                    isUnitTestCase = true;
                }
            }
            catch (ClassNotFoundException classNotFoundException) {}
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return isUnitTestCase;
    }

    private boolean isJUnit3TestSuite(Class scriptClass) {
        boolean isUnitTestSuite = false;
        try {
            try {
                Class<?> testSuiteClass = this.loader.loadClass("junit.framework.TestSuite");
                if (testSuiteClass.isAssignableFrom(scriptClass)) {
                    isUnitTestSuite = true;
                }
            }
            catch (ClassNotFoundException classNotFoundException) {}
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return isUnitTestSuite;
    }

    private boolean isJUnit4Test(Class scriptClass) {
        boolean isTest = false;
        try {
            if (InvokerHelper.invokeStaticMethod("org.codehaus.groovy.vmplugin.v5.JUnit4Utils", "realIsJUnit4Test", (Object)new Object[]{scriptClass, this.loader}) == Boolean.TRUE) {
                isTest = true;
            }
        }
        catch (ClassNotFoundException e) {
            throw new GroovyRuntimeException("Failed to invoke the JUnit 4 helper class.", e);
        }
        return isTest;
    }

    public Object run(final String scriptText, final String fileName, String[] args) throws CompilationFailedException {
        GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>(){

            @Override
            public GroovyCodeSource run() {
                return new GroovyCodeSource(scriptText, fileName, GroovyShell.DEFAULT_CODE_BASE);
            }
        });
        return this.run(gcs, args);
    }

    public Object run(GroovyCodeSource source, List args) throws CompilationFailedException {
        return this.run(source, args.toArray(new String[args.size()]));
    }

    public Object run(GroovyCodeSource source, String[] args) throws CompilationFailedException {
        Class scriptClass = this.parseClass(source);
        return this.runScriptOrMainOrTestOrRunnable(scriptClass, args);
    }

    public Object run(URI source, List args) throws CompilationFailedException, IOException {
        return this.run(new GroovyCodeSource(source), args.toArray(new String[args.size()]));
    }

    public Object run(URI source, String[] args) throws CompilationFailedException, IOException {
        return this.run(new GroovyCodeSource(source), args);
    }

    public Object run(Reader in, String fileName, List list) throws CompilationFailedException {
        return this.run(in, fileName, list.toArray(new String[list.size()]));
    }

    public Object run(final Reader in, final String fileName, String[] args) throws CompilationFailedException {
        GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>(){

            @Override
            public GroovyCodeSource run() {
                return new GroovyCodeSource(in, fileName, GroovyShell.DEFAULT_CODE_BASE);
            }
        });
        Class scriptClass = this.parseClass(gcs);
        return this.runScriptOrMainOrTestOrRunnable(scriptClass, args);
    }

    public Object getVariable(String name) {
        return this.context.getVariables().get(name);
    }

    public void setVariable(String name, Object value) {
        this.context.setVariable(name, value);
    }

    public Object evaluate(GroovyCodeSource codeSource) throws CompilationFailedException {
        Script script = this.parse(codeSource);
        return script.run();
    }

    public Object evaluate(String scriptText) throws CompilationFailedException {
        return this.evaluate(scriptText, this.generateScriptName(), DEFAULT_CODE_BASE);
    }

    public Object evaluate(String scriptText, String fileName) throws CompilationFailedException {
        return this.evaluate(scriptText, fileName, DEFAULT_CODE_BASE);
    }

    public Object evaluate(final String scriptText, final String fileName, final String codeBase) throws CompilationFailedException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new GroovyCodeSourcePermission(codeBase));
        }
        GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>(){

            @Override
            public GroovyCodeSource run() {
                return new GroovyCodeSource(scriptText, fileName, codeBase);
            }
        });
        return this.evaluate(gcs);
    }

    public Object evaluate(File file) throws CompilationFailedException, IOException {
        return this.evaluate(new GroovyCodeSource(file, this.config.getSourceEncoding()));
    }

    public Object evaluate(URI uri) throws CompilationFailedException, IOException {
        return this.evaluate(new GroovyCodeSource(uri));
    }

    public Object evaluate(Reader in) throws CompilationFailedException {
        return this.evaluate(in, this.generateScriptName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object evaluate(Reader in, String fileName) throws CompilationFailedException {
        Script script = null;
        try {
            script = this.parse(in, fileName);
            Object object = script.run();
            return object;
        }
        finally {
            if (script != null) {
                InvokerHelper.removeClass(script.getClass());
            }
        }
    }

    public Script parse(Reader reader, String fileName) throws CompilationFailedException {
        return this.parse(new GroovyCodeSource(reader, fileName, DEFAULT_CODE_BASE));
    }

    private Class parseClass(GroovyCodeSource codeSource) throws CompilationFailedException {
        return this.loader.parseClass(codeSource, false);
    }

    public Script parse(GroovyCodeSource codeSource) throws CompilationFailedException {
        return InvokerHelper.createScript(this.parseClass(codeSource), this.context);
    }

    public Script parse(File file) throws CompilationFailedException, IOException {
        return this.parse(new GroovyCodeSource(file, this.config.getSourceEncoding()));
    }

    public Script parse(URI uri) throws CompilationFailedException, IOException {
        return this.parse(new GroovyCodeSource(uri));
    }

    public Script parse(String scriptText) throws CompilationFailedException {
        return this.parse(scriptText, this.generateScriptName());
    }

    public Script parse(final String scriptText, final String fileName) throws CompilationFailedException {
        GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>(){

            @Override
            public GroovyCodeSource run() {
                return new GroovyCodeSource(scriptText, fileName, GroovyShell.DEFAULT_CODE_BASE);
            }
        });
        return this.parse(gcs);
    }

    public Script parse(Reader in) throws CompilationFailedException {
        return this.parse(in, this.generateScriptName());
    }

    protected synchronized String generateScriptName() {
        return "Script" + ++this.counter + ".groovy";
    }
}

