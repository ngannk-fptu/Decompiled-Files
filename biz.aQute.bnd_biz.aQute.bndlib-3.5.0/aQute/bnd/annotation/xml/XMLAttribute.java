/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.annotation.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.ANNOTATION_TYPE})
public @interface XMLAttribute {
    public String namespace();

    public String prefix() default "ns";

    public String[] embedIn() default {"*"};

    public String[] mapping() default {};
}

