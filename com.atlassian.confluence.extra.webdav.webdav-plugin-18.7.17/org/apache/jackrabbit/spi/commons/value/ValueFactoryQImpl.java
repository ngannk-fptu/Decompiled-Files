/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.value;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.Binary;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.value.QValueValue;
import org.apache.jackrabbit.util.ISO8601;

public class ValueFactoryQImpl
implements ValueFactory {
    private final QValueFactory qfactory;
    private final NamePathResolver resolver;

    public ValueFactoryQImpl(QValueFactory qfactory, NamePathResolver resolver) {
        this.qfactory = qfactory;
        this.resolver = resolver;
    }

    public QValueFactory getQValueFactory() {
        return this.qfactory;
    }

    public Value createValue(QValue qvalue) {
        return new QValueValue(qvalue, this.resolver);
    }

    @Override
    public Value createValue(String value) {
        try {
            QValue qvalue = this.qfactory.create(value, 1);
            return new QValueValue(qvalue, this.resolver);
        }
        catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Value createValue(long value) {
        try {
            QValue qvalue = this.qfactory.create(value);
            return new QValueValue(qvalue, this.resolver);
        }
        catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Value createValue(double value) {
        try {
            QValue qvalue = this.qfactory.create(value);
            return new QValueValue(qvalue, this.resolver);
        }
        catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Value createValue(boolean value) {
        try {
            QValue qvalue = this.qfactory.create(value);
            return new QValueValue(qvalue, this.resolver);
        }
        catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Value createValue(Calendar value) {
        try {
            ISO8601.getYear(value);
            QValue qvalue = this.qfactory.create(value);
            return new QValueValue(qvalue, this.resolver);
        }
        catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Value createValue(InputStream value) {
        QValueValue qValueValue;
        try {
            QValue qvalue = this.qfactory.create(value);
            qValueValue = new QValueValue(qvalue, this.resolver);
        }
        catch (Throwable throwable) {
            try {
                value.close();
                throw throwable;
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            catch (RepositoryException ex) {
                throw new RuntimeException(ex);
            }
        }
        value.close();
        return qValueValue;
    }

    @Override
    public Value createValue(Node value) throws RepositoryException {
        return this.createValue(value, false);
    }

    @Override
    public Value createValue(String value, int type) throws ValueFormatException {
        try {
            QValue qvalue;
            if (type == 7) {
                Name name = this.resolver.getQName(value);
                qvalue = this.qfactory.create(name);
            } else if (type == 8) {
                Path path = this.resolver.getQPath(value, false);
                qvalue = this.qfactory.create(path);
            } else {
                qvalue = this.qfactory.create(value, type);
            }
            return new QValueValue(qvalue, this.resolver);
        }
        catch (IllegalNameException ex) {
            throw new ValueFormatException(ex);
        }
        catch (MalformedPathException ex) {
            throw new ValueFormatException(ex);
        }
        catch (NamespaceException ex) {
            throw new ValueFormatException(ex);
        }
        catch (ValueFormatException ex) {
            throw ex;
        }
        catch (RepositoryException ex) {
            throw new ValueFormatException(ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Binary createBinary(InputStream stream) throws RepositoryException {
        Binary binary;
        try {
            QValue qvalue = this.qfactory.create(stream);
            binary = qvalue.getBinary();
        }
        catch (Throwable throwable) {
            try {
                stream.close();
                throw throwable;
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            catch (RepositoryException ex) {
                throw new RuntimeException(ex);
            }
        }
        stream.close();
        return binary;
    }

    @Override
    public Value createValue(Binary value) {
        try {
            return this.createValue(value.getStream());
        }
        catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Value createValue(BigDecimal value) {
        try {
            QValue qvalue = this.qfactory.create(value);
            return new QValueValue(qvalue, this.resolver);
        }
        catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Value createValue(Node value, boolean weak) throws RepositoryException {
        QValue qvalue = this.qfactory.create(value.getUUID(), weak ? 10 : 9);
        return new QValueValue(qvalue, this.resolver);
    }
}

