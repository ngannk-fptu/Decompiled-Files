/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.Lists
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.SessionFactory
 *  org.springframework.jdbc.core.namedparam.MapSqlParameterSource
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.pages.ancestors;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.security.AncestorsUpdateEvent;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.pages.ancestors.PageWithAncestors;
import com.atlassian.event.api.EventPublisher;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Internal
public class AncestorsDao {
    static final int IN_CLAUSE_LIMIT = Integer.getInteger("ancestors-repairer.in-clause-limit", 100);
    private final SessionFactory sessionFactory;
    private final EventPublisher eventPublisher;

    public AncestorsDao(SessionFactory sessionFactory, EventPublisher eventPublisher) {
        this.sessionFactory = sessionFactory;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly=true)
    public Map<Long, List<Long>> getAllChildrenFromDB(@NonNull List<Long> pageIdList) {
        String GET_ALL_CHILDREN = "SELECT CONTENTID, PARENTID FROM CONTENT WHERE PARENTID IN (:ids) AND CONTENTTYPE = 'PAGE' AND (CONTENT_STATUS = 'current' OR CONTENT_STATUS = 'draft') and PREVVER IS NULL";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", pageIdList);
        HashMap<Long, List<Long>> childrenGroups = new HashMap<Long, List<Long>>();
        Map<String, List<Long>> paramMap = Collections.singletonMap("ids", pageIdList);
        DataSource dataSource = Objects.requireNonNull(DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).getDataSource());
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        List records = template.queryForList("SELECT CONTENTID, PARENTID FROM CONTENT WHERE PARENTID IN (:ids) AND CONTENTTYPE = 'PAGE' AND (CONTENT_STATUS = 'current' OR CONTENT_STATUS = 'draft') and PREVVER IS NULL", paramMap);
        for (Map record : records) {
            Long contentId = AncestorsDao.convertToLong(record.get("CONTENTID"));
            Long parentId = AncestorsDao.convertToLong(record.get("PARENTID"));
            childrenGroups.compute(parentId, (parentId1, childrenIdList) -> {
                if (childrenIdList != null) {
                    childrenIdList.add(contentId);
                    return childrenIdList;
                }
                return new ArrayList<Long>(Collections.singletonList(contentId));
            });
        }
        return childrenGroups;
    }

    private static Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal)value).longValue();
        }
        return (Long)value;
    }

    @Transactional(readOnly=true)
    public Map<Long, List<Long>> getAncestorsFromConfancestorsTable(@NonNull List<Long> pageIdList) {
        String GET_ANCESTORS = "SELECT DESCENDENTID, ANCESTORID FROM CONFANCESTORS WHERE DESCENDENTID IN (:ids) ORDER BY DESCENDENTID, ANCESTORPOSITION";
        if (pageIdList.size() == 0) {
            return Collections.emptyMap();
        }
        HashMap<Long, List<Long>> ancestorsGroups = new HashMap<Long, List<Long>>();
        List pageIdPartitions = Lists.partition(pageIdList, (int)IN_CLAUSE_LIMIT);
        for (List pageIdPartition : pageIdPartitions) {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("ids", (Object)pageIdPartition);
            for (Long pageId : pageIdPartition) {
                ancestorsGroups.put(pageId, new ArrayList());
            }
            Map<String, List> paramMap = Collections.singletonMap("ids", pageIdPartition);
            DataSource dataSource = Objects.requireNonNull(DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).getDataSource());
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
            List records = template.queryForList("SELECT DESCENDENTID, ANCESTORID FROM CONFANCESTORS WHERE DESCENDENTID IN (:ids) ORDER BY DESCENDENTID, ANCESTORPOSITION", paramMap);
            for (Map record : records) {
                Long descendentId = AncestorsDao.convertToLong(record.get("DESCENDENTID"));
                Long ancestorId = AncestorsDao.convertToLong(record.get("ANCESTORID"));
                ((List)ancestorsGroups.get(descendentId)).add(ancestorId);
            }
        }
        return ancestorsGroups;
    }

    @Transactional(readOnly=true)
    public List<Long> getTopLevelPages(@NonNull Long spaceId) {
        Objects.requireNonNull(spaceId, "spaceId must not be null");
        String GET_TOP_LEVEL_PAGES_SQL = "SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'PAGE' AND CONTENT_STATUS = 'current' and PREVVER IS NULL AND PARENTID IS NULL AND SPACEID = ?";
        Object[] parameters = new Object[]{spaceId};
        ArrayList<Long> idList = new ArrayList<Long>();
        DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).query("SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'PAGE' AND CONTENT_STATUS = 'current' and PREVVER IS NULL AND PARENTID IS NULL AND SPACEID = ?", parameters, resultSet -> {
            Long contentId = resultSet.getLong("CONTENTID");
            idList.add(contentId);
        });
        return idList;
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void deleteAllAncestors(@NonNull Long pageId) {
        String DELETE_SQL = "DELETE FROM CONFANCESTORS WHERE DESCENDENTID = ?";
        DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).update("DELETE FROM CONFANCESTORS WHERE DESCENDENTID = ?", new Object[]{pageId});
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void addsAncestor(@NonNull Long pageId, @NonNull Long ancestorId, @NonNull int position) {
        String INSERT_SQL = "INSERT INTO CONFANCESTORS (DESCENDENTID, ANCESTORID, ANCESTORPOSITION) VALUES (?, ?, ?)";
        DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).update("INSERT INTO CONFANCESTORS (DESCENDENTID, ANCESTORID, ANCESTORPOSITION) VALUES (?, ?, ?)", new Object[]{pageId, ancestorId, position});
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void fixAncestorsForOnePage(Long pageId, List<Long> ancestors) {
        this.deleteAllAncestors(pageId);
        int counter = 0;
        for (Long ancestorId : ancestors) {
            this.addsAncestor(pageId, ancestorId, counter++);
        }
        this.fireUpdateEvent(pageId, ancestors);
    }

    private void fireUpdateEvent(Long pageId, List<Long> ancestors) {
        this.eventPublisher.publish((Object)new AncestorsUpdateEvent(pageId, ancestors));
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void fixPages(List<PageWithAncestors> pagesToFix) {
        for (PageWithAncestors pageToFix : pagesToFix) {
            Long pageId = pageToFix.getPageId();
            this.fixAncestorsForOnePage(pageId, pageToFix.getAncestors());
        }
    }
}

