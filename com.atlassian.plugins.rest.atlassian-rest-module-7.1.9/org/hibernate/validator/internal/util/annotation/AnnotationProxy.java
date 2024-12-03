/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationAttributes;

class AnnotationProxy
implements Annotation,
InvocationHandler,
Serializable {
    private static final long serialVersionUID = 6907601010599429454L;
    private final AnnotationDescriptor<? extends Annotation> descriptor;

    AnnotationProxy(AnnotationDescriptor<? extends Annotation> descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object value = this.descriptor.getAttribute(method.getName());
        if (value != null) {
            return value;
        }
        return method.invoke((Object)this, args);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return this.descriptor.getType();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!this.descriptor.getType().isInstance(obj)) {
            return false;
        }
        Annotation other = this.descriptor.getType().cast(obj);
        Map<String, Object> otherAttributes = this.getAnnotationAttributes(other);
        if (this.descriptor.getAttributes().size() != otherAttributes.size()) {
            return false;
        }
        for (Map.Entry<String, Object> member : this.descriptor.getAttributes().entrySet()) {
            Object otherValue;
            Object value = member.getValue();
            if (this.areEqual(value, otherValue = otherAttributes.get(member.getKey()))) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.descriptor.hashCode();
    }

    @Override
    public String toString() {
        return this.descriptor.toString();
    }

    private boolean areEqual(Object o1, Object o2) {
        return !o1.getClass().isArray() ? o1.equals(o2) : (o1.getClass() == boolean[].class ? Arrays.equals((boolean[])o1, (boolean[])o2) : (o1.getClass() == byte[].class ? Arrays.equals((byte[])o1, (byte[])o2) : (o1.getClass() == char[].class ? Arrays.equals((char[])o1, (char[])o2) : (o1.getClass() == double[].class ? Arrays.equals((double[])o1, (double[])o2) : (o1.getClass() == float[].class ? Arrays.equals((float[])o1, (float[])o2) : (o1.getClass() == int[].class ? Arrays.equals((int[])o1, (int[])o2) : (o1.getClass() == long[].class ? Arrays.equals((long[])o1, (long[])o2) : (o1.getClass() == short[].class ? Arrays.equals((short[])o1, (short[])o2) : Arrays.equals((Object[])o1, (Object[])o2)))))))));
    }

    private Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        InvocationHandler invocationHandler;
        if (Proxy.isProxyClass(annotation.getClass()) && System.getSecurityManager() == null && (invocationHandler = Proxy.getInvocationHandler(annotation)) instanceof AnnotationProxy) {
            return ((AnnotationProxy)invocationHandler).descriptor.getAttributes();
        }
        return this.run(GetAnnotationAttributes.action(annotation));
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

