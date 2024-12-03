/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import org.hibernate.engine.jdbc.CharacterStream;

public interface ClobImplementer {
    public CharacterStream getUnderlyingStream();
}

