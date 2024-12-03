/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilationFailedException;

public class Eval {
    public static Object me(String expression) throws CompilationFailedException {
        return Eval.me(null, null, expression);
    }

    public static Object me(String symbol, Object object, String expression) throws CompilationFailedException {
        Binding b = new Binding();
        b.setVariable(symbol, object);
        GroovyShell sh = new GroovyShell(b);
        return sh.evaluate(expression);
    }

    public static Object x(Object x, String expression) throws CompilationFailedException {
        return Eval.me("x", x, expression);
    }

    public static Object xy(Object x, Object y, String expression) throws CompilationFailedException {
        Binding b = new Binding();
        b.setVariable("x", x);
        b.setVariable("y", y);
        GroovyShell sh = new GroovyShell(b);
        return sh.evaluate(expression);
    }

    public static Object xyz(Object x, Object y, Object z, String expression) throws CompilationFailedException {
        Binding b = new Binding();
        b.setVariable("x", x);
        b.setVariable("y", y);
        b.setVariable("z", z);
        GroovyShell sh = new GroovyShell(b);
        return sh.evaluate(expression);
    }
}

