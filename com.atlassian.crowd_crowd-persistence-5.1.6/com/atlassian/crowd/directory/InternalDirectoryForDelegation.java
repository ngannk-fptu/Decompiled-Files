/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.embedded.spi.MembershipDao
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.dao.tombstone.TombstoneDao;
import com.atlassian.crowd.directory.InternalDirectory;
import com.atlassian.crowd.directory.InternalDirectoryUtils;
import com.atlassian.crowd.directory.PasswordConstraintsLoader;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.embedded.spi.MembershipDao;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;

public class InternalDirectoryForDelegation
extends InternalDirectory {
    public InternalDirectoryForDelegation(InternalDirectoryUtils internalDirectoryUtils, PasswordEncoderFactory passwordEncoderFactory, DirectoryDao directoryDao, UserDao userDao, GroupDao groupDao, MembershipDao membershipDao, TombstoneDao tombstoneDao, PasswordConstraintsLoader passwordConstraints) {
        super(internalDirectoryUtils, passwordEncoderFactory, directoryDao, userDao, groupDao, membershipDao, tombstoneDao, passwordConstraints);
    }

    @Override
    public boolean isUserExternalIdReadOnly() {
        return false;
    }
}

