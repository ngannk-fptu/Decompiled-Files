/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws.soap;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id="http://www.w3.org/2005/08/addressing/module", bean=AddressingFeature.class)
public @interface Addressing {
    public boolean enabled() default true;

    public boolean required() default false;

    public AddressingFeature.Responses responses() default AddressingFeature.Responses.ALL;
}

