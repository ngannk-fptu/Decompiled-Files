/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import org.hibernate.tool.hbm2ddl.ImportScriptException;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;

public class SingleLineSqlCommandExtractor
implements ImportSqlCommandExtractor {
    @Override
    public String[] extractCommands(Reader reader) {
        BufferedReader bufferedReader = new BufferedReader(reader);
        LinkedList<String> statementList = new LinkedList<String>();
        try {
            String sql = bufferedReader.readLine();
            while (sql != null) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty() && !this.isComment(trimmedSql)) {
                    if (trimmedSql.endsWith(";")) {
                        trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
                    }
                    statementList.add(trimmedSql);
                }
                sql = bufferedReader.readLine();
            }
            return statementList.toArray(new String[statementList.size()]);
        }
        catch (IOException e) {
            throw new ImportScriptException("Error during import script parsing.", e);
        }
    }

    private boolean isComment(String line) {
        return line.startsWith("--") || line.startsWith("//") || line.startsWith("/*");
    }
}

