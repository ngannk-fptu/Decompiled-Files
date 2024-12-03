/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.persistence.ColumnResult;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ConstructorResult {
    public Class targetClass();

    public ColumnResult[] columns();
}

