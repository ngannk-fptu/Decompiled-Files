/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.factory;

import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.service.ContextService;
import com.atlassian.confluence.plugins.mobile.service.impl.BlogpostContextServiceImpl;
import com.atlassian.confluence.plugins.mobile.service.impl.GlobalContextServiceImpl;
import com.atlassian.confluence.plugins.mobile.service.impl.PageContextServiceImpl;
import com.atlassian.confluence.plugins.mobile.service.impl.SpaceContextServiceImpl;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContextServiceFactory {
    private final GlobalContextServiceImpl globalContextService;
    private final SpaceContextServiceImpl spaceContextService;
    private final PageContextServiceImpl pageContextService;
    private final BlogpostContextServiceImpl blogpostContextService;

    @Autowired
    public ContextServiceFactory(GlobalContextServiceImpl globalContextService, SpaceContextServiceImpl spaceContextService, PageContextServiceImpl pageContextService, BlogpostContextServiceImpl blogpostContextService) {
        this.globalContextService = Objects.requireNonNull(globalContextService);
        this.spaceContextService = Objects.requireNonNull(spaceContextService);
        this.pageContextService = Objects.requireNonNull(pageContextService);
        this.blogpostContextService = Objects.requireNonNull(blogpostContextService);
    }

    public ContextService getContextService(Context.Type type) {
        switch (type) {
            case BLOGPOST: {
                return this.blogpostContextService;
            }
            case PAGE: {
                return this.pageContextService;
            }
            case GLOBAL: {
                return this.globalContextService;
            }
            case SPACE: {
                return this.spaceContextService;
            }
        }
        throw new NotImplementedServiceException(type + " is not yet supported.");
    }
}

