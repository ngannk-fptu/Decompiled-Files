/*
 * Decompiled with CFR 0.152.
 */
package javax.jws.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.METHOD})
public @interface SOAPBinding {
    public Style style() default Style.DOCUMENT;

    public Use use() default Use.LITERAL;

    public ParameterStyle parameterStyle() default ParameterStyle.WRAPPED;

    public static enum ParameterStyle {
        BARE,
        WRAPPED;

    }

    public static enum Use {
        LITERAL,
        ENCODED;

    }

    public static enum Style {
        DOCUMENT,
        RPC;

    }
}

