/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.AccessorException
 *  com.sun.xml.bind.api.RawAccessor
 */
package com.sun.xml.ws.db.glassfish;

import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.api.RawAccessor;
import com.sun.xml.ws.spi.db.DatabindingException;
import com.sun.xml.ws.spi.db.PropertyAccessor;

public class RawAccessorWrapper
implements PropertyAccessor {
    private RawAccessor accessor;

    public RawAccessorWrapper(RawAccessor a) {
        this.accessor = a;
    }

    public boolean equals(Object obj) {
        return this.accessor.equals(obj);
    }

    public Object get(Object bean) throws DatabindingException {
        try {
            return this.accessor.get(bean);
        }
        catch (AccessorException e) {
            throw new DatabindingException(e);
        }
    }

    public int hashCode() {
        return this.accessor.hashCode();
    }

    public void set(Object bean, Object value) throws DatabindingException {
        try {
            this.accessor.set(bean, value);
        }
        catch (AccessorException e) {
            throw new DatabindingException(e);
        }
    }

    public String toString() {
        return this.accessor.toString();
    }
}

