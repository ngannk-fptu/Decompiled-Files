/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.AbstractCsvReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.BeanInterfaceProxy;
import org.supercsv.util.MethodCache;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CsvBeanReader
extends AbstractCsvReader
implements ICsvBeanReader {
    private final List<Object> processedColumns = new ArrayList<Object>();
    private final MethodCache cache = new MethodCache();

    public CsvBeanReader(Reader reader, CsvPreference preferences) {
        super(reader, preferences);
    }

    public CsvBeanReader(ITokenizer tokenizer, CsvPreference preferences) {
        super(tokenizer, preferences);
    }

    private static <T> T instantiateBean(Class<T> clazz) {
        T bean;
        if (clazz.isInterface()) {
            bean = BeanInterfaceProxy.createProxy(clazz);
        } else {
            try {
                bean = clazz.newInstance();
            }
            catch (InstantiationException e) {
                throw new SuperCsvReflectionException(String.format("error instantiating bean, check that %s has a default no-args constructor", clazz.getName()), e);
            }
            catch (IllegalAccessException e) {
                throw new SuperCsvReflectionException("error instantiating bean", e);
            }
        }
        return bean;
    }

    private static void invokeSetter(Object bean, Method setMethod, Object fieldValue) {
        try {
            setMethod.invoke(bean, fieldValue);
        }
        catch (Exception e) {
            throw new SuperCsvReflectionException(String.format("error invoking method %s()", setMethod.getName()), e);
        }
    }

    private <T> T populateBean(Class<T> clazz, String[] nameMapping) {
        T resultBean = CsvBeanReader.instantiateBean(clazz);
        for (int i = 0; i < nameMapping.length; ++i) {
            Object fieldValue = this.processedColumns.get(i);
            if (nameMapping[i] == null || fieldValue == null) continue;
            Method setMethod = this.cache.getSetMethod(resultBean, nameMapping[i], fieldValue.getClass());
            CsvBeanReader.invokeSetter(resultBean, setMethod, fieldValue);
        }
        return resultBean;
    }

    @Override
    public <T> T read(Class<T> clazz, String ... nameMapping) throws IOException {
        if (clazz == null) {
            throw new NullPointerException("clazz should not be null");
        }
        if (nameMapping == null) {
            throw new NullPointerException("nameMapping should not be null");
        }
        if (this.readRow()) {
            if (nameMapping.length != this.length()) {
                throw new IllegalArgumentException(String.format("the nameMapping array and the number of columns read should be the same size (nameMapping length = %d, columns = %d)", nameMapping.length, this.length()));
            }
            this.processedColumns.clear();
            this.processedColumns.addAll(this.getColumns());
            return this.populateBean(clazz, nameMapping);
        }
        return null;
    }

    @Override
    public <T> T read(Class<T> clazz, String[] nameMapping, CellProcessor ... processors) throws IOException {
        if (clazz == null) {
            throw new NullPointerException("clazz should not be null");
        }
        if (nameMapping == null) {
            throw new NullPointerException("nameMapping should not be null");
        }
        if (processors == null) {
            throw new NullPointerException("processors should not be null");
        }
        if (this.readRow()) {
            this.executeProcessors(this.processedColumns, processors);
            return this.populateBean(clazz, nameMapping);
        }
        return null;
    }
}

