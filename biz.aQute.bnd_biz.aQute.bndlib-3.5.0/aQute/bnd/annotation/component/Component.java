/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.annotation.component;

import aQute.bnd.annotation.component.ConfigurationPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.TYPE})
public @interface Component {
    public static final String RNAME = "LaQute/bnd/annotation/component/Component;";
    public static final String PROVIDE = "provide";
    public static final String NAME = "name";
    public static final String FACTORY = "factory";
    public static final String SERVICEFACTORY = "servicefactory";
    public static final String IMMEDIATE = "immediate";
    public static final String CONFIGURATION_POLICY = "configurationPolicy";
    public static final String ENABLED = "enabled";
    public static final String PROPERTIES = "properties";
    public static final String VERSION = "version";
    public static final String DESIGNATE = "designate";
    public static final String DESIGNATE_FACTORY = "designateFactory";

    public String name() default "";

    public Class<?>[] provide() default {Object.class};

    public String factory() default "";

    public boolean servicefactory() default false;

    public boolean enabled() default true;

    public boolean immediate() default false;

    public ConfigurationPolicy configurationPolicy() default ConfigurationPolicy.optional;

    public String[] properties() default {};

    public Class<?> designate() default Object.class;

    public Class<?> designateFactory() default Object.class;
}

