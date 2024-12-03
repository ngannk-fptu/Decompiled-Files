/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.MBeanExportConfiguration;
import org.springframework.jmx.support.RegistrationPolicy;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Import(value={MBeanExportConfiguration.class})
public @interface EnableMBeanExport {
    public String defaultDomain() default "";

    public String server() default "";

    public RegistrationPolicy registration() default RegistrationPolicy.FAIL_ON_EXISTING;
}

