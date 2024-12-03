/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.JsonString
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty$SpacePropertyBuilder
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 *  com.atlassian.confluence.api.service.content.SpacePropertyService
 *  com.atlassian.confluence.api.service.exceptions.InternalServerException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.event.api.EventPublisher
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.retention.manager;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.api.service.content.SpacePropertyService;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.event.events.retention.SpaceRetentionPolicyChangedEvent;
import com.atlassian.confluence.event.events.retention.SpaceRetentionPolicyCreatedEvent;
import com.atlassian.confluence.event.events.retention.SpaceRetentionPolicyDeletedEvent;
import com.atlassian.confluence.impl.retention.SpacePropertyServiceProvider;
import com.atlassian.confluence.impl.retention.manager.SpaceRetentionPolicyManager;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSpaceRetentionPolicyManager
implements SpaceRetentionPolicyManager {
    public static final String SPACE_RETENTION_POLICY_KEY = "com.atlassian.confluence.impl.content.retentionrules:space-retention-policy";
    private static final Logger logger = LoggerFactory.getLogger(DefaultSpaceRetentionPolicyManager.class);
    private static final String FAILED_TO_PARSE_SPACE_RETENTION_POLICY = "Failed to parse SpaceRetentionPolicy";
    private static final String PROPERTY_EXPANSIONS = "space,version";
    private final SpaceManagerInternal spaceManager;
    private final SpacePropertyServiceProvider spacePropertyServiceProvider;
    private final PermissionManager permissionManager;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public DefaultSpaceRetentionPolicyManager(SpaceManagerInternal spaceManager, SpacePropertyServiceProvider spacePropertyServiceProvider, PermissionManager permissionManager, EventPublisher eventPublisher) {
        this(spaceManager, spacePropertyServiceProvider, permissionManager, eventPublisher, new ObjectMapper());
    }

    protected DefaultSpaceRetentionPolicyManager(SpaceManagerInternal spaceManager, SpacePropertyServiceProvider spacePropertyServiceProvider, PermissionManager permissionManager, EventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.eventPublisher = eventPublisher;
        this.spaceManager = spaceManager;
        this.spacePropertyServiceProvider = spacePropertyServiceProvider;
        this.permissionManager = permissionManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<SpaceRetentionPolicy> getPolicy(String spaceKey) {
        Optional<JsonSpaceProperty> spaceProperty = this.getSpaceRetentionPolicyProperty(spaceKey);
        if (spaceProperty.isPresent()) {
            return Optional.ofNullable(this.parseSpaceProperty(spaceProperty.get()));
        }
        logger.debug("No SpaceRetentionPolicies found for space with key {}", (Object)spaceKey);
        return Optional.empty();
    }

    @Override
    public Optional<SpaceRetentionPolicy> getPolicy(long spaceId) {
        return Optional.ofNullable(this.spaceManager.getSpace(spaceId)).flatMap(space -> this.getPolicy(space.getKey()));
    }

    @Override
    public void deletePolicy(String spaceKey) {
        Optional<JsonSpaceProperty> existingProperty = this.getSpaceRetentionPolicyProperty(spaceKey);
        if (!existingProperty.isPresent()) {
            throw new NotFoundException("Space not found: " + spaceKey);
        }
        this.getSpacePropertyService().delete(existingProperty.get());
        SpaceRetentionPolicy oldPolicy = this.parseSpaceProperty(existingProperty.get());
        Space space = this.getSpace(spaceKey);
        this.eventPublisher.publish((Object)new SpaceRetentionPolicyDeletedEvent(oldPolicy, space));
    }

    @Override
    public void savePolicy(String spaceKey, SpaceRetentionPolicy newPolicy) {
        Space space = this.getSpace(spaceKey);
        if (space != null) {
            newPolicy.setLastModifiedBy(this.getAuthenticatedUserKey());
            Optional<JsonSpaceProperty> existingProperty = this.getSpaceRetentionPolicyProperty(spaceKey);
            if (!existingProperty.isPresent()) {
                this.createPolicy(spaceKey, newPolicy);
                this.eventPublisher.publish((Object)new SpaceRetentionPolicyCreatedEvent(newPolicy, space));
            } else {
                this.updatePolicy(spaceKey, newPolicy, this.parseSpacePropertyVersion(existingProperty.get()));
                SpaceRetentionPolicy oldPolicy = this.parseSpaceProperty(existingProperty.get());
                this.eventPublisher.publish((Object)new SpaceRetentionPolicyChangedEvent(oldPolicy, newPolicy, space));
            }
        } else {
            throw new NotFoundException("Space not found: " + spaceKey);
        }
    }

    private void updatePolicy(String spaceKey, SpaceRetentionPolicy newPolicy, int nextVersionNumber) {
        JsonSpaceProperty newSpaceProperty = this.createSpaceRetentionExemptionProperty(spaceKey, newPolicy, nextVersionNumber).build();
        this.getSpacePropertyService().update(newSpaceProperty);
    }

    private void createPolicy(String spaceKey, SpaceRetentionPolicy newPolicy) {
        JsonSpaceProperty newSpaceProperty = this.createSpaceRetentionExemptionProperty(spaceKey, newPolicy, 1).build();
        this.getSpacePropertyService().create(newSpaceProperty);
    }

    protected SpacePropertyService getSpacePropertyService() {
        return this.spacePropertyServiceProvider.get();
    }

    private Space getSpace(String spaceKey) {
        return this.spaceManager.getSpace(spaceKey);
    }

    private JsonSpaceProperty.SpacePropertyBuilder createSpaceRetentionExemptionProperty(String spaceKey, SpaceRetentionPolicy newPolicy, int versionNumber) {
        try {
            String policyAsString = this.objectMapper.writeValueAsString((Object)newPolicy);
            return (JsonSpaceProperty.SpacePropertyBuilder)((JsonSpaceProperty.SpacePropertyBuilder)((JsonSpaceProperty.SpacePropertyBuilder)JsonSpaceProperty.builder().space(Reference.to((Object)com.atlassian.confluence.api.model.content.Space.builder().key(spaceKey).build())).key(SPACE_RETENTION_POLICY_KEY)).version(Reference.to((Object)Version.builder().number(versionNumber).build()))).value(new JsonString(policyAsString));
        }
        catch (IOException e) {
            logger.error("Error parsing SpaceRetentionPolicy");
            throw new InternalServerException(FAILED_TO_PARSE_SPACE_RETENTION_POLICY, (Throwable)e);
        }
    }

    private Optional<JsonSpaceProperty> getSpaceRetentionPolicyProperty(String spaceKey) {
        AtomicReference property = new AtomicReference();
        this.permissionManager.withExemption(() -> {
            Expansion[] expansions = ExpansionsParser.parse((String)PROPERTY_EXPANSIONS);
            property.set((JsonSpaceProperty)this.getSpacePropertyService().find(expansions).withSpaceKey(spaceKey).withPropertyKey(SPACE_RETENTION_POLICY_KEY).fetchOrNull());
        });
        return Optional.ofNullable((JsonSpaceProperty)property.get());
    }

    private String getAuthenticatedUserKey() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return user.getKey().getStringValue();
    }

    private SpaceRetentionPolicy parseSpaceProperty(JsonSpaceProperty property) {
        try {
            JsonString propertyValue = property.getValue();
            if (propertyValue == null) {
                return null;
            }
            String rawValue = propertyValue.getValue();
            if (rawValue != null) {
                SpaceRetentionPolicy policy = (SpaceRetentionPolicy)this.objectMapper.readValue(rawValue, SpaceRetentionPolicy.class);
                OffsetDateTime lastModified = this.parseLastSpaceRetentionPropertyModifiedDate(property);
                if (lastModified != null) {
                    policy.setLastModifiedAt(lastModified);
                }
                return policy;
            }
        }
        catch (IOException e) {
            logger.error("Error parsing SpaceRetentionPolicy from JsonSpaceProperty");
            throw new InternalServerException(FAILED_TO_PARSE_SPACE_RETENTION_POLICY, (Throwable)e);
        }
        return null;
    }

    private int parseSpacePropertyVersion(JsonSpaceProperty property) {
        Version versionInfo = property.getVersion();
        if (versionInfo != null) {
            return versionInfo.getNumber() + 1;
        }
        return 1;
    }

    private OffsetDateTime parseLastSpaceRetentionPropertyModifiedDate(JsonSpaceProperty property) {
        Version version = property.getVersion();
        if (version != null) {
            return version.getWhenAt();
        }
        return null;
    }
}

