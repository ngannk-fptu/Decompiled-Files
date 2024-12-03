/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.WebServiceFeature;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface WebServiceFeatureAnnotation {
    public String id();

    public Class<? extends WebServiceFeature> bean();
}

