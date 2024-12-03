/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ozymandias;

import com.atlassian.ozymandias.error.ErrorUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionSafeInvocationHandler
implements InvocationHandler {
    @Nonnull
    private final Object proxiedObject;
    private final ReturnValueSupplier returnValueSupplier;

    public ExceptionSafeInvocationHandler(@Nonnull Object proxiedObject, @Nullable ReturnValueSupplier returnValueSupplier) {
        this.proxiedObject = Objects.requireNonNull(proxiedObject);
        this.returnValueSupplier = returnValueSupplier;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(this.proxiedObject, args);
        }
        catch (LinkageError e) {
            ErrorUtils.handleThrowable(e, e.getMessage(), this.getLogger());
            return this.getFailureValue(method, args);
        }
        catch (Exception e) {
            String message = (String)StringUtils.defaultIfEmpty((CharSequence)e.getMessage(), (CharSequence)("Exception in " + method));
            ErrorUtils.handleThrowable(e, message, this.getLogger());
            return this.getFailureValue(method, args);
        }
    }

    @Nullable
    private Object getFailureValue(@Nonnull Method method, @Nonnull Object[] args) {
        return this.returnValueSupplier == null ? null : this.returnValueSupplier.get(method, args);
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(this.proxiedObject.getClass());
    }

    public static interface ReturnValueSupplier {
        @Nullable
        public Object get(@Nonnull Method var1, Object ... var2);
    }
}

