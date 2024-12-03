/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.DelegatedAuthenticationDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.LDAPDirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.DelegatedAuthenticationDirectory;
import com.atlassian.crowd.directory.InternalDirectory;
import com.atlassian.crowd.directory.InternalDirectoryForDelegation;
import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.AbstractDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DelegatedAuthenticationDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.LDAPDirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import java.util.HashMap;
import java.util.Map;

public class DelegatedAuthenticationDirectoryInstanceLoaderImpl
extends AbstractDirectoryInstanceLoader
implements DelegatedAuthenticationDirectoryInstanceLoader {
    private final DirectoryInstanceLoader ldapDirectoryInstanceLoader;
    private final InternalDirectoryInstanceLoader internalDirectoryInstanceLoader;
    private final EventPublisher eventPublisher;
    private final DirectoryDao directoryDao;

    public DelegatedAuthenticationDirectoryInstanceLoaderImpl(LDAPDirectoryInstanceLoader ldapDirectoryInstanceLoader, InternalDirectoryInstanceLoader internalDirectoryInstanceLoader, EventPublisher eventPublisher, DirectoryDao directoryDao) {
        this.ldapDirectoryInstanceLoader = ldapDirectoryInstanceLoader;
        this.internalDirectoryInstanceLoader = internalDirectoryInstanceLoader;
        this.eventPublisher = eventPublisher;
        this.directoryDao = directoryDao;
    }

    @Override
    public RemoteDirectory getDirectory(Directory directory) throws DirectoryInstantiationException {
        RemoteDirectory ldapDirectory = this.ldapDirectoryInstanceLoader.getDirectory(this.getLdapVersionOfDirectory(directory));
        InternalRemoteDirectory internalDirectory = this.internalDirectoryInstanceLoader.getDirectory(this.getInternalVersionOfDirectory(directory, ldapDirectory));
        return new DelegatedAuthenticationDirectory(ldapDirectory, internalDirectory, this.eventPublisher, this.directoryDao);
    }

    private Directory getLdapVersionOfDirectory(Directory directory) {
        DirectoryImpl ldap = new DirectoryImpl(directory);
        String ldapClass = directory.getValue("crowd.delegated.directory.type");
        ldap.setImplementationClass(ldapClass);
        return ldap;
    }

    @VisibleForTesting
    Directory getInternalVersionOfDirectory(Directory delegatedDirectory, RemoteDirectory ldapDirectory) {
        DirectoryImpl internal = new DirectoryImpl(delegatedDirectory);
        internal.setImplementationClass(InternalDirectoryForDelegation.class.getCanonicalName());
        HashMap<String, String> newAttributes = new HashMap<String, String>(internal.getAttributes());
        newAttributes.put("user_encryption_method", "sha");
        newAttributes.put("useNestedGroups", Boolean.toString(ldapDirectory.supportsNestedGroups()));
        internal.setAttributes(newAttributes);
        return internal;
    }

    public RemoteDirectory getRawDirectory(Long id, String className, Map<String, String> attributes) throws DirectoryInstantiationException {
        String ldapClass = attributes.get("crowd.delegated.directory.type");
        RemoteDirectory ldapDirectory = this.ldapDirectoryInstanceLoader.getRawDirectory(id, ldapClass, attributes);
        InternalRemoteDirectory internalDirectory = this.internalDirectoryInstanceLoader.getRawDirectory(id, InternalDirectory.class.getCanonicalName(), attributes);
        return new DelegatedAuthenticationDirectory(ldapDirectory, internalDirectory, this.eventPublisher, this.directoryDao);
    }

    public boolean canLoad(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return DelegatedAuthenticationDirectory.class.isAssignableFrom(clazz);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}

