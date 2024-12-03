/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.io.Reader;
import java.util.List;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
import org.hibernate.tool.schema.ast.SqlScriptParser;

public class MultipleLinesSqlCommandExtractor
implements ImportSqlCommandExtractor {
    @Override
    public String[] extractCommands(Reader reader) {
        List<String> commands = SqlScriptParser.extractCommands(reader);
        return commands.toArray(new String[0]);
    }
}

