/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import java.util.Collections;
import java.util.List;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
import org.hibernate.tool.schema.spi.ScriptSourceInput;

public class ScriptSourceInputNonExistentImpl
implements ScriptSourceInput {
    public static final ScriptSourceInputNonExistentImpl INSTANCE = new ScriptSourceInputNonExistentImpl();

    @Override
    public void prepare() {
    }

    @Override
    public List<String> read(ImportSqlCommandExtractor commandExtractor) {
        return Collections.emptyList();
    }

    @Override
    public void release() {
    }
}

