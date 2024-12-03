/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.java.ao.ValueGenerator;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface Generator {
    public Class<? extends ValueGenerator<?>> value();
}

