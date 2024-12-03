/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.hateoas.EntityModel
 *  org.springframework.hateoas.IanaLinkRelations
 *  org.springframework.hateoas.Link
 *  org.springframework.hateoas.LinkRelation
 *  org.springframework.hateoas.PagedModel
 *  org.springframework.hateoas.PagedModel$PageMetadata
 *  org.springframework.hateoas.RepresentationModel
 *  org.springframework.hateoas.UriTemplate
 *  org.springframework.hateoas.server.RepresentationModelAssembler
 *  org.springframework.hateoas.server.core.EmbeddedWrapper
 *  org.springframework.hateoas.server.core.EmbeddedWrappers
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.servlet.support.ServletUriComponentsBuilder
 *  org.springframework.web.util.UriComponents
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.data.web;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.core.EmbeddedWrapper;
import org.springframework.hateoas.server.core.EmbeddedWrappers;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class PagedResourcesAssembler<T>
implements RepresentationModelAssembler<Page<T>, PagedModel<EntityModel<T>>> {
    private final HateoasPageableHandlerMethodArgumentResolver pageableResolver;
    private final Optional<UriComponents> baseUri;
    private final EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
    private boolean forceFirstAndLastRels = false;

    public PagedResourcesAssembler(@Nullable HateoasPageableHandlerMethodArgumentResolver resolver, @Nullable UriComponents baseUri) {
        this.pageableResolver = resolver == null ? new HateoasPageableHandlerMethodArgumentResolver() : resolver;
        this.baseUri = Optional.ofNullable(baseUri);
    }

    public void setForceFirstAndLastRels(boolean forceFirstAndLastRels) {
        this.forceFirstAndLastRels = forceFirstAndLastRels;
    }

    public PagedModel<EntityModel<T>> toModel(Page<T> entity) {
        return this.toModel(entity, EntityModel::of);
    }

    public PagedModel<EntityModel<T>> toModel(Page<T> page, Link selfLink) {
        return this.toModel(page, EntityModel::of, selfLink);
    }

    public <R extends RepresentationModel<?>> PagedModel<R> toModel(Page<T> page, RepresentationModelAssembler<T, R> assembler) {
        return this.createModel(page, assembler, Optional.empty());
    }

    public <R extends RepresentationModel<?>> PagedModel<R> toModel(Page<T> page, RepresentationModelAssembler<T, R> assembler, Link link) {
        Assert.notNull((Object)link, (String)"Link must not be null!");
        return this.createModel(page, assembler, Optional.of(link));
    }

    public PagedModel<?> toEmptyModel(Page<?> page, Class<?> type) {
        return this.toEmptyModel(page, type, Optional.empty());
    }

    public PagedModel<?> toEmptyModel(Page<?> page, Class<?> type, Link link) {
        return this.toEmptyModel(page, type, Optional.of(link));
    }

    private PagedModel<?> toEmptyModel(Page<?> page, Class<?> type, Optional<Link> link) {
        Assert.notNull(page, (String)"Page must not be null!");
        Assert.isTrue((!page.hasContent() ? 1 : 0) != 0, (String)"Page must not have any content!");
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull(link, (String)"Link must not be null!");
        PagedModel.PageMetadata metadata = this.asPageMetadata(page);
        EmbeddedWrapper wrapper = this.wrappers.emptyCollectionOf(type);
        List<EmbeddedWrapper> embedded = Collections.singletonList(wrapper);
        return this.addPaginationLinks(PagedModel.of(embedded, (PagedModel.PageMetadata)metadata), page, link);
    }

    protected <R extends RepresentationModel<?>, S> PagedModel<R> createPagedModel(List<R> resources, PagedModel.PageMetadata metadata, Page<S> page) {
        Assert.notNull(resources, (String)"Content resources must not be null!");
        Assert.notNull((Object)metadata, (String)"PageMetadata must not be null!");
        Assert.notNull(page, (String)"Page must not be null!");
        return PagedModel.of(resources, (PagedModel.PageMetadata)metadata);
    }

    private <S, R extends RepresentationModel<?>> PagedModel<R> createModel(Page<S> page, RepresentationModelAssembler<S, R> assembler, Optional<Link> link) {
        Assert.notNull(page, (String)"Page must not be null!");
        Assert.notNull(assembler, (String)"ResourceAssembler must not be null!");
        ArrayList<RepresentationModel> resources = new ArrayList<RepresentationModel>(page.getNumberOfElements());
        for (Object element : page) {
            resources.add(assembler.toModel(element));
        }
        PagedModel resource = this.createPagedModel(resources, this.asPageMetadata(page), page);
        return this.addPaginationLinks(resource, page, link);
    }

    private <R> PagedModel<R> addPaginationLinks(PagedModel<R> resources, Page<?> page, Optional<Link> link) {
        boolean isNavigable;
        UriTemplate base = this.getUriTemplate(link);
        boolean bl = isNavigable = page.hasPrevious() || page.hasNext();
        if (isNavigable || this.forceFirstAndLastRels) {
            resources.add(this.createLink(base, PageRequest.of(0, page.getSize(), page.getSort()), IanaLinkRelations.FIRST));
        }
        if (page.hasPrevious()) {
            resources.add(this.createLink(base, page.previousPageable(), IanaLinkRelations.PREV));
        }
        Link selfLink = link.map(Link::withSelfRel).orElseGet(() -> this.createLink(base, page.getPageable(), IanaLinkRelations.SELF));
        resources.add(selfLink);
        if (page.hasNext()) {
            resources.add(this.createLink(base, page.nextPageable(), IanaLinkRelations.NEXT));
        }
        if (isNavigable || this.forceFirstAndLastRels) {
            int lastIndex = page.getTotalPages() == 0 ? 0 : page.getTotalPages() - 1;
            resources.add(this.createLink(base, PageRequest.of(lastIndex, page.getSize(), page.getSort()), IanaLinkRelations.LAST));
        }
        return resources;
    }

    private UriTemplate getUriTemplate(Optional<Link> baseLink) {
        return UriTemplate.of((String)baseLink.map(Link::getHref).orElseGet(this::baseUriOrCurrentRequest));
    }

    private Link createLink(UriTemplate base, Pageable pageable, LinkRelation relation) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri((URI)base.expand(new Object[0]));
        this.pageableResolver.enhance(builder, this.getMethodParameter(), pageable);
        return Link.of((UriTemplate)UriTemplate.of((String)builder.build().toString()), (LinkRelation)relation);
    }

    @Nullable
    protected MethodParameter getMethodParameter() {
        return null;
    }

    private PagedModel.PageMetadata asPageMetadata(Page<?> page) {
        Assert.notNull(page, (String)"Page must not be null!");
        int number = this.pageableResolver.isOneIndexedParameters() ? page.getNumber() + 1 : page.getNumber();
        return new PagedModel.PageMetadata((long)page.getSize(), (long)number, page.getTotalElements(), (long)page.getTotalPages());
    }

    private String baseUriOrCurrentRequest() {
        return this.baseUri.map(Object::toString).orElseGet(PagedResourcesAssembler::currentRequest);
    }

    private static String currentRequest() {
        return ServletUriComponentsBuilder.fromCurrentRequest().build().toString();
    }
}

