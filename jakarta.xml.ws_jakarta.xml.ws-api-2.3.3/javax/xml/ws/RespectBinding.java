/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id="javax.xml.ws.RespectBindingFeature", bean=RespectBindingFeature.class)
public @interface RespectBinding {
    public boolean enabled() default true;
}

