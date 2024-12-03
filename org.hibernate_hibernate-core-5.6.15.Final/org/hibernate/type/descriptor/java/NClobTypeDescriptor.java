/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.Reader;
import java.io.Serializable;
import java.sql.NClob;
import java.sql.SQLException;
import java.util.Comparator;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.CharacterStream;
import org.hibernate.engine.jdbc.NClobImplementer;
import org.hibernate.engine.jdbc.NClobProxy;
import org.hibernate.engine.jdbc.WrappedNClob;
import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.DataHelper;
import org.hibernate.type.descriptor.java.IncomparableComparator;
import org.hibernate.type.descriptor.java.MutabilityPlan;

public class NClobTypeDescriptor
extends AbstractTypeDescriptor<NClob> {
    public static final NClobTypeDescriptor INSTANCE = new NClobTypeDescriptor();

    public NClobTypeDescriptor() {
        super(NClob.class, NClobMutabilityPlan.INSTANCE);
    }

    @Override
    public String extractLoggableRepresentation(NClob value) {
        return value == null ? "null" : "{nclob}";
    }

    @Override
    public String toString(NClob value) {
        return DataHelper.extractString(value);
    }

    @Override
    public NClob fromString(String string) {
        return NClobProxy.generateProxy(string);
    }

    @Override
    public Comparator<NClob> getComparator() {
        return IncomparableComparator.INSTANCE;
    }

    @Override
    public int extractHashCode(NClob value) {
        return System.identityHashCode(value);
    }

    @Override
    public boolean areEqual(NClob one, NClob another) {
        return one == another;
    }

    @Override
    public <X> X unwrap(NClob value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        try {
            if (CharacterStream.class.isAssignableFrom(type)) {
                if (NClobImplementer.class.isInstance(value)) {
                    return (X)((NClobImplementer)((Object)value)).getUnderlyingStream();
                }
                return (X)new CharacterStreamImpl(DataHelper.extractString(value.getCharacterStream()));
            }
            if (NClob.class.isAssignableFrom(type)) {
                NClob nclob = WrappedNClob.class.isInstance(value) ? ((WrappedNClob)((Object)value)).getWrappedNClob() : value;
                return (X)nclob;
            }
        }
        catch (SQLException e) {
            throw new HibernateException("Unable to access nclob stream", e);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> NClob wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (NClob.class.isAssignableFrom(value.getClass())) {
            return options.getLobCreator().wrap((NClob)value);
        }
        if (Reader.class.isAssignableFrom(value.getClass())) {
            Reader reader = (Reader)value;
            return options.getLobCreator().createNClob(DataHelper.extractString(reader));
        }
        throw this.unknownWrap(value.getClass());
    }

    public static class NClobMutabilityPlan
    implements MutabilityPlan<NClob> {
        public static final NClobMutabilityPlan INSTANCE = new NClobMutabilityPlan();

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public NClob deepCopy(NClob value) {
            return value;
        }

        @Override
        public Serializable disassemble(NClob value) {
            throw new UnsupportedOperationException("Clobs are not cacheable");
        }

        @Override
        public NClob assemble(Serializable cached) {
            throw new UnsupportedOperationException("Clobs are not cacheable");
        }
    }
}

