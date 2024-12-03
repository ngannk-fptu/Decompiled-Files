/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.query.Query
 *  org.hibernate.type.LongType
 *  org.hibernate.type.StringType
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.security.persistence.dao.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.core.persistence.hibernate.VersionedHibernateObjectDao;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.persistence.dao.SpacePermissionDao;
import com.atlassian.confluence.security.persistence.dao.hibernate.SpacePermissionDTOLight;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.hibernate.query.Query;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class HibernateSpacePermissionDao
extends VersionedHibernateObjectDao<SpacePermission>
implements SpacePermissionDao {
    private static final String GET_PERMISSIONS = "select permission from SpacePermission as permission";

    @Override
    public Class<SpacePermission> getPersistentClass() {
        return SpacePermission.class;
    }

    @Override
    public SpacePermission getById(long id) {
        return (SpacePermission)this.getByClassId(id);
    }

    @Override
    public boolean hasPermission(SpacePermission permission) {
        QueryParamRecord paramRecord = this.buildSpacePermissionQueryBySubject(permission);
        List result = this.executeSpacePermissionQueryBySubject(permission, paramRecord);
        return !result.isEmpty();
    }

    @Override
    public List<SpacePermission> findAllGlobalPermissions() {
        return this.findNamedQuery("confluence.sp_findAllGlobalPermissions");
    }

    @Override
    public List<SpacePermission> findAllGlobalPermissionsForType(String permissionType) {
        return this.findNamedQueryStringParam("confluence.sp_findAllGlobalPermissionsForType", "type", permissionType);
    }

    @Override
    public List<SpacePermission> findPermissionsForGroup(String group) {
        return this.findNamedQueryStringParam("confluence.sp_findPermissionsForGroup", "group", group);
    }

    @Override
    public List<SpacePermission> findPermissionsForSpace(Space space) {
        return this.findNamedQueryStringParam("confluence.sp_findPermissionsForSpace", "spaceid", space.getId());
    }

    @Override
    public List<SpacePermissionDTOLight> findPermissionsForSpacesAndTypes(Set<Long> spaceIdList, Collection<String> typeList) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("select new com.atlassian.confluence.security.persistence.dao.hibernate.SpacePermissionDTOLight(sp.id, sp.userSubject.key, sp.group, sp.type, sp.allUsersSubject, sp.space.id) from SpacePermission sp where space.id in (:spaceIdList) and sp.type in (:typeList)");
            query.setParameter("spaceIdList", (Object)spaceIdList);
            query.setParameter("typeList", (Object)typeList);
            return query.list();
        });
    }

    @Override
    public Collection<SpacePermission> findGroupPermissionsForSpace(Space space, String permissionType) {
        return this.findNamedQueryStringParams("confluence.sp_findPermittedGroupPermissionsForSpace", "spaceid", space.getId(), "type", (Object)permissionType, HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public Collection<SpacePermission> findGlobalGroupPermissions(String permissionType) {
        return this.findNamedQueryStringParam("confluence.sp_findGlobalPermittedGroupPermissions", "type", permissionType, HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public List<SpacePermission> findPermissionsForUser(ConfluenceUser user) {
        return this.findNamedQueryStringParam("confluence.sp_findPermissionsForUser", "user", user);
    }

    @Override
    public void removePermissionsForUser(String userName) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(userName);
        this.getHibernateTemplate().execute(session -> {
            SessionHelper.delete(session, "select permission from SpacePermission as permission where permission.userSubject = :userKey", new Object[]{user.getKey().getStringValue()}, new Type[]{StringType.INSTANCE});
            return null;
        });
    }

    @Override
    public void removePermissionsForGroup(String group) {
        this.getHibernateTemplate().execute(session -> {
            SessionHelper.delete(session, "select permission from SpacePermission as permission where permission.group = :group", new Object[]{group}, new Type[]{StringType.INSTANCE});
            return null;
        });
    }

    @Override
    public void removePermissionsForSpace(Space space) {
        this.getHibernateTemplate().execute(session -> {
            SessionHelper.delete(session, "select permission from SpacePermission as permission where SPACEID = :spaceId", new Object[]{space.getId()}, new Type[]{LongType.INSTANCE});
            return null;
        });
    }

    @Override
    public List findPermissionTypes(SpacePermission permission) {
        QueryParamRecord paramRecord = this.buildSpacePermissionQueryBySubjectIgnoringTypeField(permission);
        return this.executeSpacePermissionQueryBySubject(permission, paramRecord);
    }

    private List executeSpacePermissionQueryBySubject(SpacePermission permission, QueryParamRecord paramRecord) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery(paramRecord.hql.toString());
            query.setCacheable(false);
            if (paramRecord.hasSpace) {
                query.setParameter("spaceId", (Object)permission.getSpaceId(), (Type)LongType.INSTANCE);
            }
            if (paramRecord.hasGroup) {
                query.setParameter("group", (Object)permission.getGroup(), (Type)StringType.INSTANCE);
            }
            if (paramRecord.hasUserSubject) {
                query.setParameter("userSubject", (Object)permission.getUserSubject());
            }
            if (paramRecord.hasAllUsersSubject) {
                query.setParameter("allUsersSubject", (Object)permission.getAllUsersSubject(), (Type)StringType.INSTANCE);
            }
            if (paramRecord.hasType) {
                query.setParameter("type", (Object)permission.getType(), (Type)StringType.INSTANCE);
            }
            return query.list();
        });
    }

    private QueryParamRecord buildSpacePermissionQueryBySubjectIgnoringTypeField(SpacePermission permission) {
        QueryParamRecord paramRecord = new QueryParamRecord();
        paramRecord.hql.append("from SpacePermission sp ");
        if (permission.getSpace() == null) {
            paramRecord.hql.append("where sp.space is null ");
        } else {
            paramRecord.hql.append("where sp.space.id = :spaceId ");
            paramRecord.hasSpace = true;
        }
        if (permission.getGroup() == null) {
            paramRecord.hql.append("and sp.group is null ");
        } else {
            paramRecord.hql.append("and sp.group = :group ");
            paramRecord.hasGroup = true;
        }
        if (permission.getUserSubject() == null) {
            paramRecord.hql.append("and sp.userSubject is null ");
        } else {
            paramRecord.hql.append("and sp.userSubject = :userSubject ");
            paramRecord.hasUserSubject = true;
        }
        if (permission.getAllUsersSubject() == null) {
            paramRecord.hql.append("and sp.allUsersSubject is null ");
        } else {
            paramRecord.hql.append("and sp.allUsersSubject = :allUsersSubject ");
            paramRecord.hasAllUsersSubject = true;
        }
        return paramRecord;
    }

    private QueryParamRecord buildSpacePermissionQueryBySubject(SpacePermission permission) {
        QueryParamRecord paramRecord = this.buildSpacePermissionQueryBySubjectIgnoringTypeField(permission);
        if (permission.getType() == null) {
            paramRecord.hql.append("and sp.type is null");
        } else {
            paramRecord.hql.append("and sp.type = :type");
            paramRecord.hasType = true;
        }
        return paramRecord;
    }

    private static class QueryParamRecord {
        boolean hasSpace;
        boolean hasGroup;
        boolean hasUserSubject;
        boolean hasAllUsersSubject;
        boolean hasType;
        StringBuilder hql = new StringBuilder();

        private QueryParamRecord() {
        }
    }
}

