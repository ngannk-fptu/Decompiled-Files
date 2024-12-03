/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.upgrade.ddl.AddUniqueConstraintCommand;
import com.atlassian.confluence.upgrade.ddl.AlterColumnNullabilityCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;
import com.atlassian.confluence.upgrade.ddl.DropUniqueConstraintByColumnsCommand;
import com.atlassian.confluence.upgrade.ddl.DropUniqueConstraintCommand;
import com.atlassian.confluence.upgrade.ddl.NullChoice;
import java.util.List;

public interface AlterTableExecutor {
    public AlterColumnNullabilityCommand createAlterColumnNullChoiceCommand(String var1, String var2, NullChoice var3);

    public AddUniqueConstraintCommand createAddUniqueConstraintCommand(String var1, String ... var2);

    public DropUniqueConstraintCommand createDropUniqueConstraintCommand(String var1);

    public AlterTableCommand createDropUniqueConstraintIfExistsCommand(String var1);

    public DropUniqueConstraintByColumnsCommand createDropUniqueConstraintByColumnsCommand(String ... var1);

    public void alterTable(String var1, List<? extends AlterTableCommand> var2);

    public List<String> getAlterTableStatements(String var1, List<? extends AlterTableCommand> var2);
}

