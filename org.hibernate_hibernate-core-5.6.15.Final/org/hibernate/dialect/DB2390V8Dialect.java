/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.DB2390Dialect;

public class DB2390V8Dialect
extends DB2390Dialect {
    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select nextval for " + sequenceName + " from sysibm.sysdummy1";
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName + " as integer start with 1 increment by 1 minvalue 1 nomaxvalue nocycle nocache";
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence " + sequenceName + " restrict";
    }

    @Override
    public String getQuerySequencesString() {
        return "select * from sysibm.syssequences";
    }
}

