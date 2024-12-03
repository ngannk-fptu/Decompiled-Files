/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewLinkMarshallerMetricsKey;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollectors;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MetricsCollectingMarshaller;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.xhtml.api.Link;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViewUserLinkMarshaller
implements Marshaller<Link> {
    private static final ViewLinkMarshallerMetricsKey METRICS_ACCUMULATION_KEY = new ViewLinkMarshallerMetricsKey("userLink");
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final ConfluenceUserResolver confluenceUserResolver;
    private final StaxStreamMarshaller<UserResourceIdentifier> resourceIdentifierStaxStreamMarshaller;
    private final StaxStreamMarshaller<Link> linkStaxStreamMarshaller;
    private final CommonLinkAttributesWriter commonLinkAttributesWriter;
    private final Marshaller<Link> linkBodyMarshaller;
    private final Marshaller<Link> unresolvedLinkMarshaller;
    private final PersonalInformationManager personalInformationManager;
    private final HrefEvaluator hrefEvaluator;
    private final GlobalSettingsManager settingsManager;

    public ViewUserLinkMarshaller(ConfluenceUserResolver confluenceUserResolver, XmlStreamWriterTemplate xmlStreamWriterTemplate, CommonLinkAttributesWriter commonLinkAttributesWriter, Marshaller<Link> linkBodyMarshaller, Marshaller<Link> unresolvedLinkMarshaller, @Nullable StaxStreamMarshaller<UserResourceIdentifier> resourceIdentifierStaxStreamMarshaller, @Nullable StaxStreamMarshaller<Link> linkStaxStreamMarshaller, PersonalInformationManager personalInformationManager, HrefEvaluator hrefEvaluator, GlobalSettingsManager settingsManager) {
        this.commonLinkAttributesWriter = (CommonLinkAttributesWriter)Preconditions.checkNotNull((Object)commonLinkAttributesWriter);
        this.xmlStreamWriterTemplate = (XmlStreamWriterTemplate)Preconditions.checkNotNull((Object)xmlStreamWriterTemplate);
        this.confluenceUserResolver = (ConfluenceUserResolver)Preconditions.checkNotNull((Object)confluenceUserResolver);
        this.resourceIdentifierStaxStreamMarshaller = resourceIdentifierStaxStreamMarshaller;
        this.linkStaxStreamMarshaller = linkStaxStreamMarshaller;
        this.linkBodyMarshaller = (Marshaller)Preconditions.checkNotNull(linkBodyMarshaller);
        this.unresolvedLinkMarshaller = (Marshaller)Preconditions.checkNotNull(unresolvedLinkMarshaller);
        this.personalInformationManager = (PersonalInformationManager)Preconditions.checkNotNull((Object)personalInformationManager);
        this.hrefEvaluator = (HrefEvaluator)Preconditions.checkNotNull((Object)hrefEvaluator);
        this.settingsManager = (GlobalSettingsManager)Preconditions.checkNotNull((Object)settingsManager);
    }

    @Override
    public Streamable marshal(Link userLink, ConversionContext conversionContext) throws XhtmlException {
        MarshallerMetricsCollector metricsCollector = MarshallerMetricsCollectors.metricsCollector(conversionContext, METRICS_ACCUMULATION_KEY);
        Marshaller<Link> timedMarshaller = MetricsCollectingMarshaller.forMarshaller(metricsCollector, this.marshalInternal());
        return timedMarshaller.marshal(userLink, conversionContext);
    }

    private Marshaller<Link> marshalInternal() {
        return this::marshalInternal;
    }

    private Streamable marshalInternal(Link userLink, ConversionContext conversionContext) throws XhtmlException {
        UserResourceIdentifier userResourceIdentifier = (UserResourceIdentifier)userLink.getDestinationResourceIdentifier();
        ConfluenceUser user = this.confluenceUserResolver.getUserByKey(userResourceIdentifier.getUserKey());
        if (user == null) {
            return this.unresolvedLink(userLink, conversionContext);
        }
        Streamable marshalledLinkBody = this.linkBodyMarshaller.marshal(userLink, conversionContext);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> this.writeLink(xmlStreamWriter, underlyingWriter, userLink, user, conversionContext, userResourceIdentifier, marshalledLinkBody));
    }

    private Streamable unresolvedLink(Link userLink, ConversionContext conversionContext) throws XhtmlException {
        return this.unresolvedLinkMarshaller.marshal(new UnresolvedLink(userLink), conversionContext);
    }

    private void writeLink(XMLStreamWriter xmlStreamWriter, Writer underlyingWriter, Link userLink, ConfluenceUser user, ConversionContext conversionContext, UserResourceIdentifier userResourceIdentifier, Streamable marshalledLinkBody) throws XMLStreamException, IOException {
        xmlStreamWriter.writeStartElement("a");
        this.commonLinkAttributesWriter.writeCommonAttributes(userLink, xmlStreamWriter, conversionContext);
        xmlStreamWriter.writeAttribute("data-username", user.getName());
        xmlStreamWriter.writeAttribute("href", this.hrefEvaluator.createHref(conversionContext, user, null));
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation(user);
        xmlStreamWriter.writeAttribute("data-linked-resource-id", personalInformation.getIdAsString());
        xmlStreamWriter.writeAttribute("data-linked-resource-version", String.valueOf(personalInformation.getVersion()));
        xmlStreamWriter.writeAttribute("data-linked-resource-type", "userinfo");
        if (this.resourceIdentifierStaxStreamMarshaller != null) {
            this.resourceIdentifierStaxStreamMarshaller.marshal(userResourceIdentifier, xmlStreamWriter, conversionContext);
        }
        if (this.linkStaxStreamMarshaller != null) {
            this.linkStaxStreamMarshaller.marshal(userLink, xmlStreamWriter, conversionContext);
        } else {
            xmlStreamWriter.writeAttribute("data-base-url", this.settingsManager.getGlobalSettings().getBaseUrl());
        }
        StaxUtils.writeRawXML(xmlStreamWriter, underlyingWriter, marshalledLinkBody);
        xmlStreamWriter.writeEndElement();
    }
}

