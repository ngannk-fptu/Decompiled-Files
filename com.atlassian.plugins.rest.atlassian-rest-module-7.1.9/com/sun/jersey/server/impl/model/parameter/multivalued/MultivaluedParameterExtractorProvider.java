/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;

public interface MultivaluedParameterExtractorProvider {
    public MultivaluedParameterExtractor get(Parameter var1);

    public MultivaluedParameterExtractor getWithoutDefaultValue(Parameter var1);
}

