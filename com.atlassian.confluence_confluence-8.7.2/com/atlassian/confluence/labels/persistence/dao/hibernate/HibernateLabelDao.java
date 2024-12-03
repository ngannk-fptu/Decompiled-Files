/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.engine.jdbc.spi.SqlExceptionHelper
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.support.DataAccessUtils
 */
package com.atlassian.confluence.labels.persistence.dao.hibernate;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.internal.labels.persistence.LabelDaoInternal;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelableType;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.templates.persistence.dao.PageTemplateDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.sal.api.user.UserKey;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;

public class HibernateLabelDao
extends HibernateObjectDao<Label>
implements LabelDaoInternal {
    private static final Logger log = LoggerFactory.getLogger(HibernateLabelDao.class);
    private ConfluenceUserDao confluenceUserDao;
    private PageTemplateDao pageTemplateDao;
    private ContentEntityObjectDao contentEntityObjectDao;
    private AttachmentManager attachmentManager;

    @Override
    public Class<Label> getPersistentClass() {
        return Label.class;
    }

    @Override
    public Label findById(long id) {
        return (Label)super.getByClassId(id);
    }

    @Override
    public Label findByLabel(Label label) {
        Object hql = "from Label label ";
        hql = label.getName() != null ? (String)hql + "where label.name = :name " : (String)hql + "where label.name is null ";
        hql = label.getNamespace() != null ? (String)hql + "and label.namespace = :namespace " : (String)hql + "and label.namespace is null ";
        hql = label.getOwner() != null ? (String)hql + "and label.owningUser = :owner " : (String)hql + "and label.owningUser is null ";
        Object hqlQuery = hql;
        return (Label)this.getHibernateTemplate().execute(arg_0 -> this.lambda$findByLabel$0((String)hqlQuery, label, arg_0));
    }

    @Override
    public List<Label> findByDetails(String name, String namespace, String owner) {
        return this.findByDetailsInSpace(name, namespace, owner, null);
    }

    private String getFindByDetailsInSpaceHql(String name, String namespace, ConfluenceUser owner, Collection<Space> spaces) {
        return this.getFindByDetailsInSpaceHql(name, namespace, owner, spaces, null);
    }

    private String getFindByDetailsInSpaceHql(String name, String namespace, ConfluenceUser owner, String spaceKey) {
        return this.getFindByDetailsInSpaceHql(name, namespace, owner, null, spaceKey);
    }

    private String getFindByDetailsInSpaceHql(String name, String namespace, ConfluenceUser owner, Collection<Space> spaces, String spaceKey) {
        return this.getFindByDetailsInSpaceHql(name, namespace, owner, spaces, spaceKey, false);
    }

    private String getFindByDetailsInSpaceHql(String name, String namespace, ConfluenceUser owner, Collection<Space> spaces, String spaceKey, boolean isCounting) {
        if (spaces != null && spaceKey != null) {
            throw new IllegalArgumentException("You may only pass in either a collection of spaces or a space key to " + this.getClass().getSimpleName() + ".getFindByDetailsInSpaceHql");
        }
        StringBuilder hql = new StringBuilder(350);
        if (isCounting) {
            hql.append("select count (distinct l) from Label l ");
        } else {
            hql.append("select distinct l from Label l ");
        }
        String sep = " where ";
        if (spaces != null && !spaces.isEmpty()) {
            hql.append(", Labelling as lc, SpaceContentEntityObject sceo");
            hql.append(sep).append(" lc.content = sceo ");
            sep = "and";
            if (spaces.size() == 1) {
                hql.append(sep).append(" sceo.space.id = :space ");
            } else {
                hql.append(sep).append(" sceo.space.id in (:space) ");
            }
            hql.append(sep).append(" lc.label = l ");
        } else if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            hql.append(", Labelling as lc, SpaceContentEntityObject sceo");
            hql.append(sep).append(" lc.content = sceo ");
            sep = "and";
            hql.append(sep).append(" sceo.space.lowerKey = :space ");
            hql.append(sep).append(" lc.label = l ");
        }
        if (StringUtils.isNotEmpty((CharSequence)name)) {
            hql.append(sep).append(" l.name = :name ");
            sep = "and";
        }
        if (StringUtils.isNotEmpty((CharSequence)namespace)) {
            hql.append(sep).append(" l.namespace = :namespace ");
            sep = "and";
        }
        if (owner != null) {
            hql.append(sep).append(" l.owningUser = :owner ");
        }
        if (!isCounting) {
            hql.append("order by l.name");
        }
        return hql.toString();
    }

    @Override
    public List<Label> findByDetailsInSpace(String name, String namespace, String owner, String spaceKey) {
        ConfluenceUser ownerUser = this.confluenceUserDao.findByUsername(owner);
        String hqlQuery = this.getFindByDetailsInSpaceHql(name, namespace, ownerUser, spaceKey);
        return (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createQuery(hqlQuery);
            if (StringUtils.isNotEmpty((CharSequence)name)) {
                queryObject.setString("name", name);
            }
            if (StringUtils.isNotEmpty((CharSequence)namespace)) {
                queryObject.setString("namespace", namespace);
            }
            if (StringUtils.isNotEmpty((CharSequence)owner)) {
                queryObject.setParameter("owner", (Object)ownerUser);
            }
            if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
                queryObject.setString("space", spaceKey.toLowerCase());
            }
            HibernateLabelDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            queryObject.setCacheable(true);
            return queryObject.list();
        });
    }

    @Override
    public List<Label> findByDetailsInSpace(String name, String namespace, String owner, String spaceKey, LimitedRequest limitedRequest) {
        Objects.requireNonNull(limitedRequest);
        ConfluenceUser ownerUser = this.confluenceUserDao.findByUsername(owner);
        String hqlQuery = this.getFindByDetailsInSpaceHql(name, namespace, ownerUser, spaceKey);
        return (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createQuery(hqlQuery);
            if (StringUtils.isNotEmpty((CharSequence)name)) {
                queryObject.setString("name", name);
            }
            if (StringUtils.isNotEmpty((CharSequence)namespace)) {
                queryObject.setString("namespace", namespace);
            }
            if (StringUtils.isNotEmpty((CharSequence)owner)) {
                queryObject.setParameter("owner", (Object)ownerUser);
            }
            if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
                queryObject.setString("space", spaceKey.toLowerCase());
            }
            HibernateLabelDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            queryObject.setCacheable(true);
            queryObject.setFirstResult(limitedRequest.getStart());
            queryObject.setMaxResults(limitedRequest.getLimit());
            return queryObject.list();
        });
    }

    @Override
    public long getTotalLabelInSpace(String name, String namespace, String owner, String spaceKey) {
        ConfluenceUser ownerUser = this.confluenceUserDao.findByUsername(owner);
        String hqlQuery = this.getFindByDetailsInSpaceHql(name, namespace, ownerUser, null, spaceKey, true);
        Long returnValue = (Long)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createQuery(hqlQuery);
            if (StringUtils.isNotEmpty((CharSequence)name)) {
                queryObject.setString("name", name);
            }
            if (StringUtils.isNotEmpty((CharSequence)namespace)) {
                queryObject.setString("namespace", namespace);
            }
            if (StringUtils.isNotEmpty((CharSequence)owner)) {
                queryObject.setParameter("owner", (Object)ownerUser);
            }
            if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
                queryObject.setString("space", spaceKey.toLowerCase());
            }
            HibernateLabelDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            queryObject.setCacheable(true);
            return DataAccessUtils.longResult((Collection)queryObject.list());
        });
        return returnValue == null ? 0L : returnValue;
    }

    @Override
    public List<Label> findByDetailsInSpaces(String name, String namespace, String owner, Collection<Space> spaces) {
        ConfluenceUser ownerUser = this.confluenceUserDao.findByUsername(owner);
        String hqlQuery = this.getFindByDetailsInSpaceHql(name, namespace, ownerUser, spaces);
        return (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createQuery(hqlQuery);
            if (StringUtils.isNotEmpty((CharSequence)name)) {
                queryObject.setString("name", name);
            }
            if (StringUtils.isNotEmpty((CharSequence)namespace)) {
                queryObject.setString("namespace", namespace);
            }
            if (StringUtils.isNotEmpty((CharSequence)owner)) {
                queryObject.setParameter("owner", (Object)ownerUser);
            }
            if (spaces != null && !spaces.isEmpty()) {
                if (spaces.size() == 1) {
                    queryObject.setLong("space", ((Space)spaces.iterator().next()).getId());
                } else {
                    ArrayList<Long> spaceIdList = new ArrayList<Long>(spaces.size());
                    for (Space space : spaces) {
                        spaceIdList.add(space.getId());
                    }
                    queryObject.setParameterList("space", spaceIdList);
                }
            }
            HibernateLabelDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            queryObject.setCacheable(true);
            return queryObject.list();
        });
    }

    @Override
    public List<Label> findBySpace(String spaceKey, String namespace) {
        return this.findByDetailsInSpace(null, namespace, null, spaceKey);
    }

    @Override
    public List<Label> findRecentlyUsedBySpace(String spaceKey, int maxResults) {
        return LabelUtil.extractLabelsFromLabellings(this.findRecentlyUsedLabellingsBySpace(spaceKey, maxResults));
    }

    @Override
    public List<Label> findRecentlyUsed(int maxResults) {
        return LabelUtil.extractLabelsFromLabellings(this.findRecentlyUsedLabelling(maxResults));
    }

    @Override
    public List<Labelling> findRecentlyUsedLabelling(int maxResults) {
        if (maxResults != 0) {
            return this.findNamedQuery("confluence.labelling_findRecentlyUsed", HibernateObjectDao.Cacheability.NOT_CACHEABLE, maxResults);
        }
        return this.findNamedQuery("confluence.labelling_findRecentlyUsed", HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public List<Labelling> findRecentlyUsedLabellingsBySpace(String spaceKey, int maxResults) {
        if (maxResults != 0) {
            return this.findNamedQueryStringParam("confluence.labelling_findRecentlyUsedBySpace", "spaceKey", StringUtils.lowerCase((String)spaceKey), HibernateObjectDao.Cacheability.NOT_CACHEABLE, maxResults);
        }
        return this.findNamedQueryStringParam("confluence.labelling_findRecentlyUsedBySpace", "spaceKey", StringUtils.lowerCase((String)spaceKey), HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public List<Label> findRecentlyUsedUserLabels(String username, int maxResults) {
        return LabelUtil.extractLabelsFromLabellings(this.findRecentlyUsedUserLabellings(username, maxResults));
    }

    @Override
    public List<Labelling> findRecentlyUsedUserLabellings(String username, int maxResults) {
        if (maxResults != 0) {
            return this.findNamedQueryStringParam("confluence.labelling_findRecentlyUsedUserLabels", "lowerUsername", IdentifierUtils.toLowerCase((String)username), HibernateObjectDao.Cacheability.NOT_CACHEABLE, maxResults);
        }
        return this.findNamedQueryStringParam("confluence.labelling_findRecentlyUsedUserLabels", "lowerUsername", IdentifierUtils.toLowerCase((String)username), HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public List<LabelSearchResult> findMostPopular(String namespace, int maxResults) {
        if (maxResults != 0) {
            return this.createListOfLabelSearchResults(this.findNamedQueryStringParam("confluence.labelling_findMostPopular", "namespace", namespace, HibernateObjectDao.Cacheability.NOT_CACHEABLE, maxResults));
        }
        return this.createListOfLabelSearchResults(this.findNamedQueryStringParam("confluence.labelling_findMostPopular", "namespace", namespace, HibernateObjectDao.Cacheability.NOT_CACHEABLE));
    }

    private List<LabelSearchResult> createListOfLabelSearchResults(List<Object[]> labels) {
        return labels.stream().map(labelCount -> new LabelSearchResult((Label)labelCount[0], (Integer)labelCount[1])).collect(Collectors.toList());
    }

    @Override
    public List<LabelSearchResult> findMostPopularBySpace(String namespace, String spaceKey, int maxResults) {
        if (maxResults != 0) {
            return this.createListOfLabelSearchResults(this.findNamedQueryStringParams("confluence.labelling_findMostPopularBySpace", "namespace", (Object)namespace, "spaceKey", (Object)StringUtils.lowerCase((String)spaceKey), HibernateObjectDao.Cacheability.NOT_CACHEABLE, maxResults));
        }
        return this.createListOfLabelSearchResults(this.findNamedQueryStringParams("confluence.labelling_findMostPopularBySpace", "namespace", namespace, "spaceKey", (Object)StringUtils.lowerCase((String)spaceKey), HibernateObjectDao.Cacheability.NOT_CACHEABLE));
    }

    @Override
    public List<Label> findBySingleDegreeSeparation(EditableLabelable content, int maxResults) {
        return this.findBySingleDegreeSeparation(content, null, maxResults);
    }

    @Override
    public List<Label> findBySingleDegreeSeparation(EditableLabelable content, String spaceKey, int maxResults) {
        if (content == null) {
            return Collections.emptyList();
        }
        int contentLabelsSize = content.getLabelCount();
        int maxResultsToUse = maxResults == 0 ? 0 : maxResults + contentLabelsSize;
        List<Label> queryResults = this.performSingleDegreeSeperationQuery(content.getId(), spaceKey, maxResultsToUse, "confluence.label_findSingleDegreeSeparationFromContentBySpace", "confluence.label_findSingleDegreeSeparationFromContent", "contentID");
        List<Label> contentLabelFilteredList = this.filter(content.getLabels(), queryResults);
        if (maxResults != 0 && contentLabelFilteredList.size() > maxResults) {
            return contentLabelFilteredList.subList(0, maxResults);
        }
        return contentLabelFilteredList;
    }

    private List<Label> performSingleDegreeSeperationQuery(long id, String spaceKey, int maxResultsToUse, String queryNameWithSpace, String queryNameWithoutSpace, String idPropertyName) {
        List<Label> queryResults = spaceKey != null ? this.executeRelatedLabelQuery(spaceKey, maxResultsToUse, id, queryNameWithSpace, false, idPropertyName) : this.executeRelatedLabelQuery(null, maxResultsToUse, id, queryNameWithoutSpace, false, idPropertyName);
        queryResults = this.extractObjectsFromObjectArrayList((List<T[]>)queryResults);
        return queryResults;
    }

    private List executeRelatedLabelQuery(String spaceKey, int maxResults, long id, String queryName, boolean cacheable, String idPropertyName) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.getNamedQuery(queryName);
            if (maxResults != 0) {
                query.setMaxResults(maxResults);
            }
            query.setParameter(idPropertyName, (Object)id);
            if (spaceKey != null) {
                query.setParameter("spaceKey", (Object)StringUtils.lowerCase((String)spaceKey));
            }
            query.setCacheable(cacheable);
            return query.list();
        });
    }

    @Override
    public List<Label> findBySingleDegreeSeparation(Label label, int maxResults) {
        return this.findBySingleDegreeSeparation(label, null, maxResults);
    }

    @Override
    public List<Label> findBySingleDegreeSeparation(Label label, String spaceKey, int maxResults) {
        if (label == null) {
            return Collections.emptyList();
        }
        if (!label.isPersistent()) {
            throw new IllegalArgumentException("Please provide a persistent instance of the label.");
        }
        List<Label> queryResults = this.performSingleDegreeSeperationQuery(label.getId(), spaceKey, maxResults, "confluence.label_findSingleDegreeSeparationFromLabelBySpace", "confluence.label_findSingleDegreeSeparationFromLabel", "labelID");
        return queryResults;
    }

    @Override
    public List<? extends EditableLabelable> findAllUserLabelledContent(String username) {
        if (username == null) {
            return Collections.emptyList();
        }
        return this.distinctContent(new ArrayList<EditableLabelable>(this.getLabelableFromLabelling(this.findNamedQueryStringParam("confluence.label_findAllUserLabelledContent", "lowerUsername", IdentifierUtils.toLowerCase((String)username), HibernateObjectDao.Cacheability.CACHEABLE))));
    }

    private List<Labelling> getLabellingForLabel(Label label, int maxResults) {
        return this.findNamedQueryStringParam("confluence.label_findLabellingsForLabel", "labelID", label.getId(), HibernateObjectDao.Cacheability.CACHEABLE, maxResults);
    }

    private List<Labelling> getLabellingForLabel(Label label) {
        return this.findNamedQueryStringParam("confluence.label_findLabellingsForLabel", "labelID", label.getId(), HibernateObjectDao.Cacheability.CACHEABLE);
    }

    private List<EditableLabelable> distinctContent(List<EditableLabelable> queryResults) {
        HashSet retrievedContent = new HashSet();
        queryResults.removeIf(editableLabelable -> !retrievedContent.add(editableLabelable));
        return queryResults;
    }

    @Override
    public List<? extends EditableLabelable> findCurrentContentForLabel(Label label) {
        if (label == null) {
            return Collections.emptyList();
        }
        return this.getLabelableFromLabelling(this.getLabellingForLabel(label));
    }

    @Override
    public List<? extends EditableLabelable> findContentForLabel(Label label, int maxResults) {
        if (label == null) {
            return Collections.emptyList();
        }
        return this.getLabelableFromLabelling(this.getLabellingForLabel(label, maxResults));
    }

    @Override
    public <T extends EditableLabelable> PartialList<T> findForAllLabels(Class<T> labelableClass, int offset, int maxResults, Label ... labels) {
        LabelableType labelableType = LabelableType.getType(labelableClass);
        if (labelableType == LabelableType.CONTENT) {
            return this.findCEOsForAllLabels(offset, maxResults, "current", null, labels);
        }
        return this.getLabelablesForLabels(labelableType, labelableClass, offset, maxResults, labels);
    }

    @Override
    public PartialList<EditableLabelable> findForAllLabels(int offset, int maxResults, Label ... labelsArray) {
        List<Label> labels = this.filterNullLabels(labelsArray);
        if (labels.isEmpty()) {
            return PartialList.empty();
        }
        List<Object> paramList = Arrays.asList("labels", labels, "labelCount", labels.size());
        Object[] params = paramList.toArray();
        String query = "confluence.label_findLabellingDetailsForLabelsWithAnyType";
        int available = this.count(query, HibernateObjectDao.Cacheability.NOT_CACHEABLE, paramList);
        ArrayList<EditableLabelable> labelables = new ArrayList<EditableLabelable>();
        if (available > 0) {
            List results = this.findNamedQueryStringParams(query, HibernateObjectDao.Cacheability.NOT_CACHEABLE, offset, maxResults, params);
            for (Object result : results) {
                Object[] arr = (Object[])result;
                long id = (Long)arr[0];
                LabelableType type = LabelableType.getFromTypeString((String)arr[1]);
                labelables.add((EditableLabelable)this.getLabelable(id, type));
            }
        }
        return new PartialList<EditableLabelable>(available, offset, labelables);
    }

    private <T extends EditableLabelable> PartialList<T> getLabelablesForLabels(LabelableType labelableType, Class<T> labelableClass, int offset, int maxResults, Label ... labelsArray) {
        List<Label> labels = this.filterNullLabels(labelsArray);
        if (labels.isEmpty()) {
            return PartialList.empty();
        }
        List<Object> paramList = Arrays.asList("labels", labels, "labelCount", labels.size(), "labelableType", labelableType.typeString());
        Object[] params = paramList.toArray();
        String query = "confluence.label_findLabellingDetailsForLabelsWithType";
        List results = this.findNamedQueryStringParams(query, HibernateObjectDao.Cacheability.NOT_CACHEABLE, offset, maxResults, params);
        ArrayList<T> labelables = new ArrayList<T>();
        for (Object result : results) {
            Object[] arr = (Object[])result;
            long id = (Long)arr[0];
            LabelableType type = LabelableType.getFromTypeString((String)arr[1]);
            labelables.add(this.getLabelable(id, type, labelableClass));
        }
        int available = this.count(query, HibernateObjectDao.Cacheability.NOT_CACHEABLE, paramList);
        return new PartialList(available, offset, labelables);
    }

    private <T extends EditableLabelable> T getLabelable(long id, LabelableType type, Class<T> labelableClass) {
        return (T)((EditableLabelable)labelableClass.cast(this.getLabelable(id, type)));
    }

    private Object getLabelable(long id, LabelableType type) {
        switch (type) {
            case ATTACHMENT: {
                return this.attachmentManager.getAttachment(id);
            }
            case PAGE_TEMPLATE: {
                return this.pageTemplateDao.getById(id);
            }
            case CONTENT: {
                return this.contentEntityObjectDao.getById(id);
            }
        }
        throw new IllegalArgumentException("Unknown labelable type : " + type);
    }

    @Override
    public PartialList<ContentEntityObject> findContentInSpaceForAllLabels(int offset, int maxResults, String spaceKey, Label ... labels) {
        return this.findCEOsForAllLabels(offset, maxResults, "current", Collections.singleton(spaceKey), labels);
    }

    @Override
    public PartialList<ContentEntityObject> findContentInSpacesForAllLabels(int offset, int maxResults, Set<String> spaceKeys, Label ... labels) {
        return this.findCEOsForAllLabels(offset, maxResults, "current", spaceKeys, labels);
    }

    @Override
    public PartialList<ContentEntityObject> findAllContentForAllLabels(int offset, int maxResults, Label ... labels) {
        return this.findCEOsForAllLabels(offset, maxResults, null, null, labels);
    }

    private PartialList<ContentEntityObject> findCEOsForAllLabels(int offset, int maxResults, String contentStatus, Set<String> spaceKeys, Label ... labelsArray) {
        List<Label> labels = this.filterNullLabels(labelsArray);
        if (labels.isEmpty()) {
            return PartialList.empty();
        }
        String query = "confluence.label_findAllContentForAllLabels";
        ArrayList<Object> paramList = new ArrayList<Object>(Arrays.asList("labels", labels, "labelCount", labels.size()));
        if (spaceKeys != null && !spaceKeys.isEmpty()) {
            paramList.add("spaceKeys");
            Set lowerSpaceKeys = spaceKeys.stream().map(String::toLowerCase).collect(Collectors.toSet());
            paramList.add(lowerSpaceKeys);
            query = "confluence.label_findContentInSpacesForAllLabels";
        } else if (StringUtils.isNotBlank((CharSequence)contentStatus)) {
            query = "confluence.label_findContentForAllLabels";
        }
        return this.runQueryAndGetCEOs(query, offset, maxResults, paramList);
    }

    private List<Label> filterNullLabels(Label[] labelsArray) {
        ArrayList<Label> labels = new ArrayList<Label>();
        for (Label label : labelsArray) {
            if (label == null) continue;
            labels.add(label);
        }
        return labels;
    }

    private PartialList<ContentEntityObject> runQueryAndGetCEOs(String queryName, int offset, int maxResults, List<Object> paramList) {
        Object[] parameters = paramList.toArray();
        return (PartialList)this.getHibernateTemplate().execute(session -> {
            int available = this.count(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, paramList);
            if (available == 0) {
                return PartialList.empty();
            }
            List ids = this.findNamedQueryStringParams(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, offset, maxResults, parameters);
            if (ids.isEmpty()) {
                return new PartialList(available, offset, Collections.emptyList());
            }
            List results = session.createQuery("from ContentEntityObject s where s.id = " + StringUtils.join((Iterable)ids, (String)" or s.id = ")).list();
            return new PartialList(available, offset, results);
        });
    }

    @Override
    public int findContentCountForLabel(Label label) {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createQuery("select count(labelling) from Labelling labelling where labelling.label = :label");
            queryObject.setParameter("label", (Object)label);
            HibernateLabelDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.list();
        })));
    }

    @Override
    public List<? extends EditableLabelable> findCurrentContentForLabelAndSpace(Label label, String spaceKey) {
        if (label == null) {
            return Collections.emptyList();
        }
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            return this.findCurrentContentForLabel(label);
        }
        List results = this.findNamedQueryStringParams("confluence.label_findContentForLabelAndSpace", "labelID", label.getId(), "spaceKey", (Object)StringUtils.lowerCase((String)spaceKey), HibernateObjectDao.Cacheability.NOT_CACHEABLE);
        return results.stream().map(result -> (EditableLabelable)result[0]).collect(Collectors.toList());
    }

    @Override
    public List<Space> findSpacesContainingContentWithLabel(Label label) {
        if (label == null) {
            return Collections.emptyList();
        }
        return this.findNamedQueryStringParam("confluence.label_findSpacesContainingContentWithLabel", "labelID", label.getId(), HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public List<Space> findSpacesWithLabel(Label label) {
        if (label == null) {
            return Collections.emptyList();
        }
        return this.findNamedQueryStringParam("confluence.label_findSpacesWithLabel", "labelID", label.getId(), HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public Labelling findLabellingByContentAndLabel(EditableLabelable content, Label label) {
        for (Labelling labelling : content.getLabellings()) {
            if (!labelling.getLabel().equals(label)) continue;
            return labelling;
        }
        return null;
    }

    @Override
    public Labelling findLabellingById(long id) {
        return (Labelling)this.getHibernateTemplate().execute(session -> (Labelling)session.load(Labelling.class, (Serializable)Long.valueOf(id)));
    }

    @Override
    public void deleteLabellingBySpace(String key) {
        this.getHibernateTemplate().executeWithNativeSession(session -> {
            Object var3_4;
            block8: {
                session.flush();
                PreparedStatement ps = ((SessionImplementor)session).connection().prepareStatement("delete from CONTENT_LABEL where SPACEKEY = ? ");
                try {
                    ps.setString(1, key);
                    ps.execute();
                    var3_4 = null;
                    if (ps == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
                        if (ps != null) {
                            try {
                                ps.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (SQLException ex) {
                        throw new SqlExceptionHelper(true).convert(ex, ex.getMessage());
                    }
                }
                ps.close();
            }
            return var3_4;
        });
    }

    @Override
    public List<Space> getFavouriteSpaces(String username) {
        return this.findNamedQueryStringParam("confluence.label_findFavouriteSpaces", "lowerOwnerName", IdentifierUtils.toLowerCase((String)username));
    }

    @Override
    public List<Label> findUnusedLabels() {
        return this.findNamedQuery("confluence.label_unused");
    }

    @Override
    public List<Labelling> getFavouriteLabellingsByContentIds(Collection<Long> contentIds, UserKey userKey) {
        return this.findNamedQueryStringParams("confluence.label_findFavouriteLabellingsByContentIds", "contentIds", contentIds, "userKey", (Object)userKey.toString());
    }

    @Override
    public PageResponse<Label> findGlobalLabelsByNamePrefix(String namePrefix, LimitedRequest pageRequest) {
        String queryName = "confluence.label_findGlobalLabelsByNamePrefix";
        String prefixParam = namePrefix + "%";
        List<String> paramList = Arrays.asList("prefix", prefixParam);
        int available = this.count(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, paramList);
        List found = available > 0 ? this.findNamedQueryStringParams(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, pageRequest, "prefix", prefixParam) : Collections.emptyList();
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, found, null);
    }

    @Override
    public PageResponse<Label> findTeamLabelsByNamePrefix(String namePrefix, LimitedRequest pageRequest) {
        String queryName = "confluence.label_findTeamLabelsByNamePrefix";
        String prefixParam = namePrefix + "%";
        List<String> paramList = Arrays.asList("prefix", prefixParam);
        int available = this.count(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, paramList);
        List found = available > 0 ? this.findNamedQueryStringParams(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, pageRequest, "prefix", prefixParam) : Collections.emptyList();
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, found, null);
    }

    private List findNamedQueryStringParams(String queryName, HibernateObjectDao.Cacheability cacheability, LimitedRequest limitedRequest, Object ... paramNamesAndValues) {
        int offset = limitedRequest.getStart();
        int maxResultCount = limitedRequest.getLimit() + 1;
        return this.findNamedQueryStringParams(queryName, cacheability, offset, maxResultCount, paramNamesAndValues);
    }

    private List<Label> filter(List<Label> labelsToBeRemoved, List<Label> listToBeFiltered) {
        HashSet<Label> set = new HashSet<Label>(labelsToBeRemoved);
        return listToBeFiltered.stream().filter(label -> !set.contains(label)).collect(Collectors.toList());
    }

    private <T> List<T> extractObjectsFromObjectArrayList(List<T[]> queryResults) {
        return queryResults.stream().map(objects -> objects[0]).collect(Collectors.toList());
    }

    private List<? extends EditableLabelable> getLabelableFromLabelling(List<Labelling> labellings) {
        return labellings.stream().map(Labelling::getLableable).collect(Collectors.toList());
    }

    private int count(String queryName, HibernateObjectDao.Cacheability cacheability, List paramList) {
        return this.findNamedQueryStringParams(queryName, cacheability, 0, -1, paramList.toArray()).size();
    }

    public void setConfluenceUserDao(ConfluenceUserDao confluenceUserDao) {
        this.confluenceUserDao = confluenceUserDao;
    }

    public void setPageTemplateDao(PageTemplateDao pageTemplateDao) {
        this.pageTemplateDao = pageTemplateDao;
    }

    public void setContentEntityObjectDao(ContentEntityObjectDao contentEntityObjectDao) {
        this.contentEntityObjectDao = contentEntityObjectDao;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    private /* synthetic */ Object lambda$findByLabel$0(String hqlQuery, Label label, Session session) throws HibernateException {
        Query queryObject = session.createQuery(hqlQuery);
        queryObject.setCacheable(true);
        if (label.getName() != null) {
            queryObject.setString("name", label.getName());
        }
        if (label.getNamespace() != null) {
            queryObject.setString("namespace", label.getNamespace().toString());
        }
        if (label.getOwner() != null) {
            queryObject.setParameter("owner", (Object)label.getOwnerUser());
        }
        HibernateLabelDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
        List labels = queryObject.list();
        if (labels.size() == 0) {
            return null;
        }
        if (labels.size() > 1) {
            log.error("There is more than one label named '" + label.getName() + "' in the '" + label.getNamespace().toString() + "' namespace.");
        }
        return labels.get(0);
    }
}

