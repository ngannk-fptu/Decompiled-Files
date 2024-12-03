/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.OgnlException;

public class NoSuchPropertyException
extends OgnlException {
    private Object target;
    private Object name;

    public NoSuchPropertyException(Object target, Object name) {
        super(NoSuchPropertyException.getReason(target, name));
    }

    public NoSuchPropertyException(Object target, Object name, Throwable reason) {
        super(NoSuchPropertyException.getReason(target, name), reason);
        this.target = target;
        this.name = name;
    }

    static String getReason(Object target, Object name) {
        String ret = null;
        ret = target == null ? "null" : (target instanceof Class ? ((Class)target).getName() : target.getClass().getName());
        ret = ret + "." + name;
        return ret;
    }

    public Object getTarget() {
        return this.target;
    }

    public Object getName() {
        return this.name;
    }
}

