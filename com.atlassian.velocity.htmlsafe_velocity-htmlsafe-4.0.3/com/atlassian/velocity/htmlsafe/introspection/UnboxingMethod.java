/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.util.introspection.VelMethod
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.BoxingUtils;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.util.introspection.VelMethod;

final class UnboxingMethod
implements VelMethod {
    private final VelMethod delegateMethod;

    public UnboxingMethod(VelMethod delegateMethod) {
        this.delegateMethod = (VelMethod)Preconditions.checkNotNull((Object)delegateMethod, (Object)"delegateMethod must not be null");
    }

    public Object invoke(Object o, Object[] objects) throws Exception {
        Object[] unboxedArgs = BoxingUtils.unboxArrayElements(objects);
        this.unboxListArgumentElements(unboxedArgs);
        try {
            return this.delegateMethod.invoke(BoxingUtils.unboxObject(o), unboxedArgs);
        }
        catch (IllegalArgumentException e) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Failed to call ").append(BoxingUtils.unboxObject(o)).append('.').append(this.delegateMethod.getMethodName()).append(" with arguments : [").append(StringUtils.join((Object[])unboxedArgs, (String)", ")).append("]");
            throw new IllegalArgumentException(errorMessage.toString(), e);
        }
    }

    public boolean isCacheable() {
        return this.delegateMethod.isCacheable();
    }

    public String getMethodName() {
        return this.delegateMethod.getMethodName();
    }

    public Class getReturnType() {
        return this.delegateMethod.getReturnType();
    }

    private void unboxListArgumentElements(Object[] arguments) {
        for (int x = 0; x < arguments.length; ++x) {
            if (!(arguments[x] instanceof List)) continue;
            ArrayList unboxedList = new ArrayList((List)arguments[x]);
            ListIterator<Object> iterator = unboxedList.listIterator();
            while (iterator.hasNext()) {
                iterator.set(BoxingUtils.unboxObject(iterator.next()));
            }
            arguments[x] = unboxedList;
        }
    }
}

