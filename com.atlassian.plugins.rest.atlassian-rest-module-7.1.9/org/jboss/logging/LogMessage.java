/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jboss.logging.Logger;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.METHOD})
@Documented
@Deprecated
public @interface LogMessage {
    public Logger.Level level() default Logger.Level.INFO;

    public Class<?> loggingClass() default Void.class;
}

