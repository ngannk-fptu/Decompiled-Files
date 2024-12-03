/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.core.rest.representations.ErrorRepresentation;

public interface BaseRepresentationFactory {
    public ErrorRepresentation createErrorRepresentation(String var1);

    public ErrorRepresentation createErrorRepresentation(String var1, String var2);

    public ErrorRepresentation createI18nErrorRepresentation(String var1);
}

