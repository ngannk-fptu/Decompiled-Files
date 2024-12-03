/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.requestaccess.service;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.requestaccess.service.GrantAccessRequestValidator;
import com.atlassian.confluence.plugins.requestaccess.service.PagePermissionChecker;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GrantAccessRequestValidatorImpl
implements GrantAccessRequestValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrantAccessRequestValidatorImpl.class);
    private static final String PAGE_ID_REQUEST_PARAM = "pageId";
    private static final String USERNAME_REQUEST_PARAM = "username";
    private static final String GRANT_ACCESS_REQUEST_PARAM = "grantAccess";
    private final HttpContext httpContext;
    private final PageManager pageManager;
    private final PagePermissionChecker pagePermissionChecker;

    @Autowired
    public GrantAccessRequestValidatorImpl(@ComponentImport HttpContext httpContext, @ComponentImport PageManager pageManager, PagePermissionChecker pagePermissionChecker) {
        this.httpContext = httpContext;
        this.pageManager = pageManager;
        this.pagePermissionChecker = pagePermissionChecker;
    }

    @Override
    public boolean isGrantAccessRequestValid() {
        if (this.httpContext.getRequest() != null && this.isAllGrantAccessParamsPresent()) {
            try {
                String username = this.httpContext.getRequest().getParameter(USERNAME_REQUEST_PARAM);
                long pageId = Long.parseLong(this.httpContext.getRequest().getParameter(PAGE_ID_REQUEST_PARAM));
                return this.isRequestAccessValid(pageId, username);
            }
            catch (NumberFormatException exc) {
                LOGGER.info("Invalid page id value: [{}]", (Object)this.httpContext.getRequest().getParameter(PAGE_ID_REQUEST_PARAM));
            }
        }
        return false;
    }

    private boolean isAllGrantAccessParamsPresent() {
        return StringUtils.isNotBlank((CharSequence)this.httpContext.getRequest().getParameter(PAGE_ID_REQUEST_PARAM)) && StringUtils.isNotBlank((CharSequence)this.httpContext.getRequest().getParameter(USERNAME_REQUEST_PARAM)) && StringUtils.isNotBlank((CharSequence)this.httpContext.getRequest().getParameter(GRANT_ACCESS_REQUEST_PARAM));
    }

    private boolean isRequestAccessValid(long pageId, String username) {
        AbstractPage page = this.pageManager.getAbstractPage(pageId);
        return page != null && this.pagePermissionChecker.canAuthenticatedUserGrantAccessToPage(page) && !this.pagePermissionChecker.isUserPermittedToViewPage(username, page);
    }
}

