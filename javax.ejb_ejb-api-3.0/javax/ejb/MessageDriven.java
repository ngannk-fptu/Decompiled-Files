/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ejb.ActivationConfigProperty;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface MessageDriven {
    public String name() default "";

    public Class messageListenerInterface() default Object.class;

    public ActivationConfigProperty[] activationConfig() default {};

    public String mappedName() default "";

    public String description() default "";
}

