/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.links.OutgoingLinksExtractor;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.spring.container.ContainerManager;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XhtmlOutgoingLinksExtractor
implements OutgoingLinksExtractor {
    private static final Logger log = LoggerFactory.getLogger(XhtmlOutgoingLinksExtractor.class);
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller;
    private static final QName HREF_ATTRIBUTE = new QName("href");
    private final Map<Class<? extends ResourceIdentifier>, DestinationHandler<? extends ResourceIdentifier>> destinationHandlers = new HashMap<Class<? extends ResourceIdentifier>, DestinationHandler<? extends ResourceIdentifier>>(6);

    public XhtmlOutgoingLinksExtractor(XmlEventReaderFactory xmlEventReaderFactory, Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller, String contentEntityResolverId, String draftResolverId, String idAndTypeResolverId) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.resourceIdentifierUnmarshaller = resourceIdentifierUnmarshaller;
        this.destinationHandlers.put(IdAndTypeResourceIdentifier.class, new IdAndTypeHandler(idAndTypeResolverId));
        this.destinationHandlers.put(PageResourceIdentifier.class, new PageResourceHandler());
        this.destinationHandlers.put(BlogPostResourceIdentifier.class, new BlogPostResourceHandler());
        this.destinationHandlers.put(ContentEntityResourceIdentifier.class, new ContentEntityResourceHandler(contentEntityResolverId));
        this.destinationHandlers.put(DraftResourceIdentifier.class, new DraftResourceHandler(draftResolverId));
        this.destinationHandlers.put(AttachmentResourceIdentifier.class, new AttachmentResourceHandler(this.destinationHandlers));
    }

    void setResolvers(ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResolver, DraftResourceIdentifierResolver draftResolver, ContentEntityResourceIdentifierResolver contentResolver) {
        ((IdAndTypeHandler)this.destinationHandlers.get(IdAndTypeResourceIdentifier.class)).setResolver(idAndTypeResolver);
        ((DraftResourceHandler)this.destinationHandlers.get(DraftResourceIdentifier.class)).setResolver(draftResolver);
        ((ContentEntityResourceHandler)this.destinationHandlers.get(ContentEntityResourceIdentifier.class)).setResolver(contentResolver);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<OutgoingLink> extract(ContentEntityObject content) {
        if (!BodyType.XHTML.equals(content.getBodyContent().getBodyType())) {
            return Collections.emptySet();
        }
        if (StringUtils.isBlank((CharSequence)content.getBodyAsString())) {
            return Collections.emptySet();
        }
        HashSet<OutgoingLink> result = new HashSet<OutgoingLink>();
        XMLEventReader reader = null;
        try {
            reader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(content.getBodyAsString()));
            while (reader.hasNext()) {
                XMLEvent xmlEvent = reader.peek();
                if (xmlEvent.isStartElement() && this.resourceIdentifierUnmarshaller.handles(xmlEvent.asStartElement(), null)) {
                    ResourceIdentifier resourceIdentifier;
                    XMLEventReader resourceIdentifierfragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(reader);
                    try {
                        resourceIdentifier = this.resourceIdentifierUnmarshaller.unmarshal(resourceIdentifierfragmentReader, null, null);
                    }
                    catch (XhtmlException e) {
                        log.error("Error unmarshalling resource identifier fragment.", (Throwable)e);
                        continue;
                    }
                    finally {
                        StaxUtils.closeQuietly(resourceIdentifierfragmentReader);
                        continue;
                    }
                    OutgoingLink outgoingLink = this.createOutgoingLink(content, resourceIdentifier);
                    if (outgoingLink == null) continue;
                    result.add(outgoingLink);
                    continue;
                }
                if (xmlEvent.isStartElement() && "a".equals(xmlEvent.asStartElement().getName().getLocalPart()) && xmlEvent.asStartElement().getAttributeByName(HREF_ATTRIBUTE) != null) {
                    String url = reader.nextEvent().asStartElement().getAttributeByName(HREF_ATTRIBUTE).getValue();
                    if (!url.contains(":")) continue;
                    result.add(new OutgoingLink(content, StringUtils.substringBefore((String)url, (String)":"), StringUtils.substringAfter((String)url, (String)":")));
                    continue;
                }
                reader.nextEvent();
            }
        }
        catch (XMLStreamException e) {
            try {
                throw new RuntimeException("Error extracting links", e);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(reader);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly(reader);
        return result;
    }

    private OutgoingLink createOutgoingLink(ContentEntityObject content, ResourceIdentifier resourceIdentifier) {
        DestinationHandler<? extends ResourceIdentifier> handler = this.destinationHandlers.get(resourceIdentifier.getClass());
        if (handler == null) {
            return null;
        }
        SpaceKeyAndTitlePair pair = null;
        try {
            pair = handler.getDestination(resourceIdentifier, content.toPageContext());
        }
        catch (XhtmlException ex) {
            log.error("Error extracting link information.", (Throwable)ex);
        }
        if (pair == null || !pair.isComplete()) {
            return null;
        }
        return new OutgoingLink(content, pair.spaceKey, pair.title);
    }

    private static class AttachmentResourceHandler
    implements DestinationHandler<AttachmentResourceIdentifier> {
        private final Map<Class<? extends ResourceIdentifier>, DestinationHandler<? extends ResourceIdentifier>> destinationHandlers;

        public AttachmentResourceHandler(Map<Class<? extends ResourceIdentifier>, DestinationHandler<? extends ResourceIdentifier>> destinationHandlers) {
            this.destinationHandlers = destinationHandlers;
        }

        @Override
        public SpaceKeyAndTitlePair getDestination(AttachmentResourceIdentifier resourceIdentifier, PageContext pageContext) throws XhtmlException {
            AttachmentContainerResourceIdentifier attachmentContainerResourceIdentifier = resourceIdentifier.getAttachmentContainerResourceIdentifier();
            if (attachmentContainerResourceIdentifier == null) {
                SpaceKeyAndTitlePair pair = new SpaceKeyAndTitlePair();
                pair.spaceKey = pageContext.getSpaceKey();
                pair.title = pageContext.getPageTitle();
                Calendar postingDay = pageContext.getPostingDay();
                if (postingDay != null) {
                    String postingDayStr = XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(postingDay.getTime());
                    pair.title = "/" + postingDayStr + "/" + pair.title;
                }
                return pair;
            }
            DestinationHandler<? extends ResourceIdentifier> handler = this.destinationHandlers.get(attachmentContainerResourceIdentifier.getClass());
            if (handler == null) {
                return null;
            }
            return handler.getDestination(attachmentContainerResourceIdentifier, pageContext);
        }
    }

    private static class DraftResourceHandler
    implements DestinationHandler<DraftResourceIdentifier> {
        private volatile DraftResourceIdentifierResolver draftResourceIdentifierResolver;
        private final String resolverId;

        public DraftResourceHandler(String resolverId) {
            this.resolverId = resolverId;
        }

        public void setResolver(DraftResourceIdentifierResolver draftResourceIdentifierResolver) {
            this.draftResourceIdentifierResolver = draftResourceIdentifierResolver;
        }

        @Override
        public SpaceKeyAndTitlePair getDestination(DraftResourceIdentifier resourceIdentifier, PageContext pageContext) throws XhtmlException {
            if (this.draftResourceIdentifierResolver == null) {
                this.draftResourceIdentifierResolver = (DraftResourceIdentifierResolver)ContainerManager.getComponent((String)this.resolverId);
            }
            Draft draft = this.draftResourceIdentifierResolver.resolve(resourceIdentifier, (ConversionContext)new DefaultConversionContext(pageContext));
            SpaceKeyAndTitlePair pair = new SpaceKeyAndTitlePair();
            pair.title = draft.getTitle();
            pair.spaceKey = StringUtils.isNotBlank((CharSequence)draft.getDraftSpaceKey()) ? draft.getDraftSpaceKey() : pageContext.getSpaceKey();
            return pair;
        }
    }

    private static class ContentEntityResourceHandler
    implements DestinationHandler<ContentEntityResourceIdentifier> {
        private volatile ContentEntityResourceIdentifierResolver resolver;
        private final String resolverId;

        public ContentEntityResourceHandler(String resolverId) {
            this.resolverId = resolverId;
        }

        public void setResolver(ContentEntityResourceIdentifierResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        public SpaceKeyAndTitlePair getDestination(ContentEntityResourceIdentifier resourceIdentifier, PageContext pageContext) throws XhtmlException {
            if (this.resolver == null) {
                this.resolver = (ContentEntityResourceIdentifierResolver)ContainerManager.getComponent((String)this.resolverId);
            }
            ContentEntityObject ceo = this.resolver.resolve(resourceIdentifier, (ConversionContext)new DefaultConversionContext(pageContext));
            SpaceKeyAndTitlePair pair = new SpaceKeyAndTitlePair();
            if (ceo instanceof SpaceContentEntityObject) {
                SpaceContentEntityObject sceo = (SpaceContentEntityObject)ceo;
                pair.spaceKey = sceo.getSpaceKey();
            } else {
                pair.spaceKey = pageContext.getSpaceKey();
            }
            if (ceo instanceof Page) {
                pair.title = ceo.getTitle();
            } else if (ceo instanceof BlogPost) {
                String postingDay = XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(((BlogPost)ceo).getPostingCalendarDate().getTime());
                pair.title = "/" + postingDay + "/" + ceo.getTitle();
            }
            return pair;
        }
    }

    private static class BlogPostResourceHandler
    implements DestinationHandler<BlogPostResourceIdentifier> {
        private BlogPostResourceHandler() {
        }

        @Override
        public SpaceKeyAndTitlePair getDestination(BlogPostResourceIdentifier resourceIdentifier, PageContext pageContext) throws XhtmlException {
            String postingDay = XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(resourceIdentifier.getPostingDay().getTime());
            SpaceKeyAndTitlePair pair = new SpaceKeyAndTitlePair();
            pair.spaceKey = StringUtils.isNotBlank((CharSequence)resourceIdentifier.getSpaceKey()) ? resourceIdentifier.getSpaceKey() : pageContext.getSpaceKey();
            pair.title = "/" + postingDay + "/" + resourceIdentifier.getTitle();
            return pair;
        }
    }

    private static class PageResourceHandler
    implements DestinationHandler<PageResourceIdentifier> {
        private PageResourceHandler() {
        }

        @Override
        public SpaceKeyAndTitlePair getDestination(PageResourceIdentifier resourceIdentifier, PageContext pageContext) throws XhtmlException {
            SpaceKeyAndTitlePair pair = new SpaceKeyAndTitlePair();
            pair.spaceKey = StringUtils.isNotBlank((CharSequence)resourceIdentifier.getSpaceKey()) ? resourceIdentifier.getSpaceKey() : pageContext.getSpaceKey();
            pair.title = StringUtils.isNotBlank((CharSequence)resourceIdentifier.getTitle()) ? resourceIdentifier.getTitle() : pageContext.getPageTitle();
            return pair;
        }
    }

    private static class IdAndTypeHandler
    implements DestinationHandler<IdAndTypeResourceIdentifier> {
        private volatile ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResolver;
        private final String resolverId;

        public IdAndTypeHandler(String resolverId) {
            this.resolverId = resolverId;
        }

        public void setResolver(ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResolver) {
            this.idAndTypeResolver = idAndTypeResolver;
        }

        @Override
        public SpaceKeyAndTitlePair getDestination(IdAndTypeResourceIdentifier resourceIdentifier, PageContext pageContext) throws XhtmlException {
            Object obj;
            SpaceKeyAndTitlePair pair = new SpaceKeyAndTitlePair();
            if (resourceIdentifier.getType() != ContentTypeEnum.BLOG && resourceIdentifier.getType() != ContentTypeEnum.PAGE) {
                return pair;
            }
            if (this.idAndTypeResolver == null) {
                this.idAndTypeResolver = (ResourceIdentifierResolver)ContainerManager.getComponent((String)this.resolverId);
            }
            if ((obj = this.idAndTypeResolver.resolve(resourceIdentifier, null)) instanceof SpaceContentEntityObject) {
                pair.spaceKey = ((SpaceContentEntityObject)obj).getSpaceKey();
            }
            if (obj instanceof Page) {
                pair.title = ((Page)obj).getTitle();
            } else if (obj instanceof BlogPost) {
                String postingDay = XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(((BlogPost)obj).getPostingCalendarDate().getTime());
                pair.title = "/" + postingDay + "/" + ((BlogPost)obj).getTitle();
            }
            return pair;
        }
    }

    private static interface DestinationHandler<T> {
        public SpaceKeyAndTitlePair getDestination(T var1, PageContext var2) throws XhtmlException;
    }

    private static class SpaceKeyAndTitlePair {
        String spaceKey;
        String title;

        private SpaceKeyAndTitlePair() {
        }

        boolean isComplete() {
            return StringUtils.isNotBlank((CharSequence)this.spaceKey) && StringUtils.isNotBlank((CharSequence)this.title);
        }
    }
}

