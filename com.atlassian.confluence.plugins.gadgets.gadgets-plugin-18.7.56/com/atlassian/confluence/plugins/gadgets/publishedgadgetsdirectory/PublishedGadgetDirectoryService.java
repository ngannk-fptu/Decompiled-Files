/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.DocumentationBean
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetSpecProvider
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory;

import com.atlassian.confluence.plugins.gadgets.events.GadgetDirectoryRetrievalEvent;
import com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory.PublishedGadgetBean;
import com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory.TranslationBean;
import com.atlassian.confluence.plugins.gadgets.requestcontext.RequestContextBuilder;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetSpecProvider;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishedGadgetDirectoryService {
    private static final Logger log = LoggerFactory.getLogger(PublishedGadgetDirectoryService.class);
    private final GadgetSpecProvider gadgetSpecProvider;
    private final GadgetSpecFactory gadgetSpecFactory;
    private final RequestContextBuilder requestContextBuilder;
    private final ApplicationProperties applicationProperties;
    private final I18nResolver i18nResolver;
    private final DocumentationBeanFactory docBeanFactory;
    private final EventPublisher eventPublisher;

    public PublishedGadgetDirectoryService(GadgetSpecProvider gadgetSpecProvider, GadgetSpecFactory gadgetSpecFactory, RequestContextBuilder requestContextBuilder, ApplicationProperties applicationProperties, I18nResolver i18nResolver, DocumentationBeanFactory docBeanFactory, EventPublisher eventPublisher) {
        this.gadgetSpecProvider = gadgetSpecProvider;
        this.gadgetSpecFactory = gadgetSpecFactory;
        this.requestContextBuilder = requestContextBuilder;
        this.applicationProperties = applicationProperties;
        this.i18nResolver = i18nResolver;
        this.docBeanFactory = docBeanFactory;
        this.eventPublisher = eventPublisher;
    }

    List<PublishedGadgetBean> getGadgetData() {
        ArrayList<PublishedGadgetBean> data = new ArrayList<PublishedGadgetBean>();
        HashSet<GadgetSpec> specs = new HashSet<GadgetSpec>();
        HashSet<URI> uris = new HashSet<URI>();
        for (URI uri : this.gadgetSpecProvider.entries()) {
            uris.add(uri);
        }
        this.eventPublisher.publish((Object)new GadgetDirectoryRetrievalEvent(uris.size()));
        GadgetRequestContext requestContext = this.requestContextBuilder.buildRequestContext(false);
        for (URI uri : uris) {
            try {
                specs.add(this.gadgetSpecFactory.getGadgetSpec(uri, requestContext));
            }
            catch (GadgetParsingException e) {
                log.warn("Unable to parse gadget spec [ " + uri + " ]", (Throwable)e);
            }
        }
        for (GadgetSpec spec : specs) {
            String title = spec.getDirectoryTitle();
            if (StringUtils.isBlank((CharSequence)title)) {
                title = spec.getTitle();
            }
            String thumbnailUrl = null;
            URI thumbnail = spec.getThumbnail();
            if (thumbnail != null) {
                thumbnailUrl = thumbnail.toString();
            }
            String description = spec.getDescription();
            String author = spec.getAuthor();
            Object baseUrl = this.applicationProperties.getBaseUrl();
            if (!((String)baseUrl).endsWith("/")) {
                baseUrl = (String)baseUrl + "/";
            }
            String url = (String)baseUrl + spec.getUrl().toString();
            data.add(new PublishedGadgetBean(title, description, author, url, thumbnailUrl));
        }
        Collections.sort(data);
        return data;
    }

    TranslationBean getTranslations() {
        TranslationBean translations = new TranslationBean();
        DocumentationBean documentationBean = this.docBeanFactory.getDocumentationBean();
        translations.setCloseButton(this.i18nResolver.getText("gadget.directory.close.button"));
        translations.setHeader(this.i18nResolver.getText("gadget.directory.title"));
        translations.setHelpTitle1(this.i18nResolver.getText("gadget.directory.help.title.1"));
        translations.setHelpBody1(this.i18nResolver.getText("gadget.directory.help.body.1"));
        translations.setHelpTitle2(this.i18nResolver.getText("gadget.directory.help.title.2"));
        translations.setHelpBody2(this.i18nResolver.getText("gadget.directory.help.body.2"));
        String gadgetConfigDocUrl = documentationBean.getLink("help.gadgets.configuring");
        translations.setMoreInfo(this.i18nResolver.getText("gadget.directory.help.more.info", new Serializable[]{gadgetConfigDocUrl}));
        translations.setGadgetUrl(this.i18nResolver.getText("gadget.directory.gadget.url"));
        translations.setNoAuthor(this.i18nResolver.getText("gadget.directory.no.author"));
        translations.setNoDescription(this.i18nResolver.getText("gadget.directory.no.description"));
        return translations;
    }
}

