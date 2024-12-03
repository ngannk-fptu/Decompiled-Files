/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 */
package org.springframework.jdbc.support.xml;

import org.springframework.dao.InvalidDataAccessApiUsageException;

public class SqlXmlFeatureNotImplementedException
extends InvalidDataAccessApiUsageException {
    public SqlXmlFeatureNotImplementedException(String msg) {
        super(msg);
    }

    public SqlXmlFeatureNotImplementedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

