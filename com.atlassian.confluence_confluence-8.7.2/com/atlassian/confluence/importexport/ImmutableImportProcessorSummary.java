/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport;

import java.util.Set;

@Deprecated
public interface ImmutableImportProcessorSummary {
    public Object getNewIdFor(Class var1, Object var2);

    public Object getNewIdFor(PersistedKey var1);

    public Object getOriginalIdFor(Class var1, Object var2);

    public Object getOriginalIdFor(PersistedKey var1);

    public Set<PersistedKey> getOriginalPersistedKeys();

    public Set<PersistedKey> getNewPersistedKeys();

    public static interface PersistedKey {
        public Class getPersistedClass();

        public Object getPersistedId();
    }
}

