/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Assert
 *  org.junit.Test
 */
package groovy.test;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.junit.Assert;
import org.junit.Test;

public class GroovyAssert
extends Assert {
    private static final Logger log = Logger.getLogger(GroovyAssert.class.getName());
    private static final int MAX_NESTED_EXCEPTIONS = 10;
    private static final AtomicInteger counter = new AtomicInteger(0);
    public static final String TEST_SCRIPT_NAME_PREFIX = "TestScript";
    private static final ThreadLocal<Boolean> notYetImplementedFlag = new ThreadLocal();

    protected static String genericScriptName() {
        return TEST_SCRIPT_NAME_PREFIX + counter.getAndIncrement() + ".groovy";
    }

    public static void assertScript(String script) throws Exception {
        GroovyShell shell = new GroovyShell();
        shell.evaluate(script, GroovyAssert.genericScriptName());
    }

    public static Throwable shouldFail(Closure code) {
        boolean failed = false;
        Throwable th = null;
        try {
            code.call();
        }
        catch (GroovyRuntimeException gre) {
            failed = true;
            th = ScriptBytecodeAdapter.unwrap(gre);
        }
        catch (Throwable e) {
            failed = true;
            th = e;
        }
        GroovyAssert.assertTrue((String)("Closure " + code + " should have failed"), (boolean)failed);
        return th;
    }

    public static Throwable shouldFail(Class clazz, Closure code) {
        Throwable th = null;
        try {
            code.call();
        }
        catch (GroovyRuntimeException gre) {
            th = ScriptBytecodeAdapter.unwrap(gre);
        }
        catch (Throwable e) {
            th = e;
        }
        if (th == null) {
            GroovyAssert.fail((String)("Closure " + code + " should have failed with an exception of type " + clazz.getName()));
        } else if (!clazz.isInstance(th)) {
            GroovyAssert.fail((String)("Closure " + code + " should have failed with an exception of type " + clazz.getName() + ", instead got Exception " + th));
        }
        return th;
    }

    public static Throwable shouldFailWithCause(Class expectedCause, Closure code) {
        if (expectedCause == null) {
            GroovyAssert.fail((String)"The expectedCause class cannot be null");
        }
        Throwable cause = null;
        Throwable orig = null;
        int level = 0;
        try {
            code.call();
        }
        catch (GroovyRuntimeException gre) {
            orig = ScriptBytecodeAdapter.unwrap(gre);
            cause = orig.getCause();
        }
        catch (Throwable e) {
            orig = e;
            cause = orig.getCause();
        }
        if (orig != null && cause == null) {
            GroovyAssert.fail((String)("Closure " + code + " was expected to fail due to a nested cause of type " + expectedCause.getName() + " but instead got a direct exception of type " + orig.getClass().getName() + " with no nested cause(s). Code under test has a bug or perhaps you meant shouldFail?"));
        }
        while (cause != null && !expectedCause.isInstance(cause) && cause != cause.getCause() && level < 10) {
            cause = cause.getCause();
            ++level;
        }
        if (orig == null) {
            GroovyAssert.fail((String)("Closure " + code + " should have failed with an exception having a nested cause of type " + expectedCause.getName()));
        } else if (cause == null || !expectedCause.isInstance(cause)) {
            GroovyAssert.fail((String)("Closure " + code + " should have failed with an exception having a nested cause of type " + expectedCause.getName() + ", instead found these Exceptions:\n" + GroovyAssert.buildExceptionList(orig)));
        }
        return cause;
    }

    public static Throwable shouldFail(Class clazz, String script) {
        Throwable th = null;
        try {
            GroovyShell shell = new GroovyShell();
            shell.evaluate(script, GroovyAssert.genericScriptName());
        }
        catch (GroovyRuntimeException gre) {
            th = ScriptBytecodeAdapter.unwrap(gre);
        }
        catch (Throwable e) {
            th = e;
        }
        if (th == null) {
            GroovyAssert.fail((String)("Script should have failed with an exception of type " + clazz.getName()));
        } else if (!clazz.isInstance(th)) {
            GroovyAssert.fail((String)("Script should have failed with an exception of type " + clazz.getName() + ", instead got Exception " + th));
        }
        return th;
    }

    public static Throwable shouldFail(String script) {
        boolean failed = false;
        Throwable th = null;
        try {
            GroovyShell shell = new GroovyShell();
            shell.evaluate(script, GroovyAssert.genericScriptName());
        }
        catch (GroovyRuntimeException gre) {
            failed = true;
            th = ScriptBytecodeAdapter.unwrap(gre);
        }
        catch (Throwable e) {
            failed = true;
            th = e;
        }
        GroovyAssert.assertTrue((String)"Script should have failed", (boolean)failed);
        return th;
    }

    private static Method findRunningJUnitTestMethod(Class caller) {
        Class[] args = new Class[]{};
        Exception t = new Exception();
        for (int i = t.getStackTrace().length - 1; i >= 0; --i) {
            StackTraceElement element = t.getStackTrace()[i];
            if (!element.getClassName().equals(caller.getName())) continue;
            try {
                Method m = caller.getMethod(element.getMethodName(), args);
                if (!GroovyAssert.isPublicTestMethod(m)) continue;
                return m;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        throw new RuntimeException("No JUnit test case method found in call stack");
    }

    private static boolean isPublicTestMethod(Method method) {
        String name = method.getName();
        Class<?>[] parameters = method.getParameterTypes();
        Class<?> returnType = method.getReturnType();
        return parameters.length == 0 && (name.startsWith("test") || method.getAnnotation(Test.class) != null) && returnType.equals(Void.TYPE) && Modifier.isPublic(method.getModifiers());
    }

    public static boolean notYetImplemented(Object caller) {
        if (notYetImplementedFlag.get() != null) {
            return false;
        }
        notYetImplementedFlag.set(Boolean.TRUE);
        Method testMethod = GroovyAssert.findRunningJUnitTestMethod(caller.getClass());
        try {
            log.info("Running " + testMethod.getName() + " as not yet implemented");
            testMethod.invoke(caller, (Object[])new Class[0]);
            GroovyAssert.fail((String)(testMethod.getName() + " is marked as not yet implemented but passes unexpectedly"));
        }
        catch (Exception e) {
            log.info(testMethod.getName() + " fails which is expected as it is not yet implemented");
        }
        finally {
            notYetImplementedFlag.set(null);
        }
        return true;
    }

    private static String buildExceptionList(Throwable th) {
        StringBuilder sb = new StringBuilder();
        int level = 0;
        while (th != null) {
            if (level > 1) {
                for (int i = 0; i < level - 1; ++i) {
                    sb.append("   ");
                }
            }
            if (level > 0) {
                sb.append("-> ");
            }
            if (level > 10) {
                sb.append("...");
                break;
            }
            sb.append(th.getClass().getName()).append(": ").append(th.getMessage()).append("\n");
            if (th == th.getCause()) break;
            th = th.getCause();
            ++level;
        }
        return sb.toString();
    }
}

