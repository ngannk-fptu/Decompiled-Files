/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import org.hibernate.engine.jdbc.CharacterStream;
import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.DataHelper;

public class StringTypeDescriptor
extends AbstractTypeDescriptor<String> {
    public static final StringTypeDescriptor INSTANCE = new StringTypeDescriptor();

    public StringTypeDescriptor() {
        super(String.class);
    }

    @Override
    public String toString(String value) {
        return value;
    }

    @Override
    public String fromString(String string) {
        return string;
    }

    @Override
    public <X> X unwrap(String value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (Reader.class.isAssignableFrom(type)) {
            return (X)new StringReader(value);
        }
        if (CharacterStream.class.isAssignableFrom(type)) {
            return (X)new CharacterStreamImpl(value);
        }
        if (DataHelper.isNClob(type)) {
            return (X)options.getLobCreator().createNClob(value);
        }
        if (Clob.class.isAssignableFrom(type)) {
            return (X)options.getLobCreator().createClob(value);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> String wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isInstance(value)) {
            return (String)value;
        }
        if (Reader.class.isInstance(value)) {
            return DataHelper.extractString((Reader)value);
        }
        if (Clob.class.isInstance(value)) {
            return DataHelper.extractString((Clob)value);
        }
        throw this.unknownWrap(value.getClass());
    }
}

