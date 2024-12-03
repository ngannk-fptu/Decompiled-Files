/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.Types;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface PropertyType {
    public Types type();

    public Cardinality card() default Cardinality.Simple;
}

