/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.sql.ResultSet;

public interface ResultSetIdentifierConsumer {
    public Serializable consumeIdentifier(ResultSet var1);
}

