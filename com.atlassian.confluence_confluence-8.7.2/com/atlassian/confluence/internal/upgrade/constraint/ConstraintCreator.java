/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.internal.upgrade.constraint;

import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.ddl.AlterTableExecutor;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DataAccessException;

public class ConstraintCreator {
    private final DdlExecutor ddlExecutor;
    private final AlterTableExecutor alterTableExecutor;

    public ConstraintCreator(DdlExecutor ddlExecutor, AlterTableExecutor alterTableExecutor) {
        this.ddlExecutor = Objects.requireNonNull(ddlExecutor);
        this.alterTableExecutor = Objects.requireNonNull(alterTableExecutor);
    }

    public void create(String table, String name, List<String> columns) throws UpgradeException {
        try {
            if (columns.size() == 1) {
                this.ddlExecutor.executeDdl(Collections.singletonList(this.ddlExecutor.createUniqueConstraintWithMultipleNullsCommand(name, table, columns.get(0))));
            } else {
                this.alterTableExecutor.alterTable(table, Collections.singletonList(this.alterTableExecutor.createAddUniqueConstraintCommand(name, columns.toArray(new String[0]))));
            }
        }
        catch (DataAccessException dae) {
            throw new UpgradeException("Error adding unique constraint " + name, (Throwable)dae);
        }
    }
}

