/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.CLASS)
@Documented
@Deprecated
public @interface Message {
    public static final int NONE = 0;
    public static final int INHERIT = -1;

    public int id() default -1;

    public String value();

    public Format format() default Format.PRINTF;

    public static enum Format {
        PRINTF,
        MESSAGE_FORMAT,
        NO_FORMAT;

    }
}

