/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Assert
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.junit.Assert;

@Deprecated
public class GroovyAssert {
    private static final int MAX_NESTED_EXCEPTIONS = 10;

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
        Assert.assertTrue((String)("Closure " + code + " should have failed"), (boolean)failed);
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
            Assert.fail((String)("Closure " + code + " should have failed with an exception of type " + clazz.getName()));
        } else if (!clazz.isInstance(th)) {
            Assert.fail((String)("Closure " + code + " should have failed with an exception of type " + clazz.getName() + ", instead got Exception " + th));
        }
        return th;
    }

    public static Throwable shouldFailWithCause(Class clazz, Closure code) {
        Throwable th = null;
        Throwable orig = null;
        int level = 0;
        try {
            code.call();
        }
        catch (GroovyRuntimeException gre) {
            orig = ScriptBytecodeAdapter.unwrap(gre);
            th = orig.getCause();
        }
        catch (Throwable e) {
            orig = e;
            th = orig.getCause();
        }
        while (th != null && !clazz.isInstance(th) && th != th.getCause() && level < 10) {
            th = th.getCause();
            ++level;
        }
        if (orig == null) {
            Assert.fail((String)("Closure " + code + " should have failed with an exception caused by type " + clazz.getName()));
        } else if (th == null || !clazz.isInstance(th)) {
            Assert.fail((String)("Closure " + code + " should have failed with an exception caused by type " + clazz.getName() + ", instead found these Exceptions:\n" + GroovyAssert.buildExceptionList(orig)));
        }
        return th;
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

