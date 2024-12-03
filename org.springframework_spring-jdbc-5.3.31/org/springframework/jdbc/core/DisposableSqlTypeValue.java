/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import org.springframework.jdbc.core.SqlTypeValue;

public interface DisposableSqlTypeValue
extends SqlTypeValue {
    public void cleanup();
}

