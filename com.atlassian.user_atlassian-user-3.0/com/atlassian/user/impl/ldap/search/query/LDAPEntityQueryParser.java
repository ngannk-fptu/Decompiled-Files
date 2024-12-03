/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  net.sf.ldaptemplate.support.LdapEncoder
 */
package com.atlassian.user.impl.ldap.search.query;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.impl.ldap.DefaultLDAPGroupFactory;
import com.atlassian.user.impl.ldap.LDAPGroupFactory;
import com.atlassian.user.impl.ldap.LDAPUserFactory;
import com.atlassian.user.impl.ldap.LiteralFilter;
import com.atlassian.user.impl.ldap.adaptor.LDAPGroupAdaptor;
import com.atlassian.user.impl.ldap.properties.LdapMembershipProperties;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.DefaultLDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.impl.ldap.search.LDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LdapFilterFactory;
import com.atlassian.user.impl.ldap.search.page.LDAPEntityPager;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.DefaultSearchResult;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.query.BooleanQuery;
import com.atlassian.user.search.query.EmailTermQuery;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.search.query.FullNameTermQuery;
import com.atlassian.user.search.query.GroupNameTermQuery;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.QueryContext;
import com.atlassian.user.search.query.TermQuery;
import com.atlassian.user.search.query.UserNameTermQuery;
import com.atlassian.util.profiling.UtilTimerStack;
import net.sf.ldaptemplate.support.LdapEncoder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LDAPEntityQueryParser
implements EntityQueryParser {
    public static final String OPEN_PARAN = "(";
    public static final String CLOSE_PARAN = ")";
    public static final String EQ = "=";
    public static final String AND = "&";
    public static final String OR = "|";
    public static final String WILDCARD = "*";
    private final LdapContextFactory repository;
    private final LDAPUserAdaptor userAdaptor;
    private final LDAPGroupAdaptor groupAdaptor;
    private final LDAPGroupFactory groupFactory;
    private final LDAPUserFactory userFactory;
    private final RepositoryIdentifier repositoryIdentifier;
    private final LdapSearchProperties searchProperties;
    private final LdapMembershipProperties membershipProperties;

    public LDAPEntityQueryParser(LdapContextFactory repository, LDAPGroupAdaptor groupAdaptor, RepositoryIdentifier repositoryIdentifier, LDAPUserFactory userFactory, LdapSearchProperties searchProperties, LdapMembershipProperties membershipProperties, LdapFilterFactory filterFactory) {
        this.repositoryIdentifier = repositoryIdentifier;
        this.repository = repository;
        this.groupAdaptor = groupAdaptor;
        this.userFactory = userFactory;
        this.userAdaptor = new DefaultLDAPUserAdaptor(this.repository, searchProperties, filterFactory);
        this.groupFactory = new DefaultLDAPGroupFactory(searchProperties, membershipProperties);
        this.searchProperties = searchProperties;
        this.membershipProperties = membershipProperties;
    }

    @Override
    public SearchResult<User> findUsers(Query<User> query) throws EntityException {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_findUsers"));
        }
        String parsedQuery = null;
        parsedQuery = this.directQuery(query, parsedQuery);
        LDAPPagerInfo info = this.userAdaptor.search(new LiteralFilter(parsedQuery));
        LDAPEntityPager<User> iter = new LDAPEntityPager<User>(this.searchProperties, this.repository, this.userFactory, info);
        DefaultSearchResult<User> searchResult = new DefaultSearchResult<User>(iter, this.repositoryIdentifier.getKey());
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(this.getClass().getName() + "_findUsers"));
        }
        return searchResult;
    }

    @Override
    public SearchResult<Group> findGroups(Query<Group> query) throws EntityException {
        String parsedQuery = this.directQuery(query, null);
        LDAPPagerInfo info = this.groupAdaptor.search(new LiteralFilter(parsedQuery));
        LDAPEntityPager<Group> pager = new LDAPEntityPager<Group>(this.searchProperties, this.repository, this.groupFactory, info);
        return new DefaultSearchResult<Group>(pager, this.repositoryIdentifier.getKey());
    }

    @Override
    public SearchResult<User> findUsers(Query<User> query, QueryContext context) throws EntityException {
        if (!context.contains(this.repositoryIdentifier)) {
            return null;
        }
        return this.findUsers(query);
    }

    @Override
    public SearchResult<Group> findGroups(Query<Group> query, QueryContext context) throws EntityException {
        if (!context.contains(this.repositoryIdentifier)) {
            return null;
        }
        return this.findGroups(query);
    }

    private String directQuery(Query query, String defaultQuery) throws EntityException {
        if (query instanceof TermQuery) {
            StringBuffer parsedQueryStringBuffer = this.parseQuery((TermQuery)query);
            return parsedQueryStringBuffer.toString();
        }
        if (query instanceof BooleanQuery) {
            return this.parseQuery((BooleanQuery)query).toString();
        }
        return defaultQuery;
    }

    public StringBuffer parseQuery(BooleanQuery query) throws EntityException {
        StringBuffer parsedClause = new StringBuffer();
        parsedClause.append(OPEN_PARAN);
        if (query.isAND()) {
            parsedClause.append(AND);
        } else {
            parsedClause.append(OR);
        }
        for (Query foundQuery : query.getQueries()) {
            if (foundQuery instanceof BooleanQuery) {
                parsedClause.append(this.parseQuery((BooleanQuery)foundQuery));
                continue;
            }
            parsedClause.append(this.parseQuery((TermQuery)foundQuery));
        }
        parsedClause.append(CLOSE_PARAN);
        return parsedClause;
    }

    public StringBuffer parseQuery(TermQuery q) throws EntityException {
        StringBuffer parsedQuery = null;
        if (q instanceof UserNameTermQuery) {
            parsedQuery = this.parseTermQuery(q, this.searchProperties.getUsernameAttribute());
        } else if (q instanceof GroupNameTermQuery) {
            parsedQuery = this.parseTermQuery(q, this.searchProperties.getGroupnameAttribute());
        } else if (q instanceof EmailTermQuery) {
            parsedQuery = this.parseTermQuery(q, this.searchProperties.getEmailAttribute());
        } else if (q instanceof FullNameTermQuery) {
            parsedQuery = this.parseFullNameTermQuery(q);
        }
        return parsedQuery;
    }

    private StringBuffer parseFullNameTermQuery(TermQuery q) {
        StringBuffer query = new StringBuffer();
        query.insert(0, OR);
        query.insert(0, OPEN_PARAN);
        query.append(this.parseTermQuery(q, this.searchProperties.getFirstnameAttribute()));
        query.append(this.parseTermQuery(q, this.searchProperties.getSurnameAttribute()));
        query.append(CLOSE_PARAN);
        return query;
    }

    private StringBuffer parseTermQuery(TermQuery q, String attributeType) {
        StringBuffer parsedQuery = new StringBuffer();
        parsedQuery.append(OPEN_PARAN);
        parsedQuery.append(attributeType);
        parsedQuery.append(EQ);
        if (q.isMatchingSubstring() && (q.getMatchingRule().equals("ends_with") || q.getMatchingRule().equals("contains"))) {
            parsedQuery.append(WILDCARD);
        }
        parsedQuery.append(LdapEncoder.filterEncode((String)q.getTerm()));
        if (q.isMatchingSubstring() && (q.getMatchingRule().equals("starts_with") || q.getMatchingRule().equals("contains"))) {
            parsedQuery.append(WILDCARD);
        }
        parsedQuery.append(CLOSE_PARAN);
        return parsedQuery;
    }
}

