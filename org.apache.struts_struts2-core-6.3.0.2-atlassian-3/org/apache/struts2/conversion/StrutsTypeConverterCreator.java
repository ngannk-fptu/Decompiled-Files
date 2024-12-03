/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.conversion;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.inject.Inject;

public class StrutsTypeConverterCreator
implements TypeConverterCreator {
    private ObjectFactory objectFactory;

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public TypeConverter createTypeConverter(String className) throws Exception {
        Class clazz = this.objectFactory.getClassInstance(className);
        return this.createTypeConverter(clazz);
    }

    @Override
    public TypeConverter createTypeConverter(Class<?> clazz) throws Exception {
        if (TypeConverter.class.isAssignableFrom(clazz)) {
            Class<?> converterClass = clazz;
            return this.objectFactory.buildConverter(converterClass, null);
        }
        throw new IllegalArgumentException("Type converter class " + clazz.getName() + " doesn't implement " + TypeConverter.class.getName());
    }
}

