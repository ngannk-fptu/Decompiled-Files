/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostsForDateResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.WikiLinkBasedResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroId;
import com.atlassian.confluence.content.render.xhtml.storage.macro.PlainTextMacroBodySubParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroBodyParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageMacroV2Unmarshaller
implements Unmarshaller<MacroDefinition> {
    private static final Logger log = LoggerFactory.getLogger(StorageMacroV2Unmarshaller.class);
    private static final QName ANCHOR_ELEMENT = new QName("http://www.w3.org/1999/xhtml", "a");
    private static final QName HREF_ATTRIBUTE = new QName("", "href");
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final MacroMetadataManager macroMetadataManager;
    private final MacroManager macroManager;
    private final Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller;
    private final Unmarshaller<Link> linkUnmarshaller;
    private final StorageMacroBodyParser storageMacroBodyParser;

    public StorageMacroV2Unmarshaller(XmlEventReaderFactory xmlEventReaderFactory, MacroMetadataManager macroMetadataManager, Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller, Unmarshaller<Link> linkUnmarshaller, StorageMacroBodyParser storageMacroBodyParser, MacroManager macroManager) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.macroMetadataManager = macroMetadataManager;
        this.macroManager = macroManager;
        this.resourceIdentifierUnmarshaller = resourceIdentifierUnmarshaller;
        this.linkUnmarshaller = linkUnmarshaller;
        this.storageMacroBodyParser = storageMacroBodyParser;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return StorageMacroConstants.MACRO_V2_ELEMENT.equals(startElementEvent.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MacroDefinition unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StartElement macroElementEvent = (StartElement)reader.nextEvent();
            Attribute macroNameAttribute = macroElementEvent.getAttributeByName(StorageMacroConstants.NAME_ATTRIBUTE);
            String macroName = macroNameAttribute.getValue();
            Optional<MacroId> macroUuid = this.unmarshalMacroId(macroElementEvent);
            int schemaVersion = this.unmarshallSchemaVersion(macroElementEvent);
            Map<String, MacroParameter> parametersMetadata = this.macroMetadataManager.getParameters(macroName);
            MacroBody macroBody = null;
            HashMap<String, String> macroParameters = new HashMap<String, String>();
            HashMap<String, Object> typedMacroParameters = new HashMap<String, Object>();
            Macro.BodyType bodyType = Macro.BodyType.RICH_TEXT;
            Macro macro = this.macroManager.getMacroByName(macroName);
            if (macro != null) {
                bodyType = macro.getBodyType();
            }
            while (reader.hasNext()) {
                if (reader.peek().isStartElement()) {
                    XMLEventReader bodyReader;
                    StartElement startElement = reader.peek().asStartElement();
                    if (StorageMacroConstants.MACRO_PARAMETER_ELEMENT.equals(startElement.getName())) {
                        Attribute nameAttribute = startElement.getAttributeByName(StorageMacroConstants.NAME_ATTRIBUTE);
                        String parameterName = nameAttribute.getValue();
                        MacroParameter parameterMetadata = parametersMetadata.get(parameterName);
                        String parameterValue = this.parseV2Parameter(reader, mainFragmentTransformer, conversionContext, parameterMetadata, typedMacroParameters, parameterName);
                        macroParameters.put(parameterName, parameterValue);
                        continue;
                    }
                    if (StorageMacroConstants.DEFAULT_PARAMETER_ELEMENT.equals(startElement.getName())) {
                        MacroParameter parameterMetadata = parametersMetadata.get("");
                        String parameterValue = this.parseV2Parameter(reader, mainFragmentTransformer, conversionContext, parameterMetadata, typedMacroParameters, "");
                        macroParameters.put("", parameterValue);
                        continue;
                    }
                    if (StorageMacroConstants.PLAIN_TEXT_BODY_PARAMETER_ELEMENT.equals(startElement.getName()) && Macro.BodyType.PLAIN_TEXT.equals((Object)bodyType)) {
                        bodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader);
                        try {
                            macroBody = PlainTextMacroBodySubParser.parse(bodyReader);
                            continue;
                        }
                        finally {
                            StaxUtils.closeQuietly(bodyReader);
                            continue;
                        }
                    }
                    if (StorageMacroConstants.RICH_TEXT_BODY_PARAMETER_ELEMENT.equals(startElement.getName()) && Macro.BodyType.RICH_TEXT.equals((Object)bodyType)) {
                        bodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader);
                        try {
                            macroBody = this.storageMacroBodyParser.getMacroBody(macroName, bodyReader, conversionContext, mainFragmentTransformer);
                            continue;
                        }
                        finally {
                            StaxUtils.closeQuietly(bodyReader);
                            continue;
                        }
                    }
                    reader.nextEvent();
                    continue;
                }
                reader.nextEvent();
            }
            macroParameters.putAll(this.computeDerivedParams(this.getDerivedParams(parametersMetadata), parametersMetadata, typedMacroParameters));
            return MacroDefinition.builder(macroName).withMacroBody(macroBody).withParameters(macroParameters).withTypedParameters(typedMacroParameters).withStorageVersion("2").withMacroIdentifier((MacroId)macroUuid.orElse(null)).withSchemaVersion(schemaVersion).build();
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException(ex);
        }
    }

    private int unmarshallSchemaVersion(StartElement macroElementEvent) throws XhtmlException {
        Attribute schemaVersionAttribute = macroElementEvent.getAttributeByName(StorageMacroConstants.MACRO_SCHEMA_VERSION_ATTRIBUTE);
        if (schemaVersionAttribute != null) {
            try {
                return Integer.parseInt(schemaVersionAttribute.getValue());
            }
            catch (NumberFormatException ex) {
                throw new XhtmlException("Could not parse schema version as int", ex);
            }
        }
        return 1;
    }

    private Collection<String> getDerivedParams(Map<String, MacroParameter> paramsMetadata) {
        return Collections2.filter(paramsMetadata.keySet(), s -> paramsMetadata.get(s) != null && ((MacroParameter)paramsMetadata.get(s)).getOptions() != null && ((MacroParameter)paramsMetadata.get(s)).getOptions().get("derived") != null);
    }

    private Map<String, String> computeDerivedParams(Collection<String> derivedParamList, Map<String, MacroParameter> paramsMetadata, Map<String, Object> typedMacroParameters) {
        HashMap<String, String> macroParameters = new HashMap<String, String>();
        for (String derivedParamName : derivedParamList) {
            String derivedParamValue = this.computeSingleDerivedParam(derivedParamName, paramsMetadata, typedMacroParameters);
            if (derivedParamValue == null) continue;
            macroParameters.put(derivedParamName, derivedParamValue);
        }
        return macroParameters;
    }

    private String computeSingleDerivedParam(String derivedParamName, Map<String, MacroParameter> paramsMetadata, Map<String, Object> typedMacroParameters) {
        String derivedParamValue = null;
        MacroParameter derivedParamMeta = paramsMetadata.get(derivedParamName);
        String sourceParamName = (String)derivedParamMeta.getOptions().get("derived");
        MacroParameter sourceParamMeta = paramsMetadata.get(sourceParamName);
        if (sourceParamMeta.getType() == MacroParameterType.FULL_ATTACHMENT) {
            derivedParamValue = this.computeDerivedParamFromFullAttachment(derivedParamMeta, sourceParamMeta, typedMacroParameters);
        }
        return derivedParamValue;
    }

    private String computeDerivedParamFromFullAttachment(MacroParameter derivedParamMeta, MacroParameter sourceParamMeta, Map<String, Object> typedMacroParameters) {
        Calendar postingDay;
        String derivedParamValue = null;
        AttachmentResourceIdentifier attachmentRI = (AttachmentResourceIdentifier)typedMacroParameters.get(sourceParamMeta.getName());
        AttachmentContainerResourceIdentifier container = attachmentRI.getAttachmentContainerResourceIdentifier();
        if (derivedParamMeta.getType() == MacroParameterType.CONFLUENCE_CONTENT) {
            if (container instanceof PageResourceIdentifier) {
                derivedParamValue = ((PageResourceIdentifier)container).getTitle();
            } else if (container instanceof BlogPostResourceIdentifier) {
                derivedParamValue = ((BlogPostResourceIdentifier)container).getTitle();
            }
        } else if (derivedParamMeta.getType() == MacroParameterType.SPACE_KEY) {
            if (container instanceof PageResourceIdentifier) {
                derivedParamValue = ((PageResourceIdentifier)container).getSpaceKey();
            } else if (container instanceof BlogPostResourceIdentifier) {
                derivedParamValue = ((BlogPostResourceIdentifier)container).getSpaceKey();
            }
        } else if (derivedParamMeta.getType() == MacroParameterType.DATE && container instanceof BlogPostResourceIdentifier && (postingDay = ((BlogPostResourceIdentifier)container).getPostingDay()) != null) {
            derivedParamValue = XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(postingDay.getTime());
        }
        return derivedParamValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String parseV2Parameter(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext, @Nullable MacroParameter parameterMetadata, Map<String, Object> typedMacroParameters, String parameterName) throws XMLStreamException, XhtmlException {
        XMLEventReader parameterBodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader);
        try {
            if (!parameterBodyReader.hasNext()) {
                String string = "";
                return string;
            }
            StringBuilder leadingWhitespace = new StringBuilder();
            if (!parameterBodyReader.peek().isStartElement()) {
                StaxUtils.collectWhitespace(leadingWhitespace, parameterBodyReader);
            }
            if (parameterBodyReader.hasNext() && parameterBodyReader.peek().isStartElement()) {
                StartElement startElement = parameterBodyReader.peek().asStartElement();
                if (startElement.getName().equals(ANCHOR_ELEMENT)) {
                    String string = this.parseAnchorElements(conversionContext, parameterMetadata, typedMacroParameters, parameterName, parameterBodyReader);
                    return string;
                }
                if (this.linkUnmarshaller.handles(startElement, conversionContext)) {
                    String string = this.parseLinkElements(mainFragmentTransformer, conversionContext, parameterMetadata, typedMacroParameters, parameterName, parameterBodyReader);
                    return string;
                }
                if (this.resourceIdentifierUnmarshaller.handles(startElement, conversionContext)) {
                    String string = this.parseResourceIdentifierElements(mainFragmentTransformer, conversionContext, parameterMetadata, typedMacroParameters, parameterName, parameterBodyReader);
                    return string;
                }
                throw new XhtmlException(String.format("Unhandled element type '%s'", startElement.getName()));
            }
            String string = this.parseStringParameter(parameterBodyReader, typedMacroParameters, parameterName, leadingWhitespace.toString());
            return string;
        }
        finally {
            StaxUtils.closeQuietly(parameterBodyReader);
        }
    }

    private String parseResourceIdentifierElements(FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext, MacroParameter parameterMetadata, Map<String, Object> typedMacroParameters, String parameterName, XMLEventReader parameterBodyReader) throws XMLStreamException, XhtmlException {
        ResourceIdentifier resourceIdentifier;
        ArrayList<ResourceIdentifier> resourceIdentifiers = new ArrayList<ResourceIdentifier>();
        while (parameterBodyReader.hasNext() && parameterBodyReader.peek().isStartElement()) {
            resourceIdentifier = this.resourceIdentifierUnmarshaller.unmarshal(parameterBodyReader, mainFragmentTransformer, conversionContext);
            resourceIdentifiers.add(resourceIdentifier);
            StaxUtils.skipWhitespace(parameterBodyReader);
        }
        if (resourceIdentifiers.isEmpty()) {
            return null;
        }
        if (resourceIdentifiers.size() == 1) {
            resourceIdentifier = (ResourceIdentifier)resourceIdentifiers.get(0);
            typedMacroParameters.put(parameterName, resourceIdentifier);
            return this.toSimpleString(resourceIdentifier);
        }
        typedMacroParameters.put(parameterName, resourceIdentifiers);
        return this.toSimpleString(resourceIdentifiers, parameterMetadata);
    }

    private String parseLinkElements(FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext, MacroParameter parameterMetadata, Map<String, Object> typedMacroParameters, String parameterName, XMLEventReader parameterBodyReader) throws XMLStreamException, XhtmlException {
        Link link;
        ArrayList<Link> links = new ArrayList<Link>();
        while (parameterBodyReader.hasNext() && parameterBodyReader.peek().isStartElement()) {
            link = this.linkUnmarshaller.unmarshal(parameterBodyReader, mainFragmentTransformer, conversionContext);
            links.add(link);
            StaxUtils.skipWhitespace(parameterBodyReader);
        }
        if (links.isEmpty()) {
            return null;
        }
        if (links.size() == 1) {
            link = (Link)links.get(0);
            typedMacroParameters.put(parameterName, link);
            return this.toWikiLink(link, conversionContext);
        }
        typedMacroParameters.put(parameterName, links);
        return this.toWikiLink(links, conversionContext, parameterMetadata);
    }

    private String parseAnchorElements(ConversionContext conversionContext, MacroParameter parameterMetadata, Map<String, Object> typedMacroParameters, String parameterName, XMLEventReader parameterBodyReader) throws XMLStreamException, XhtmlException {
        Link link;
        ArrayList<Link> links = new ArrayList<Link>();
        while (parameterBodyReader.hasNext() && parameterBodyReader.peek().isStartElement()) {
            link = this.parseAnchorElement(parameterBodyReader);
            links.add(link);
            StaxUtils.skipWhitespace(parameterBodyReader);
        }
        if (links.isEmpty()) {
            return null;
        }
        if (links.size() == 1) {
            link = (Link)links.get(0);
            typedMacroParameters.put(parameterName, link);
            return this.toWikiLink(link, conversionContext);
        }
        typedMacroParameters.put(parameterName, links);
        return this.toWikiLink(links, conversionContext, parameterMetadata);
    }

    private Link parseAnchorElement(XMLEventReader parameterBodyReader) throws XMLStreamException {
        StartElement startElement = parameterBodyReader.nextEvent().asStartElement();
        Attribute hrefAttribute = startElement.getAttributeByName(HREF_ATTRIBUTE);
        if (hrefAttribute == null) {
            log.debug("No href attribute for anchor element - probably squashed by antisamy");
            throw new XMLStreamException("Missing href attribute in anchor element");
        }
        String href = hrefAttribute.getValue();
        XMLEventReader anchorBodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(parameterBodyReader);
        StaxUtils.closeQuietly(anchorBodyReader);
        return new DefaultLink(new UrlResourceIdentifier(href), null);
    }

    private String parseStringParameter(XMLEventReader parameterBodyReader, Map<String, Object> typedMacroParameters, String parameterName, String leadingWhitespace) throws XMLStreamException {
        String parameterValue = leadingWhitespace + this.doParseSimpleStringParameter(parameterBodyReader);
        typedMacroParameters.put(parameterName, parameterValue);
        return parameterValue;
    }

    private String doParseSimpleStringParameter(XMLEventReader parameterBodyReader) throws XMLStreamException {
        return StaxUtils.readCharactersAndEntities(parameterBodyReader);
    }

    private String toSimpleString(List<ResourceIdentifier> resourceIdentifiers, @Nullable MacroParameter parameterMetadata) {
        String delimiter = null;
        if (parameterMetadata != null) {
            delimiter = parameterMetadata.getOptions().getProperty("delimiter");
        }
        if (delimiter == null) {
            delimiter = ",";
        }
        return StringUtils.join((Iterable)Iterables.transform(resourceIdentifiers, this::toSimpleString), (String)delimiter);
    }

    private String toWikiLink(Iterable<Link> links, ConversionContext conversionContext, @Nullable MacroParameter parameterMetadata) {
        return this.toResourceWikiLink(Iterables.transform(links, Link::getDestinationResourceIdentifier), conversionContext, parameterMetadata);
    }

    private String toResourceWikiLink(Iterable<ResourceIdentifier> resourceIdentifiers, ConversionContext conversionContext, @Nullable MacroParameter parameterMetadata) {
        String delimiter = null;
        if (parameterMetadata != null) {
            delimiter = parameterMetadata.getOptions().getProperty("delimiter");
        }
        if (delimiter == null) {
            delimiter = ",";
        }
        return StringUtils.join((Iterable)Iterables.transform(resourceIdentifiers, resourceIdentifier -> this.toWikiLink((ResourceIdentifier)resourceIdentifier, conversionContext)), (String)delimiter);
    }

    private String toSimpleString(ResourceIdentifier resourceIdentifier) {
        if (resourceIdentifier instanceof AttachmentResourceIdentifier) {
            AttachmentResourceIdentifier attachment = (AttachmentResourceIdentifier)resourceIdentifier;
            return this.toAttachmentName(attachment);
        }
        if (resourceIdentifier instanceof SpaceResourceIdentifier) {
            SpaceResourceIdentifier space = (SpaceResourceIdentifier)resourceIdentifier;
            return this.toSpaceKey(space);
        }
        if (resourceIdentifier instanceof UrlResourceIdentifier) {
            UrlResourceIdentifier url = (UrlResourceIdentifier)resourceIdentifier;
            return url.getUrl();
        }
        if (resourceIdentifier instanceof UserResourceIdentifier) {
            UserResourceIdentifier user = (UserResourceIdentifier)resourceIdentifier;
            return this.toUserName(user);
        }
        log.warn("Unhandled resource identifier {}", (Object)resourceIdentifier);
        return null;
    }

    private String toWikiLink(Link link, ConversionContext conversionContext) {
        return this.toWikiLink(link.getDestinationResourceIdentifier(), conversionContext);
    }

    private String toWikiLink(ResourceIdentifier resourceIdentifier, ConversionContext conversionContext) {
        if (resourceIdentifier instanceof AttachmentResourceIdentifier) {
            AttachmentResourceIdentifier attachment = (AttachmentResourceIdentifier)resourceIdentifier;
            return this.toAttachmentWikiLink(conversionContext, attachment);
        }
        if (resourceIdentifier instanceof BlogPostResourceIdentifier) {
            BlogPostResourceIdentifier blogPost = (BlogPostResourceIdentifier)resourceIdentifier;
            return this.toBlogPostWikiLink(blogPost);
        }
        if (resourceIdentifier instanceof BlogPostsForDateResourceIdentifier) {
            BlogPostsForDateResourceIdentifier blogPosts = (BlogPostsForDateResourceIdentifier)resourceIdentifier;
            return this.toBlogPostsForDateWikiLink(blogPosts);
        }
        if (resourceIdentifier instanceof ContentEntityResourceIdentifier) {
            ContentEntityResourceIdentifier content = (ContentEntityResourceIdentifier)resourceIdentifier;
            return this.toContentEntityWikiLink(content);
        }
        if (resourceIdentifier instanceof DraftResourceIdentifier) {
            DraftResourceIdentifier draft = (DraftResourceIdentifier)resourceIdentifier;
            return this.toDraftWikiLink(draft);
        }
        if (resourceIdentifier instanceof IdAndTypeResourceIdentifier) {
            IdAndTypeResourceIdentifier idAndType = (IdAndTypeResourceIdentifier)resourceIdentifier;
            return this.toIdWikiLink(idAndType);
        }
        if (resourceIdentifier instanceof PageResourceIdentifier) {
            PageResourceIdentifier page = (PageResourceIdentifier)resourceIdentifier;
            return this.toPageWikiLink(page);
        }
        if (resourceIdentifier instanceof ShortcutResourceIdentifier) {
            ShortcutResourceIdentifier shortcut = (ShortcutResourceIdentifier)resourceIdentifier;
            return this.toShortcutWikiLink(shortcut);
        }
        if (resourceIdentifier instanceof SpaceResourceIdentifier) {
            SpaceResourceIdentifier space = (SpaceResourceIdentifier)resourceIdentifier;
            return this.toSpaceWikiLink(space);
        }
        if (resourceIdentifier instanceof UrlResourceIdentifier) {
            UrlResourceIdentifier url = (UrlResourceIdentifier)resourceIdentifier;
            return url.getUrl();
        }
        if (resourceIdentifier instanceof UserResourceIdentifier) {
            UserResourceIdentifier user = (UserResourceIdentifier)resourceIdentifier;
            return this.toUserWikiLink(user);
        }
        if (resourceIdentifier instanceof WikiLinkBasedResourceIdentifier) {
            WikiLinkBasedResourceIdentifier wikiLink = (WikiLinkBasedResourceIdentifier)resourceIdentifier;
            return wikiLink.getOriginalLinkText();
        }
        if (resourceIdentifier == null && conversionContext != null && conversionContext.getPageContext() != null) {
            log.debug("Interpreting a null link ResourceIdentifier as a link to the current page");
            return conversionContext.getPageContext().getPageTitle();
        }
        log.warn("Unhandled resource identifier {}", (Object)resourceIdentifier);
        return null;
    }

    private String toUserName(UserResourceIdentifier userResourceIdentifier) {
        return userResourceIdentifier.getUsername();
    }

    private String toUserWikiLink(UserResourceIdentifier user) {
        return "~" + this.toUserName(user);
    }

    private String toSpaceKey(SpaceResourceIdentifier space) {
        return space.getSpaceKey();
    }

    private String toSpaceWikiLink(SpaceResourceIdentifier space) {
        return space.getSpaceKey() + ":";
    }

    private String toShortcutWikiLink(ShortcutResourceIdentifier shortcut) {
        String shortcutKey = shortcut.getShortcutKey();
        String parameter = shortcut.getShortcutParameter();
        return parameter + "@" + shortcutKey;
    }

    private String toPageWikiLink(PageResourceIdentifier page) {
        String spaceKey = page.getSpaceKey();
        String title = page.getTitle();
        if (spaceKey != null) {
            return spaceKey + ":" + title;
        }
        return title;
    }

    private String toIdWikiLink(IdAndTypeResourceIdentifier idAndType) {
        long id = idAndType.getId();
        return "$" + id;
    }

    private String toDraftWikiLink(DraftResourceIdentifier draft) {
        long draftId = draft.getDraftId();
        return "$" + draftId;
    }

    private String toContentEntityWikiLink(ContentEntityResourceIdentifier content) {
        long contentId = content.getContentId();
        return "$" + contentId;
    }

    private String toBlogPostsForDateWikiLink(BlogPostsForDateResourceIdentifier blogPosts) {
        String spaceKey = blogPosts.getSpaceKey();
        Calendar postingDay = blogPosts.getPostingDay();
        String postingDayStr = XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(postingDay.getTime());
        if (spaceKey != null) {
            return spaceKey + ":/" + postingDayStr;
        }
        return "/" + postingDayStr;
    }

    private String toBlogPostWikiLink(BlogPostResourceIdentifier blogPost) {
        String spaceKey = blogPost.getSpaceKey();
        Calendar postingDay = blogPost.getPostingDay();
        String postingDayStr = XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(postingDay.getTime());
        String title = blogPost.getTitle();
        if (spaceKey != null) {
            return spaceKey + ":/" + postingDayStr + "/" + title;
        }
        return "/" + postingDayStr + "/" + title;
    }

    private String toAttachmentName(AttachmentResourceIdentifier attachment) {
        return attachment.getFilename();
    }

    private String toAttachmentWikiLink(ConversionContext conversionContext, AttachmentResourceIdentifier attachment) {
        String filename = attachment.getFilename();
        AttachmentContainerResourceIdentifier containerResourceIdentifier = attachment.getAttachmentContainerResourceIdentifier();
        String containerLink = this.toWikiLink(containerResourceIdentifier, conversionContext);
        if (containerLink != null) {
            return containerLink + "^" + filename;
        }
        return "^" + filename;
    }

    private Optional<MacroId> unmarshalMacroId(StartElement macroElementEvent) throws XhtmlException {
        Attribute schemaVersionAttribute = macroElementEvent.getAttributeByName(StorageMacroConstants.MACRO_ID_ATTRIBUTE);
        return schemaVersionAttribute == null ? Optional.empty() : Optional.of(MacroId.fromString(schemaVersionAttribute.getValue()));
    }
}

