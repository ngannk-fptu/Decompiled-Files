/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.BootstrapException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.confluence.util.HtmlUtil;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;

public class DatabaseTestResult {
    private static final String EMPTY = "";
    private boolean status;
    private String message;
    private String sqlState;
    private int errorCode;
    private String title;

    public DatabaseTestResult(boolean status, String title) {
        this.status = status;
        this.title = title;
        this.sqlState = EMPTY;
        this.errorCode = 0;
        this.message = EMPTY;
    }

    public DatabaseTestResult(boolean status, String title, SQLException exception) {
        this.status = status;
        this.title = title;
        this.sqlState = StringUtils.defaultString((String)exception.getSQLState(), (String)EMPTY);
        this.errorCode = exception.getErrorCode();
        this.message = HtmlUtil.htmlEncode(exception.toString());
    }

    public DatabaseTestResult(boolean status, String title, String detail) {
        this.status = status;
        this.title = title;
        this.sqlState = EMPTY;
        this.errorCode = 0;
        this.message = detail;
    }

    public DatabaseTestResult(boolean status, String title, BootstrapException exception) {
        this.status = status;
        this.title = title;
        this.sqlState = EMPTY;
        this.errorCode = 0;
        this.message = HtmlUtil.htmlEncode(exception.toString());
    }

    public boolean getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public String getSqlState() {
        return this.sqlState;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getTitle() {
        return this.title;
    }
}

