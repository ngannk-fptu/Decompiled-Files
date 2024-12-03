/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity.htmlsafe;

import com.atlassian.velocity.htmlsafe.annotations.CollectionInheritable;
import com.atlassian.velocity.htmlsafe.annotations.ReturnValueAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
@ReturnValueAnnotation
@CollectionInheritable
public @interface HtmlSafe {
}

