/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface StaticMetamodel {
    public Class<?> value();
}

