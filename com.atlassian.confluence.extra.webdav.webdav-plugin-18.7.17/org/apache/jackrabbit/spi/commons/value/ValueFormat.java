/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.value;

import java.io.IOException;
import java.util.ArrayList;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.value.QValueValue;
import org.apache.jackrabbit.spi.commons.value.ValueFactoryQImpl;

public class ValueFormat {
    public static QValue getQValue(Value jcrValue, NamePathResolver resolver, QValueFactory factory) throws RepositoryException {
        if (jcrValue == null) {
            throw new IllegalArgumentException("null value");
        }
        if (jcrValue instanceof QValueValue) {
            return ((QValueValue)jcrValue).getQValue();
        }
        if (jcrValue.getType() == 2) {
            try {
                return factory.create(jcrValue.getStream());
            }
            catch (IOException e) {
                throw new RepositoryException(e);
            }
        }
        if (jcrValue.getType() == 5) {
            return factory.create(jcrValue.getDate());
        }
        if (jcrValue.getType() == 4) {
            return factory.create(jcrValue.getDouble());
        }
        if (jcrValue.getType() == 3) {
            return factory.create(jcrValue.getLong());
        }
        if (jcrValue.getType() == 12) {
            return factory.create(jcrValue.getDecimal());
        }
        return ValueFormat.getQValue(jcrValue.getString(), jcrValue.getType(), resolver, factory);
    }

    public static QValue[] getQValues(Value[] jcrValues, NamePathResolver resolver, QValueFactory factory) throws RepositoryException {
        if (jcrValues == null) {
            throw new IllegalArgumentException("null value");
        }
        ArrayList<QValue> qValues = new ArrayList<QValue>();
        for (Value jcrValue : jcrValues) {
            if (jcrValue == null) continue;
            qValues.add(ValueFormat.getQValue(jcrValue, resolver, factory));
        }
        return qValues.toArray(new QValue[qValues.size()]);
    }

    public static QValue getQValue(String jcrValue, int propertyType, NamePathResolver resolver, QValueFactory factory) throws RepositoryException {
        QValue qValue;
        switch (propertyType) {
            case 1: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 9: 
            case 10: 
            case 11: 
            case 12: {
                qValue = factory.create(jcrValue, propertyType);
                break;
            }
            case 2: {
                qValue = factory.create(jcrValue.getBytes());
                break;
            }
            case 7: {
                Name qName = resolver.getQName(jcrValue);
                qValue = factory.create(qName);
                break;
            }
            case 8: {
                Path qPath = resolver.getQPath(jcrValue, false);
                qValue = factory.create(qPath);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid property type.");
            }
        }
        return qValue;
    }

    public static Value getJCRValue(QValue value, NamePathResolver resolver, ValueFactory factory) throws RepositoryException {
        Value jcrValue;
        if (factory instanceof ValueFactoryQImpl) {
            return ((ValueFactoryQImpl)factory).createValue(value);
        }
        int propertyType = value.getType();
        switch (propertyType) {
            case 1: 
            case 9: 
            case 10: 
            case 11: {
                jcrValue = factory.createValue(value.getString(), propertyType);
                break;
            }
            case 8: {
                Path qPath = value.getPath();
                jcrValue = factory.createValue(resolver.getJCRPath(qPath), propertyType);
                break;
            }
            case 7: {
                Name qName = value.getName();
                jcrValue = factory.createValue(resolver.getJCRName(qName), propertyType);
                break;
            }
            case 6: {
                jcrValue = factory.createValue(value.getBoolean());
                break;
            }
            case 2: {
                jcrValue = factory.createValue(value.getBinary());
                break;
            }
            case 5: {
                jcrValue = factory.createValue(value.getCalendar());
                break;
            }
            case 4: {
                jcrValue = factory.createValue(value.getDouble());
                break;
            }
            case 3: {
                jcrValue = factory.createValue(value.getLong());
                break;
            }
            case 12: {
                jcrValue = factory.createValue(value.getDecimal());
                break;
            }
            default: {
                throw new RepositoryException("illegal internal value type");
            }
        }
        return jcrValue;
    }

    public static String getJCRString(QValue value, NamePathResolver resolver) throws RepositoryException {
        String jcrString;
        int propertyType = value.getType();
        switch (propertyType) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 9: 
            case 10: 
            case 11: 
            case 12: {
                jcrString = value.getString();
                break;
            }
            case 8: {
                Path qPath = value.getPath();
                jcrString = resolver.getJCRPath(qPath);
                break;
            }
            case 7: {
                Name qName = value.getName();
                jcrString = resolver.getJCRName(qName);
                break;
            }
            default: {
                throw new RepositoryException("illegal internal value type");
            }
        }
        return jcrString;
    }
}

