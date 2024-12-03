/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$ContentBuilder
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.History
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.model.permissions.ContentRestriction
 *  com.atlassian.confluence.api.model.permissions.OperationCheckResult
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.confluence.api.model.reference.BuilderUtils
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.NavigationService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.permissions.ContentRestrictionService
 *  com.atlassian.confluence.api.service.permissions.OperationService
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 *  org.apache.commons.collections.iterators.SingletonIterator
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.ChildContentFactory;
import com.atlassian.confluence.api.impl.service.content.factory.ContentBodyFactory;
import com.atlassian.confluence.api.impl.service.content.factory.ContentExtensionsFactory;
import com.atlassian.confluence.api.impl.service.content.factory.ContentMetadataFactory;
import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.api.impl.service.content.factory.HistoryFactory;
import com.atlassian.confluence.api.impl.service.content.factory.ModelFactory;
import com.atlassian.confluence.api.impl.service.content.factory.SpaceFactory;
import com.atlassian.confluence.api.impl.service.content.factory.VersionFactory;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.model.permissions.OperationCheckResult;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.permissions.ContentRestrictionService;
import com.atlassian.confluence.api.service.permissions.OperationService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Option;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections.iterators.SingletonIterator;

public class ContentFactory
extends ModelFactory<ContentEntityObject, Content> {
    private static final String EXTENSIONS = "extensions";
    private final ContentBodyFactory contentBodyFactory;
    private final SpaceFactory spaceFactory;
    private final ContentMetadataFactory metadataFactory;
    private final VersionFactory versionFactory;
    private final HistoryFactory historyFactory;
    private final ChildContentFactory childContentFactory;
    private final ContentExtensionsFactory extensionsFactory;
    private final OperationService operationService;
    private final ContentRestrictionService contentRestrictionService;
    private final NavigationService navigationService;

    public ContentFactory(ContentBodyFactory contentBodyFactory, SpaceFactory spaceFactory, ContentMetadataFactory metadataFactory, VersionFactory versionFactory, HistoryFactory historyFactory, ChildContentFactory childContentFactory, ContentExtensionsFactory extensionsFactory, OperationService operationService, ContentRestrictionService contentRestrictionService, NavigationService navigationService) {
        this.contentBodyFactory = contentBodyFactory;
        this.spaceFactory = spaceFactory;
        this.metadataFactory = metadataFactory;
        this.versionFactory = versionFactory;
        this.historyFactory = historyFactory;
        this.childContentFactory = childContentFactory;
        this.extensionsFactory = extensionsFactory;
        this.operationService = operationService;
        this.contentRestrictionService = contentRestrictionService;
        this.navigationService = navigationService;
    }

    @Override
    public Content buildFrom(ContentEntityObject entity, Expansions expansions) {
        return this.buildFrom(() -> new SingletonIterator((Object)entity), expansions).iterator().next();
    }

    @Override
    public <HC extends ContentEntityObject> Iterable<Content> buildFrom(Iterable<HC> entities, Expansions expansions) {
        Map<ContentEntityObject, Content.ContentBuilder> builders = this.buildersFrom(entities, expansions);
        ImmutableList.Builder results = ImmutableList.builder();
        for (ContentEntityObject entity : entities) {
            results.add((Object)builders.get(entity).build());
        }
        return results.build();
    }

    public Reference<Content> buildRef(ContentEntityObject entity, Fauxpansions fauxpansions) {
        if (!fauxpansions.canExpand()) {
            if (entity == null) {
                return Reference.collapsed(Content.class);
            }
            return Content.buildReference((ContentSelector)entity.getSelector());
        }
        if (entity == null) {
            return Reference.empty(Content.class);
        }
        return Reference.to((Object)this.buildFrom(entity, fauxpansions.getSubExpansions()));
    }

    public Content.ContentBuilder builderFrom(ContentEntityObject entity, ContentType contentType, Expansions expansions) {
        return (Content.ContentBuilder)Iterables.first(this.buildersFrom((Iterable<? extends ContentEntityObject>)Option.some((Object)entity), expansions).values()).get();
    }

    private Map<ContentEntityObject, Content.ContentBuilder> buildersFrom(Iterable<? extends ContentEntityObject> entities, Expansions expansions) {
        HashMap<ContentEntityObject, Content.ContentBuilder> map = new HashMap<ContentEntityObject, Content.ContentBuilder>();
        for (ContentEntityObject contentEntityObject : entities) {
            if (map.containsKey(contentEntityObject)) continue;
            if (!(contentEntityObject instanceof ContentConvertible) || !((ContentConvertible)((Object)contentEntityObject)).shouldConvertToContent()) {
                throw new BadRequestException("The entity " + contentEntityObject + " is not ContentConvertible or API available");
            }
            ContentType contentType = ((ContentConvertible)((Object)contentEntityObject)).getContentTypeObject();
            Content.ContentBuilder builder = Content.builder((ContentType)contentType);
            builder.ancestors(this.makeAncestry(contentEntityObject, expansions));
            builder.body(this.makeContentBodies(contentEntityObject, expansions));
            this.addCommonFieldsToBuilder(builder, contentEntityObject, expansions);
            map.put(contentEntityObject, builder);
        }
        this.addCommonFieldsToBuilders(map, expansions);
        return map;
    }

    private void addCommonFieldsToBuilders(Map<ContentEntityObject, Content.ContentBuilder> contentMap, Expansions expansions) {
        Fauxpansions historyFauxpansions = Fauxpansions.fauxpansions(expansions, "history");
        Map<ContentEntityObject, Reference<History>> historyMap = this.historyFactory.buildReferences(contentMap.keySet(), historyFauxpansions, this);
        for (Map.Entry<ContentEntityObject, Reference<History>> historyEntry : historyMap.entrySet()) {
            contentMap.get(historyEntry.getKey()).history(historyEntry.getValue());
        }
        Map<ContentEntityObject, Map<String, Object>> metadata = this.metadataFactory.buildMetadataForContentEntityObjects(Maps.transformEntries(contentMap, (k, v) -> () -> ((Supplier)Suppliers.memoize(() -> ((Content.ContentBuilder)v).build())).get()), Fauxpansions.fauxpansions(expansions, "metadata"));
        for (Map.Entry<ContentEntityObject, Map<String, Object>> entry : metadata.entrySet()) {
            contentMap.get(entry.getKey()).metadata(entry.getValue());
        }
    }

    void addCommonFieldsToBuilder(Content.ContentBuilder builder, ContentEntityObject entity, Expansions expansions) {
        Option<String> tinyUrlOption;
        ContentConvertible convertibleEntity = (ContentConvertible)((Object)entity);
        ContentSelector selector = entity.getSelector();
        ContentId newId = selector.getId();
        builder.id(newId);
        builder.container(this.makeContainerReference(convertibleEntity, expansions));
        builder.status(entity.getContentStatusObject());
        builder.title(entity.getDisplayTitle());
        builder.addLink(LinkType.WEB_UI, entity.getUrlPath());
        if (entity instanceof AbstractPage) {
            builder.addLink(LinkType.EDIT_UI, ((AbstractPage)entity).getEditUrlPath());
        }
        if (!(tinyUrlOption = this.makeOptionalTinyUrl(entity)).isEmpty()) {
            builder.addLink(LinkType.TINY_UI, (String)tinyUrlOption.get());
        }
        for (Link link : this.extensionsFactory.buildLinks(convertibleEntity)) {
            builder.addLink(link);
        }
        builder.extensions(this.extensionsFactory.buildExtensions(convertibleEntity, expansions.getSubExpansions(EXTENSIONS)));
        builder.space(this.makeSpaceReference(entity, expansions));
        builder.version(this.versionFactory.buildRef(entity, Fauxpansions.fauxpansions(expansions, "version"), this));
        builder.children(this.childContentFactory.buildFrom(selector, Depth.ROOT, Fauxpansions.fauxpansions(expansions, "children")));
        builder.descendants(this.childContentFactory.buildFrom(selector, Depth.ALL, Fauxpansions.fauxpansions(expansions, "descendants")));
        builder.operations(this.makeOperations(entity, expansions));
        builder.restrictions(this.makeRestrictions(entity, Fauxpansions.fauxpansions(expansions, "restrictions")));
    }

    private Map<OperationKey, ContentRestriction> makeRestrictions(ContentEntityObject entity, Fauxpansions fauxpansions) {
        if (!fauxpansions.canExpand()) {
            return BuilderUtils.collapsedMap((Navigation.Builder)this.navigationService.createNavigation().content(entity.getSelector()).restrictionByOperation());
        }
        Expansions subExpansions = fauxpansions.getSubExpansions();
        subExpansions.checkRecursiveExpansion("content");
        return this.contentRestrictionService.getRestrictionsGroupByOperation(entity.getContentId(), subExpansions.toArray());
    }

    private Iterable<OperationCheckResult> makeOperations(ContentEntityObject entity, Expansions expansions) {
        if (!expansions.canExpand("operations")) {
            return BuilderUtils.collapsedList();
        }
        Expansions subExpansions = expansions.getSubExpansions("operations");
        subExpansions.checkRecursiveExpansion("operations");
        return this.operationService.getAvailableOperations(Target.forModelObject((Object)this.buildFrom(entity, ExpansionsParser.parseAsExpansions((String)"container"))));
    }

    private Map<ContentRepresentation, ContentBody> makeContentBodies(ContentEntityObject entity, Expansions expansions) {
        if (!expansions.canExpand("body")) {
            return BuilderUtils.collapsedMap();
        }
        Expansions bodyExpansions = expansions.getSubExpansions("body");
        bodyExpansions.checkRecursiveExpansion("body");
        return this.contentBodyFactory.makeContentBodies(entity, entity.getBodyContent(), bodyExpansions, this);
    }

    private Reference<? extends Container> makeContainerReference(ContentConvertible entity, Expansions expansions) {
        Expansions subExpansions;
        boolean expand = expansions.canExpand("container");
        if (expand) {
            subExpansions = expansions.getSubExpansions("container");
            subExpansions.checkRecursiveExpansion("container");
        } else {
            subExpansions = Expansions.EMPTY;
        }
        Optional<Object> optionalContainer = this.extensionsFactory.containerEntity(entity, subExpansions);
        if (!optionalContainer.isPresent()) {
            return expand ? Reference.empty(Container.class) : Reference.collapsed(Container.class);
        }
        Object containerEntity = optionalContainer.get();
        if (containerEntity instanceof Draft) {
            Draft draft = (Draft)containerEntity;
            ContentType contentType = ContentType.valueOf((String)draft.getDraftType());
            Content draftContent = Content.builder().type(contentType).id(ContentId.deserialise((String)draft.getIdAsString())).title(draft.getTitle()).status(ContentStatus.DRAFT).build();
            return expand ? Reference.to((Object)draftContent) : Content.buildReference((ContentSelector)draft.getSelector());
        }
        if (containerEntity instanceof ContentEntityObject) {
            ContentEntityObject ceo = (ContentEntityObject)containerEntity;
            return expand ? Reference.to((Object)this.buildFrom(ceo, subExpansions)) : Content.buildReference((ContentSelector)ceo.getSelector());
        }
        if (containerEntity instanceof Space) {
            return expand ? Reference.to((Object)this.spaceFactory.buildFrom((Space)containerEntity, subExpansions)) : com.atlassian.confluence.api.model.content.Space.buildReference((String)((Space)containerEntity).getKey());
        }
        if (containerEntity instanceof Container) {
            return expand ? Reference.to((Object)((Container)containerEntity)) : Reference.collapsed((Object)((Container)containerEntity));
        }
        throw new IllegalStateException("Unknown container of type " + containerEntity.getClass() + " on entity " + entity.getContentId());
    }

    private Option<String> makeOptionalTinyUrl(Object entity) {
        if (entity instanceof AbstractPage) {
            TinyUrl tinyUrl = new TinyUrl((AbstractPage)entity);
            return Option.some((Object)("/x/" + tinyUrl.getIdentifier()));
        }
        return Option.none(String.class);
    }

    private Iterable<Content> makeAncestry(Versioned entity, Expansions expansions) {
        List ancestors;
        if (!expansions.canExpand("ancestors")) {
            return BuilderUtils.collapsedList();
        }
        Expansions subExpansions = expansions.getSubExpansions("ancestors");
        subExpansions.checkRecursiveExpansion("ancestors");
        ContentEntityObject latestVersion = (ContentEntityObject)entity.getLatestVersion();
        if (latestVersion instanceof Page) {
            Page page = (Page)latestVersion;
            if (page.getAncestors().contains(null)) {
                ancestors = new ArrayList();
                for (Page parent = page.getParent(); parent != null; parent = parent.getParent()) {
                    ancestors.add(parent);
                }
                ancestors = Lists.reverse((List)ancestors);
            } else {
                ancestors = Lists.newArrayList(page.getAncestors());
            }
        } else if (latestVersion instanceof Comment) {
            ancestors = new ArrayList();
            Comment comment = (Comment)latestVersion;
            for (Comment parent = comment.getParent(); parent != null; parent = parent.getParent()) {
                ancestors.add(parent);
            }
        } else {
            return ImmutableList.of();
        }
        return this.buildFrom(ancestors, subExpansions);
    }

    private Reference<com.atlassian.confluence.api.model.content.Space> makeSpaceReference(Versioned entity, Expansions expansions) {
        Space space = null;
        if (entity instanceof Spaced) {
            space = ((Spaced)((Object)entity.getLatestVersion())).getSpace();
        }
        if (expansions.canExpand("space")) {
            return space == null ? Reference.empty(com.atlassian.confluence.api.model.content.Space.class) : Reference.to((Object)this.spaceFactory.buildFrom(space, expansions.getSubExpansions("space")));
        }
        return space == null ? Reference.collapsed(com.atlassian.confluence.api.model.content.Space.class) : com.atlassian.confluence.api.model.content.Space.buildReference((String)space.getKey());
    }
}

