/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.api.model.AbstractMethod
 *  com.sun.jersey.spi.container.ResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilterFactory
 */
package com.atlassian.confluence.plugins.restapi.filters;

import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.plugins.restapi.filters.RequestLimitingResourceFilter;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;

public class RequestSizeLimitingResourceFilterFactory
implements ResourceFilterFactory {
    public List<ResourceFilter> create(AbstractMethod abstractMethod) {
        if (RequestSizeLimitingResourceFilterFactory.hasLimitRequestSizeAnnotation(abstractMethod)) {
            long defaultLimitRequestSize = RequestSizeLimitingResourceFilterFactory.hasLimitRequestSizeAnnotationOnMethod(abstractMethod) ? ((LimitRequestSize)abstractMethod.getAnnotation(LimitRequestSize.class)).value() : ((LimitRequestSize)abstractMethod.getResource().getAnnotation(LimitRequestSize.class)).value();
            if (defaultLimitRequestSize < 65536L) {
                defaultLimitRequestSize = 65536L;
            }
            return Collections.singletonList(new RequestLimitingResourceFilter(defaultLimitRequestSize));
        }
        return Collections.emptyList();
    }

    private static boolean hasLimitRequestSizeAnnotation(AbstractMethod abstractMethod) {
        return RequestSizeLimitingResourceFilterFactory.hasLimitRequestSizeAnnotationOnMethod(abstractMethod) || abstractMethod.getResource().isAnnotationPresent(LimitRequestSize.class) || abstractMethod.getResource().getResourceClass().getPackage().isAnnotationPresent(LimitRequestSize.class);
    }

    private static boolean hasLimitRequestSizeAnnotationOnMethod(AbstractMethod abstractMethod) {
        return abstractMethod.isAnnotationPresent(LimitRequestSize.class);
    }
}

