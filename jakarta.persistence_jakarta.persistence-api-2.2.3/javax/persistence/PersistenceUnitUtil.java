/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import javax.persistence.PersistenceUtil;

public interface PersistenceUnitUtil
extends PersistenceUtil {
    @Override
    public boolean isLoaded(Object var1, String var2);

    @Override
    public boolean isLoaded(Object var1);

    public Object getIdentifier(Object var1);
}

