/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.spring.container.LazyComponentReference
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.compat.api.service.accessmode.impl;

import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.compat.api.service.accessmode.AccessModeCompatService;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;

@Component
public class DefaultAccessModeCompatService
implements AccessModeCompatService {
    private Supplier accessModeServiceSupplier = new LazyComponentReference("accessModeService");

    @Override
    public boolean isReadOnlyAccessModeEnabled() {
        try {
            Class<?> accessModeServiceClass = Class.forName("com.atlassian.confluence.api.service.accessmode.AccessModeService");
            Method isReadOnlyAccessModeEnabledMethod = accessModeServiceClass.getDeclaredMethod("isReadOnlyAccessModeEnabled", new Class[0]);
            return (Boolean)isReadOnlyAccessModeEnabledMethod.invoke(this.getAccessModeService(), new Object[0]);
        }
        catch (Exception exception) {
            return false;
        }
    }

    @Override
    public <T> T withReadOnlyAccessExemption(Callable<T> callable) throws ServiceException {
        try {
            Class<?> accessModeServiceClass = Class.forName("com.atlassian.confluence.api.service.accessmode.AccessModeService");
            Method withReadOnlyAccessExemptionMethod = accessModeServiceClass.getDeclaredMethod("withReadOnlyAccessExemption", Callable.class);
            return (T)withReadOnlyAccessExemptionMethod.invoke(this.getAccessModeService(), callable);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ServiceException("Cannot run the callable", (Throwable)e);
        }
    }

    private Object getAccessModeService() {
        return this.accessModeServiceSupplier.get();
    }
}

