/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.annotation.Persistent
 *  org.springframework.data.keyvalue.annotation.KeySpace
 */
package org.springframework.vault.repository.mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.keyvalue.annotation.KeySpace;

@Persistent
@Documented
@Inherited
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@KeySpace
public @interface Secret {
    @AliasFor(annotation=KeySpace.class, attribute="value")
    public String value() default "";

    public String backend() default "secret";
}

