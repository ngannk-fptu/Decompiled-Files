/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.hudson.annotation_indexer.Indexed
 */
package com.infradna.tool.bridge_method_injector;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jvnet.hudson.annotation_indexer.Indexed;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.METHOD})
@Documented
@Indexed
public @interface WithBridgeMethods {
    public Class<?>[] value();

    public boolean castRequired() default false;
}

