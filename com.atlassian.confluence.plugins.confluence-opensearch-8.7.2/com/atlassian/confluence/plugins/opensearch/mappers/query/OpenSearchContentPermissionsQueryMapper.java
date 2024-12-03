/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.ContentPermissionCalculator
 *  com.atlassian.confluence.search.v2.SearchFieldMappings
 *  com.atlassian.confluence.search.v2.query.ContentPermissionsQuery
 *  org.opensearch.client.opensearch._types.FieldValue
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.ContentPermissionCalculator;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.query.ContentPermissionsQuery;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public class OpenSearchContentPermissionsQueryMapper
implements OpenSearchQueryMapper<ContentPermissionsQuery> {
    private final ContentPermissionCalculator contentPermissionCalculator;

    public OpenSearchContentPermissionsQueryMapper(ContentPermissionCalculator contentPermissionCalculator) {
        this.contentPermissionCalculator = contentPermissionCalculator;
    }

    @Override
    public Query mapQueryToOpenSearch(ContentPermissionsQuery query) {
        ArrayList<String> encodedPermissions = new ArrayList<String>();
        if (query.getUserKey() != null) {
            encodedPermissions.add(this.contentPermissionCalculator.getEncodedUserKey(query.getUserKey()));
            for (String group : query.getGroupNames()) {
                encodedPermissions.add(this.contentPermissionCalculator.getEncodedGroupName(group));
            }
        }
        Query termsQuery = Query.of(q -> q.terms(t -> t.field(SearchFieldMappings.CONTENT_PERMISSION_SETS.getFullName()).terms(ts -> ts.value(encodedPermissions.stream().map(FieldValue::of).collect(Collectors.toList())))));
        return Query.of(q -> q.bool(b -> b.mustNot(m -> m.nested(n -> n.path(SearchFieldMappings.CONTENT_PERMISSION_SETS.getName()).query(qq -> qq.bool(bb -> bb.mustNot(termsQuery, new Query[0])))))));
    }

    @Override
    public String getKey() {
        return "contentPermissions";
    }
}

