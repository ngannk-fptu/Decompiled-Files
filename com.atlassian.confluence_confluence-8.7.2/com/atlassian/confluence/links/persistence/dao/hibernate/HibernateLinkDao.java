/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.query.NativeQuery
 *  org.hibernate.query.Query
 *  org.springframework.dao.support.DataAccessUtils
 */
package com.atlassian.confluence.links.persistence.dao.hibernate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.internal.links.persistence.LinkDaoInternal;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.GeneralUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.dao.support.DataAccessUtils;

public class HibernateLinkDao
extends ConfluenceHibernateObjectDao<OutgoingLink>
implements LinkDaoInternal {
    private static final int BATCH_SIZE = 1000;

    @Override
    public Class<OutgoingLink> getPersistentClass() {
        return OutgoingLink.class;
    }

    @Override
    public <T> Stream<T> countIncomingLinksForContents(SpaceContentEntityObject rootPage, SpaceContentEntityObject parentPage, Function<Object, T> mapper) {
        Objects.requireNonNull(rootPage);
        Objects.requireNonNull(parentPage);
        Preconditions.checkArgument((boolean)rootPage.getSpaceKey().equals(parentPage.getSpaceKey()));
        String lowerSpaceKey = rootPage.getSpace().getLowerKey();
        Stream stream = (Stream)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.getNamedQuery("confluence.links_countOutgoingLinkInSpaceHierarchy").setParameter("searchSpaceKey", (Object)lowerSpaceKey).setParameter("rootPageId", (Object)rootPage.getId()).setParameter("parentPageId", (Object)parentPage.getId());
            return queryObject.list().stream();
        });
        return Objects.requireNonNull(stream).map(mapper);
    }

    @Override
    public int countPagesWithIncomingLinks(SpaceContentEntityObject rootPage) {
        Objects.requireNonNull(rootPage);
        String lowerSpaceKey = rootPage.getSpace().getLowerKey();
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.getNamedQuery("confluence.links_countNumberOfPageHaveIncomingLinkInHierarchy").setParameter("searchSpaceKey", (Object)lowerSpaceKey).setParameter("rootPageId", (Object)rootPage.getId());
            return queryObject.list();
        })));
    }

    @Override
    public List<OutgoingLink> getLinksTo(ContentEntityObject theContent) {
        if (theContent == null || theContent.isDraft()) {
            return Collections.emptyList();
        }
        ContentEntityObject content = (ContentEntityObject)theContent.getLatestVersion();
        return (List)this.getHibernateTemplate().executeWithNativeSession(session -> {
            Space space;
            Query queryObject = session.createNamedQuery("confluence.links_findOutgoingLinkBySpaceKeyAndPageTitle", OutgoingLink.class);
            HibernateLinkDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            String spaceKey = null;
            String pageTitle = this.extractTitle(content);
            if (content instanceof SpaceContentEntityObject && (space = ((SpaceContentEntityObject)content).getSpace()) != null) {
                spaceKey = space.getKey();
            }
            if (StringUtils.isEmpty(spaceKey)) {
                return Collections.emptyList();
            }
            return queryObject.setParameter("spaceKey", (Object)GeneralUtil.specialToLowerCase(spaceKey)).setParameter("pageTitle", (Object)GeneralUtil.specialToLowerCase(pageTitle)).list();
        });
    }

    @Override
    public List<ContentEntityObject> getReferringContent(ContentEntityObject content) {
        if (content == null || content.isDraft()) {
            return Collections.emptyList();
        }
        String extractedSpaceKey = this.extractSpaceKey(content);
        if (StringUtils.isBlank((CharSequence)extractedSpaceKey)) {
            extractedSpaceKey = this.extractSpaceKey((ContentEntityObject)content.getLatestVersion());
        }
        if (StringUtils.isEmpty((CharSequence)extractedSpaceKey)) {
            return Collections.emptyList();
        }
        return this.getReferringContent(extractedSpaceKey, Collections.singletonList(content));
    }

    @Override
    public List<ContentEntityObject> getReferringContent(String spaceKey, List<ContentEntityObject> contents) {
        if (StringUtils.isEmpty((CharSequence)spaceKey)) {
            return Collections.emptyList();
        }
        List titles = contents.stream().map(content -> GeneralUtil.specialToLowerCase(this.extractTitle((ContentEntityObject)content))).filter(Objects::nonNull).collect(Collectors.toList());
        Iterable titleBatch = Iterables.partition(titles, (int)1000);
        HashSet set = new HashSet();
        for (List batch : titleBatch) {
            List result = Objects.requireNonNull((List)this.getHibernateTemplate().execute(session -> {
                session.flush();
                NativeQuery query = session.createNativeQuery("select {content.*} from CONTENT {content} INNER JOIN LINKS links on {content}.CONTENTID = links.CONTENTID where links.DESTSPACEKEY = :spacekey and links.LOWERDESTPAGETITLE IN(:titles)").addEntity("content", ContentEntityObject.class);
                query.setParameter("spacekey", (Object)spaceKey);
                query.setParameterList("titles", batch.toArray());
                HibernateLinkDao.applyTransactionTimeout((Query)query, this.getSessionFactory());
                return query.list();
            }));
            set.addAll(result);
        }
        return set.stream().collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    private String extractSpaceKey(ContentEntityObject content) {
        if (content instanceof SpaceContentEntityObject) {
            return ((SpaceContentEntityObject)content).getSpaceKey();
        }
        return "";
    }

    private String extractTitle(ContentEntityObject content) {
        return content instanceof BlogPost ? ((BlogPost)content).getLinkPart() : content.getTitle();
    }

    @Override
    public void removeCorruptOutgoingLinks() {
        this.getHibernateTemplate().execute(session -> {
            List corruptLinks = session.createNativeQuery("select {outgoingLink.*} from LINKS {outgoingLink}, CONTENT content where {outgoingLink}.CONTENTID = content.CONTENTID and content.SPACEID IS NULL", OutgoingLink.class).list();
            for (OutgoingLink link : corruptLinks) {
                link.getSourceContent().removeOutgoingLink(link);
                session.delete((Object)link);
            }
            return null;
        });
    }
}

