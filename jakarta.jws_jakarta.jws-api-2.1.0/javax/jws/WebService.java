/*
 * Decompiled with CFR 0.152.
 */
package javax.jws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface WebService {
    public String name() default "";

    public String targetNamespace() default "";

    public String serviceName() default "";

    public String portName() default "";

    public String wsdlLocation() default "";

    public String endpointInterface() default "";
}

