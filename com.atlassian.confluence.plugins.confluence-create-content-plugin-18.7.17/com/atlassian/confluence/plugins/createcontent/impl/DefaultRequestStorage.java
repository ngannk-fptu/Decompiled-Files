/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPropertyManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.web.context.HttpContext
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.plugins.createcontent.api.services.ContentBlueprintSanitiserManager;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateBlueprintPageRestEntity;
import com.atlassian.confluence.plugins.createcontent.services.RequestStorage;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.web.context.HttpContext;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultRequestStorage
implements RequestStorage {
    public static final String DRAFT_CREATE_REQUEST = "create.blueprint.page.draft.request";
    private final HttpContext httpContext;
    private final ContentPropertyManager contentPropertyManager;
    private final ObjectMapper objectMapper;
    private final ContentBlueprintSanitiserManager sanitiserManager;

    @Autowired
    public DefaultRequestStorage(@ComponentImport HttpContext httpContext, @ComponentImport ContentPropertyManager contentPropertyManager, ContentBlueprintSanitiserManager sanitiserManager) {
        this.httpContext = httpContext;
        this.contentPropertyManager = contentPropertyManager;
        this.sanitiserManager = sanitiserManager;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void storeCreateRequest(CreateBlueprintPageEntity createRequest, ContentEntityObject ceo) {
        CreateBlueprintPageEntity sanitisedRequest = this.sanitiserManager.sanitise(createRequest);
        if (ceo.isPersistent()) {
            try {
                String jsonRequest = this.objectMapper.writeValueAsString((Object)sanitisedRequest);
                this.contentPropertyManager.setTextProperty(ceo, DRAFT_CREATE_REQUEST, jsonRequest);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.getSession().setAttribute(this.getAttributeKey(ceo), (Object)sanitisedRequest);
        }
    }

    @Override
    public CreateBlueprintPageEntity retrieveRequest(ContentEntityObject ceo) {
        CreateBlueprintPageEntity request;
        if (ceo.isPersistent()) {
            String sanitisedJsonRequest = this.contentPropertyManager.getTextProperty(ceo, DRAFT_CREATE_REQUEST);
            if (StringUtils.isBlank((CharSequence)sanitisedJsonRequest)) {
                throw new IllegalStateException("No persisted CreateBlueprint request found for draft with id: " + ceo.getId());
            }
            try {
                request = (CreateBlueprintPageEntity)this.objectMapper.readValue(sanitisedJsonRequest, CreateBlueprintPageRestEntity.class);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            request = (CreateBlueprintPageEntity)this.getSession().getAttribute(this.getAttributeKey(ceo));
        }
        return this.sanitiserManager.unsanitise(request);
    }

    @Override
    public void clear(ContentEntityObject ceo) {
        if (ceo.isPersistent()) {
            this.contentPropertyManager.removeProperty(ceo, DRAFT_CREATE_REQUEST);
            this.contentPropertyManager.removeProperty(ceo, "create.blueprint.page.draft.cleaned");
        } else {
            this.getSession().removeAttribute(this.getAttributeKey(ceo));
        }
    }

    private String getAttributeKey(ContentEntityObject ceo) {
        return DRAFT_CREATE_REQUEST + ceo.getIdAsString();
    }

    private HttpSession getSession() {
        return this.httpContext.getSession(true);
    }
}

