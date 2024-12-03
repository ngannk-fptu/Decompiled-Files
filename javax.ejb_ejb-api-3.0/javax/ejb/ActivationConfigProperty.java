/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ActivationConfigProperty {
    public String propertyName();

    public String propertyValue();
}

