/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
import org.hibernate.tool.schema.internal.SchemaCreatorImpl;
import org.hibernate.tool.schema.spi.ScriptSourceInput;

public abstract class AbstractScriptSourceInput
implements ScriptSourceInput {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(SchemaCreatorImpl.class);

    protected abstract Reader reader();

    @Override
    public void prepare() {
        log.executingImportScript(this.getScriptDescription());
    }

    protected abstract String getScriptDescription();

    @Override
    public List<String> read(ImportSqlCommandExtractor commandExtractor) {
        String[] commands = commandExtractor.extractCommands(this.reader());
        if (commands == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(commands);
    }

    @Override
    public void release() {
    }
}

