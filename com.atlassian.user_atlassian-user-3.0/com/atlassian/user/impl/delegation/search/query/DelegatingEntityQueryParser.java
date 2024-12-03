/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.delegation.search.query;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.DefaultSearchResult;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.query.AllRepositoriesQueryContext;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.QueryContext;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DelegatingEntityQueryParser
implements EntityQueryParser {
    private static final Logger log = Logger.getLogger(DelegatingEntityQueryParser.class);
    private final List<EntityQueryParser> entityQueryParsers;

    public DelegatingEntityQueryParser(List<EntityQueryParser> entityQueryParsers) {
        this.entityQueryParsers = entityQueryParsers;
    }

    public SearchResult findUsers(Query query) throws EntityException {
        return this.findUsers(query, (QueryContext)new AllRepositoriesQueryContext());
    }

    public SearchResult findGroups(Query query) throws EntityException {
        return this.findGroups(query, (QueryContext)new AllRepositoriesQueryContext());
    }

    public SearchResult findUsers(Query query, QueryContext context) throws EntityException {
        Iterator<EntityQueryParser> iter = this.entityQueryParsers.iterator();
        DefaultSearchResult<User> result = new DefaultSearchResult<User>();
        boolean withinContext = false;
        while (iter.hasNext()) {
            EntityQueryParser parser = iter.next();
            SearchResult<User> returned = null;
            try {
                returned = parser.findUsers(query, context);
            }
            catch (EntityException e) {
                log.info((Object)e.getMessage());
            }
            if (returned == null) continue;
            withinContext = true;
            for (String repokey : returned.repositoryKeyset()) {
                result.addToResults(repokey, returned.pager(repokey));
            }
        }
        return withinContext ? result : null;
    }

    public SearchResult findGroups(Query query, QueryContext context) throws EntityException {
        Iterator<EntityQueryParser> iter = this.entityQueryParsers.iterator();
        DefaultSearchResult<Group> result = new DefaultSearchResult<Group>();
        boolean withinContext = false;
        while (iter.hasNext()) {
            EntityQueryParser parser = iter.next();
            SearchResult<Group> returned = null;
            try {
                returned = parser.findGroups(query, context);
            }
            catch (EntityException e) {
                log.info((Object)e.getMessage());
            }
            if (returned == null) continue;
            withinContext = true;
            for (String repokey : returned.repositoryKeyset()) {
                result.addToResults(repokey, returned.pager(repokey));
            }
        }
        return withinContext ? result : null;
    }
}

