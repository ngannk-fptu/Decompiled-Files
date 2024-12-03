/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 */
package com.atlassian.data.activeobjects.repository;

import net.java.ao.RawEntity;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface ActiveObjectsNoOpRepository<T extends RawEntity<ID>, ID>
extends Repository<T, ID> {
}

