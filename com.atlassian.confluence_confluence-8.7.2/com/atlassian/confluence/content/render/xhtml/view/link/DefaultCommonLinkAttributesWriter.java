/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.Jsoup
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.ModelToRenderedClassMapper;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.util.i18n.UserI18NBeanFactory;
import com.atlassian.confluence.xhtml.api.EmbeddedImageLinkBody;
import com.atlassian.confluence.xhtml.api.Link;
import com.google.common.annotations.VisibleForTesting;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultCommonLinkAttributesWriter
implements CommonLinkAttributesWriter {
    public static final Logger log = LoggerFactory.getLogger(DefaultCommonLinkAttributesWriter.class);
    public static final String SCREEN_READER_NEW_WINDOW_TAG_I18N_KEY = "link.browser.link.openInNewTabAriaLabelTag";
    public static final String OPEN_IN_NEW_WINDOW_DARK_FEATURE = "link.openInNewWindow";
    public static final String NEW_TAB_CLASS = "new-tab";
    private static final String ARIA_LABEL_ATTRIBUTE = "aria-label";
    private final Marshaller<Link> linkBodyMarshaller;
    private final ModelToRenderedClassMapper mapper;
    private final UserI18NBeanFactory userI18NBeanFactory;
    private final DarkFeaturesManager darkFeaturesManager;

    @VisibleForTesting
    DefaultCommonLinkAttributesWriter(Marshaller<Link> linkBodyMarshaller, ModelToRenderedClassMapper mapper, UserI18NBeanFactory userI18NBeanFactory, DarkFeaturesManager darkFeaturesManager) {
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.mapper = Objects.requireNonNull(mapper);
        this.userI18NBeanFactory = Objects.requireNonNull(userI18NBeanFactory);
        this.darkFeaturesManager = Objects.requireNonNull(darkFeaturesManager);
    }

    @Override
    public void writeCommonAttributes(Link link, XMLStreamWriter writer, ConversionContext conversionContext) throws XMLStreamException {
        HashSet<String> classesToAdd = new HashSet<String>();
        this.determineDisplayClass(link, classesToAdd);
        if (this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(OPEN_IN_NEW_WINDOW_DARK_FEATURE)) {
            this.writeTargetRelatedAttributes(link, writer, conversionContext, classesToAdd);
        }
        if (!classesToAdd.isEmpty()) {
            writer.writeAttribute("class", classesToAdd.stream().collect(Collectors.joining(" ")));
        }
    }

    private void writeTargetRelatedAttributes(Link link, XMLStreamWriter writer, ConversionContext conversionContext, Set<String> classesToAdd) throws XMLStreamException {
        if (link.getTarget().isPresent()) {
            writer.writeAttribute("target", link.getTarget().get());
            this.writeAriaLabelAttribute(link, writer, conversionContext);
            if (!(link.getDestinationResourceIdentifier() instanceof UserResourceIdentifier) && !(link.getBody() instanceof EmbeddedImageLinkBody)) {
                classesToAdd.add(NEW_TAB_CLASS);
            }
        }
    }

    private void determineDisplayClass(Link link, Set<String> classesToAdd) {
        String displayClass = this.mapper.getRenderedClass(link);
        if (StringUtils.isNotBlank((CharSequence)displayClass)) {
            classesToAdd.add(displayClass);
        }
    }

    private void writeAriaLabelAttribute(Link link, XMLStreamWriter writer, ConversionContext conversionContext) throws XMLStreamException {
        if (link.getBody() instanceof EmbeddedImageLinkBody) {
            log.trace("Not writing aria-label attribute for link with image body: {}", (Object)link);
            return;
        }
        try {
            String htmlBody = Streamables.writeToString(this.linkBodyMarshaller.marshal(link, conversionContext));
            Optional<String> body = this.stripHtml(Optional.ofNullable(htmlBody));
            if (body.isPresent()) {
                writer.writeAttribute(ARIA_LABEL_ATTRIBUTE, body.get() + " " + this.userI18NBeanFactory.getI18NBean().getText(SCREEN_READER_NEW_WINDOW_TAG_I18N_KEY));
            }
        }
        catch (XhtmlException e) {
            log.warn("Could not write aria-label attribute for link: {}", (Object)link);
        }
    }

    private Optional<String> stripHtml(Optional<String> html) {
        if (html.isPresent()) {
            String text = Jsoup.parse((String)html.get()).text();
            return StringUtils.isBlank((CharSequence)text) ? Optional.empty() : Optional.of(text);
        }
        return Optional.empty();
    }
}

