/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ClassIntrospector<T extends BeanDescription> {
    protected ClassIntrospector() {
    }

    public abstract T forSerialization(SerializationConfig var1, JavaType var2, MixInResolver var3);

    public abstract T forDeserialization(DeserializationConfig var1, JavaType var2, MixInResolver var3);

    public abstract T forCreation(DeserializationConfig var1, JavaType var2, MixInResolver var3);

    public abstract T forClassAnnotations(MapperConfig<?> var1, JavaType var2, MixInResolver var3);

    public abstract T forDirectClassAnnotations(MapperConfig<?> var1, JavaType var2, MixInResolver var3);

    @Deprecated
    public T forClassAnnotations(MapperConfig<?> cfg, Class<?> cls, MixInResolver r) {
        return this.forClassAnnotations(cfg, cfg.constructType(cls), r);
    }

    @Deprecated
    public T forDirectClassAnnotations(MapperConfig<?> cfg, Class<?> cls, MixInResolver r) {
        return this.forDirectClassAnnotations(cfg, cfg.constructType(cls), r);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface MixInResolver {
        public Class<?> findMixInClassFor(Class<?> var1);
    }
}

