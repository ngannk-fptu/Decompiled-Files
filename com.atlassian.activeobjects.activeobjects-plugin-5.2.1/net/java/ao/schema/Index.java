/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={})
public @interface Index {
    public String name();

    public String[] methodNames();
}

