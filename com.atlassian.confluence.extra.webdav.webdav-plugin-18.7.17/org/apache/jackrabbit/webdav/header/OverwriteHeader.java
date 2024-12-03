/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.header;

import javax.servlet.http.HttpServletRequest;
import org.apache.jackrabbit.webdav.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverwriteHeader
implements Header {
    private static Logger log = LoggerFactory.getLogger(OverwriteHeader.class);
    public static final String OVERWRITE_TRUE = "T";
    public static final String OVERWRITE_FALSE = "F";
    private final boolean doOverwrite;

    public OverwriteHeader(boolean doOverwrite) {
        this.doOverwrite = doOverwrite;
    }

    public OverwriteHeader(HttpServletRequest request) {
        String overwriteHeader = request.getHeader("Overwrite");
        this.doOverwrite = overwriteHeader != null ? overwriteHeader.equalsIgnoreCase(OVERWRITE_TRUE) : true;
    }

    @Override
    public String getHeaderName() {
        return "Overwrite";
    }

    @Override
    public String getHeaderValue() {
        return this.doOverwrite ? OVERWRITE_TRUE : OVERWRITE_FALSE;
    }

    public boolean isOverwrite() {
        return this.doOverwrite;
    }
}

