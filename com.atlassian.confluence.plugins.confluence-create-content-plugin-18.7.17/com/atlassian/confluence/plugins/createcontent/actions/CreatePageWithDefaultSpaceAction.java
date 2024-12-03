/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.createcontent.rest.SpaceResultsEntity;
import com.atlassian.confluence.plugins.createcontent.services.SpaceCollectionService;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.xwork.XsrfTokenGenerator;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatePageWithDefaultSpaceAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(CreatePageWithDefaultSpaceAction.class);
    private final SpaceCollectionService spaceCollectionService;
    private final XsrfTokenGenerator simpleXsrfTokenGenerator;
    private String redirectUrl;

    public CreatePageWithDefaultSpaceAction(SpaceCollectionService spaceCollectionService, @ComponentImport XsrfTokenGenerator simpleXsrfTokenGenerator) {
        this.spaceCollectionService = spaceCollectionService;
        this.simpleXsrfTokenGenerator = simpleXsrfTokenGenerator;
    }

    public String execute() throws Exception {
        String spaceKey = this.getDefaultSpaceKey();
        UrlBuilder urlBuilder = new UrlBuilder("/pages/createpage.action");
        urlBuilder.add("spaceKey", spaceKey);
        urlBuilder.add("src", "quick-create");
        urlBuilder.add("atl_token", this.getAtlToken());
        this.redirectUrl = urlBuilder.toUrl();
        return "success";
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }

    private String getAtlToken() {
        String token = this.simpleXsrfTokenGenerator.getToken(this.getCurrentRequest(), true);
        return token;
    }

    private String getDefaultSpaceKey() {
        String spaceKey = "";
        try {
            Map<String, SpaceResultsEntity> spaces = this.spaceCollectionService.getSpaces(Collections.emptyList(), 1, 1, "EDITSPACE");
            if (!CollectionUtils.isEmpty(spaces.get("promotedSpaces").getSpaces())) {
                spaceKey = spaces.get("promotedSpaces").getSpaces().iterator().next().getId();
            } else if (!CollectionUtils.isEmpty(spaces.get("otherSpaces").getSpaces())) {
                spaceKey = spaces.get("otherSpaces").getSpaces().iterator().next().getId();
            }
            if (spaceKey.isEmpty()) {
                log.error("Couldn't find any space with create page permission. CreateContentCondition should have failed and QuickCreateUrlContextProvider.getContextMap() should not be called ");
            }
        }
        catch (Exception e) {
            log.error("Exception while calculating default space key. Catching to avoid bringing down the whole page.", (Throwable)e);
        }
        return spaceKey;
    }
}

