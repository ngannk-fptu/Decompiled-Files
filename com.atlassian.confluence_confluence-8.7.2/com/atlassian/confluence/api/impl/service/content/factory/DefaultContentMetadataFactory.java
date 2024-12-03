/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.extension.MetadataProperty
 *  com.atlassian.confluence.api.extension.ModelMetadataProvider
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.reference.BuilderUtils
 *  com.atlassian.confluence.api.model.reference.EnrichableMap
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.extension.MetadataProperty;
import com.atlassian.confluence.api.extension.ModelMetadataProvider;
import com.atlassian.confluence.api.impl.service.content.factory.ContentMetadataFactory;
import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.DeletedUser;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class DefaultContentMetadataFactory
implements ContentMetadataFactory {
    private static final String ANONYMOUS_USER_FLAG = "anonymousUser";
    private static final String DELETED_USER_FLAG = "deletedUser";
    private static final Logger log = LoggerFactory.getLogger(DefaultContentMetadataFactory.class);
    private final PluginAccessor pluginAccessor;
    private final PlatformTransactionManager transactionManager;

    public DefaultContentMetadataFactory(PluginAccessor pluginAccessor, PlatformTransactionManager transactionManager) {
        this.pluginAccessor = pluginAccessor;
        this.transactionManager = transactionManager;
    }

    @Override
    public Map<ContentEntityObject, Map<String, Object>> buildMetadataForContentEntityObjects(Map<ContentEntityObject, Supplier<Content>> contentMap, Fauxpansions fauxpansions) {
        if (contentMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Object, Map<String, ?>> providerResults = this.getAllMetadataFromProviders(contentMap, fauxpansions);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<ContentEntityObject, Supplier<Content>> entry : contentMap.entrySet()) {
            ContentEntityObject ceo = entry.getKey();
            Map<String, ?> providerResult = providerResults.get(entry.getValue().get());
            Map<String, Object> contentMetadata = this.buildContentMetadata(ceo, providerResult, fauxpansions);
            builder.put((Object)entry.getKey(), contentMetadata);
        }
        return builder.build();
    }

    private Map<Object, Map<String, ?>> getAllMetadataFromProviders(Map<ContentEntityObject, Supplier<Content>> contentMap, Fauxpansions fauxpansions) {
        if (!fauxpansions.canExpand()) {
            return Collections.emptyMap();
        }
        List providers = this.pluginAccessor.getEnabledModulesByClass(ModelMetadataProvider.class);
        HashMap<Object, ModelMapBuilder<String, Object>> metadata = new HashMap<Object, ModelMapBuilder<String, Object>>();
        ImmutableList.Builder providerCollapsedProperties = ImmutableList.builder();
        for (ModelMetadataProvider provider : providers) {
            try {
                TransactionTemplate template = new TransactionTemplate(this.transactionManager);
                template.setPropagationBehavior(0);
                template.setName("tx-metadata-provider");
                template.setReadOnly(true);
                template.execute(status -> {
                    if (this.hasMetadataExpansions(fauxpansions.getSubExpansions(), provider, (ImmutableList.Builder<String>)providerCollapsedProperties)) {
                        this.addMetadataFromProvider(provider, contentMap, fauxpansions.getSubExpansions(), metadata);
                    }
                    return null;
                });
            }
            catch (Exception ex) {
                log.warn("API metadata provider module threw an exception attempting to expand metadata. Class : {} provides properties {}. Exception {}", new Object[]{provider.getClass().getName(), DefaultContentMetadataFactory.getMetadataProperties(provider), ex.toString()});
                log.debug("More details : ", (Throwable)ex);
            }
        }
        this.addProviderCollapsedEntries(contentMap, metadata, (List<String>)providerCollapsedProperties.build());
        return Maps.transformEntries(metadata, (k, v) -> v.build());
    }

    private boolean hasMetadataExpansions(Expansions subExpansions, ModelMetadataProvider provider, ImmutableList.Builder<String> providerCollapsedProperties) {
        boolean result = false;
        for (String property : DefaultContentMetadataFactory.getMetadataProperties(provider)) {
            if (subExpansions.canExpand(property)) {
                result = true;
                continue;
            }
            providerCollapsedProperties.add((Object)property);
        }
        return result;
    }

    private void addMetadataFromProvider(ModelMetadataProvider provider, Map<ContentEntityObject, Supplier<Content>> contentMap, Expansions subExpansions, Map<Object, ModelMapBuilder<String, Object>> allMetadata) {
        Map<Object, Map<String, ?>> providerMetadata = DefaultContentMetadataFactory.getMetadataFromProvider(provider, Iterables.transform(contentMap.values(), Supplier::get), subExpansions);
        this.copyFromProviderMetaData(providerMetadata, allMetadata);
    }

    private static Map<Object, Map<String, ?>> getMetadataFromProvider(ModelMetadataProvider provider, Iterable<Object> entities, Expansions expansions) {
        return provider.getMetadataForAll(entities, expansions);
    }

    private void addProviderCollapsedEntries(Map<ContentEntityObject, Supplier<Content>> contentMap, Map<Object, ModelMapBuilder<String, Object>> metadata, List<String> providerCollapsedProperties) {
        ModelMapBuilder allCollapsed = ModelMapBuilder.newInstance().addCollapsedEntries(providerCollapsedProperties);
        for (Supplier<Content> content : contentMap.values()) {
            ModelMapBuilder<String, Object> builder = metadata.get(content.get());
            if (builder == null) {
                metadata.put(content.get(), (ModelMapBuilder<String, Object>)allCollapsed);
                continue;
            }
            builder.addCollapsedEntries(providerCollapsedProperties);
        }
    }

    private void copyFromProviderMetaData(Map<Object, Map<String, ?>> providerMetadata, Map<Object, ModelMapBuilder<String, Object>> allMetadata) {
        for (Map.Entry<Object, Map<String, ?>> entry : providerMetadata.entrySet()) {
            ModelMapBuilder modelMap = allMetadata.get(entry.getKey());
            if (modelMap == null) {
                modelMap = ModelMapBuilder.newExpandedInstance();
                allMetadata.put(entry.getKey(), (ModelMapBuilder<String, Object>)modelMap);
            }
            DefaultContentMetadataFactory.putAllIncludingCollapsed(entry.getValue(), (ModelMapBuilder<String, Object>)modelMap);
        }
    }

    private Map<String, Object> buildContentMetadata(ContentEntityObject ceo, Map<String, ?> providerResult, Fauxpansions fauxpansions) {
        ModelMapBuilder metadata = ModelMapBuilder.newExpandedInstance();
        if (ceo instanceof Attachment) {
            Attachment attachment = (Attachment)ceo;
            this.addVersionComment(attachment, (ModelMapBuilder<String, Object>)metadata);
            this.addMediaType(attachment, (ModelMapBuilder<String, Object>)metadata);
        }
        if (ceo.isDeleted()) {
            this.addTrashDateMetadata(ceo.getProperties(), (ModelMapBuilder<String, Object>)metadata);
            this.addTrashCreatedByMetadata(ceo, (ModelMapBuilder<String, Object>)metadata);
            this.addTrashDeletedByMetadata(ceo.getProperties(), (ModelMapBuilder<String, Object>)metadata);
        }
        if (!fauxpansions.canExpand()) {
            return ceo instanceof Attachment || ceo.isDeleted() ? metadata.build() : BuilderUtils.collapsedMap();
        }
        DefaultContentMetadataFactory.putAllIncludingCollapsed(providerResult, (ModelMapBuilder<String, Object>)metadata);
        return metadata.build();
    }

    private void addVersionComment(Attachment attachment, ModelMapBuilder<String, Object> metadata) {
        String versionComment = attachment.getVersionComment();
        if (StringUtils.isNotEmpty((CharSequence)versionComment)) {
            metadata.put((Object)"comment", (Object)versionComment);
        }
    }

    private void addMediaType(Attachment attachment, ModelMapBuilder<String, Object> metadata) {
        metadata.put((Object)"mediaType", (Object)attachment.getMediaType());
    }

    private static void putAllIncludingCollapsed(Map<String, ?> source, ModelMapBuilder<String, Object> target) {
        target.putAll(source);
        if (source instanceof EnrichableMap) {
            target.addCollapsedEntries(((EnrichableMap)source).getCollapsedEntries());
        }
    }

    private static List<String> getMetadataProperties(ModelMetadataProvider provider) {
        List properties = provider.getProperties();
        if (properties == null) {
            return provider.getMetadataProperties();
        }
        return provider.getProperties().stream().map(MetadataProperty::getPropertyName).collect(Collectors.toList());
    }

    private void addTrashDateMetadata(ContentProperties properties, ModelMapBuilder<String, Object> metadata) {
        long trashDateLong = properties.getLongProperty("trash-date", -1L);
        if (trashDateLong != -1L) {
            metadata.put((Object)"trashdate", (Object)trashDateLong);
        }
    }

    @VisibleForTesting
    void addTrashCreatedByMetadata(ContentEntityObject ceo, ModelMapBuilder<String, Object> metadata) {
        ConfluenceUser creator = ceo.getCreator();
        this.addUserMetadata(creator, "createdByUsername", "createdByFullName", metadata);
    }

    @VisibleForTesting
    void addTrashDeletedByMetadata(ContentProperties properties, ModelMapBuilder<String, Object> metadata) {
        String userKey = properties.getStringProperty("deleted-by");
        if (userKey != null) {
            if (ANONYMOUS_USER_FLAG.equals(userKey)) {
                metadata.put((Object)"deletedByUsername", (Object)ANONYMOUS_USER_FLAG);
            } else if (!userKey.isEmpty()) {
                ConfluenceUser deletedByUser = FindUserHelper.getUserByUserKey(new UserKey(userKey.toString()));
                this.addUserMetadata(deletedByUser, "deletedByUsername", "deletedByFullName", metadata);
            }
        }
    }

    private void addUserMetadata(ConfluenceUser user, String usernameKey, String fullNameKey, ModelMapBuilder<String, Object> metadata) {
        if (user != null) {
            if (DeletedUser.isDeletedUser(user)) {
                metadata.put((Object)usernameKey, (Object)DELETED_USER_FLAG);
                metadata.put((Object)fullNameKey, (Object)user.getFullName());
            } else if (!UnknownUser.isUnknownUser(user)) {
                metadata.put((Object)usernameKey, (Object)user.getName());
                metadata.put((Object)fullNameKey, (Object)user.getFullName());
            }
        }
    }
}

