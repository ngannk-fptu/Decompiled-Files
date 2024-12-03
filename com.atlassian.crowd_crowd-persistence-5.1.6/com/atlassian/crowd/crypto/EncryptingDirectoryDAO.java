/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.DataReEncryptor
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.search.query.DirectoryQueries
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.crypto.DirectoryPasswordsEncryptor;
import com.atlassian.crowd.embedded.api.DataReEncryptor;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.search.query.DirectoryQueries;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptingDirectoryDAO
implements DirectoryDao,
DataReEncryptor {
    private static final Logger log = LoggerFactory.getLogger(EncryptingDirectoryDAO.class);
    private DirectoryDao delegate;
    private DirectoryPasswordsEncryptor directoryPasswordsEncryptor;

    public void setDelegate(DirectoryDao delegate) {
        this.delegate = delegate;
    }

    public void setDirectoryPasswordsEncryptor(DirectoryPasswordsEncryptor directoryPasswordsEncryptor) {
        this.directoryPasswordsEncryptor = directoryPasswordsEncryptor;
    }

    public Directory findById(long directoryId) throws DirectoryNotFoundException {
        return this.directoryPasswordsEncryptor.decryptPasswords(this.delegate.findById(directoryId));
    }

    public Directory findByName(String name) throws DirectoryNotFoundException {
        return this.directoryPasswordsEncryptor.decryptPasswords(this.delegate.findByName(name));
    }

    public List<Directory> findAll() {
        return this.delegate.findAll().stream().map(this.directoryPasswordsEncryptor::decryptPasswords).collect(Collectors.toList());
    }

    public Directory add(Directory directory) {
        Directory encryptedDirectory = this.directoryPasswordsEncryptor.encryptPasswords(directory);
        Directory added = this.delegate.add(encryptedDirectory);
        return this.directoryPasswordsEncryptor.decryptPasswords(added);
    }

    public Directory update(Directory directory) throws DirectoryNotFoundException {
        Directory encryptedDirectory = this.directoryPasswordsEncryptor.encryptPasswords(directory);
        Directory updated = this.delegate.update(encryptedDirectory);
        return this.directoryPasswordsEncryptor.decryptPasswords(updated);
    }

    public void remove(Directory directory) throws DirectoryNotFoundException {
        this.delegate.remove(directory);
    }

    public List<Directory> search(EntityQuery<Directory> entityQuery) {
        return this.delegate.search(entityQuery).stream().map(this.directoryPasswordsEncryptor::decryptPasswords).collect(Collectors.toList());
    }

    public void reEncrypt() {
        for (Directory directory : this.search((EntityQuery<Directory>)DirectoryQueries.allDirectories())) {
            try {
                this.update(directory);
            }
            catch (DirectoryNotFoundException e) {
                log.warn("Could not encrypt passwords of directory {}.", (Object)directory.getId(), (Object)e);
            }
            catch (Exception e) {
                log.error("Encryption of directory {} password failed.", (Object)directory.getId(), (Object)e);
            }
        }
    }
}

