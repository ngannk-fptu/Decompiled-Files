/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand;

import com.atlassian.plugins.rest.common.expand.EntityExpander;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
public @interface Expander {
    public Class<? extends EntityExpander<?>> value();
}

