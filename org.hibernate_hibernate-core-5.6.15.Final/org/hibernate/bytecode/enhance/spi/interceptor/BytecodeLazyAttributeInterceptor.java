/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import java.util.Set;
import org.hibernate.Incubating;
import org.hibernate.bytecode.enhance.spi.interceptor.SessionAssociableInterceptor;

@Incubating
public interface BytecodeLazyAttributeInterceptor
extends SessionAssociableInterceptor {
    public String getEntityName();

    public Object getIdentifier();

    @Override
    public Set<String> getInitializedLazyAttributeNames();

    @Override
    public void attributeInitialized(String var1);

    @Override
    public boolean isAttributeLoaded(String var1);

    public boolean hasAnyUninitializedAttributes();
}

