/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.TransactionalDirectoryDao;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DirectoryDaoTransactionalDecorator
implements TransactionalDirectoryDao {
    private final DirectoryDao dao;

    public DirectoryDaoTransactionalDecorator(DirectoryDao dao) {
        this.dao = dao;
    }

    @Transactional(readOnly=true)
    public Directory findById(long directoryId) throws DirectoryNotFoundException {
        return this.dao.findById(directoryId);
    }

    public Directory update(Directory directory) throws DirectoryNotFoundException {
        return this.dao.update(directory);
    }

    @Transactional(readOnly=true)
    public Directory findByName(String name) throws DirectoryNotFoundException {
        return this.dao.findByName(name);
    }

    public Directory add(Directory directory) {
        return this.dao.add(directory);
    }

    public void remove(Directory directory) throws DirectoryNotFoundException {
        this.dao.remove(directory);
    }

    @Deprecated
    @Transactional(readOnly=true)
    public List<Directory> findAll() {
        return this.dao.findAll();
    }

    @Transactional(readOnly=true)
    public List<Directory> search(EntityQuery<Directory> entityQuery) {
        return this.dao.search(entityQuery);
    }
}

