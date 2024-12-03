/*
 * Decompiled with CFR 0.152.
 */
package javax.jws.soap;

import javax.jws.soap.InitParam;

@Deprecated
public @interface SOAPMessageHandler {
    public String name() default "";

    public String className();

    public InitParam[] initParams() default {};

    public String[] roles() default {};

    public String[] headers() default {};
}

