/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.base.Strings
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.confluence.plugins.cql.rest;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugins.cql.impl.SearchTypeManager;
import com.atlassian.confluence.plugins.cql.rest.CQLMetaDataService;
import com.atlassian.confluence.plugins.cql.rest.DisplayableType;
import com.atlassian.confluence.plugins.cql.rest.model.QueryExpression;
import com.atlassian.confluence.plugins.cql.rest.model.QueryField;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Strings;
import java.util.Collection;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/")
public class CQLMetaDataResource {
    public static final String EXPRESSIONS_PATH = "expressions";
    private final CQLMetaDataService metadataService;
    private final SearchTypeManager searchTypeManager;

    public CQLMetaDataResource(CQLMetaDataService metadataService, SearchTypeManager searchTypeManager) {
        this.metadataService = metadataService;
        this.searchTypeManager = searchTypeManager;
    }

    @Path(value="fields")
    @GET
    public Map<QueryField.FieldType, Iterable<QueryField>> fields(@QueryParam(value="filter") CQLMetaDataService.GetFieldsFilter filter) {
        return this.metadataService.getFields(filter);
    }

    @Path(value="contenttypes")
    @GET
    public Collection<DisplayableType> contentTypes(@QueryParam(value="category") @DefaultValue(value="content") String category) {
        switch (category.toLowerCase()) {
            case "all": {
                return this.searchTypeManager.getTypes().values();
            }
            case "content": {
                return this.searchTypeManager.getContentTypes().values();
            }
        }
        throw new BadRequestException("Unrecognised type category : " + category);
    }

    @Path(value="expressions")
    @GET
    public Iterable<QueryExpression> getExpressions(@QueryParam(value="cql") String cql) {
        if (Strings.isNullOrEmpty((String)cql)) {
            throw new BadRequestException("cql query param is required");
        }
        return this.metadataService.parseExpressions(cql, null);
    }

    private static class TypeCategories {
        private static final String ALL = "all";
        private static final String CONTENT = "content";
        private static final String SPACE = "space";
        private static final String USER = "user";

        private TypeCategories() {
        }
    }
}

