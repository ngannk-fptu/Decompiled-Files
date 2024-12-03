/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.method.annotation.InitBinderDataBinderFactory
 *  org.springframework.web.method.support.InvocableHandlerMethod
 *  org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
package com.atlassian.crowd.embedded.admin.spring;

import com.atlassian.crowd.embedded.admin.spring.EmbeddedCrowdServletRequestDataBinderFactory;
import java.util.List;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

public class EmbeddedCrowdRequestMappingHandlerAdapter
extends RequestMappingHandlerAdapter {
    protected InitBinderDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> binderMethods) {
        return new EmbeddedCrowdServletRequestDataBinderFactory(binderMethods, this.getWebBindingInitializer());
    }
}

