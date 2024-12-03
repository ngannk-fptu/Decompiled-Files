/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Entity
 *  com.opensymphony.xwork2.interceptor.ValidationAware
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Scope
 *  org.springframework.context.annotation.ScopedProxyMode
 */
package com.atlassian.confluence.impl.jsonator;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.DateFormatterFactory;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.json.jsonator.AttachmentJsonator;
import com.atlassian.confluence.json.jsonator.BreadcrumbJsonator;
import com.atlassian.confluence.json.jsonator.ContentMetadataJsonator;
import com.atlassian.confluence.json.jsonator.DefaultJsonator;
import com.atlassian.confluence.json.jsonator.DocumentationLinkJsonator;
import com.atlassian.confluence.json.jsonator.EntityJsonator;
import com.atlassian.confluence.json.jsonator.GsonJsonator;
import com.atlassian.confluence.json.jsonator.Gsonable;
import com.atlassian.confluence.json.jsonator.I18nJsonator;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.confluence.json.jsonator.SearchResultJsonator;
import com.atlassian.confluence.json.jsonator.ValidationAwareJsonator;
import com.atlassian.confluence.json.jsonator.ValidationErrorJsonator;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.confluence.user.UserProfilePictureAccessor;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationLink;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.user.Entity;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
class JsonatorConfig {
    @Resource
    private FormatSettingsManager formatSettingsManager;
    @Resource
    private LocaleManager localeManager;
    @Resource
    private ContextPathHolder contextPathHolder;
    @Resource
    private ThumbnailManager thumbnailManager;
    @Resource
    private I18NBeanFactory userI18NBeanFactory;
    @Resource
    private DateFormatterFactory dateFormatterFactory;
    @Resource
    private UserProfilePictureAccessor userProfilePictureAccessor;
    @Resource
    private DocumentationBean docBean;
    @Resource
    private UserPreferencesAccessor userPreferencesAccessor;

    JsonatorConfig() {
    }

    @Bean
    @Scope(value="prototype", proxyMode=ScopedProxyMode.INTERFACES)
    public Jsonator<?> jsonator() {
        return new DefaultJsonator(this.buildJsonators());
    }

    private Map<Class<?>, Jsonator<?>> buildJsonators() {
        LinkedHashMap jsonators = new LinkedHashMap();
        jsonators.put(Attachment.class, new AttachmentJsonator(this.contextPathHolder, this.thumbnailManager));
        jsonators.put(ContentEntityObject.class, new ContentMetadataJsonator(this.userI18NBeanFactory, this.dateFormatterFactory));
        jsonators.put(Entity.class, new EntityJsonator(this.userProfilePictureAccessor));
        jsonators.put(ValidationError.class, new ValidationErrorJsonator(this.userI18NBeanFactory));
        jsonators.put(Message.class, new I18nJsonator(this.userI18NBeanFactory.getI18NBean()));
        jsonators.put(DocumentationLink.class, new DocumentationLinkJsonator(this.docBean));
        jsonators.put(ValidationAware.class, new ValidationAwareJsonator());
        jsonators.put(Breadcrumb.class, new BreadcrumbJsonator(this.contextPathHolder, this.userI18NBeanFactory));
        jsonators.put(SearchResult.class, new SearchResultJsonator(this.contextPathHolder, this.userI18NBeanFactory, this.userPreferencesAccessor, this.formatSettingsManager, this.localeManager));
        jsonators.put(Gsonable.class, new GsonJsonator());
        return jsonators;
    }
}

