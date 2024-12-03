/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAuthor
 *  com.atlassian.audit.entity.AuditAuthor$Builder
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditEntity$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditResource$Builder
 *  com.atlassian.audit.spi.entity.AuditEntityTransformationService
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  javax.annotation.Nonnull
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.spi.entity.AuditEntityTransformationService;
import com.atlassian.confluence.api.impl.service.audit.uri.ResourceUriGenerator;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfluenceAuditEntityTransformationService
implements AuditEntityTransformationService {
    private final ApplicationProperties applicationProperties;
    private final Map<String, ResourceUriGenerator> generatorByObjectType;
    private final ResourceUriGenerator userResourceGenerator;

    public ConfluenceAuditEntityTransformationService(ApplicationProperties applicationProperties, Map<String, ResourceUriGenerator> generatorByObjectType, ResourceUriGenerator userResourceGenerator) {
        this.applicationProperties = applicationProperties;
        this.generatorByObjectType = generatorByObjectType;
        this.userResourceGenerator = userResourceGenerator;
    }

    @Nonnull
    public List<AuditEntity> transform(@Nonnull List<AuditEntity> list) {
        try {
            URI baseUri = new URI(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL));
            Map<AuditResource, AuditResource> transformedResourceByOriginal = this.getTransformedResourceByOriginal(baseUri, list);
            Map<AuditAuthor, AuditAuthor> transformedAuthorByOriginal = this.getTransformedAuthorByOriginal(baseUri, list);
            return list.stream().map(entity -> new AuditEntity.Builder(entity).author(transformedAuthorByOriginal.getOrDefault(entity.getAuthor(), entity.getAuthor())).affectedObjects(entity.getAffectedObjects().stream().map(r -> transformedResourceByOriginal.getOrDefault(r, (AuditResource)r)).collect(Collectors.toList())).build()).collect(Collectors.toList());
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private @NonNull Map<AuditAuthor, AuditAuthor> getTransformedAuthorByOriginal(URI baseUri, @NonNull List<AuditEntity> entities) {
        Set authorsToTransform = entities.stream().map(AuditEntity::getAuthor).filter(a -> "user".equals(a.getType())).filter(a -> a.getUri() == null).collect(Collectors.toSet());
        Set<String> idsToTransform = authorsToTransform.stream().map(AuditAuthor::getId).collect(Collectors.toSet());
        Map<String, URI> urlById = this.userResourceGenerator.generate(baseUri, idsToTransform);
        return authorsToTransform.stream().filter(author -> urlById.containsKey(author.getId())).collect(Collectors.toMap(author -> author, author -> new AuditAuthor.Builder(author).uri(((URI)urlById.get(author.getId())).toString()).build()));
    }

    private @NonNull Map<AuditResource, AuditResource> getTransformedResourceByOriginal(URI baseUri, @NonNull List<AuditEntity> entities) {
        List resourcesToTransform = entities.stream().flatMap(e -> e.getAffectedObjects().stream()).filter(r -> r.getUri() == null).filter(r -> r.getId() != null).collect(Collectors.toList());
        ImmutableSetMultimap resourcesByType = ImmutableSetMultimap.copyOf((Multimap)Multimaps.index(resourcesToTransform, AuditResource::getType));
        HashMap<AuditResource, AuditResource> transformedResourceByOriginal = new HashMap<AuditResource, AuditResource>();
        resourcesByType.asMap().forEach((resourceType, untransformedResources) -> {
            Set<String> idsToTransform = untransformedResources.stream().map(AuditResource::getId).collect(Collectors.toSet());
            Map<String, URI> resourceUriById = this.generatorByObjectType.getOrDefault(resourceType, new ResourceUriGenerator.NoopGenerator()).generate(baseUri, idsToTransform);
            transformedResourceByOriginal.putAll(untransformedResources.stream().filter(resource -> resourceUriById.containsKey(resource.getId())).collect(Collectors.toMap(resource -> resource, resource -> new AuditResource.Builder(resource).uri(((URI)resourceUriById.get(resource.getId())).toString()).build())));
        });
        return transformedResourceByOriginal;
    }
}

