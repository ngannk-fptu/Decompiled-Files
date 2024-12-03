/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.hibernate.SessionFactory
 *  org.hibernate.type.LongType
 *  org.hibernate.type.Type
 *  org.springframework.orm.hibernate5.HibernateOperations
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.pages.attachments;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.impl.content.render.prefetch.ImageDetailsPrefetchDao;
import com.atlassian.confluence.impl.content.render.prefetch.hibernate.HibernatePrefetchHelper;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.attachments.ImageDetails;
import com.atlassian.confluence.pages.attachments.ImageDetailsDao;
import com.atlassian.confluence.pages.attachments.ImageDetailsDto;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate5.HibernateOperations;
import org.springframework.orm.hibernate5.HibernateTemplate;

@ParametersAreNonnullByDefault
public class DefaultImageDetailsDao
implements ImageDetailsDao,
ImageDetailsPrefetchDao {
    private final HibernateTemplate hibernateTemplate;

    public DefaultImageDetailsDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public ImageDetails getImageDetails(Attachment attachment) {
        ImageDetailsDto storedDetails = (ImageDetailsDto)this.hibernateTemplate.get(ImageDetailsDto.class, (Serializable)Long.valueOf(DefaultImageDetailsDao.imageId(attachment)));
        if (storedDetails == null) {
            return null;
        }
        return storedDetails.toImageDetails();
    }

    @Override
    public void save(ImageDetails imageDetails) {
        this.hibernateTemplate.executeWithNativeSession(session -> session.save((Object)new ImageDetailsDto(imageDetails)));
    }

    @Override
    public void removeDetailsFor(Attachment attachment) {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from ImageDetailsDto details where details.id = :imageId", new Object[]{DefaultImageDetailsDao.imageId(attachment)}, new Type[]{LongType.INSTANCE}));
    }

    public static long imageId(Attachment attachment) {
        return attachment.getId();
    }

    @Override
    @Internal
    public int prefetchImageDetails(Collection<Attachment> attachments) {
        Set ids = attachments.stream().map(a -> DefaultImageDetailsDao.imageId(a)).collect(Collectors.toSet());
        return new HibernatePrefetchHelper((HibernateOperations)this.hibernateTemplate).prefetchEntitiesById("id", ids, ImageDetailsDto.class).size();
    }
}

