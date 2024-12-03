/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;

public class ResultSetMappingDefinition
implements Serializable {
    private final String name;
    private final List<NativeSQLQueryReturn> queryReturns = new ArrayList<NativeSQLQueryReturn>();

    public ResultSetMappingDefinition(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addQueryReturn(NativeSQLQueryReturn queryReturn) {
        this.queryReturns.add(queryReturn);
    }

    public NativeSQLQueryReturn[] getQueryReturns() {
        return this.queryReturns.toArray(new NativeSQLQueryReturn[this.queryReturns.size()]);
    }

    public String traceLoggableFormat() {
        final StringBuilder buffer = new StringBuilder().append("ResultSetMappingDefinition[\n").append("    name=").append(this.name).append("\n").append("    returns=[\n");
        for (NativeSQLQueryReturn rtn : this.queryReturns) {
            rtn.traceLog(new NativeSQLQueryReturn.TraceLogger(){

                @Override
                public void writeLine(String traceLine) {
                    buffer.append("        ").append(traceLine).append("\n");
                }
            });
        }
        buffer.append("    ]\n").append("]");
        return buffer.toString();
    }
}

