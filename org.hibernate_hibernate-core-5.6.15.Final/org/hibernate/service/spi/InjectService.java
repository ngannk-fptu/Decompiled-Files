/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface InjectService {
    public Class serviceRole() default Void.class;

    public boolean required() default true;
}

