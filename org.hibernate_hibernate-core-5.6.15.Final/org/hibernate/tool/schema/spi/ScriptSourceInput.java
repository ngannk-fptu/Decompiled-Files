/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import java.util.List;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;

public interface ScriptSourceInput {
    public void prepare();

    public List<String> read(ImportSqlCommandExtractor var1);

    public void release();
}

