/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELResolver
 */
package org.apache.el.stream;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.el.ELContext;
import javax.el.ELResolver;
import org.apache.el.stream.Stream;

public class StreamELResolverImpl
extends ELResolver {
    public Object getValue(ELContext context, Object base, Object property) {
        return null;
    }

    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }

    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        if ("stream".equals(method) && params.length == 0) {
            if (base.getClass().isArray()) {
                context.setPropertyResolved(true);
                return new Stream(new ArrayIterator(base));
            }
            if (base instanceof Collection) {
                context.setPropertyResolved(true);
                Collection collection = (Collection)base;
                return new Stream(collection.iterator());
            }
        }
        return null;
    }

    private static class ArrayIterator
    implements Iterator<Object> {
        private final Object base;
        private final int size;
        private int index = 0;

        ArrayIterator(Object base) {
            this.base = base;
            this.size = Array.getLength(base);
        }

        @Override
        public boolean hasNext() {
            return this.size > this.index;
        }

        @Override
        public Object next() {
            try {
                return Array.get(this.base, this.index++);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

