/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.query.Query
 *  org.hibernate.type.EntityType
 *  org.hibernate.type.LongType
 *  org.hibernate.type.Type
 *  org.hibernate.type.TypeFactory
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.like;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.like.LikeEntity;
import com.atlassian.confluence.like.LikeEntityDao;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.Query;
import org.hibernate.type.EntityType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

@ParametersAreNonnullByDefault
public class HibernateLikeEntityDao
implements LikeEntityDao {
    private static final Function<Searchable, Long> SEARCHABLE_ID_EXTRACTOR = Searchable::getId;
    private final HibernateTemplate hibernate;
    private final SessionFactoryImplementor sessionFactory;

    public HibernateLikeEntityDao(SessionFactory sessionFactory) {
        this.hibernate = new HibernateTemplate(sessionFactory);
        this.sessionFactory = (SessionFactoryImplementor)sessionFactory;
    }

    @Override
    public LikeEntity addLike(ContentEntityObject contentEntity, User user) {
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        LikeEntity likeEntity = new LikeEntity(contentEntity, confluenceUser, RequestTimeThreadLocal.getTimeOrNow());
        this.hibernate.executeWithNativeSession(session -> session.save((Object)likeEntity));
        return likeEntity;
    }

    @Override
    public void removeLike(ContentEntityObject contentEntity, User user) {
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        TypeFactory typeFactory = this.sessionFactory.getTypeResolver().getTypeFactory();
        this.hibernate.execute(session -> SessionHelper.delete(session, "from LikeEntity l where l.content.id = :contentId and l.user = :user", new Object[]{contentEntity.getId(), confluenceUser}, new Type[]{LongType.INSTANCE, typeFactory.manyToOne(ConfluenceUserImpl.class.getName())}));
    }

    @Override
    public void removeAllLikesOn(ContentEntityObject contentEntity) {
        this.hibernate.execute(session -> SessionHelper.delete(session, "from LikeEntity l where l.content.id = :contentId", new Object[]{contentEntity.getId()}, new Type[]{LongType.INSTANCE}));
    }

    @Override
    public void removeAllLikesFor(String username) {
        ConfluenceUser confluenceUser = FindUserHelper.getUserByUsername(username);
        if (confluenceUser != null) {
            this.deleteByUserInternal(confluenceUser);
        }
    }

    @Override
    public void removeAllLikesFor(@NonNull UserKey key) {
        ConfluenceUser confluenceUser = FindUserHelper.getUserByUserKey(key);
        if (confluenceUser != null) {
            this.deleteByUserInternal(confluenceUser);
        }
    }

    private void deleteByUserInternal(ConfluenceUser confluenceUser) {
        EntityType userType = this.sessionFactory.getTypeResolver().getTypeFactory().manyToOne(ConfluenceUserImpl.class.getName());
        this.hibernate.execute(arg_0 -> HibernateLikeEntityDao.lambda$deleteByUserInternal$3(confluenceUser, (Type)userType, arg_0));
    }

    @Override
    public boolean hasLike(ContentEntityObject contentEntity, User user) {
        return DataAccessUtils.intResult((Collection)((Collection)this.hibernate.execute(session -> {
            Query query = session.createQuery("select count(l) from LikeEntity l where l.content.id = :contentId and l.user = :user");
            query.setParameter("contentId", (Object)contentEntity.getId());
            query.setParameter("user", (Object)FindUserHelper.getUser(user));
            return query.list();
        }))) > 0;
    }

    @Override
    public List<LikeEntity> getLikeEntities(Collection<? extends ContentEntityObject> contentEntities) {
        Objects.requireNonNull(contentEntities);
        if (contentEntities.isEmpty()) {
            return Collections.emptyList();
        }
        return Objects.requireNonNull((List)this.hibernate.execute(session -> {
            Query query = session.createQuery("from LikeEntity l where l.content.id in (:contentEntities) order by l.creationDate desc", LikeEntity.class);
            query.setParameterList("contentEntities", Collections2.transform((Collection)contentEntities, SEARCHABLE_ID_EXTRACTOR));
            return query.list();
        }));
    }

    @Override
    public int countLikes(Searchable searchable) {
        Objects.requireNonNull(searchable);
        return DataAccessUtils.intResult((Collection)((Collection)this.hibernate.execute(session -> {
            Query query = session.createQuery("select count(l) from LikeEntity l where l.content.id = :contentId");
            query.setParameter("contentId", (Object)searchable.getId());
            return query.list();
        })));
    }

    @Override
    public Map<Searchable, Integer> countLikes(Collection<? extends Searchable> searchables) {
        Objects.requireNonNull(searchables);
        if (searchables.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap idToSearchableMap = Maps.newHashMap();
        HashMap results = Maps.newHashMap();
        for (Searchable searchable : searchables) {
            idToSearchableMap.put(searchable.getId(), searchable);
            results.put(searchable, 0);
        }
        this.hibernate.execute(session -> {
            Query query = session.createQuery("select content.id, count(l) from LikeEntity l join l.content as content where l.content.id in (:contentEntities) group by content.id");
            query.setParameterList("contentEntities", Collections2.transform((Collection)searchables, SEARCHABLE_ID_EXTRACTOR));
            List hibernateResults = query.list();
            for (Object[] result : hibernateResults) {
                int numberOfLikes = ((Number)result[1]).intValue();
                Long contentId = (Long)result[0];
                results.put((Searchable)idToSearchableMap.get(contentId), numberOfLikes);
            }
            return null;
        });
        return ImmutableMap.copyOf((Map)results);
    }

    private static /* synthetic */ Integer lambda$deleteByUserInternal$3(ConfluenceUser confluenceUser, Type userType, Session session) throws HibernateException {
        return SessionHelper.delete(session, "from LikeEntity l where l.user = :user", new Object[]{confluenceUser}, new Type[]{userType});
    }
}

