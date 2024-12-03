/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRefs;

@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value=WebServiceRefs.class)
public @interface WebServiceRef {
    public String name() default "";

    public Class<?> type() default Object.class;

    public String mappedName() default "";

    public Class<? extends Service> value() default Service.class;

    public String wsdlLocation() default "";

    public String lookup() default "";
}

