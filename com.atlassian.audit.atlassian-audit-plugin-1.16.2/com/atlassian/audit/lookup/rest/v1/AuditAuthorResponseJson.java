/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.entity.AuditAuthor
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.UriInfo
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.lookup.rest.v1;

import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.rest.model.AuditAuthorJson;
import com.atlassian.audit.rest.model.RestPage;
import com.atlassian.audit.rest.model.RestPageCursor;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditAuthorResponseJson
extends RestPage<AuditAuthorJson, String> {
    public AuditAuthorResponseJson(Page<AuditAuthor, String> page, Function<AuditAuthor, AuditAuthorJson> restTransform, String baseUrl, UriInfo uriInfo) {
        super(page, restTransform, baseUrl, uriInfo);
    }

    @Nonnull
    @JsonProperty(value="resources")
    public List<AuditAuthorJson> getEntities() {
        return super.getValues();
    }

    @Override
    @Nonnull
    protected RestPageCursor serializeCursor(@Nonnull String cursor) {
        return () -> cursor;
    }
}

