/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;
import java.util.Iterator;
import org.codehaus.groovy.runtime.InvokerHelper;

public class Proxy
extends GroovyObjectSupport {
    private Object adaptee = null;

    public Proxy wrap(Object adaptee) {
        this.setAdaptee(adaptee);
        return this;
    }

    public Object getAdaptee() {
        return this.adaptee;
    }

    public void setAdaptee(Object adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return super.invokeMethod(name, args);
        }
        catch (MissingMethodException e) {
            return InvokerHelper.invokeMethod(this.adaptee, name, args);
        }
    }

    public Iterator iterator() {
        return InvokerHelper.asIterator(this.adaptee);
    }
}

