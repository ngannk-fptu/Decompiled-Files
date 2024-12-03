/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.content.ContentProperties
 *  com.atlassian.confluence.content.apisupport.CommentExtensionsSupport
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentStatus
 *  com.atlassian.confluence.pages.CommentStatus$Builder
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.inlinecomments.extensions;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.content.apisupport.CommentExtensionsSupport;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentStatus;
import com.atlassian.confluence.plugins.inlinecomments.models.InlineCreationProperties;
import com.atlassian.confluence.plugins.inlinecomments.models.InlineProperties;
import com.atlassian.confluence.plugins.inlinecomments.models.Resolution;
import com.atlassian.confluence.plugins.inlinecomments.service.InlineCommentPropertyManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;

public class InlineCommentExtensionsSupport
implements CommentExtensionsSupport {
    private static final String LOCATION = "location";
    private static final String INLINE_PROPERTIES = "inlineProperties";
    private static final String RESOLUTION = "resolution";
    private final PersonService personService;
    private final InlineCommentPropertyManager propertyManager;

    public InlineCommentExtensionsSupport(PersonService personService, InlineCommentPropertyManager propertyManager) {
        this.personService = personService;
        this.propertyManager = propertyManager;
    }

    public Iterable<ContentType> getCommentContainerType() {
        return ImmutableList.of((Object)ContentType.BLOG_POST, (Object)ContentType.PAGE);
    }

    public Map<ContentId, Map<String, Object>> getExtensions(Iterable<Comment> comments, Expansions expansions) {
        ImmutableMap.Builder extensionsByContentId = ImmutableMap.builder();
        for (Comment comment : comments) {
            ImmutableMap.Builder propertyToValueBuilder = ImmutableMap.builder();
            String location = comment.isInlineComment() ? "inline" : "footer";
            propertyToValueBuilder.put((Object)LOCATION, (Object)location);
            ContentProperties commentProperties = comment.getProperties();
            if (comment.isInlineComment()) {
                if (expansions.canExpand(INLINE_PROPERTIES)) {
                    String originalSelection = Strings.nullToEmpty((String)commentProperties.getStringProperty("inline-original-selection"));
                    String markerRef = Strings.nullToEmpty((String)commentProperties.getStringProperty("inline-marker-ref"));
                    InlineProperties inlineProperties = new InlineProperties.Builder().setMarkerRef(markerRef).setOriginalSelection(originalSelection).build();
                    propertyToValueBuilder.put((Object)INLINE_PROPERTIES, (Object)Reference.to((Object)inlineProperties));
                } else {
                    propertyToValueBuilder.put((Object)INLINE_PROPERTIES, (Object)Reference.collapsed(InlineProperties.class));
                }
            }
            if (expansions.canExpand(RESOLUTION)) {
                this.expandResolution(comment, (ImmutableMap.Builder<String, Object>)propertyToValueBuilder);
            } else {
                propertyToValueBuilder.put((Object)RESOLUTION, (Object)Reference.collapsed(Resolution.class));
            }
            extensionsByContentId.put((Object)comment.getContentId(), (Object)propertyToValueBuilder.build());
        }
        return extensionsByContentId.build();
    }

    private void expandResolution(Comment comment, ImmutableMap.Builder<String, Object> propertyToValueBuilder) {
        CommentStatus commentStatus = comment.getStatus();
        if (commentStatus != null) {
            String userName = commentStatus.getLastModifier();
            UserKey userKey = userName != null ? new UserKey(userName) : null;
            Person person = (Person)this.personService.find(new Expansion[0]).withUserKey(userKey).fetchOne().get();
            Resolution resolution = new Resolution.Builder().setStatus(commentStatus.getValue().getStringValue()).setLastModifier(person).setLastModifiedDate(new DateTime((Object)commentStatus.getLastModifiedDate())).build();
            propertyToValueBuilder.put((Object)RESOLUTION, (Object)Reference.to((Object)resolution));
        } else {
            propertyToValueBuilder.put((Object)RESOLUTION, (Object)Reference.collapsed(Resolution.class));
        }
    }

    public Map<String, Optional<String>> expansions() {
        HashMap expansions = Maps.newHashMap();
        expansions.put(INLINE_PROPERTIES, Optional.empty());
        expansions.put(RESOLUTION, Optional.empty());
        return expansions;
    }

    public ValidationResult validateExtensionsForCreate(Map<String, Object> extensions, SimpleValidationResult.Builder builder) {
        if (!this.isInlineComment(extensions)) {
            return builder.build();
        }
        InlineCreationProperties properties = (InlineCreationProperties)new ObjectMapper().convertValue(extensions.get(INLINE_PROPERTIES), InlineCreationProperties.class);
        if (properties == null) {
            return builder.build();
        }
        if (properties.getMatchIndex() == null) {
            builder.addError("matchIndex", new Object[0]);
        }
        if (properties.getLastFetchTime() == null) {
            builder.addError("lastFetchTime", new Object[0]);
        }
        if (StringUtils.isBlank((CharSequence)properties.getOriginalSelection())) {
            builder.addError("originalSelection", new Object[0]);
        }
        if (StringUtils.isBlank((CharSequence)properties.getSerializedHighlights())) {
            builder.addError("serializedHighlights", new Object[0]);
        }
        return builder.build();
    }

    public ValidationResult validateExtensionsForUpdate(Comment comment, Map<String, Object> extensions, SimpleValidationResult.Builder builder) {
        return builder.build();
    }

    public void updateExtensionsOnEntity(Comment comment, Map<String, Object> extensions) {
        if (!this.isInlineComment(extensions)) {
            return;
        }
        if (comment.isNew()) {
            try {
                InlineCreationProperties properties = (InlineCreationProperties)new ObjectMapper().convertValue(extensions.get(INLINE_PROPERTIES), InlineCreationProperties.class);
                this.propertyManager.setProperties(comment, properties);
            }
            catch (Exception e) {
                throw new BadRequestException("Can not create inline comment", (Throwable)e);
            }
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            Resolution resolution = (Resolution)objectMapper.convertValue(extensions.get(RESOLUTION), Resolution.class);
            if (resolution != null) {
                comment.setStatus(new CommentStatus.Builder().setValue(resolution.getStatus()).setLastModifider(AuthenticatedUserThreadLocal.get().getName()).setLastModifiedDate(Long.valueOf(new Date().getTime())).build());
            }
        }
    }

    private boolean isInlineComment(Map<String, Object> extensions) {
        return extensions != null && "inline".equals(extensions.get(LOCATION));
    }
}

