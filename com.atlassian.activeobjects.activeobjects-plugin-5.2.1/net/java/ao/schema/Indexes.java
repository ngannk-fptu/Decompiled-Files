/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.java.ao.schema.Index;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface Indexes {
    public Index[] value();
}

