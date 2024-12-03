/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao;

import com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao.SpaceKeyWithPermission;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.DenormalisedSpacePermission;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.SpacePermissionType;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpacesQuery;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;

@SuppressFBWarnings(value={"SQL_INJECTION_HIBERNATE"}, justification="Enum values are used for building SQL queries, so SQL injections are not possible. Can't apply the annotation to the particular method due to this bug: https://github.com/spotbugs/spotbugs/issues/724")
public class DenormalisedSpacePermissionDao {
    public static final long ANONYMOUS_USERS_SID = -1L;
    public static final long ALL_AUTHENTICATED_USERS_SID = -2L;
    public static final long FULL_CONFLUENCE_ACCESS = -3L;
    private final HibernateTemplate hibernateTemplate;

    public DenormalisedSpacePermissionDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public Map<Long, List<DenormalisedSpacePermission>> findPermissionsForSpaces(Set<Long> spaceIds, SpacePermissionType permissionType) {
        if (spaceIds.size() == 0) {
            return Collections.emptyMap();
        }
        return (Map)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "from " + permissionType.getEntityName() + " where SPACE_ID in (:spaceIdList)";
            Query query = session.createQuery(hqlQuery, DenormalisedSpacePermission.class);
            query.setParameter("spaceIdList", (Object)spaceIds);
            query.setCacheable(false);
            return query.list().stream().collect(Collectors.groupingBy(permission -> permission.getSpaceToSidMapId().getSpaceId()));
        });
    }

    public List<Space> findPermittedSpaces(List<Long> sidList, SpacePermissionType permissionType) {
        if (sidList.size() == 0) {
            return Collections.emptyList();
        }
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "select s from Space s where s.id in (select p.spaceToSidMapId.spaceId from " + permissionType.getEntityName() + " p where SID_ID in (:sidIdList))";
            Query query = session.createQuery(hqlQuery, Space.class);
            query.setParameter("sidIdList", (Object)sidList);
            query.setCacheable(true);
            return query.list();
        });
    }

    public Set<Long> findPermittedSpaceIds(Set<Long> sids, Set<Long> spaceIds, SpacePermissionType permissionType) {
        if (sids.size() == 0 || spaceIds.size() == 0) {
            return Collections.emptySet();
        }
        return (Set)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "select distinct p.spaceToSidMapId.spaceId from " + permissionType.getEntityName() + " p where SID_ID in (:sids) and SPACE_ID in (:spaceIds)";
            Query query = session.createQuery(hqlQuery, Long.class);
            query.setParameter("sids", (Object)sids);
            query.setParameter("spaceIds", (Object)spaceIds);
            query.setCacheable(false);
            return new HashSet(query.list());
        });
    }

    public List<SpaceKeyWithPermission> getAllSpacesKeysWithPermissionInfo(Set<Long> sids, SpacePermissionType permissionType) {
        return (List)this.hibernateTemplate.execute(session -> {
            if (!sids.isEmpty()) {
                String hqlQuery = "select distinct new " + SpaceKeyWithPermission.class.getName() + "(s.key, case when d.spaceToSidMapId.spaceId is null then false else true end) from Space s LEFT JOIN " + permissionType.getEntityName() + " d ON s.id = d.spaceToSidMapId.spaceId AND SID_ID in (:sids)";
                Query query = session.createQuery(hqlQuery, SpaceKeyWithPermission.class);
                query.setParameter("sids", (Object)sids);
                query.setCacheable(true);
                return query.list();
            }
            String hqlQuery = "select distinct new " + SpaceKeyWithPermission.class.getName() + "(s.key, false) from Space s";
            Query query = session.createQuery(hqlQuery, SpaceKeyWithPermission.class);
            query.setCacheable(true);
            return query.list();
        });
    }

    public void removeRecord(DenormalisedSpacePermission spacePermission, SpacePermissionType spacePermissionType) {
        this.hibernateTemplate.delete(spacePermissionType.getEntityName(), (Object)spacePermission);
    }

    public void addRecord(DenormalisedSpacePermission spacePermission, SpacePermissionType spacePermissionType) {
        this.hibernateTemplate.save(spacePermissionType.getEntityName(), (Object)spacePermission);
    }

    public List<Space> getSpaces(Set<Long> sids, SpacePermissionType permissionType, SpacesQuery spaceQuery, int offset, int limit, boolean permissionExempt) {
        if (sids.size() == 0) {
            return Collections.emptyList();
        }
        return (List)this.hibernateTemplate.execute(session -> {
            List<String> sortBy;
            Set<SpaceStatus> spaceStatuses;
            Set<Long> spaceIdSet;
            StringBuilder hqlQuery = new StringBuilder("select s from Space s");
            if (!permissionExempt) {
                hqlQuery.append(" where s.id in (select p.spaceToSidMapId.spaceId from " + permissionType.getEntityName() + " p where SID_ID in (:sids))");
            } else {
                hqlQuery.append(" where 1 = 1");
            }
            HashMap<String, Object> queryParameters = new HashMap<String, Object>();
            List<String> spaceKeys = spaceQuery.getSpaceKeys();
            if (spaceKeys != null && !spaceKeys.isEmpty()) {
                hqlQuery.append(" and s.lowerKey in (:spaceKeyList)");
                queryParameters.put("spaceKeyList", spaceKeys.stream().map(String::toLowerCase).collect(Collectors.toList()));
            }
            if ((spaceIdSet = spaceQuery.getSpaceIds()) != null && !spaceIdSet.isEmpty()) {
                hqlQuery.append(" and s.id in (:spaceIdSet)");
                queryParameters.put("spaceIdSet", spaceIdSet);
            }
            if ((spaceStatuses = spaceQuery.getSpaceStatuses()) != null && !spaceStatuses.isEmpty()) {
                hqlQuery.append(" and s.spaceStatus in (:spaceStatusList)");
                queryParameters.put("spaceStatusList", spaceStatuses);
            }
            if ((sortBy = spaceQuery.getSortBy()) != null && !sortBy.isEmpty()) {
                hqlQuery.append(" order by ");
                hqlQuery.append(String.join((CharSequence)",", sortBy));
            }
            Query query = session.createQuery(hqlQuery.toString(), Space.class);
            if (!permissionExempt) {
                query.setParameter("sids", (Object)sids);
            }
            queryParameters.forEach((arg_0, arg_1) -> ((Query)query).setParameter(arg_0, arg_1));
            boolean enableCaching = limit < 100;
            query.setCacheable(enableCaching);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.list();
        });
    }

    public List<Long> getOrphanSpacesInFastPermissions(SpacePermissionType spacePermissionType, int limit) {
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "select distinct sp.spaceToSidMapId.spaceId from " + spacePermissionType.getEntityName() + " sp left join Space s on s.id = sp.spaceToSidMapId.spaceId where s.id is null";
            Query query = session.createQuery(hqlQuery, Long.class);
            query.setMaxResults(limit);
            return query.list();
        });
    }

    public void deleteFastSpacePermissionsForSpaces(SpacePermissionType spacePermissionType, Collection<Long> spaceIds) {
        this.hibernateTemplate.execute(session -> {
            String hqlQuery = "delete from " + spacePermissionType.getEntityName() + " sp where sp.spaceToSidMapId.spaceId in (:spaceIds)";
            Query query = session.createQuery(hqlQuery);
            query.setParameter("spaceIds", (Object)spaceIds);
            query.executeUpdate();
            return null;
        });
    }
}

