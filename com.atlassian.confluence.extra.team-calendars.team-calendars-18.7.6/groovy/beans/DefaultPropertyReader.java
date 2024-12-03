/*
 * Decompiled with CFR 0.152.
 */
package groovy.beans;

import groovy.beans.PropertyReader;
import org.codehaus.groovy.runtime.InvokerHelper;

public class DefaultPropertyReader
implements PropertyReader {
    public static final PropertyReader INSTANCE = new DefaultPropertyReader();

    @Override
    public Object read(Object owner, String propertyName) {
        return InvokerHelper.getPropertySafe(owner, propertyName);
    }
}

