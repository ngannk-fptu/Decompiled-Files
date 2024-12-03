/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import java.io.File;
import java.io.IOException;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;

public abstract class Script
extends GroovyObjectSupport {
    private Binding binding;

    protected Script() {
        this(new Binding());
    }

    protected Script(Binding binding) {
        this.binding = binding;
    }

    public Binding getBinding() {
        return this.binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    @Override
    public Object getProperty(String property) {
        try {
            return this.binding.getVariable(property);
        }
        catch (MissingPropertyException e) {
            return super.getProperty(property);
        }
    }

    @Override
    public void setProperty(String property, Object newValue) {
        if ("binding".equals(property)) {
            this.setBinding((Binding)newValue);
        } else if ("metaClass".equals(property)) {
            this.setMetaClass((MetaClass)newValue);
        } else {
            this.binding.setVariable(property, newValue);
        }
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return super.invokeMethod(name, args);
        }
        catch (MissingMethodException mme) {
            try {
                if (name.equals(mme.getMethod())) {
                    Object boundClosure = this.getProperty(name);
                    if (boundClosure != null && boundClosure instanceof Closure) {
                        return ((Closure)boundClosure).call((Object[])args);
                    }
                    throw mme;
                }
                throw mme;
            }
            catch (MissingPropertyException mpe) {
                throw mme;
            }
        }
    }

    public abstract Object run();

    public void println() {
        Object object;
        try {
            object = this.getProperty("out");
        }
        catch (MissingPropertyException e) {
            System.out.println();
            return;
        }
        InvokerHelper.invokeMethod(object, "println", ArgumentListExpression.EMPTY_ARRAY);
    }

    public void print(Object value) {
        Object object;
        try {
            object = this.getProperty("out");
        }
        catch (MissingPropertyException e) {
            DefaultGroovyMethods.print(System.out, value);
            return;
        }
        InvokerHelper.invokeMethod(object, "print", new Object[]{value});
    }

    public void println(Object value) {
        Object object;
        try {
            object = this.getProperty("out");
        }
        catch (MissingPropertyException e) {
            DefaultGroovyMethods.println(System.out, value);
            return;
        }
        InvokerHelper.invokeMethod(object, "println", new Object[]{value});
    }

    public void printf(String format, Object value) {
        Object object;
        try {
            object = this.getProperty("out");
        }
        catch (MissingPropertyException e) {
            DefaultGroovyMethods.printf((Object)System.out, format, value);
            return;
        }
        InvokerHelper.invokeMethod(object, "printf", new Object[]{format, value});
    }

    public void printf(String format, Object[] values) {
        Object object;
        try {
            object = this.getProperty("out");
        }
        catch (MissingPropertyException e) {
            DefaultGroovyMethods.printf((Object)System.out, format, values);
            return;
        }
        InvokerHelper.invokeMethod(object, "printf", new Object[]{format, values});
    }

    public Object evaluate(String expression) throws CompilationFailedException {
        GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), this.binding);
        return shell.evaluate(expression);
    }

    public Object evaluate(File file) throws CompilationFailedException, IOException {
        GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), this.binding);
        return shell.evaluate(file);
    }

    public void run(File file, String[] arguments) throws CompilationFailedException, IOException {
        GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), this.binding);
        shell.run(file, arguments);
    }
}

