/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.event.application.ApplicationCreatedEvent
 *  com.atlassian.crowd.event.application.ApplicationDeletedEvent
 *  com.atlassian.crowd.event.application.ApplicationDirectoryAddedEvent
 *  com.atlassian.crowd.event.application.ApplicationDirectoryOrderUpdatedEvent
 *  com.atlassian.crowd.event.application.ApplicationDirectoryRemovedEvent
 *  com.atlassian.crowd.event.application.ApplicationRemoteAddressAddedEvent
 *  com.atlassian.crowd.event.application.ApplicationRemoteAddressRemovedEvent
 *  com.atlassian.crowd.event.application.ApplicationUpdatedEvent
 *  com.atlassian.crowd.exception.ApplicationAlreadyExistsException
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.manager.application.ApplicationManager
 *  com.atlassian.crowd.manager.application.ApplicationManagerException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.crowd.model.application.RemoteAddress
 *  com.atlassian.crowd.password.encoder.PasswordEncoder
 *  com.atlassian.crowd.password.encoder.UpgradeablePasswordEncoder
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.ApplicationTermKeys
 *  com.atlassian.event.api.EventPublisher
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.event.application.ApplicationCreatedEvent;
import com.atlassian.crowd.event.application.ApplicationDeletedEvent;
import com.atlassian.crowd.event.application.ApplicationDirectoryAddedEvent;
import com.atlassian.crowd.event.application.ApplicationDirectoryOrderUpdatedEvent;
import com.atlassian.crowd.event.application.ApplicationDirectoryRemovedEvent;
import com.atlassian.crowd.event.application.ApplicationRemoteAddressAddedEvent;
import com.atlassian.crowd.event.application.ApplicationRemoteAddressRemovedEvent;
import com.atlassian.crowd.event.application.ApplicationUpdatedEvent;
import com.atlassian.crowd.exception.ApplicationAlreadyExistsException;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.manager.application.ApplicationManager;
import com.atlassian.crowd.manager.application.ApplicationManagerException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.application.RemoteAddress;
import com.atlassian.crowd.password.encoder.PasswordEncoder;
import com.atlassian.crowd.password.encoder.UpgradeablePasswordEncoder;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.ApplicationTermKeys;
import com.atlassian.event.api.EventPublisher;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ApplicationManagerGeneric
implements ApplicationManager {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationManagerGeneric.class);
    private final ApplicationDAO applicationDao;
    private final PasswordEncoderFactory passwordEncoderFactory;
    private final EventPublisher eventPublisher;

    public ApplicationManagerGeneric(ApplicationDAO applicationDao, PasswordEncoderFactory passwordEncoderFactory, EventPublisher eventPublisher) {
        this.applicationDao = applicationDao;
        this.passwordEncoderFactory = passwordEncoderFactory;
        this.eventPublisher = eventPublisher;
    }

    public Application add(Application application) throws InvalidCredentialException, ApplicationAlreadyExistsException {
        Validate.notNull((Object)application, (String)"application should not be null", (Object[])new Object[0]);
        if (application.getCredential() == null) {
            throw new InvalidCredentialException("Password of the application cannot be null");
        }
        if (this.applicationWithNameExists(application.getName())) {
            throw new ApplicationAlreadyExistsException("An application with the specified name already exists");
        }
        PasswordCredential encryptedCredential = this.encryptAndUpdateApplicationCredential(application.getCredential());
        Application app = this.applicationDao.add(application, encryptedCredential);
        this.eventPublisher.publish((Object)new ApplicationCreatedEvent(app));
        return app;
    }

    public Application findById(long id) throws ApplicationNotFoundException {
        return this.applicationDao.findById(id);
    }

    public Application findByName(String name) throws ApplicationNotFoundException {
        return this.applicationDao.findByName(name);
    }

    public void remove(Application application) throws ApplicationManagerException {
        if (application.isPermanent()) {
            throw new ApplicationManagerException("Cannot delete a permanent application");
        }
        try {
            ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
            this.applicationDao.remove(application);
            this.eventPublisher.publish((Object)new ApplicationDeletedEvent((Application)oldApplication));
        }
        catch (ApplicationNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void removeDirectoryFromApplication(Directory directory, Application application) throws ApplicationManagerException {
        ApplicationDirectoryMapping mapping = application.getApplicationDirectoryMapping(directory.getId().longValue());
        if (mapping != null) {
            try {
                ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
                this.applicationDao.removeDirectoryMapping(application.getId().longValue(), directory.getId().longValue());
                ImmutableApplication newApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
                this.eventPublisher.publish((Object)new ApplicationDirectoryRemovedEvent((Application)oldApplication, (Application)newApplication, directory));
            }
            catch (ApplicationNotFoundException applicationNotFoundException) {
                // empty catch block
            }
        }
    }

    public List<Application> search(EntityQuery query) {
        return this.applicationDao.search(query);
    }

    public List<Application> findAll() {
        return this.search(QueryBuilder.queryFor(Application.class, (EntityDescriptor)EntityDescriptor.application()).returningAtMost(-1));
    }

    public Application update(Application application) throws ApplicationManagerException, ApplicationNotFoundException {
        Application currentApplication;
        if (application.getType() == ApplicationType.CROWD && !application.isActive()) {
            throw new ApplicationManagerException("Cannot deactivate the Crowd application");
        }
        if (application.isPermanent()) {
            try {
                Application savedApp = this.findById(application.getId());
                if (!savedApp.getName().equals(application.getName())) {
                    throw new ApplicationManagerException("Cannot rename a permanent application");
                }
            }
            catch (ApplicationNotFoundException e) {
                throw new ApplicationManagerException(e.getMessage(), (Throwable)e);
            }
        }
        try {
            currentApplication = this.findByName(application.getName());
        }
        catch (ApplicationNotFoundException e) {
            currentApplication = this.findById(application.getId());
        }
        if (application.getId().equals(currentApplication.getId())) {
            ImmutableApplication oldApplication = ImmutableApplication.from((Application)currentApplication);
            Application savedApplication = this.applicationDao.update(application);
            this.eventPublisher.publish((Object)new ApplicationUpdatedEvent((Application)oldApplication, (Application)ImmutableApplication.from((Application)this.findById(oldApplication.getId()))));
            return savedApplication;
        }
        throw new ApplicationManagerException("You potentially tried to update an application with a different ID than the one you passed in");
    }

    public void updateCredential(Application application, PasswordCredential passwordCredential) throws ApplicationManagerException, ApplicationNotFoundException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)passwordCredential);
        Validate.notNull((Object)passwordCredential.getCredential());
        PasswordCredential oldPassword = new PasswordCredential(application.getCredential());
        ImmutableApplication oldApplication = ImmutableApplication.builder((Application)this.findById(application.getId())).setPasswordCredential(oldPassword).build();
        PasswordCredential encryptedCredential = this.encryptAndUpdateApplicationCredential(passwordCredential);
        this.applicationDao.updateCredential(application, encryptedCredential);
        this.eventPublisher.publish((Object)new ApplicationUpdatedEvent((Application)oldApplication, (Application)ImmutableApplication.from((Application)this.findById(application.getId()))));
    }

    public boolean authenticate(Application application, PasswordCredential testCredential) throws ApplicationNotFoundException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)testCredential);
        Validate.notNull((Object)testCredential.getCredential());
        if (PasswordCredential.NONE.equals((Object)application.getCredential())) {
            return false;
        }
        PasswordEncoder encoder = this.getAtlassianSecurityEncoder();
        if (!encoder.isPasswordValid(application.getCredential().getCredential(), testCredential.getCredential(), null)) {
            return false;
        }
        this.upgradePasswordIfRequired(application, encoder, testCredential.getCredential());
        return true;
    }

    private void upgradePasswordIfRequired(Application application, PasswordEncoder encoder, String rawPass) throws ApplicationNotFoundException {
        UpgradeablePasswordEncoder upgradeableEncoder;
        if (encoder instanceof UpgradeablePasswordEncoder && (upgradeableEncoder = (UpgradeablePasswordEncoder)encoder).isUpgradeRequired(application.getCredential().getCredential())) {
            String newEncPass = encoder.encodePassword(rawPass, null);
            this.applicationDao.updateCredential(application, new PasswordCredential(newEncPass, true));
        }
    }

    public void addDirectoryMapping(Application application, Directory directory, boolean allowAllToAuthenticate, OperationType ... operationTypes) throws ApplicationNotFoundException, DirectoryNotFoundException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)application.getId());
        Validate.notNull((Object)directory);
        Validate.notNull((Object)directory.getId());
        ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.applicationDao.addDirectoryMapping(application.getId().longValue(), directory.getId().longValue(), allowAllToAuthenticate, operationTypes);
        ImmutableApplication newApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.eventPublisher.publish((Object)new ApplicationDirectoryAddedEvent((Application)oldApplication, (Application)newApplication, directory));
    }

    public void updateDirectoryMapping(Application application, Directory directory, int position) throws ApplicationNotFoundException, DirectoryNotFoundException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)application.getId());
        Validate.notNull((Object)directory);
        Validate.notNull((Object)directory.getId());
        ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.applicationDao.updateDirectoryMapping(application.getId().longValue(), directory.getId().longValue(), position);
        ImmutableApplication newApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.eventPublisher.publish((Object)new ApplicationDirectoryOrderUpdatedEvent((Application)oldApplication, (Application)newApplication, directory));
        logger.debug("Changed directory mapping order within application <{}> of directory <{}> to position <{}>", new Object[]{application.getId(), directory.getId(), position});
    }

    public void updateDirectoryMapping(Application application, Directory directory, boolean allowAllToAuthenticate) throws ApplicationNotFoundException, DirectoryNotFoundException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)application.getId());
        Validate.notNull((Object)directory);
        Validate.notNull((Object)directory.getId());
        ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.applicationDao.updateDirectoryMapping(application.getId().longValue(), directory.getId().longValue(), allowAllToAuthenticate);
        ImmutableApplication newApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.eventPublisher.publish((Object)new ApplicationUpdatedEvent((Application)oldApplication, (Application)newApplication));
    }

    public void updateDirectoryMapping(Application application, Directory directory, boolean allowAllToAuthenticate, Set<OperationType> operationTypes) throws ApplicationNotFoundException, DirectoryNotFoundException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)application.getId());
        Validate.notNull((Object)directory);
        Validate.notNull((Object)directory.getId());
        ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.applicationDao.updateDirectoryMapping(application.getId().longValue(), directory.getId().longValue(), allowAllToAuthenticate, operationTypes);
        ImmutableApplication newApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.eventPublisher.publish((Object)new ApplicationUpdatedEvent((Application)oldApplication, (Application)newApplication));
    }

    public void addRemoteAddress(Application application, RemoteAddress remoteAddress) throws ApplicationNotFoundException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)application.getId());
        ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.applicationDao.addRemoteAddress(application.getId().longValue(), remoteAddress);
        ImmutableApplication newApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.eventPublisher.publish((Object)new ApplicationRemoteAddressAddedEvent(application, remoteAddress));
        this.eventPublisher.publish((Object)new ApplicationUpdatedEvent((Application)oldApplication, (Application)newApplication));
    }

    public void removeRemoteAddress(Application application, RemoteAddress remoteAddress) throws ApplicationNotFoundException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)application.getId());
        Validate.notNull((Object)remoteAddress);
        ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.applicationDao.removeRemoteAddress(application.getId().longValue(), remoteAddress);
        this.eventPublisher.publish((Object)new ApplicationRemoteAddressRemovedEvent(application, remoteAddress));
        this.eventPublisher.publish((Object)new ApplicationUpdatedEvent((Application)oldApplication, application));
    }

    public void addGroupMapping(Application application, Directory directory, String groupName) throws ApplicationNotFoundException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)application.getId());
        Validate.notNull((Object)directory);
        Validate.notNull((Object)directory.getId());
        ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.applicationDao.addGroupMapping(application.getId().longValue(), directory.getId().longValue(), groupName);
        ImmutableApplication newApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.eventPublisher.publish((Object)new ApplicationUpdatedEvent((Application)oldApplication, (Application)newApplication));
    }

    public void removeGroupMapping(Application application, Directory directory, String groupName) throws ApplicationNotFoundException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)application.getId());
        Validate.notNull((Object)directory);
        Validate.notNull((Object)directory.getId());
        ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.applicationDao.removeGroupMapping(application.getId().longValue(), directory.getId().longValue(), groupName);
        ImmutableApplication newApplication = ImmutableApplication.from((Application)this.findById(application.getId()));
        this.eventPublisher.publish((Object)new ApplicationUpdatedEvent((Application)oldApplication, (Application)newApplication));
    }

    private PasswordCredential encryptAndUpdateApplicationCredential(PasswordCredential passwordCredential) {
        if (passwordCredential.isEncryptedCredential()) {
            return passwordCredential;
        }
        PasswordEncoder encoder = this.getAtlassianSecurityEncoder();
        String encryptedPassword = encoder.encodePassword(passwordCredential.getCredential(), null);
        return new PasswordCredential(encryptedPassword, true);
    }

    private PasswordEncoder getAtlassianSecurityEncoder() {
        return this.passwordEncoderFactory.getEncoder("atlassian-security");
    }

    private boolean applicationWithNameExists(String name) {
        EntityQuery query = QueryBuilder.queryFor(Application.class, (EntityDescriptor)EntityDescriptor.application()).with((SearchRestriction)Restriction.on((Property)ApplicationTermKeys.NAME).exactlyMatching((Object)name)).returningAtMost(1);
        List results = this.applicationDao.search(query);
        return results.size() > 0;
    }
}

