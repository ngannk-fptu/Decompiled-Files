/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditEntityCursor
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.entity.AuditEntity
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.UriInfo
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.rest.model.AuditEntityJson;
import com.atlassian.audit.rest.model.RestAuditEntityCursor;
import com.atlassian.audit.rest.model.RestPage;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditEntitiesResponseJson
extends RestPage<AuditEntityJson, AuditEntityCursor> {
    public AuditEntitiesResponseJson(Page<AuditEntity, AuditEntityCursor> page, Function<AuditEntity, AuditEntityJson> restTransform, @Nullable String baseUrl, @Nullable UriInfo uriInfo) {
        super(page, restTransform, baseUrl, uriInfo);
    }

    @JsonCreator
    public AuditEntitiesResponseJson(@JsonProperty(value="entities") List<AuditEntityJson> entities, @JsonProperty(value="pagingInfo") Map<String, Object> ignored) {
        super(entities);
    }

    @Nonnull
    @JsonProperty(value="entities")
    public List<AuditEntityJson> getEntities() {
        return super.getValues();
    }

    @Nonnull
    protected RestAuditEntityCursor serializeCursor(@Nonnull AuditEntityCursor cursor) {
        return new RestAuditEntityCursor(Objects.requireNonNull(cursor, "cursor"));
    }
}

