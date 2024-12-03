/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.ws.Holder
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client.sei;

import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.spi.db.PropertyAccessor;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public abstract class ValueSetter {
    private static final ValueSetter RETURN_VALUE = new ReturnValue();
    private static final ValueSetter[] POOL = new ValueSetter[16];
    static final ValueSetter SINGLE_VALUE;

    private ValueSetter() {
    }

    abstract Object put(Object var1, Object[] var2);

    static ValueSetter getSync(ParameterImpl p) {
        int idx = p.getIndex();
        if (idx == -1) {
            return RETURN_VALUE;
        }
        if (idx < POOL.length) {
            return POOL[idx];
        }
        return new Param(idx);
    }

    static {
        for (int i = 0; i < POOL.length; ++i) {
            ValueSetter.POOL[i] = new Param(i);
        }
        SINGLE_VALUE = new SingleValue();
    }

    static final class AsyncBeanValueSetter
    extends ValueSetter {
        private final PropertyAccessor accessor;

        AsyncBeanValueSetter(ParameterImpl p, Class wrapper) {
            QName name = p.getName();
            try {
                this.accessor = p.getOwner().getBindingContext().getElementPropertyAccessor(wrapper, name.getNamespaceURI(), name.getLocalPart());
            }
            catch (JAXBException e) {
                throw new WebServiceException(wrapper + " do not have a property of the name " + name, (Throwable)e);
            }
        }

        @Override
        Object put(Object obj, Object[] args) {
            assert (args != null);
            assert (args.length == 1);
            assert (args[0] != null);
            Object bean = args[0];
            try {
                this.accessor.set(bean, obj);
            }
            catch (Exception e) {
                throw new WebServiceException((Throwable)e);
            }
            return null;
        }
    }

    private static final class SingleValue
    extends ValueSetter {
        private SingleValue() {
        }

        @Override
        Object put(Object obj, Object[] args) {
            args[0] = obj;
            return null;
        }
    }

    static final class Param
    extends ValueSetter {
        private final int idx;

        public Param(int idx) {
            this.idx = idx;
        }

        @Override
        Object put(Object obj, Object[] args) {
            Object arg = args[this.idx];
            if (arg != null) {
                assert (arg instanceof Holder);
                ((Holder)arg).value = obj;
            }
            return null;
        }
    }

    private static final class ReturnValue
    extends ValueSetter {
        private ReturnValue() {
        }

        @Override
        Object put(Object obj, Object[] args) {
            return obj;
        }
    }
}

