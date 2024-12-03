/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.util.UrlUtil
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.Jsoup
 */
package com.atlassian.confluence.content.render.xhtml.view;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.links.WebLink;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.LegacyFragmentTransformer;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.i18n.UserI18NBeanFactory;
import com.atlassian.renderer.util.UrlUtil;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

public class ViewHtmlAnchorFragmentTransformer
extends LegacyFragmentTransformer
implements FragmentTransformer {
    private static final QName REL_ATTRIBUTE = new QName("rel");
    private static final QName HREF_ATTRIBUTE = new QName("href");
    private static final QName CLASS_ATTRIBUTE = new QName("class");
    private static final QName TARGET_ATTRIBUTE = new QName("target");
    private static final QName ARIA_LABEL_ATTRIBUTE = new QName("aria-label");
    private static final String EXTERNAL_LINK = "external-link";
    private static final Pattern EMBEDDED_IMAGE_REGEX = Pattern.compile("^.*<img[^>]*class\\s*=\\s*[\"'][^[\"']]*confluence-embedded-image.*$", 2);
    private final SettingsManager settingsManager;
    private final XMLOutputFactory xmlOutputFactory;
    private final XMLEventFactory xmlEventFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final HrefEvaluator hrefEvaluator;
    private final UserI18NBeanFactory userI18NBeanFactory;
    private final DarkFeaturesManager darkFeaturesManager;

    public ViewHtmlAnchorFragmentTransformer(SettingsManager settingsManager, XMLOutputFactory xmlOutputFactory, XMLEventFactory xmlEventFactory, XmlEventReaderFactory xmlEventReaderFactory, HrefEvaluator hrefEvaluator, UserI18NBeanFactory userI18NBeanFactory, DarkFeaturesManager darkFeaturesManager) {
        this.settingsManager = Objects.requireNonNull(settingsManager);
        this.xmlOutputFactory = Objects.requireNonNull(xmlOutputFactory);
        this.xmlEventFactory = Objects.requireNonNull(xmlEventFactory);
        this.xmlEventReaderFactory = Objects.requireNonNull(xmlEventReaderFactory);
        this.hrefEvaluator = Objects.requireNonNull(hrefEvaluator);
        this.userI18NBeanFactory = Objects.requireNonNull(userI18NBeanFactory);
        this.darkFeaturesManager = Objects.requireNonNull(darkFeaturesManager);
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        String defaultNamespace = startElementEvent.getNamespaceContext().getNamespaceURI("");
        return new QName(defaultNamespace, "a").equals(startElementEvent.getName());
    }

    @Override
    public String transformToString(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        StringWriter result = new StringWriter();
        XMLEventWriter xmlEventWriter = null;
        try {
            xmlEventWriter = this.xmlOutputFactory.createXMLEventWriter(result);
            StartElement startElement = reader.peek().asStartElement();
            ArrayList<Attribute> attributesToWrite = new ArrayList<Attribute>();
            HashSet<String> classesToAdd = new HashSet<String>();
            HashSet<String> relContent = new HashSet<String>();
            boolean addExternalClassAttribute = false;
            Iterator<Attribute> attributesIterator = startElement.getAttributes();
            Optional<String> body = this.transformBodyFragment(reader, mainFragmentTransformer, conversionContext);
            while (attributesIterator.hasNext()) {
                Attribute attribute = attributesIterator.next();
                if (HREF_ATTRIBUTE.equals(attribute.getName())) {
                    String baseURL;
                    if (UrlUtil.getUrlIndex((String)attribute.getValue()) != -1) {
                        baseURL = this.settingsManager.getGlobalSettings().getBaseUrl();
                        if (!attribute.getValue().contains(baseURL)) {
                            addExternalClassAttribute = true;
                        }
                    } else if (attribute.getValue().startsWith("www")) {
                        baseURL = this.settingsManager.getGlobalSettings().getBaseUrl();
                        if (!attribute.getValue().contains(baseURL)) {
                            addExternalClassAttribute = true;
                        }
                    }
                    attribute = this.modifyHrefForContext(attribute, conversionContext);
                } else if (TARGET_ATTRIBUTE.equals(attribute.getName())) {
                    if (!this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled("link.openInNewWindow")) continue;
                    this.maybeAddNewTabClass(body, classesToAdd);
                    this.maybeAddAriaLabelAttribute(body, attributesToWrite);
                    relContent.add("noopener");
                    relContent.add("noreferrer");
                }
                attributesToWrite.add(attribute);
            }
            if (addExternalClassAttribute) {
                classesToAdd.add(EXTERNAL_LINK);
            }
            if (this.settingsManager.getGlobalSettings().isNofollowExternalLinks() && startElement.getAttributeByName(REL_ATTRIBUTE) == null) {
                relContent.add("nofollow");
            }
            if (!classesToAdd.isEmpty()) {
                attributesToWrite.add(this.xmlEventFactory.createAttribute(CLASS_ATTRIBUTE, classesToAdd.stream().collect(Collectors.joining(" "))));
            }
            if (!relContent.isEmpty()) {
                attributesToWrite.add(this.xmlEventFactory.createAttribute("rel", relContent.stream().collect(Collectors.joining(" "))));
            }
            xmlEventWriter.add(this.xmlEventFactory.createStartElement(startElement.getName(), attributesToWrite.iterator(), null));
            StaxUtils.flushEventWriter(xmlEventWriter);
            if (body.isPresent()) {
                result.append(body.get());
            }
            xmlEventWriter.add(this.xmlEventFactory.createEndElement(startElement.getName(), null));
        }
        catch (XMLStreamException e) {
            try {
                throw new XhtmlException(e);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(xmlEventWriter);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly(xmlEventWriter);
        return result.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Optional<String> transformBodyFragment(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        try (XMLEventReader fragmentReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader);){
            String transformOutput = Streamables.writeToString(mainFragmentTransformer.transform(fragmentReader, mainFragmentTransformer, conversionContext));
            if (StringUtils.isNotBlank((CharSequence)transformOutput)) {
                Optional<String> optional = Optional.of(transformOutput);
                return optional;
            }
        }
        return Optional.empty();
    }

    private Attribute modifyHrefForContext(Attribute href, ConversionContext context) {
        WebLink webLink = new WebLink(href.getValue());
        String newHref = this.hrefEvaluator.createHref(context, webLink, null);
        return this.xmlEventFactory.createAttribute(HREF_ATTRIBUTE, newHref);
    }

    private void maybeAddNewTabClass(Optional<String> body, Set<String> classesToAdd) {
        if (!body.isPresent() || !EMBEDDED_IMAGE_REGEX.matcher(body.get()).matches()) {
            classesToAdd.add("new-tab");
        }
    }

    private void maybeAddAriaLabelAttribute(Optional<String> body, List<Attribute> attributesToWrite) {
        String noHtmlBody;
        if (body.isPresent() && StringUtils.isNotBlank((CharSequence)(noHtmlBody = Jsoup.parse((String)body.get()).text()))) {
            Attribute ariaLabel = this.xmlEventFactory.createAttribute(ARIA_LABEL_ATTRIBUTE, noHtmlBody + " " + this.userI18NBeanFactory.getI18NBean().getText("link.browser.link.openInNewTabAriaLabelTag"));
            attributesToWrite.add(ariaLabel);
        }
    }
}

