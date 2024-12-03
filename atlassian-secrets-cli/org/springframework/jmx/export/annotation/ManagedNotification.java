/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.jmx.export.annotation.ManagedNotifications;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Repeatable(value=ManagedNotifications.class)
public @interface ManagedNotification {
    public String name();

    public String description() default "";

    public String[] notificationTypes();
}

