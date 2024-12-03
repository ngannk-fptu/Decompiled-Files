/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.value;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.UUID;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;
import org.apache.jackrabbit.spi.commons.value.DefaultQValue;
import org.apache.jackrabbit.util.ISO8601;

public abstract class AbstractQValueFactory
implements QValueFactory {
    public static final String DEFAULT_ENCODING = "UTF-8";
    protected static final PathFactory PATH_FACTORY = PathFactoryImpl.getInstance();
    protected static final NameFactory NAME_FACTORY = NameFactoryImpl.getInstance();

    @Override
    public QValue[] computeAutoValues(QPropertyDefinition propertyDefinition) throws RepositoryException {
        Name declaringNT = propertyDefinition.getDeclaringNodeType();
        Name name = propertyDefinition.getName();
        if (NameConstants.JCR_UUID.equals(name) && NameConstants.MIX_REFERENCEABLE.equals(declaringNT)) {
            return new QValue[]{this.create(UUID.randomUUID().toString(), 1)};
        }
        throw new RepositoryException("createFromDefinition not implemented for: " + name);
    }

    @Override
    public QValue create(String value, int type) throws RepositoryException {
        if (value == null) {
            throw new IllegalArgumentException("Cannot create QValue from null value.");
        }
        try {
            switch (type) {
                case 6: {
                    return this.create(Boolean.valueOf(value));
                }
                case 5: {
                    Calendar cal = ISO8601.parse(value);
                    if (cal == null) {
                        throw new ValueFormatException("not a valid date: " + value);
                    }
                    return this.create(cal);
                }
                case 4: {
                    return this.create(Double.valueOf(value));
                }
                case 3: {
                    return this.create(Long.valueOf(value));
                }
                case 12: {
                    return this.create(new BigDecimal(value));
                }
                case 11: {
                    return this.create(URI.create(value));
                }
                case 8: {
                    return this.create(PATH_FACTORY.create(value));
                }
                case 7: {
                    return this.create(NAME_FACTORY.create(value));
                }
                case 1: {
                    return this.createString(value);
                }
                case 9: {
                    return this.createReference(value, false);
                }
                case 10: {
                    return this.createReference(value, true);
                }
                case 2: {
                    return this.create(value.getBytes(DEFAULT_ENCODING));
                }
            }
        }
        catch (IllegalArgumentException ex) {
            throw new ValueFormatException(ex);
        }
        catch (UnsupportedEncodingException ex) {
            throw new RepositoryException(ex);
        }
        throw new IllegalArgumentException("illegal type " + type);
    }

    @Override
    public QValue create(Calendar value) throws RepositoryException {
        if (value == null) {
            throw new IllegalArgumentException("Cannot create QValue from null value.");
        }
        return new DefaultQValue(value);
    }

    @Override
    public QValue create(double value) throws RepositoryException {
        return new DefaultQValue(value);
    }

    @Override
    public QValue create(long value) throws RepositoryException {
        return new DefaultQValue(value);
    }

    @Override
    public QValue create(boolean value) throws RepositoryException {
        if (value) {
            return DefaultQValue.TRUE;
        }
        return DefaultQValue.FALSE;
    }

    @Override
    public QValue create(Name value) throws RepositoryException {
        if (value == null) {
            throw new IllegalArgumentException("Cannot create QValue from null value.");
        }
        return new DefaultQValue(value);
    }

    @Override
    public QValue create(Path value) throws RepositoryException {
        if (value == null) {
            throw new IllegalArgumentException("Cannot create QValue from null value.");
        }
        return new DefaultQValue(value);
    }

    @Override
    public QValue create(URI value) throws RepositoryException {
        if (value == null) {
            throw new IllegalArgumentException("Cannot create QValue from null value.");
        }
        return new DefaultQValue(value);
    }

    @Override
    public QValue create(BigDecimal value) throws RepositoryException {
        if (value == null) {
            throw new IllegalArgumentException("Cannot create QValue from null value.");
        }
        return new DefaultQValue(value);
    }

    protected QValue createString(String value) {
        return new DefaultQValue(value, 1);
    }

    protected QValue createReference(String ref, boolean weak) {
        if (weak) {
            return new DefaultQValue(ref, 10);
        }
        return new DefaultQValue(ref, 9);
    }
}

