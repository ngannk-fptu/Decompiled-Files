/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.commons.logging.AbstractLogger;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;

public class QValueFactoryLogger
extends AbstractLogger
implements QValueFactory {
    private final QValueFactory qValueFactory;

    public QValueFactoryLogger(QValueFactory qValueFactory, LogWriter writer) {
        super(writer);
        this.qValueFactory = qValueFactory;
    }

    public QValueFactory getQValueFactory() {
        return this.qValueFactory;
    }

    @Override
    public QValue create(final String value, final int type) throws RepositoryException {
        return (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.create(value, type);
            }
        }, "create(String, int)", new Object[]{value, new Integer(type)});
    }

    @Override
    public QValue create(final Calendar value) throws RepositoryException {
        return (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.create(value);
            }
        }, "create(Calendar)", new Object[]{value});
    }

    @Override
    public QValue create(final double value) throws RepositoryException {
        return (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.create(value);
            }
        }, "create(double)", new Object[]{new Double(value)});
    }

    @Override
    public QValue create(final long value) throws RepositoryException {
        return (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.create(value);
            }
        }, "create(long)", new Object[]{new Long(value)});
    }

    @Override
    public QValue create(final boolean value) throws RepositoryException {
        return (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.create(value);
            }
        }, "create(boolean)", new Object[]{value});
    }

    @Override
    public QValue create(final Name value) throws RepositoryException {
        return (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.create(value);
            }
        }, "create(Name)", new Object[]{value});
    }

    @Override
    public QValue create(final Path value) throws RepositoryException {
        return (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.create(value);
            }
        }, "create(Path)", new Object[]{value});
    }

    @Override
    public QValue create(final URI value) throws RepositoryException {
        return (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.create(value);
            }
        }, "create(URI)", new Object[]{value});
    }

    @Override
    public QValue create(final BigDecimal value) throws RepositoryException {
        return (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.create(value);
            }
        }, "create(BigDecimal)", new Object[]{value});
    }

    @Override
    public QValue create(final byte[] value) throws RepositoryException {
        return (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.create(value);
            }
        }, "create(byte[])", new Object[]{value});
    }

    @Override
    public QValue create(final InputStream value) throws RepositoryException, IOException {
        String methodName = "create(InputStream)";
        Object[] args = new Object[]{value};
        final IOException[] ex = new IOException[1];
        QValue result = (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                try {
                    return QValueFactoryLogger.this.qValueFactory.create(value);
                }
                catch (IOException e) {
                    ex[0] = e;
                    return null;
                }
            }
        }, "create(InputStream)", args);
        if (ex[0] != null) {
            throw ex[0];
        }
        return result;
    }

    @Override
    public QValue create(final File value) throws RepositoryException, IOException {
        String methodName = "create(File)";
        Object[] args = new Object[]{value};
        final IOException[] ex = new IOException[1];
        QValue result = (QValue)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                try {
                    return QValueFactoryLogger.this.qValueFactory.create(value);
                }
                catch (IOException e) {
                    ex[0] = e;
                    return null;
                }
            }
        }, "create(File)", args);
        if (ex[0] != null) {
            throw ex[0];
        }
        return result;
    }

    @Override
    public QValue[] computeAutoValues(final QPropertyDefinition propertyDefinition) throws RepositoryException {
        return (QValue[])this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return QValueFactoryLogger.this.qValueFactory.computeAutoValues(propertyDefinition);
            }
        }, "computeAutoValues(QPropertyDefinition)", new Object[]{propertyDefinition});
    }
}

