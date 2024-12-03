/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.annotations.CollectionInheritable
 */
package com.atlassian.confluence.velocity.htmlsafe;

import com.atlassian.confluence.velocity.annotations.ReturnValueAnnotation;
import com.atlassian.velocity.htmlsafe.annotations.CollectionInheritable;
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

