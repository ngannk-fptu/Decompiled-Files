/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.copy;

import java.sql.SQLException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.copy.CopyOperation;

public interface CopyOut
extends CopyOperation {
    public byte @Nullable [] readFromCopy() throws SQLException;

    public byte @Nullable [] readFromCopy(boolean var1) throws SQLException;
}

