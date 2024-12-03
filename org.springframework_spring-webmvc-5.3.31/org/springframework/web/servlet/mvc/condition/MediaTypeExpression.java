/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.MediaType
 */
package org.springframework.web.servlet.mvc.condition;

import org.springframework.http.MediaType;

public interface MediaTypeExpression {
    public MediaType getMediaType();

    public boolean isNegated();
}

