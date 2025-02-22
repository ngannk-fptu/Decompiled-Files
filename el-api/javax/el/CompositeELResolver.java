/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.el.ELContext;
import javax.el.ELResolver;

public class CompositeELResolver
extends ELResolver {
    private static final Class<?> SCOPED_ATTRIBUTE_EL_RESOLVER;
    private int size = 0;
    private ELResolver[] resolvers = new ELResolver[8];

    public void add(ELResolver elResolver) {
        Objects.requireNonNull(elResolver);
        if (this.size >= this.resolvers.length) {
            ELResolver[] nr = new ELResolver[this.size * 2];
            System.arraycopy(this.resolvers, 0, nr, 0, this.size);
            this.resolvers = nr;
        }
        this.resolvers[this.size++] = elResolver;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; ++i) {
            Object result = this.resolvers[i].getValue(context, base, property);
            if (!context.isPropertyResolved()) continue;
            return result;
        }
        return null;
    }

    @Override
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; ++i) {
            Object obj = this.resolvers[i].invoke(context, base, method, paramTypes, params);
            if (!context.isPropertyResolved()) continue;
            return obj;
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; ++i) {
            Object value;
            Class<?> type = this.resolvers[i].getType(context, base, property);
            if (!context.isPropertyResolved()) continue;
            if (SCOPED_ATTRIBUTE_EL_RESOLVER != null && SCOPED_ATTRIBUTE_EL_RESOLVER.isAssignableFrom(this.resolvers[i].getClass()) && (value = this.resolvers[i].getValue(context, base, property)) != null) {
                return value.getClass();
            }
            return type;
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; ++i) {
            this.resolvers[i].setValue(context, base, property, value);
            if (!context.isPropertyResolved()) continue;
            return;
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; ++i) {
            boolean readOnly = this.resolvers[i].isReadOnly(context, base, property);
            if (!context.isPropertyResolved()) continue;
            return readOnly;
        }
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return new FeatureIterator(context, base, this.resolvers, this.size);
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        Class<?> commonType = null;
        int sz = this.size;
        for (int i = 0; i < sz; ++i) {
            Class<?> type = this.resolvers[i].getCommonPropertyType(context, base);
            if (type == null || commonType != null && !commonType.isAssignableFrom(type)) continue;
            commonType = type;
        }
        return commonType;
    }

    @Override
    public Object convertToType(ELContext context, Object obj, Class<?> type) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; ++i) {
            Object result = this.resolvers[i].convertToType(context, obj, type);
            if (!context.isPropertyResolved()) continue;
            return result;
        }
        return null;
    }

    static {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("javax.servlet.jsp.el.ScopedAttributeELResolver");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        SCOPED_ATTRIBUTE_EL_RESOLVER = clazz;
    }

    private static final class FeatureIterator
    implements Iterator<FeatureDescriptor> {
        private final ELContext context;
        private final Object base;
        private final ELResolver[] resolvers;
        private final int size;
        private Iterator<FeatureDescriptor> itr;
        private int idx;
        private FeatureDescriptor next;

        FeatureIterator(ELContext context, Object base, ELResolver[] resolvers, int size) {
            this.context = context;
            this.base = base;
            this.resolvers = resolvers;
            this.size = size;
            this.idx = 0;
            this.guaranteeIterator();
        }

        private void guaranteeIterator() {
            while (this.itr == null && this.idx < this.size) {
                this.itr = this.resolvers[this.idx].getFeatureDescriptors(this.context, this.base);
                ++this.idx;
            }
        }

        @Override
        public boolean hasNext() {
            if (this.next != null) {
                return true;
            }
            if (this.itr != null) {
                while (this.next == null && this.itr.hasNext()) {
                    this.next = this.itr.next();
                }
            } else {
                return false;
            }
            if (this.next == null) {
                this.itr = null;
                this.guaranteeIterator();
            }
            return this.hasNext();
        }

        @Override
        public FeatureDescriptor next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            FeatureDescriptor result = this.next;
            this.next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

