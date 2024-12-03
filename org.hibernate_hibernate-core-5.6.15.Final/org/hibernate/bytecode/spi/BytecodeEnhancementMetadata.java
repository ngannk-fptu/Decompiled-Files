/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.spi;

import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributesMetadata;
import org.hibernate.bytecode.spi.NotInstrumentedException;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface BytecodeEnhancementMetadata {
    public String getEntityName();

    public boolean isEnhancedForLazyLoading();

    public LazyAttributesMetadata getLazyAttributesMetadata();

    public PersistentAttributeInterceptable createEnhancedProxy(EntityKey var1, boolean var2, SharedSessionContractImplementor var3);

    public LazyAttributeLoadingInterceptor injectInterceptor(Object var1, Object var2, SharedSessionContractImplementor var3) throws NotInstrumentedException;

    public void injectInterceptor(Object var1, PersistentAttributeInterceptor var2, SharedSessionContractImplementor var3);

    public void injectEnhancedEntityAsProxyInterceptor(Object var1, EntityKey var2, SharedSessionContractImplementor var3);

    public LazyAttributeLoadingInterceptor extractInterceptor(Object var1) throws NotInstrumentedException;

    public BytecodeLazyAttributeInterceptor extractLazyInterceptor(Object var1) throws NotInstrumentedException;

    public boolean hasUnFetchedAttributes(Object var1);

    public boolean isAttributeLoaded(Object var1, String var2);
}

