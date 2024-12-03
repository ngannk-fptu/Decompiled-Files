/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.templaterenderer.annotations;

import com.atlassian.templaterenderer.annotations.CollectionInheritable;
import com.atlassian.templaterenderer.annotations.ReturnValueAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
@ReturnValueAnnotation
@CollectionInheritable
@Deprecated
public @interface HtmlSafe {
}

