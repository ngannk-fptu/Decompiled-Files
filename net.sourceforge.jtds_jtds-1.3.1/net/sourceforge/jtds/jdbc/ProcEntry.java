/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import net.sourceforge.jtds.jdbc.ColInfo;
import net.sourceforge.jtds.jdbc.ParamInfo;

public class ProcEntry {
    public static final int PROCEDURE = 1;
    public static final int PREPARE = 2;
    public static final int CURSOR = 3;
    public static final int PREP_FAILED = 4;
    private String name;
    private ColInfo[] colMetaData;
    private ParamInfo[] paramMetaData;
    private int type;
    private int refCount;

    public final String toString() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHandle(int handle) {
        this.name = Integer.toString(handle);
    }

    public ColInfo[] getColMetaData() {
        return this.colMetaData;
    }

    public void setColMetaData(ColInfo[] colMetaData) {
        this.colMetaData = colMetaData;
    }

    public ParamInfo[] getParamMetaData() {
        return this.paramMetaData;
    }

    public void setParamMetaData(ParamInfo[] paramMetaData) {
        this.paramMetaData = paramMetaData;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void appendDropSQL(StringBuilder sql) {
        switch (this.type) {
            case 1: {
                sql.append("DROP PROC ").append(this.name).append('\n');
                break;
            }
            case 2: {
                sql.append("EXEC sp_unprepare ").append(this.name).append('\n');
                break;
            }
            case 3: {
                sql.append("EXEC sp_cursorunprepare ").append(this.name).append('\n');
                break;
            }
            case 4: {
                break;
            }
            default: {
                throw new IllegalStateException("Invalid cached statement type " + this.type);
            }
        }
    }

    public void addRef() {
        ++this.refCount;
    }

    public void release() {
        if (this.refCount > 0) {
            --this.refCount;
        }
    }

    public int getRefCount() {
        return this.refCount;
    }
}

