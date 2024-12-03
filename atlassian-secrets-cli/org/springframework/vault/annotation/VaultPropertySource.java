/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.vault.annotation.VaultPropertySourceRegistrar;
import org.springframework.vault.annotation.VaultPropertySources;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value=VaultPropertySources.class)
@Import(value={VaultPropertySourceRegistrar.class})
public @interface VaultPropertySource {
    public String[] value();

    public String propertyNamePrefix() default "";

    public boolean ignoreSecretNotFound() default true;

    public String vaultTemplateRef() default "vaultTemplate";

    public Renewal renewal() default Renewal.OFF;

    public static enum Renewal {
        OFF,
        RENEW,
        ROTATE;

    }
}

