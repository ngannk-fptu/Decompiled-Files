/*
 * Decompiled with CFR 0.152.
 */
package groovy.beans;

import groovy.beans.PropertyWriter;
import org.codehaus.groovy.runtime.InvokerHelper;

public class DefaultPropertyWriter
implements PropertyWriter {
    public static final PropertyWriter INSTANCE = new DefaultPropertyWriter();

    @Override
    public void write(Object owner, String propertyName, Object value) {
        InvokerHelper.setProperty(owner, propertyName, value);
    }
}

