/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.user.persistence.dao;

import bucket.core.persistence.ObjectDao;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface PersonalInformationDao
extends ObjectDao {
    public PersonalInformation getByUser(ConfluenceUser var1);

    public List<PersonalInformation> getAllByUser(ConfluenceUser var1);

    public PersonalInformation getById(long var1);

    public @NonNull List<Long> findIdsWithAssociatedUser();
}

