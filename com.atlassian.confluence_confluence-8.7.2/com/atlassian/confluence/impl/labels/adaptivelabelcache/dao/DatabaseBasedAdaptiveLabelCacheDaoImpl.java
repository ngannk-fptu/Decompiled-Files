/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.hibernate.SessionFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.labels.adaptivelabelcache.dao;

import com.atlassian.confluence.impl.labels.adaptivelabelcache.LiteSearchResultCacheEntry;
import com.atlassian.confluence.impl.labels.adaptivelabelcache.dao.AdaptiveLabelCacheDao;
import com.atlassian.confluence.impl.labels.adaptivelabelcache.dao.MostUsedLabelsCacheRecord;
import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.persistence.dao.SpaceDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DatabaseBasedAdaptiveLabelCacheDaoImpl
implements AdaptiveLabelCacheDao {
    private final SessionFactory sessionFactory;
    private final SpaceDao spaceDao;
    private final Gson gson = new Gson();

    public DatabaseBasedAdaptiveLabelCacheDaoImpl(SessionFactory sessionFactory, SpaceDao spaceDao) {
        this.sessionFactory = sessionFactory;
        this.spaceDao = spaceDao;
    }

    @Override
    public LiteSearchResultCacheEntry read(long spaceId) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        MostUsedLabelsCacheRecord cacheRecord = (MostUsedLabelsCacheRecord)template.execute(session -> (MostUsedLabelsCacheRecord)session.find(MostUsedLabelsCacheRecord.class, (Object)spaceId));
        if (cacheRecord == null) {
            return null;
        }
        if (1 != cacheRecord.getVersion()) {
            return null;
        }
        List<LiteLabelSearchResult> labels = this.deserialiseLabels(cacheRecord.getLabels());
        return new LiteSearchResultCacheEntry(labels, cacheRecord.getRequestLimit(), cacheRecord.getExpirationTs(), cacheRecord.getRequestTs());
    }

    @Override
    public void clear() {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        template.execute(session -> {
            session.createQuery("delete from MostUsedLabelsCacheRecord").executeUpdate();
            return null;
        });
    }

    @Override
    public void removeRecordsExpiredAfter(long timestamp) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        template.execute(session -> {
            session.createQuery("delete from MostUsedLabelsCacheRecord where expirationTs < :ts").setParameter("ts", (Object)timestamp).executeUpdate();
            return null;
        });
    }

    @Override
    public void removeRecord(long spaceId) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        template.execute(session -> {
            session.createQuery("delete from MostUsedLabelsCacheRecord where spaceId = :spaceId").setParameter("spaceId", (Object)spaceId).executeUpdate();
            return null;
        });
    }

    @Override
    public void write(long spaceId, LiteSearchResultCacheEntry cacheEntry) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        MostUsedLabelsCacheRecord dbCacheRecord = new MostUsedLabelsCacheRecord();
        dbCacheRecord.setRequestTs(cacheEntry.getRequestTs());
        dbCacheRecord.setExpirationTs(cacheEntry.getExpirationTs());
        dbCacheRecord.setLabels(this.serialiseLabels(cacheEntry.getList()));
        dbCacheRecord.setRequestLimit(cacheEntry.getRequestedLimit());
        dbCacheRecord.setSpaceId(spaceId);
        dbCacheRecord.setVersion(1);
        template.execute(session -> {
            session.saveOrUpdate((Object)dbCacheRecord);
            return null;
        });
    }

    private List<LiteLabelSearchResult> deserialiseLabels(String labels) {
        return (List)this.gson.fromJson(labels, new TypeToken<List<LiteLabelSearchResult>>(){}.getType());
    }

    private String serialiseLabels(List<LiteLabelSearchResult> list) {
        return this.gson.toJson(list);
    }

    @Override
    public String getSpaceKeyFromSpaceId(long spaceId) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        return (String)template.execute(session -> {
            Space space = this.spaceDao.getById(spaceId);
            if (space == null) {
                throw new RuntimeException("A space with '" + spaceId + "' id does not exist");
            }
            return space.getKey();
        });
    }

    @Override
    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="spotbugs still thinks that NPE is possible")
    public long getSpaceIdByKey(String spaceKey) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        return (Long)template.execute(session -> {
            Space space = this.spaceDao.getSpace(spaceKey);
            if (space == null) {
                throw new RuntimeException("A space with '" + spaceKey + "' key does not exist");
            }
            return space.getId();
        });
    }
}

