/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface EJB {
    public String name() default "";

    public String description() default "";

    public String beanName() default "";

    public Class beanInterface() default Object.class;

    public String mappedName() default "";
}

