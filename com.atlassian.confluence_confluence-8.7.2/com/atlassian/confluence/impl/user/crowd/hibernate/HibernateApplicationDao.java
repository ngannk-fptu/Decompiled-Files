/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryMappingNotFoundException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.application.RemoteAddress
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Root
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.type.LongType
 *  org.hibernate.type.Type
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateSearch;
import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryMappingNotFoundException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationImpl;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.application.RemoteAddress;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate5.HibernateTemplate;

public final class HibernateApplicationDao
implements ApplicationDAO {
    private final HibernateTemplate hibernateTemplate;
    private final DirectoryDao directoryDao;

    public HibernateApplicationDao(SessionFactory sessionFactory, DirectoryDao directoryDao) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.directoryDao = directoryDao;
    }

    public ApplicationImpl findById(long id) throws ApplicationNotFoundException {
        ApplicationImpl result = (ApplicationImpl)this.hibernateTemplate.execute(session -> (ApplicationImpl)session.get(ApplicationImpl.class, (Serializable)Long.valueOf(id)));
        if (result == null) {
            throw new ApplicationNotFoundException(Long.valueOf(id));
        }
        return result;
    }

    public ApplicationImpl findByName(String name) throws ApplicationNotFoundException {
        ApplicationImpl result = (ApplicationImpl)this.hibernateTemplate.execute(session -> (ApplicationImpl)session.createQuery(HibernateApplicationDao.createFindByNameQuery(session, name)).uniqueResult());
        if (result == null) {
            throw new ApplicationNotFoundException(name);
        }
        return result;
    }

    private static CriteriaQuery<ApplicationImpl> createFindByNameQuery(Session session, String name) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(ApplicationImpl.class);
        return query.where((Expression)builder.equal((Expression)query.from(ApplicationImpl.class).get("lowerName"), (Object)IdentifierUtils.toLowerCase((String)name)));
    }

    public Application add(Application application, PasswordCredential passwordCredential) {
        ApplicationImpl internalApplication = ApplicationImpl.newInstance((Application)application);
        internalApplication.setCredential(passwordCredential);
        internalApplication.setCreatedDateToNow();
        internalApplication.setUpdatedDateToNow();
        internalApplication.setAttribute("atlassian_sha1_applied", Boolean.TRUE.toString());
        internalApplication.validate();
        this.hibernateTemplate.save((Object)internalApplication);
        return internalApplication;
    }

    public Application update(Application application) throws ApplicationNotFoundException {
        ApplicationImpl internalApplication = this.findById(application.getId());
        internalApplication.updateDetailsFromApplication(application);
        internalApplication.setUpdatedDateToNow();
        internalApplication.validate();
        this.hibernateTemplate.saveOrUpdate((Object)internalApplication);
        return internalApplication;
    }

    public void updateCredential(Application application, PasswordCredential passwordCredential) throws ApplicationNotFoundException {
        if (!passwordCredential.isEncryptedCredential()) {
            throw new IllegalArgumentException("The application password needs to be encrypted before being updated");
        }
        ApplicationImpl internalApplication = this.findByName(application.getName());
        internalApplication.setCredential(passwordCredential);
        internalApplication.setAttribute("atlassian_sha1_applied", Boolean.TRUE.toString());
        internalApplication.setUpdatedDateToNow();
        this.hibernateTemplate.saveOrUpdate((Object)internalApplication);
    }

    public void remove(Application application) {
        this.hibernateTemplate.delete((Object)application);
    }

    public List<Application> search(EntityQuery<Application> query) {
        if (query.getEntityDescriptor().getEntityType() != Entity.APPLICATION) {
            throw new IllegalArgumentException("ApplicationDAO can only evaluate EntityQueries for Entity.APPLICATION");
        }
        return (List)this.hibernateTemplate.executeWithNativeSession(HibernateSearch.forEntities(query)::doInHibernate);
    }

    public void addDirectoryMapping(long applicationId, long directoryId, boolean allowAllToAuthenticate, OperationType ... operationTypes) throws ApplicationNotFoundException, DirectoryNotFoundException {
        ApplicationImpl application = this.findById(applicationId);
        Directory directory = this.directoryDao.findById(directoryId);
        application.addDirectoryMapping(directory, allowAllToAuthenticate, operationTypes);
        application.setUpdatedDateToNow();
        this.hibernateTemplate.saveOrUpdate((Object)application);
    }

    public void addRemoteAddress(long applicationId, RemoteAddress remoteAddress) {
        throw new UnsupportedOperationException("Remote addresses not supported.");
    }

    public void removeRemoteAddress(long applicationId, RemoteAddress remoteAddress) {
        throw new UnsupportedOperationException("Remote addresses not supported.");
    }

    public void removeDirectoryMapping(long applicationId, long directoryId) throws ApplicationNotFoundException {
        ApplicationImpl application = this.findById(applicationId);
        application.removeDirectoryMapping(directoryId);
        application.setUpdatedDateToNow();
        this.hibernateTemplate.saveOrUpdate((Object)application);
    }

    public void removeDirectoryMappings(long directoryId) {
        for (Application application : this.findApplicationsWithDirectoryMapping(directoryId)) {
            ApplicationImpl applicationImpl = (ApplicationImpl)application;
            applicationImpl.removeDirectoryMapping(directoryId);
            applicationImpl.setUpdatedDateToNow();
            this.hibernateTemplate.saveOrUpdate((Object)applicationImpl);
            this.hibernateTemplate.flush();
        }
    }

    public void addGroupMapping(long applicationId, long directoryId, String groupName) {
        throw new UnsupportedOperationException("Group mappings for directories not supported.");
    }

    public void removeGroupMapping(long applicationId, long directoryId, String groupName) {
        throw new UnsupportedOperationException("Group mappings for directories not supported.");
    }

    public void removeGroupMappings(long directoryId, String groupName) {
    }

    public List<Application> findAuthorisedApplications(long directoryId, List<String> groupNames) {
        return this.findApplicationsWithDirectoryMapping(directoryId);
    }

    public void updateDirectoryMapping(long applicationId, long directoryId, int position) throws ApplicationNotFoundException {
        ApplicationImpl application = this.findById(applicationId);
        List directoryMappings = application.getDirectoryMappings();
        int currentIndex = directoryMappings.indexOf(application.getDirectoryMapping(directoryId));
        if (currentIndex >= 0 && position >= 0 && position < directoryMappings.size()) {
            DirectoryMapping directoryMappingToMove = (DirectoryMapping)directoryMappings.remove(currentIndex);
            directoryMappings.add(position, directoryMappingToMove);
            application.setUpdatedDateToNow();
            this.hibernateTemplate.saveOrUpdate((Object)application);
        }
    }

    public void updateDirectoryMapping(long applicationId, long directoryId, boolean allowAllToAuthenticate) throws ApplicationNotFoundException {
        ApplicationImpl application = this.findById(applicationId);
        DirectoryMapping directoryMapping = application.getDirectoryMapping(directoryId);
        directoryMapping.setAllowAllToAuthenticate(allowAllToAuthenticate);
        application.setUpdatedDateToNow();
        this.hibernateTemplate.saveOrUpdate((Object)application);
    }

    public void updateDirectoryMapping(long applicationId, long directoryId, boolean allowAllToAuthenticate, Set<OperationType> operationTypes) throws ApplicationNotFoundException {
        ApplicationImpl application = this.findById(applicationId);
        DirectoryMapping directoryMapping = application.getDirectoryMapping(directoryId);
        directoryMapping.setAllowAllToAuthenticate(allowAllToAuthenticate);
        directoryMapping.setAllowedOperations(operationTypes);
        application.setUpdatedDateToNow();
        this.hibernateTemplate.saveOrUpdate((Object)application);
    }

    public DirectoryMapping findDirectoryMapping(long applicationId, long directoryId) throws ApplicationNotFoundException, DirectoryMappingNotFoundException {
        ApplicationImpl application = this.findById(applicationId);
        DirectoryMapping result = (DirectoryMapping)this.hibernateTemplate.execute(arg_0 -> HibernateApplicationDao.lambda$findDirectoryMapping$2((Application)application, directoryId, arg_0));
        return Optional.ofNullable(result).orElseThrow(() -> new DirectoryMappingNotFoundException(applicationId, directoryId));
    }

    private List<Application> findApplicationsWithDirectoryMapping(long directoryId) {
        return (List)this.hibernateTemplate.execute(session -> session.getNamedQuery("findApplicationsWithDirectoryMapping").setParameter("directoryId", (Object)directoryId, (Type)LongType.INSTANCE).list());
    }

    private static /* synthetic */ DirectoryMapping lambda$findDirectoryMapping$2(Application application, long directoryId, Session session) throws HibernateException {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(DirectoryMapping.class);
        Root root = query.from(DirectoryMapping.class);
        query.where((Expression)builder.and((Expression)builder.equal((Expression)root.get("application"), (Object)application), (Expression)builder.equal((Expression)root.get("directory").get("id"), (Object)directoryId)));
        return (DirectoryMapping)session.createQuery(query).uniqueResult();
    }
}

