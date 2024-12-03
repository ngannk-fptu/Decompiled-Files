/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.search.query;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.util.InitializationCheck;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.DefaultSearchResult;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerUtils;
import com.atlassian.user.search.query.BooleanQuery;
import com.atlassian.user.search.query.EmailTermQuery;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.search.query.FullNameTermQuery;
import com.atlassian.user.search.query.GroupNameTermQuery;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.QueryContext;
import com.atlassian.user.search.query.QueryValidator;
import com.atlassian.user.search.query.TermQuery;
import com.atlassian.user.search.query.UserNameTermQuery;
import com.atlassian.user.search.query.match.ContainsIgnoreCaseMatcher;
import com.atlassian.user.search.query.match.EndsWithIgnoreCaseMatcher;
import com.atlassian.user.search.query.match.EqualsIgnoreCaseMatcher;
import com.atlassian.user.search.query.match.Matcher;
import com.atlassian.user.search.query.match.StartsWithIgnoreCaseMatcher;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultEntityQueryParser
implements EntityQueryParser {
    private static final Logger log = Logger.getLogger(DefaultEntityQueryParser.class);
    private final QueryValidator queryValidator = new QueryValidator();
    protected UserManager userManager;
    protected GroupManager groupManager;
    protected RepositoryIdentifier repository;
    protected Method entityNameMethod;
    protected Method emailMethod;
    protected Method fullnameMethod;
    private static final Class<User> userClass = User.class;

    public DefaultEntityQueryParser(RepositoryIdentifier repo, UserManager userManager, GroupManager groupManager) {
        try {
            this.entityNameMethod = userClass.getMethod("getName", new Class[0]);
            this.emailMethod = userClass.getMethod("getEmail", new Class[0]);
            this.fullnameMethod = userClass.getMethod("getFullName", new Class[0]);
        }
        catch (NoSuchMethodException e) {
            log.error((Object)e.getMessage());
        }
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.repository = repo;
    }

    public void init(HashMap args) throws ConfigurationException {
        this.userManager = (UserManager)args.get("userManager");
        this.groupManager = (GroupManager)args.get("groupManager");
        this.repository = (RepositoryIdentifier)args.get("repository");
        InitializationCheck.validateArgs(args, new String[]{"userManager", "groupManager", "repository"}, this);
        try {
            this.entityNameMethod = userClass.getMethod("getName", new Class[0]);
            this.emailMethod = userClass.getMethod("getEmail", new Class[0]);
            this.fullnameMethod = userClass.getMethod("getFullName", new Class[0]);
        }
        catch (NoSuchMethodException e) {
            log.error((Object)e.getMessage());
        }
    }

    protected <T extends Entity> Pager<T> parseQuery(Method userMethod, TermQuery<T> q, Pager<T> data) throws IllegalAccessException, InvocationTargetException {
        String searchTerm = StringUtils.defaultString((String)q.getTerm()).toLowerCase();
        if (searchTerm.indexOf("*") >= 0) {
            return data;
        }
        Matcher matcher = q.isMatchingSubstring() ? (q.getMatchingRule().equals("starts_with") ? new StartsWithIgnoreCaseMatcher() : (q.getMatchingRule().equals("ends_with") ? new EndsWithIgnoreCaseMatcher() : new ContainsIgnoreCaseMatcher())) : new EqualsIgnoreCaseMatcher();
        ArrayList<Entity> matches = new ArrayList<Entity>();
        for (Entity entity : data) {
            String userInfo = (String)userMethod.invoke((Object)entity, new Object[0]);
            if (!matcher.matches(userInfo, searchTerm)) continue;
            matches.add(entity);
        }
        return new DefaultPager(matches);
    }

    public <T extends Entity> Pager<T> find(Query<T> query) throws EntityException {
        block9: {
            if (query instanceof TermQuery) {
                try {
                    if (query instanceof UserNameTermQuery) {
                        return this.parseQuery(this.entityNameMethod, (TermQuery)query, this.userManager.getUsers());
                    }
                    if (query instanceof GroupNameTermQuery) {
                        return this.parseQuery(this.entityNameMethod, (TermQuery)query, this.groupManager.getGroups());
                    }
                    if (query instanceof EmailTermQuery) {
                        return this.parseQuery(this.emailMethod, (TermQuery)query, this.userManager.getUsers());
                    }
                    if (query instanceof FullNameTermQuery) {
                        return this.parseQuery(this.fullnameMethod, (TermQuery)query, this.userManager.getUsers());
                    }
                    break block9;
                }
                catch (IllegalAccessException e) {
                    throw new EntityException(e);
                }
                catch (InvocationTargetException e) {
                    throw new EntityException(e);
                }
            }
            if (query instanceof BooleanQuery) {
                return this.evaluateBoolean((BooleanQuery)query);
            }
        }
        return null;
    }

    private <T extends Entity> Pager<T> evaluateBoolean(BooleanQuery<T> query) {
        List<Query<T>> queries = query.getQueries();
        Pager<T> allResults = null;
        boolean anding = query.isAND();
        for (Query<T> nextQuery : queries) {
            try {
                List<T> initialResult;
                if (allResults == null) {
                    allResults = this.find(nextQuery);
                    continue;
                }
                if (nextQuery instanceof BooleanQuery) {
                    if (anding) {
                        Pager<T> resultsToAnd = this.evaluateBoolean((BooleanQuery)nextQuery);
                        List<T> allResultsList = PagerUtils.toList(allResults);
                        ArrayList<T> resultsToAndList = new ArrayList<T>(PagerUtils.toList(resultsToAnd));
                        resultsToAndList.retainAll(allResultsList);
                        allResults = new DefaultPager<T>(resultsToAndList);
                        continue;
                    }
                    Pager<T> resultsToOr = this.evaluateBoolean((BooleanQuery)nextQuery);
                    ArrayList<T> resultsToOrList = new ArrayList<T>(PagerUtils.toList(resultsToOr));
                    List<T> intersection = this.findIntersection(PagerUtils.toList(allResults), resultsToOrList);
                    allResults = new DefaultPager<T>(intersection);
                    continue;
                }
                if (anding) {
                    if (nextQuery instanceof UserNameTermQuery) {
                        initialResult = PagerUtils.toList(this.parseQuery(this.entityNameMethod, (TermQuery)nextQuery, allResults));
                        initialResult.addAll(PagerUtils.toList(allResults));
                        allResults = new DefaultPager<T>(initialResult);
                        continue;
                    }
                    if (nextQuery instanceof GroupNameTermQuery) {
                        initialResult = PagerUtils.toList(this.parseQuery(this.entityNameMethod, (TermQuery)nextQuery, allResults));
                        initialResult.addAll(PagerUtils.toList(allResults));
                        allResults = new DefaultPager<T>(initialResult);
                        continue;
                    }
                    if (nextQuery instanceof EmailTermQuery) {
                        initialResult = PagerUtils.toList(this.parseQuery(this.emailMethod, (TermQuery)nextQuery, allResults));
                        initialResult.addAll(PagerUtils.toList(allResults));
                        allResults = new DefaultPager<T>(initialResult);
                        continue;
                    }
                    if (!(nextQuery instanceof FullNameTermQuery)) continue;
                    initialResult = PagerUtils.toList(this.parseQuery(this.fullnameMethod, (TermQuery)nextQuery, allResults));
                    initialResult.addAll(PagerUtils.toList(allResults));
                    allResults = new DefaultPager<T>(initialResult);
                    continue;
                }
                initialResult = PagerUtils.toList(this.find(nextQuery));
                List<T> intersection = this.findIntersection(PagerUtils.toList(allResults), initialResult);
                allResults = new DefaultPager<T>(intersection);
            }
            catch (Exception e) {
                log.error((Object)(e.getClass().getName() + " - " + e.getMessage()));
            }
        }
        return allResults;
    }

    private <T> List<T> findIntersection(List<? extends T> list1, List<? extends T> list2) {
        ArrayList<T> result = new ArrayList<T>(list1);
        list2.removeAll(list1);
        result.addAll(list2);
        return result;
    }

    @Override
    public SearchResult<User> findUsers(Query<User> query) throws EntityException {
        this.queryValidator.assertValid(query);
        Pager<User> pager = this.find(query);
        return new DefaultSearchResult<User>(pager, this.repository.getKey());
    }

    @Override
    public SearchResult<Group> findGroups(Query<Group> query) throws EntityException {
        this.queryValidator.assertValid(query);
        Pager<Group> pager = this.find(query);
        return new DefaultSearchResult<Group>(pager, this.repository.getKey());
    }

    @Override
    public SearchResult<User> findUsers(Query<User> query, QueryContext context) throws EntityException {
        if (!context.contains(this.repository)) {
            return null;
        }
        return this.findUsers(query);
    }

    @Override
    public SearchResult<Group> findGroups(Query<Group> query, QueryContext context) throws EntityException {
        if (!context.contains(this.repository)) {
            return null;
        }
        return this.findGroups(query);
    }
}

