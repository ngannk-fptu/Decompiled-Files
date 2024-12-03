/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.group;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface GroupSequenceProvider {
    public Class<? extends DefaultGroupSequenceProvider<?>> value();
}

