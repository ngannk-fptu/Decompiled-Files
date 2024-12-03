/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.accept;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.accept.AbstractMappingContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

public class ParameterContentNegotiationStrategy
extends AbstractMappingContentNegotiationStrategy {
    private String parameterName = "format";

    public ParameterContentNegotiationStrategy(Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
    }

    public void setParameterName(String parameterName) {
        Assert.notNull((Object)parameterName, (String)"'parameterName' is required");
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    @Override
    @Nullable
    protected String getMediaTypeKey(NativeWebRequest request) {
        return request.getParameter(this.getParameterName());
    }
}

