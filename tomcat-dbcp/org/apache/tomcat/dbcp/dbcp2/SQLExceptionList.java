/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.SQLException;
import java.util.List;

public class SQLExceptionList
extends SQLException {
    private static final long serialVersionUID = 1L;
    private final List<? extends Throwable> causeList;

    public SQLExceptionList(List<? extends Throwable> causeList) {
        super(String.format("%,d exceptions: %s", causeList == null ? 0 : causeList.size(), causeList), causeList == null ? null : causeList.get(0));
        this.causeList = causeList;
    }

    public List<? extends Throwable> getCauseList() {
        return this.causeList;
    }
}

