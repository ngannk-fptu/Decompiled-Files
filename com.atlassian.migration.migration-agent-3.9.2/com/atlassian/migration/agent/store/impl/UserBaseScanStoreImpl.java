/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.ScanStatus;
import com.atlassian.migration.agent.entity.UserBaseScan;
import com.atlassian.migration.agent.store.UserBaseScanStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.time.Instant;
import java.util.Optional;

public class UserBaseScanStoreImpl
implements UserBaseScanStore {
    private final EntityManagerTemplate tmpl;

    public UserBaseScanStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public void save(UserBaseScan userBaseScan) {
        this.tmpl.persist(userBaseScan);
    }

    @Override
    public void updateStatus(String scanId, ScanStatus status) {
        this.tmpl.query("update UserBaseScan userBaseScan set userBaseScan.status=:newStatus, userBaseScan.finished=:now where userBaseScan.id=:scanId").param("newStatus", (Object)status).param("now", (Object)Instant.now()).param("scanId", (Object)scanId).update();
    }

    @Override
    public void updateStatusAndCounts(String scanId, ScanStatus status, long invalidUsersCount, long duplicateUsersCount) {
        this.tmpl.query("update UserBaseScan userBaseScan set userBaseScan.status=:newStatus, userBaseScan.finished=:now, userBaseScan.invalidUsers=:invalidUsersCount, userBaseScan.duplicateUsers=:duplicateUsersCount where userBaseScan.id=:scanId").param("newStatus", (Object)status).param("now", (Object)Instant.now()).param("scanId", (Object)scanId).param("invalidUsersCount", (Object)invalidUsersCount).param("duplicateUsersCount", (Object)duplicateUsersCount).update();
    }

    @Override
    public Optional<UserBaseScan> get(String scanId) {
        return this.tmpl.query(UserBaseScan.class, "select userBaseScan from UserBaseScan userBaseScan where userBaseScan.id=:scanId").param("scanId", (Object)scanId).first();
    }

    @Override
    public Optional<UserBaseScan> getLatestStarted() {
        return this.tmpl.query(UserBaseScan.class, "select userBaseScan from UserBaseScan userBaseScan order by userBaseScan.created desc").first();
    }
}

