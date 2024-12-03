/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.History
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate
 *  com.atlassian.confluence.api.model.longtasks.LongTaskId
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.Navigation$ContentNav
 *  com.atlassian.confluence.api.nav.Navigation$ExperimentalContentNav
 *  com.atlassian.confluence.api.nav.Navigation$ExperimentalContentTemplateNav
 *  com.atlassian.confluence.api.nav.Navigation$ExperimentalNav
 *  com.atlassian.confluence.api.nav.Navigation$GroupNav
 *  com.atlassian.confluence.api.nav.Navigation$LongTaskNav
 *  com.atlassian.confluence.api.nav.Navigation$SpaceNav
 *  com.atlassian.confluence.api.nav.Navigation$UserNav
 *  com.atlassian.confluence.rest.api.services.RestNavigation
 *  com.atlassian.confluence.rest.api.services.RestNavigation$RestBuilder
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 *  javax.ws.rs.core.UriBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.plugins.rest.navigation.impl.AbstractNav;
import com.atlassian.confluence.plugins.rest.navigation.impl.BaseNav;
import com.atlassian.confluence.plugins.rest.navigation.impl.ContentNavImpl;
import com.atlassian.confluence.plugins.rest.navigation.impl.ContentTemplateNavImpl;
import com.atlassian.confluence.plugins.rest.navigation.impl.DelegatingPathBuilder;
import com.atlassian.confluence.plugins.rest.navigation.impl.GroupNavBuilderImpl;
import com.atlassian.confluence.plugins.rest.navigation.impl.LongTaskNavImpl;
import com.atlassian.confluence.plugins.rest.navigation.impl.SpaceNavBuilderImpl;
import com.atlassian.confluence.plugins.rest.navigation.impl.UserNavBuilderImpl;
import com.atlassian.confluence.rest.api.services.RestNavigation;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestNavigationImpl
implements RestNavigation {
    private static final String EXPERIMENTAL_PATH = "/rest/experimental";
    private String baseURL;
    private String contextPath;
    private Logger log = LoggerFactory.getLogger(RestNavigationImpl.class);

    public RestNavigationImpl(String baseUrl, String contextPath) {
        this.baseURL = baseUrl;
        this.contextPath = contextPath;
    }

    public Navigation.ExperimentalNav experimental() {
        return new Experimental(this.baseURL, this.contextPath);
    }

    public Navigation.ContentNav content(Content content) {
        return ContentNavImpl.build(content, (AbstractNav)this.baseApiPath());
    }

    public Navigation.ContentNav content(Reference<Content> contentReference) {
        return this.content(Content.getSelector(contentReference));
    }

    public Navigation.ContentNav content(ContentId contentId) {
        Preconditions.checkNotNull((Object)contentId);
        return this.content(ContentSelector.fromId((ContentId)contentId));
    }

    public Navigation.ContentNav content(ContentSelector selector) {
        Preconditions.checkNotNull((Object)selector);
        return ContentNavImpl.build(selector, (AbstractNav)this.baseApiPath());
    }

    public BaseApiPathBuilder baseApiPath() {
        return new BaseApiPathBuilder(this.baseURL, this.contextPath);
    }

    public String baseUrl() {
        return this.baseURL;
    }

    public String contextPath() {
        return this.contextPath;
    }

    public RestNavigation.RestBuilder fromUriBuilder(final UriBuilder uriBuilder) {
        return new RestNavigation.RestBuilder(){

            public String buildRelativeWithContext() {
                throw new UnsupportedOperationException("buildRelativeWithContext not implemented for uriBuilder");
            }

            public String buildRelative() {
                URI absoluteUri = uriBuilder.build(new Object[0]);
                String basePath = URI.create(RestNavigationImpl.this.baseURL).getPath();
                return UriBuilder.fromPath((String)absoluteUri.getPath().substring(basePath.length())).replaceQuery(absoluteUri.getRawQuery()).fragment(absoluteUri.getFragment()).build(new Object[0]).toString();
            }

            public String buildAbsolute() {
                return uriBuilder.build(null).toString();
            }

            public String buildCanonicalAbsolute() {
                return RestNavigationImpl.this.baseURL + this.buildRelative();
            }

            public UriBuilder toAbsoluteUriBuilder() {
                return uriBuilder;
            }
        };
    }

    public Navigation.Builder fromReference(Reference reference) {
        Class referentClass = reference.referentClass();
        if (referentClass.equals(Space.class)) {
            return this.space((Reference<Space>)reference);
        }
        if (referentClass.equals(Content.class)) {
            return this.content((Reference<Content>)reference);
        }
        if (referentClass.equals(History.class)) {
            return this.content((Reference<Content>)History.getParentReference((Reference)reference)).history();
        }
        this.log.debug("Cannot create navigation from reference, unrecognised referentClass " + referentClass);
        return Navigation.Builder.NONE;
    }

    public Navigation.Builder collection(Content content) {
        return new DelegatingPathBuilder("/content", this.baseApiPath());
    }

    public Navigation.Builder collection(Space spaceData) {
        return new DelegatingPathBuilder("/space", this.baseApiPath());
    }

    public Navigation.SpaceNav space(Space space) {
        return new SpaceNavBuilderImpl(space.getKey(), this.baseApiPath());
    }

    public Navigation.SpaceNav space(Reference<Space> space) {
        return new SpaceNavBuilderImpl(Space.getSpaceKey(space), this.baseApiPath());
    }

    public Navigation.GroupNav group(Group group) {
        return new GroupNavBuilderImpl(group, (AbstractNav)this.baseApiPath());
    }

    public Navigation.UserNav user(UserKey userKey) {
        return new UserNavBuilderImpl(userKey, (AbstractNav)this.baseApiPath());
    }

    public Navigation.LongTaskNav longTask(LongTaskId id) {
        return new LongTaskNavImpl(id, this.baseApiPath());
    }

    private static class Experimental
    extends BaseApiPathBuilder
    implements Navigation.ExperimentalNav {
        public Experimental(String baseUrl, String context) {
            super(baseUrl, context);
        }

        public Navigation.ExperimentalContentNav content(Content content) {
            return this.content(content.getSelector());
        }

        public Navigation.ExperimentalContentNav content(Reference<Content> contentReference) {
            return this.content(Content.getSelector(contentReference));
        }

        public Navigation.ExperimentalContentNav content(ContentId contentId) {
            Preconditions.checkNotNull((Object)contentId);
            return this.content(ContentSelector.fromId((ContentId)contentId));
        }

        public Navigation.ExperimentalContentNav content(ContentSelector selector) {
            Preconditions.checkNotNull((Object)selector);
            return ContentNavImpl.build(selector, (AbstractNav)this);
        }

        public Navigation.UserNav user(UserKey userKey) {
            return new UserNavBuilderImpl(userKey, (AbstractNav)this);
        }

        public Navigation.Builder group(Group group) {
            Preconditions.checkNotNull((Object)group);
            return new GroupNavBuilderImpl(group, (AbstractNav)this);
        }

        public Navigation.ExperimentalContentTemplateNav template(ContentTemplate contentTemplate) {
            Preconditions.checkNotNull((Object)contentTemplate);
            return new ContentTemplateNavImpl(contentTemplate, (AbstractNav)this);
        }

        @Override
        protected AbstractNav copy() {
            Experimental clone = new Experimental(this.baseUrl, this.contextPath);
            this.cloneAttributes(clone);
            return clone;
        }

        @Override
        protected String buildPath() {
            return RestNavigationImpl.EXPERIMENTAL_PATH;
        }
    }

    static class BaseApiPathBuilder
    extends BaseNav {
        public BaseApiPathBuilder(String baseUrl, String contextPath) {
            super(baseUrl, contextPath);
        }

        @Override
        protected String buildPath() {
            return "/rest/api";
        }

        @Override
        protected AbstractNav copy() {
            BaseApiPathBuilder clone = new BaseApiPathBuilder(this.baseUrl, this.contextPath);
            this.cloneAttributes(clone);
            return clone;
        }

        protected void cloneAttributes(BaseApiPathBuilder clone) {
            this.getParams().entrySet().forEach(entry -> clone.addParam((String)entry.getKey(), entry.getValue()));
            clone.setAnchor(this.getAnchor());
        }
    }
}

