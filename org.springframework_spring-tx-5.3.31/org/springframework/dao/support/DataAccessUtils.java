/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.NumberUtils
 */
package org.springframework.dao.support;

import java.util.Collection;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;

public abstract class DataAccessUtils {
    @Nullable
    public static <T> T singleResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }
        return results.iterator().next();
    }

    public static <T> T requiredSingleResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
        if (CollectionUtils.isEmpty(results)) {
            throw new EmptyResultDataAccessException(1);
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }
        return results.iterator().next();
    }

    @Nullable
    public static <T> T nullableSingleResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
        if (CollectionUtils.isEmpty(results)) {
            throw new EmptyResultDataAccessException(1);
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }
        return results.iterator().next();
    }

    @Nullable
    public static <T> T uniqueResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }
        if (!CollectionUtils.hasUniqueObject(results)) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }
        return results.iterator().next();
    }

    public static <T> T requiredUniqueResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
        if (CollectionUtils.isEmpty(results)) {
            throw new EmptyResultDataAccessException(1);
        }
        if (!CollectionUtils.hasUniqueObject(results)) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }
        return results.iterator().next();
    }

    public static <T> T objectResult(@Nullable Collection<?> results, @Nullable Class<T> requiredType) throws IncorrectResultSizeDataAccessException, TypeMismatchDataAccessException {
        Object result = DataAccessUtils.requiredUniqueResult(results);
        if (requiredType != null && !requiredType.isInstance(result)) {
            if (String.class == requiredType) {
                result = result.toString();
            } else if (Number.class.isAssignableFrom(requiredType) && result instanceof Number) {
                try {
                    result = NumberUtils.convertNumberToTargetClass((Number)((Number)result), requiredType);
                }
                catch (IllegalArgumentException ex) {
                    throw new TypeMismatchDataAccessException(ex.getMessage());
                }
            } else {
                throw new TypeMismatchDataAccessException("Result object is of type [" + result.getClass().getName() + "] and could not be converted to required type [" + requiredType.getName() + "]");
            }
        }
        return (T)result;
    }

    public static int intResult(@Nullable Collection<?> results) throws IncorrectResultSizeDataAccessException, TypeMismatchDataAccessException {
        return DataAccessUtils.objectResult(results, Number.class).intValue();
    }

    public static long longResult(@Nullable Collection<?> results) throws IncorrectResultSizeDataAccessException, TypeMismatchDataAccessException {
        return DataAccessUtils.objectResult(results, Number.class).longValue();
    }

    public static RuntimeException translateIfNecessary(RuntimeException rawException, PersistenceExceptionTranslator pet) {
        Assert.notNull((Object)pet, (String)"PersistenceExceptionTranslator must not be null");
        DataAccessException dae = pet.translateExceptionIfPossible(rawException);
        return dae != null ? dae : rawException;
    }
}

