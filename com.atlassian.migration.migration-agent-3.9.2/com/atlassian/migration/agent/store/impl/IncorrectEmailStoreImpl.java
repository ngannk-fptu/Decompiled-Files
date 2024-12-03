/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.IncorrectEmail;
import com.atlassian.migration.agent.entity.SortOrder;
import com.atlassian.migration.agent.entity.UserBaseScanSortKey;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.store.IncorrectEmailStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;

public class IncorrectEmailStoreImpl
implements IncorrectEmailStore {
    private final EntityManagerTemplate tmpl;

    public IncorrectEmailStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public void save(IncorrectEmail incorrectEmail) {
        this.tmpl.persist(incorrectEmail);
    }

    @Override
    public void deleteAll() {
        this.tmpl.query("delete from IncorrectEmail ie").update();
    }

    @Override
    public List<IncorrectEmail> getIncorrectEmailsByCheckType(String scanId, CheckType checkType) {
        String jpql = "select ie from IncorrectEmail ie where ie.checkType=:checkType and ie.scanId=:scanId";
        return this.tmpl.query(IncorrectEmail.class, "select ie from IncorrectEmail ie where ie.checkType=:checkType and ie.scanId=:scanId").param("checkType", (Object)checkType.value()).param("scanId", (Object)scanId).list();
    }

    @Override
    public List<IncorrectEmail> getIncorrectEmailsByCheckType(String scanId, CheckType checkType, int page, int limit, UserBaseScanSortKey sortKey, SortOrder sortOrder) {
        int offset = (page - 1) * limit;
        String jpql = "select ie from IncorrectEmail ie where ie.checkType=:checkType and ie.scanId=:scanId " + this.orderBy(sortKey, sortOrder);
        return this.tmpl.query(IncorrectEmail.class, jpql).param("checkType", (Object)checkType.value()).param("scanId", (Object)scanId).first(offset).max(limit).list();
    }

    String orderBy(UserBaseScanSortKey sortKey, SortOrder sortOrder) {
        return "order by " + sortKey.getDatabaseColumnToSortBy() + " " + sortOrder.name() + ", id";
    }

    @Override
    public long countIncorrectEmailsByCheckType(String scanId, CheckType checkType) {
        String jpql = "select count(ie) from IncorrectEmail ie where ie.checkType=:checkType and ie.scanId=:scanId";
        return this.tmpl.query(Long.class, "select count(ie) from IncorrectEmail ie where ie.checkType=:checkType and ie.scanId=:scanId").param("checkType", (Object)checkType.value()).param("scanId", (Object)scanId).single();
    }
}

