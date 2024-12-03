/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.TestCase
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.test.GroovyAssert;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class GroovyTestCase
extends TestCase {
    protected static Logger log = Logger.getLogger(GroovyTestCase.class.getName());
    private static final AtomicInteger scriptFileNameCounter = new AtomicInteger(0);
    public static final String TEST_SCRIPT_NAME_PREFIX = "TestScript";
    private boolean useAgileDoxNaming = false;

    public String getName() {
        if (this.useAgileDoxNaming) {
            return super.getName().substring(4).replaceAll("([A-Z])", " $1").toLowerCase();
        }
        return super.getName();
    }

    public String getMethodName() {
        return super.getName();
    }

    protected void assertArrayEquals(Object[] expected, Object[] value) {
        String message = "expected array: " + InvokerHelper.toString(expected) + " value array: " + InvokerHelper.toString(value);
        GroovyTestCase.assertNotNull((String)(message + ": expected should not be null"), (Object)expected);
        GroovyTestCase.assertNotNull((String)(message + ": value should not be null"), (Object)value);
        GroovyTestCase.assertEquals((String)message, (int)expected.length, (int)value.length);
        int size = expected.length;
        for (int i = 0; i < size; ++i) {
            GroovyTestCase.assertEquals("value[" + i + "] when " + message, expected[i], value[i]);
        }
    }

    protected void assertLength(int length, char[] array) {
        GroovyTestCase.assertEquals((int)length, (int)array.length);
    }

    protected void assertLength(int length, int[] array) {
        GroovyTestCase.assertEquals((int)length, (int)array.length);
    }

    protected void assertLength(int length, Object[] array) {
        GroovyTestCase.assertEquals((int)length, (int)array.length);
    }

    protected void assertContains(char expected, char[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != expected) continue;
            return;
        }
        StringBuilder message = new StringBuilder();
        message.append(expected).append(" not in {");
        for (int i = 0; i < array.length; ++i) {
            message.append("'").append(array[i]).append("'");
            if (i >= array.length - 1) continue;
            message.append(", ");
        }
        message.append(" }");
        GroovyTestCase.fail((String)message.toString());
    }

    protected void assertContains(int expected, int[] array) {
        for (int anInt : array) {
            if (anInt != expected) continue;
            return;
        }
        StringBuilder message = new StringBuilder();
        message.append(expected).append(" not in {");
        for (int i = 0; i < array.length; ++i) {
            message.append("'").append(array[i]).append("'");
            if (i >= array.length - 1) continue;
            message.append(", ");
        }
        message.append(" }");
        GroovyTestCase.fail((String)message.toString());
    }

    protected void assertToString(Object value, String expected) {
        Object console = InvokerHelper.invokeMethod(value, "toString", null);
        GroovyTestCase.assertEquals("toString() on value: " + value, expected, console);
    }

    protected void assertInspect(Object value, String expected) {
        Object console = InvokerHelper.invokeMethod(value, "inspect", null);
        GroovyTestCase.assertEquals("inspect() on value: " + value, expected, console);
    }

    protected void assertScript(String script) throws Exception {
        GroovyAssert.assertScript(script);
    }

    protected String getTestClassName() {
        return TEST_SCRIPT_NAME_PREFIX + this.getMethodName() + scriptFileNameCounter.getAndIncrement() + ".groovy";
    }

    protected String shouldFail(Closure code) {
        return GroovyAssert.shouldFail(code).getMessage();
    }

    protected String shouldFail(Class clazz, Closure code) {
        return GroovyAssert.shouldFail(clazz, code).getMessage();
    }

    protected String shouldFailWithCause(Class clazz, Closure code) {
        return GroovyAssert.shouldFailWithCause(clazz, code).getMessage();
    }

    protected String shouldFail(Class clazz, String script) {
        return GroovyAssert.shouldFail(clazz, script).getMessage();
    }

    protected String shouldFail(String script) {
        return GroovyAssert.shouldFail(script).getMessage();
    }

    protected String fixEOLs(String value) {
        return value.replaceAll("(\\r\\n?)|\n", "\n");
    }

    public static boolean notYetImplemented(Object caller) {
        return GroovyAssert.notYetImplemented(caller);
    }

    public boolean notYetImplemented() {
        return GroovyTestCase.notYetImplemented((Object)this);
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && DefaultTypeTransformation.compareEqual(expected, actual)) {
            return;
        }
        TestCase.assertEquals((String)message, (Object)expected, (Object)actual);
    }

    public static void assertEquals(Object expected, Object actual) {
        GroovyTestCase.assertEquals(null, expected, actual);
    }

    public static void assertEquals(String expected, String actual) {
        GroovyTestCase.assertEquals(null, (String)expected, (String)actual);
    }
}

