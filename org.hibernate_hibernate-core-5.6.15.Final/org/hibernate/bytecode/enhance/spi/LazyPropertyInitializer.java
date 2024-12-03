/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface LazyPropertyInitializer {
    public static final Serializable UNFETCHED_PROPERTY = new Serializable(){

        public String toString() {
            return "<lazy>";
        }

        public Object readResolve() {
            return UNFETCHED_PROPERTY;
        }
    };

    public Object initializeLazyProperty(String var1, Object var2, SharedSessionContractImplementor var3);

    @Deprecated
    public static interface InterceptorImplementor {
        default public Set<String> getInitializedLazyAttributeNames() {
            return Collections.emptySet();
        }

        default public void attributeInitialized(String name) {
        }
    }
}

