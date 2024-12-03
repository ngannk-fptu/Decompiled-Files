/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.CreateUniqueConstraintWithMultipleNullsCommand;
import com.atlassian.confluence.upgrade.ddl.DdlCommand;
import com.atlassian.confluence.upgrade.ddl.DropIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DropTableCommand;
import com.atlassian.confluence.upgrade.ddl.RenameTableCommand;
import java.util.List;

public interface DdlExecutor {
    public CreateIndexCommand createCreateIndexCommand(String var1, String var2, String ... var3);

    public CreateIndexCommand createCreateIndexCommand(String var1, String var2, boolean var3, String ... var4);

    public CreateUniqueConstraintWithMultipleNullsCommand createUniqueConstraintWithMultipleNullsCommand(String var1, String var2, String var3);

    public DropIndexCommand createDropIndexCommand(String var1, String var2);

    public DropTableCommand createDropTableCommand(String var1);

    public RenameTableCommand createRenameTableCommand(String var1, String var2);

    public void executeDdl(List<? extends DdlCommand> var1);

    public void executeDdlStatements(List<String> var1);

    public List<String> getDdlStatements(List<? extends DdlCommand> var1);
}

