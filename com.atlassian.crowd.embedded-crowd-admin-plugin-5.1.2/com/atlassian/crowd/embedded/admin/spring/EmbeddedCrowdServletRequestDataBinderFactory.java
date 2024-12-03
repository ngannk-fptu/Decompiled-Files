/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  org.springframework.web.bind.ServletRequestDataBinder
 *  org.springframework.web.bind.support.WebBindingInitializer
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.InvocableHandlerMethod
 *  org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory
 */
package com.atlassian.crowd.embedded.admin.spring;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

public class EmbeddedCrowdServletRequestDataBinderFactory
extends ServletRequestDataBinderFactory {
    private static final List<String> BANNED_FIELDS = ImmutableList.of((Object)"class.*", (Object)"Class.*", (Object)"*.class.*", (Object)"*.Class.*");

    public EmbeddedCrowdServletRequestDataBinderFactory(List<InvocableHandlerMethod> binderMethods, WebBindingInitializer initializer) {
        super(binderMethods, initializer);
    }

    protected ServletRequestDataBinder createBinderInstance(Object target, String objectName, NativeWebRequest request) throws Exception {
        ServletRequestDataBinder binder = super.createBinderInstance(target, objectName, request);
        this.configureBannedFields(binder);
        return binder;
    }

    @VisibleForTesting
    void configureBannedFields(ServletRequestDataBinder binder) {
        HashSet<String> effectiveBannedFields = new HashSet<String>(Optional.ofNullable(binder.getDisallowedFields()).map(Arrays::asList).orElse(Collections.emptyList()));
        effectiveBannedFields.addAll(BANNED_FIELDS);
        binder.setDisallowedFields(effectiveBannedFields.toArray(new String[0]));
    }
}

