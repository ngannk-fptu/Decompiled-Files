/*
 * Decompiled with CFR 0.152.
 */
package javax.mail;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.mail.MailSessionDefinitions;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=MailSessionDefinitions.class)
public @interface MailSessionDefinition {
    public String description() default "";

    public String name();

    public String storeProtocol() default "";

    public String transportProtocol() default "";

    public String host() default "";

    public String user() default "";

    public String password() default "";

    public String from() default "";

    public String[] properties() default {};
}

