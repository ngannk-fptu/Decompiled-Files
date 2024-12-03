/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.sql.NClob;
import org.hibernate.engine.jdbc.WrappedClob;

public interface WrappedNClob
extends WrappedClob {
    @Override
    @Deprecated
    public NClob getWrappedClob();

    public NClob getWrappedNClob();
}

