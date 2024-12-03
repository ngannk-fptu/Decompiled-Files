/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.TYPE})
public @interface Component {
    public static final String NAME = "$";

    public String name() default "";

    public Class<?>[] service() default {};

    public String factory() default "";

    public boolean servicefactory() default false;

    public boolean enabled() default true;

    public boolean immediate() default false;

    public String[] property() default {};

    public String[] properties() default {};

    public String xmlns() default "";

    public ConfigurationPolicy configurationPolicy() default ConfigurationPolicy.OPTIONAL;

    public String[] configurationPid() default {"$"};

    public ServiceScope scope() default ServiceScope.DEFAULT;

    public Reference[] reference() default {};
}

