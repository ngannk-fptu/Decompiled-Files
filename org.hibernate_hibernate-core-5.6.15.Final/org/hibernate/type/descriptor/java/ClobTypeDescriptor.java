/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.Reader;
import java.io.Serializable;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Comparator;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.CharacterStream;
import org.hibernate.engine.jdbc.ClobImplementer;
import org.hibernate.engine.jdbc.ClobProxy;
import org.hibernate.engine.jdbc.WrappedClob;
import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.DataHelper;
import org.hibernate.type.descriptor.java.IncomparableComparator;
import org.hibernate.type.descriptor.java.MutabilityPlan;

public class ClobTypeDescriptor
extends AbstractTypeDescriptor<Clob> {
    public static final ClobTypeDescriptor INSTANCE = new ClobTypeDescriptor();

    public ClobTypeDescriptor() {
        super(Clob.class, ClobMutabilityPlan.INSTANCE);
    }

    @Override
    public String extractLoggableRepresentation(Clob value) {
        return value == null ? "null" : "{clob}";
    }

    @Override
    public String toString(Clob value) {
        return DataHelper.extractString(value);
    }

    @Override
    public Clob fromString(String string) {
        return ClobProxy.generateProxy(string);
    }

    @Override
    public Comparator<Clob> getComparator() {
        return IncomparableComparator.INSTANCE;
    }

    @Override
    public int extractHashCode(Clob value) {
        return System.identityHashCode(value);
    }

    @Override
    public boolean areEqual(Clob one, Clob another) {
        return one == another;
    }

    @Override
    public <X> X unwrap(Clob value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        try {
            if (CharacterStream.class.isAssignableFrom(type)) {
                if (value instanceof ClobImplementer) {
                    return (X)((ClobImplementer)((Object)value)).getUnderlyingStream();
                }
                return (X)new CharacterStreamImpl(DataHelper.extractString(value.getCharacterStream()));
            }
            if (Clob.class.isAssignableFrom(type)) {
                Clob clob = value instanceof WrappedClob ? ((WrappedClob)((Object)value)).getWrappedClob() : value;
                return (X)clob;
            }
            if (String.class.isAssignableFrom(type)) {
                if (value instanceof ClobImplementer) {
                    return (X)((ClobImplementer)((Object)value)).getUnderlyingStream().asString();
                }
                return (X)DataHelper.extractString(value.getCharacterStream());
            }
        }
        catch (SQLException e) {
            throw new HibernateException("Unable to access clob stream", e);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Clob wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Clob.class.isAssignableFrom(value.getClass())) {
            return options.getLobCreator().wrap((Clob)value);
        }
        if (Reader.class.isAssignableFrom(value.getClass())) {
            Reader reader = (Reader)value;
            return options.getLobCreator().createClob(DataHelper.extractString(reader));
        }
        if (String.class.isAssignableFrom(value.getClass())) {
            return options.getLobCreator().createClob((String)value);
        }
        throw this.unknownWrap(value.getClass());
    }

    public static class ClobMutabilityPlan
    implements MutabilityPlan<Clob> {
        public static final ClobMutabilityPlan INSTANCE = new ClobMutabilityPlan();

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public Clob deepCopy(Clob value) {
            return value;
        }

        @Override
        public Serializable disassemble(Clob value) {
            throw new UnsupportedOperationException("Clobs are not cacheable");
        }

        @Override
        public Clob assemble(Serializable cached) {
            throw new UnsupportedOperationException("Clobs are not cacheable");
        }
    }
}

