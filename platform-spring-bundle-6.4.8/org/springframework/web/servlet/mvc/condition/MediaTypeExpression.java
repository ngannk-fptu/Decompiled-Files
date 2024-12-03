/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.condition;

import org.springframework.http.MediaType;

public interface MediaTypeExpression {
    public MediaType getMediaType();

    public boolean isNegated();
}

