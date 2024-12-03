/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.internal;

import org.hibernate.FetchMode;
import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.engine.profile.Fetch;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.PropertyPath;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.AssociationType;

public final class FetchStrategyHelper {
    private FetchStrategyHelper() {
    }

    public static FetchStyle determineFetchStyleByProfile(LoadQueryInfluencers loadQueryInfluencers, EntityPersister persister, PropertyPath path, int propertyNumber) {
        String rootPropertyName;
        if (!loadQueryInfluencers.hasEnabledFetchProfiles()) {
            return null;
        }
        String fullPath = path.getFullPath();
        int pos = fullPath.lastIndexOf(rootPropertyName = ((OuterJoinLoadable)persister).getSubclassPropertyName(propertyNumber));
        String relativePropertyPath = pos >= 0 ? fullPath.substring(pos) : rootPropertyName;
        String fetchRole = persister.getEntityName() + '.' + relativePropertyPath;
        for (String profileName : loadQueryInfluencers.getEnabledFetchProfileNames()) {
            FetchProfile profile = loadQueryInfluencers.getSessionFactory().getFetchProfile(profileName);
            Fetch fetch = profile.getFetchByRole(fetchRole);
            if (fetch == null || Fetch.Style.JOIN != fetch.getStyle()) continue;
            return FetchStyle.JOIN;
        }
        return null;
    }

    public static FetchStyle determineFetchStyleByMetadata(FetchMode mappingFetchMode, AssociationType type, SessionFactoryImplementor sessionFactory) {
        if (!type.isEntityType() && !type.isCollectionType()) {
            return FetchStyle.SELECT;
        }
        if (mappingFetchMode == FetchMode.JOIN) {
            return FetchStyle.JOIN;
        }
        if (type.isEntityType()) {
            EntityPersister persister = (EntityPersister)((Object)type.getAssociatedJoinable(sessionFactory));
            if (persister.isBatchLoadable()) {
                return FetchStyle.BATCH;
            }
            if (mappingFetchMode == FetchMode.SELECT) {
                return FetchStyle.SELECT;
            }
            if (!persister.hasProxy()) {
                return FetchStyle.JOIN;
            }
        } else {
            CollectionPersister persister = (CollectionPersister)((Object)type.getAssociatedJoinable(sessionFactory));
            if (persister instanceof AbstractCollectionPersister && ((AbstractCollectionPersister)persister).isSubselectLoadable()) {
                return FetchStyle.SUBSELECT;
            }
            if (persister.getBatchSize() > 0) {
                return FetchStyle.BATCH;
            }
        }
        return FetchStyle.SELECT;
    }

    public static FetchTiming determineFetchTiming(FetchStyle style, AssociationType type, SessionFactoryImplementor sessionFactory) {
        switch (style) {
            case JOIN: {
                return FetchTiming.IMMEDIATE;
            }
            case BATCH: 
            case SUBSELECT: {
                return FetchTiming.DELAYED;
            }
        }
        return FetchStrategyHelper.isSubsequentSelectDelayed(type, sessionFactory) ? FetchTiming.DELAYED : FetchTiming.IMMEDIATE;
    }

    private static boolean isSubsequentSelectDelayed(AssociationType type, SessionFactoryImplementor sessionFactory) {
        if (type.isAnyType()) {
            return false;
        }
        if (type.isEntityType()) {
            return ((EntityPersister)((Object)type.getAssociatedJoinable(sessionFactory))).hasProxy();
        }
        CollectionPersister cp = (CollectionPersister)((Object)type.getAssociatedJoinable(sessionFactory));
        return cp.isLazy() || cp.isExtraLazy();
    }

    public static boolean isJoinFetched(FetchStrategy fetchStrategy) {
        return fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN;
    }
}

