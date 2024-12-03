/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.FindRecentlyUsedSpacesQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class FindRecentlyUsedSpacesQuery
implements GrQuery<FindRecentlyUsedSpacesQueryResult>,
L1AssessmentQuery<FindRecentlyUsedSpacesQueryResult> {
    private final EntityManagerTemplate tmpl;

    public FindRecentlyUsedSpacesQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.RECENTLY_USED_SPACES.name();
    }

    @Override
    public FindRecentlyUsedSpacesQueryResult execute() {
        String query = "select s.key as spacekey, s.status as spacestatus, s.type as spacetype, count(*) as numberOfContent, (select count(*) from Content nc where nc.spaceId = s.id and nc.lastModDate > current_date - 180) as spaceLastModified, (select count(s) from s where s.key in (select spaceKey from RecentlyViewed group by contentId, spaceKey having max(lastViewDate) > current_date - 180)) as numberOfUniqueUsersVisited FROM Content c join Space s on c.spaceId = s.id where c.type = 'PAGE' and c.previousVersion is null and c.status = 'current' group by s.id, s.key, s.name, s.status, s.type";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).list();
        return new FindRecentlyUsedSpacesQueryResult(result);
    }
}

