/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.template;

import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import java.util.List;

public class ViewResourceMethod
extends ResourceMethod {
    public ViewResourceMethod(List<QualitySourceMediaType> produces) {
        super("GET", UriTemplate.EMPTY, MediaTypes.GENERAL_MEDIA_TYPE_LIST, produces, false, null);
    }
}

