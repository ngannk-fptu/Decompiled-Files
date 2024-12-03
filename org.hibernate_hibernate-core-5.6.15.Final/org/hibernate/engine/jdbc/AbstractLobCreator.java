/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.SerializableBlobProxy;
import org.hibernate.engine.jdbc.SerializableClobProxy;
import org.hibernate.engine.jdbc.SerializableNClobProxy;

public abstract class AbstractLobCreator
implements LobCreator {
    @Override
    public Blob wrap(Blob blob) {
        return SerializableBlobProxy.generateProxy(blob);
    }

    @Override
    public Clob wrap(Clob clob) {
        if (NClob.class.isInstance(clob)) {
            return this.wrap((NClob)clob);
        }
        return SerializableClobProxy.generateProxy(clob);
    }

    @Override
    public NClob wrap(NClob nclob) {
        return SerializableNClobProxy.generateProxy(nclob);
    }
}

