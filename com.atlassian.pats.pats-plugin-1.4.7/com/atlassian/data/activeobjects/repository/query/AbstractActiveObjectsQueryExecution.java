/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.support.ConfigurableConversionService
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQuery;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethod;
import java.util.Collection;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class AbstractActiveObjectsQueryExecution {
    private static final ConversionService CONVERSION_SERVICE;

    @Nullable
    public Object execute(AbstractActiveObjectsQuery query, Object[] values) {
        Assert.notNull((Object)query, (String)"AbstractActiveObjectsQuery must not be null!");
        Assert.notNull((Object)values, (String)"Values must not be null!");
        Object result = this.doExecute(query, values);
        if (result == null) {
            return null;
        }
        ActiveObjectsQueryMethod queryMethod = query.getQueryMethod();
        Class<?> requiredType = queryMethod.getReturnType();
        if (Void.TYPE.equals(requiredType) || requiredType.isAssignableFrom(result.getClass())) {
            return result;
        }
        return CONVERSION_SERVICE.canConvert(result.getClass(), requiredType) ? CONVERSION_SERVICE.convert(result, requiredType) : result;
    }

    @Nullable
    protected abstract Object doExecute(AbstractActiveObjectsQuery var1, Object[] var2);

    public static void potentiallyRemoveOptionalConverter(ConfigurableConversionService conversionService) {
        ClassLoader classLoader = AbstractActiveObjectsQueryExecution.class.getClassLoader();
        if (ClassUtils.isPresent((String)"java.util.Optional", (ClassLoader)classLoader)) {
            try {
                Class optionalType = ClassUtils.forName((String)"java.util.Optional", (ClassLoader)classLoader);
                conversionService.removeConvertible(Object.class, optionalType);
            }
            catch (ClassNotFoundException | LinkageError throwable) {
                // empty catch block
            }
        }
    }

    static {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.removeConvertible(Collection.class, Object.class);
        AbstractActiveObjectsQueryExecution.potentiallyRemoveOptionalConverter((ConfigurableConversionService)conversionService);
        CONVERSION_SERVICE = conversionService;
    }
}

