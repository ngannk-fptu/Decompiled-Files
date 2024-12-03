/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.model.directory.DirectorySynchronisationStatus
 */
package com.atlassian.crowd.embedded.spi;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.directory.DirectorySynchronisationStatus;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@ExperimentalSpi
public interface DirectorySynchronisationStatusDao {
    public Optional<DirectorySynchronisationStatus> findActiveForDirectory(long var1);

    public Optional<DirectorySynchronisationStatus> findLastForDirectory(long var1);

    public DirectorySynchronisationStatus add(DirectorySynchronisationStatus var1);

    public DirectorySynchronisationStatus update(DirectorySynchronisationStatus var1) throws ObjectNotFoundException;

    public long removeStatusesForDirectory(Long var1);

    public long removeAll();

    public long removeAllExcept(long var1, int var3);

    public Collection<DirectorySynchronisationStatus> findActiveSyncsWhereNodeIdNotIn(Set<String> var1);
}

