/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface QueryHint {
    public String name();

    public String value();
}

