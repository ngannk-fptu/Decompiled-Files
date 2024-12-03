/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.Criteria
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.Session
 *  net.sf.hibernate.SessionFactory
 *  net.sf.hibernate.expression.Conjunction
 *  net.sf.hibernate.expression.Criterion
 *  net.sf.hibernate.expression.EqExpression
 *  net.sf.hibernate.expression.Expression
 *  net.sf.hibernate.expression.MatchMode
 *  net.sf.hibernate.expression.Order
 *  org.springframework.orm.hibernate.SessionFactoryUtils
 */
package com.atlassian.user.impl.hibernate.search.query;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.hibernate.DefaultHibernateGroup;
import com.atlassian.user.impl.hibernate.DefaultHibernateUser;
import com.atlassian.user.impl.hibernate.repository.HibernateRepository;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.DefaultSearchResult;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.query.BooleanQuery;
import com.atlassian.user.search.query.EmailTermQuery;
import com.atlassian.user.search.query.EntityQueryException;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.search.query.FullNameTermQuery;
import com.atlassian.user.search.query.GroupNameTermQuery;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.QueryContext;
import com.atlassian.user.search.query.QueryValidator;
import com.atlassian.user.search.query.TermQuery;
import com.atlassian.user.search.query.UserNameTermQuery;
import java.util.List;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.expression.Conjunction;
import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.EqExpression;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.MatchMode;
import net.sf.hibernate.expression.Order;
import org.springframework.orm.hibernate.SessionFactoryUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class HibernateEntityQueryParser
implements EntityQueryParser {
    private final RepositoryIdentifier identifier;
    private final HibernateRepository repository;
    private final QueryValidator queryValidator = new QueryValidator();

    public HibernateEntityQueryParser(RepositoryIdentifier identifier, HibernateRepository repository) {
        this.identifier = identifier;
        this.repository = repository;
    }

    private Session getSession() {
        return SessionFactoryUtils.getSession((SessionFactory)this.repository.getSessionFactory(), (boolean)true);
    }

    @Override
    public SearchResult<User> findUsers(Query<User> query) throws EntityException {
        List results;
        this.queryValidator.assertValid(query);
        Criteria criteria = this.getSession().createCriteria(DefaultHibernateUser.class);
        criteria = HibernateEntityQueryParser.identifyAndAddSearchCriteria(query, criteria);
        criteria.addOrder(Order.asc((String)"name"));
        try {
            results = criteria.list();
        }
        catch (HibernateException e) {
            throw new RepositoryException(e);
        }
        return new DefaultSearchResult<User>(new DefaultPager(results), this.identifier.getKey());
    }

    @Override
    public SearchResult<Group> findGroups(Query<Group> query) throws EntityException {
        List results;
        this.queryValidator.assertValid(query);
        Criteria criteria = this.getSession().createCriteria(DefaultHibernateGroup.class);
        criteria = HibernateEntityQueryParser.identifyAndAddSearchCriteria(query, criteria);
        criteria.addOrder(Order.asc((String)"name"));
        try {
            results = criteria.list();
        }
        catch (HibernateException e) {
            throw new RepositoryException(e);
        }
        return new DefaultSearchResult<Group>(new DefaultPager(results), this.identifier.getKey());
    }

    @Override
    public SearchResult<User> findUsers(Query<User> query, QueryContext context) throws EntityException {
        if (!context.contains(this.identifier)) {
            return null;
        }
        return this.findUsers(query);
    }

    @Override
    public SearchResult<Group> findGroups(Query<Group> query, QueryContext context) throws EntityException {
        if (!context.contains(this.identifier)) {
            return null;
        }
        return this.findGroups(query);
    }

    private static MatchMode getMatchMode(String matchingRule) {
        if (matchingRule.equals("contains")) {
            return MatchMode.ANYWHERE;
        }
        if (matchingRule.equals("ends_with")) {
            return MatchMode.END;
        }
        if (matchingRule.equals("starts_with")) {
            return MatchMode.START;
        }
        return MatchMode.EXACT;
    }

    private static String identifyProperty(TermQuery q) {
        if (q instanceof UserNameTermQuery) {
            return "name";
        }
        if (q instanceof EmailTermQuery) {
            return "email";
        }
        if (q instanceof FullNameTermQuery) {
            return "fullName";
        }
        if (q instanceof GroupNameTermQuery) {
            return "name";
        }
        return null;
    }

    private static Criteria identifyAndAddSearchCriteria(Query q, Criteria baseCriteria) throws EntityQueryException {
        if (q instanceof BooleanQuery) {
            return HibernateEntityQueryParser.addSearchCriteria((BooleanQuery)q, baseCriteria);
        }
        return HibernateEntityQueryParser.addSearchCriteria((TermQuery)q, baseCriteria);
    }

    private static Criteria addSearchCriteria(BooleanQuery<?> booleanQuery, Criteria baseCriteria) throws EntityQueryException {
        Conjunction junction = booleanQuery.isAND() ? Expression.conjunction() : Expression.disjunction();
        baseCriteria.add((Criterion)junction);
        for (Query<?> nestedQuery : booleanQuery.getQueries()) {
            if (nestedQuery instanceof BooleanQuery) {
                HibernateEntityQueryParser.addSearchCriteria((BooleanQuery)nestedQuery, baseCriteria);
                continue;
            }
            if (nestedQuery instanceof TermQuery) {
                junction.add(HibernateEntityQueryParser.getQueryExpression((TermQuery)nestedQuery));
                continue;
            }
            throw new EntityQueryException("Unknown query type: [" + nestedQuery.getClass().getName() + "]");
        }
        return baseCriteria;
    }

    private static Criteria addSearchCriteria(TermQuery q, Criteria baseCriteria) {
        Criterion expression = HibernateEntityQueryParser.getQueryExpression(q);
        baseCriteria.add(expression);
        return baseCriteria;
    }

    private static Criterion getQueryExpression(TermQuery termQuery) {
        String hqlField = HibernateEntityQueryParser.identifyProperty(termQuery);
        if (termQuery.isMatchingSubstring()) {
            MatchMode matchMode = HibernateEntityQueryParser.getMatchMode(termQuery.getMatchingRule());
            return Expression.ilike((String)hqlField, (String)termQuery.getTerm(), (MatchMode)matchMode);
        }
        return new EqExpression(hqlField, (Object)termQuery.getTerm(), true);
    }
}

