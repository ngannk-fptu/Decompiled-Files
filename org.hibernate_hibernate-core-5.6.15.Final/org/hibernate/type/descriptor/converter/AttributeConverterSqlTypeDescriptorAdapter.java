/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  org.jboss.logging.Logger
 */
package org.hibernate.type.descriptor.converter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.persistence.PersistenceException;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.jboss.logging.Logger;

public class AttributeConverterSqlTypeDescriptorAdapter
implements SqlTypeDescriptor {
    private static final Logger log = Logger.getLogger(AttributeConverterSqlTypeDescriptorAdapter.class);
    private final JpaAttributeConverter converter;
    private final SqlTypeDescriptor delegate;
    private final JavaTypeDescriptor intermediateJavaTypeDescriptor;

    public AttributeConverterSqlTypeDescriptorAdapter(JpaAttributeConverter converter, SqlTypeDescriptor delegate, JavaTypeDescriptor intermediateJavaTypeDescriptor) {
        this.converter = converter;
        this.delegate = delegate;
        this.intermediateJavaTypeDescriptor = intermediateJavaTypeDescriptor;
    }

    @Override
    public int getSqlType() {
        return this.delegate.getSqlType();
    }

    @Override
    public boolean canBeRemapped() {
        return false;
    }

    @Override
    public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
        final ValueBinder realBinder = this.delegate.getBinder(this.intermediateJavaTypeDescriptor);
        return new ValueBinder<X>(){

            @Override
            public void bind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                Object convertedValue;
                try {
                    convertedValue = AttributeConverterSqlTypeDescriptorAdapter.this.converter.toRelationalValue(value);
                }
                catch (PersistenceException pe) {
                    throw pe;
                }
                catch (RuntimeException re) {
                    throw new PersistenceException("Error attempting to apply AttributeConverter", (Throwable)re);
                }
                log.debugf("Converted value on binding : %s -> %s", value, convertedValue);
                realBinder.bind(st, convertedValue, index, options);
            }

            @Override
            public void bind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                Object convertedValue;
                try {
                    convertedValue = AttributeConverterSqlTypeDescriptorAdapter.this.converter.toRelationalValue(value);
                }
                catch (PersistenceException pe) {
                    throw pe;
                }
                catch (RuntimeException re) {
                    throw new PersistenceException("Error attempting to apply AttributeConverter", (Throwable)re);
                }
                log.debugf("Converted value on binding : %s -> %s", value, convertedValue);
                realBinder.bind(st, convertedValue, name, options);
            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
        final ValueExtractor realExtractor = this.delegate.getExtractor(this.intermediateJavaTypeDescriptor);
        return new ValueExtractor<X>(){

            @Override
            public X extract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                return this.doConversion(realExtractor.extract(rs, name, options));
            }

            @Override
            public X extract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return this.doConversion(realExtractor.extract(statement, index, options));
            }

            @Override
            public X extract(CallableStatement statement, String[] paramNames, WrapperOptions options) throws SQLException {
                if (paramNames.length > 1) {
                    throw new IllegalArgumentException("Basic value extraction cannot handle multiple output parameters");
                }
                return this.doConversion(realExtractor.extract(statement, paramNames, options));
            }

            private X doConversion(Object extractedValue) {
                try {
                    Object convertedValue = AttributeConverterSqlTypeDescriptorAdapter.this.converter.toDomainValue(extractedValue);
                    log.debugf("Converted value on extraction: %s -> %s", extractedValue, convertedValue);
                    return convertedValue;
                }
                catch (PersistenceException pe) {
                    throw pe;
                }
                catch (RuntimeException re) {
                    throw new PersistenceException("Error attempting to apply AttributeConverter", (Throwable)re);
                }
            }
        };
    }
}

