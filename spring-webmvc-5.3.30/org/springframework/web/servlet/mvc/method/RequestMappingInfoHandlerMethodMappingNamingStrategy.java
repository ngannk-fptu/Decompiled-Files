/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.method.HandlerMethod
 */
package org.springframework.web.servlet.mvc.method;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public class RequestMappingInfoHandlerMethodMappingNamingStrategy
implements HandlerMethodMappingNamingStrategy<RequestMappingInfo> {
    public static final String SEPARATOR = "#";

    @Override
    public String getName(HandlerMethod handlerMethod, RequestMappingInfo mapping) {
        if (mapping.getName() != null) {
            return mapping.getName();
        }
        StringBuilder sb = new StringBuilder();
        String simpleTypeName = handlerMethod.getBeanType().getSimpleName();
        for (int i2 = 0; i2 < simpleTypeName.length(); ++i2) {
            if (!Character.isUpperCase(simpleTypeName.charAt(i2))) continue;
            sb.append(simpleTypeName.charAt(i2));
        }
        sb.append(SEPARATOR).append(handlerMethod.getMethod().getName());
        return sb.toString();
    }
}

