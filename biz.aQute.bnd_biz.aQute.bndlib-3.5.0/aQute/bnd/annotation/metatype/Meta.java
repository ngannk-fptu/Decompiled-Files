/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.annotation.metatype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Meta {
    public static final String NULL = "\u00a7NULL\u00a7";

    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface AD {
        public String description() default "\u00a7NULL\u00a7";

        public String name() default "\u00a7NULL\u00a7";

        public String id() default "\u00a7NULL\u00a7";

        public Type type() default Type.String;

        public int cardinality() default 0;

        public String min() default "\u00a7NULL\u00a7";

        public String max() default "\u00a7NULL\u00a7";

        public String deflt() default "\u00a7NULL\u00a7";

        public boolean required() default true;

        public String[] optionLabels() default {"\u00a7NULL\u00a7"};

        public String[] optionValues() default {"\u00a7NULL\u00a7"};
    }

    @Target(value={ElementType.TYPE})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface OCD {
        public String name() default "\u00a7NULL\u00a7";

        public String id() default "\u00a7NULL\u00a7";

        public String localization() default "\u00a7NULL\u00a7";

        public String description() default "\u00a7NULL\u00a7";

        public boolean factory() default false;
    }

    public static enum Type {
        Boolean,
        Byte,
        Character,
        Short,
        Integer,
        Long,
        Float,
        Double,
        String,
        Password;

    }
}

