/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 */
package com.atlassian.confluence.impl.audit.handler;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import java.util.Optional;
import java.util.stream.Stream;

interface Handler<T> {
    public Stream<ChangedValue> handle(Optional<String> var1, T var2, AuditAction var3);

    public Stream<ChangedValue> handle(Optional<String> var1, T var2, T var3);
}

