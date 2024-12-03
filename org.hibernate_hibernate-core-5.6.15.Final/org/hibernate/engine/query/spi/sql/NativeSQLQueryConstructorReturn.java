/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi.sql;

import java.util.List;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;

public class NativeSQLQueryConstructorReturn
implements NativeSQLQueryReturn {
    private final Class targetClass;
    private final NativeSQLQueryScalarReturn[] columnReturns;

    public NativeSQLQueryConstructorReturn(Class targetClass, List<NativeSQLQueryScalarReturn> columnReturns) {
        this.targetClass = targetClass;
        this.columnReturns = columnReturns.toArray(new NativeSQLQueryScalarReturn[columnReturns.size()]);
    }

    public Class getTargetClass() {
        return this.targetClass;
    }

    public NativeSQLQueryScalarReturn[] getColumnReturns() {
        return this.columnReturns;
    }

    @Override
    public void traceLog(final NativeSQLQueryReturn.TraceLogger logger2) {
        logger2.writeLine("Constructor[");
        logger2.writeLine("    targetClass=" + this.targetClass + ",");
        logger2.writeLine("    columns=[");
        NativeSQLQueryReturn.TraceLogger nestedLogger = new NativeSQLQueryReturn.TraceLogger(){

            @Override
            public void writeLine(String traceLine) {
                logger2.writeLine("    " + traceLine);
            }
        };
        for (NativeSQLQueryScalarReturn columnReturn : this.columnReturns) {
            columnReturn.traceLog(nestedLogger);
        }
        logger2.writeLine("    ]");
        logger2.writeLine("]");
    }
}

