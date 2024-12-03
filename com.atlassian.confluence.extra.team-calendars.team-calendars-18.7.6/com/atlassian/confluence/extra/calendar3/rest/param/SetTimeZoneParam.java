/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import javax.ws.rs.FormParam;

public class SetTimeZoneParam {
    @FormParam(value="timeZoneId")
    String confluenceTimeZoneId;

    public String getConfluenceTimeZoneId() {
        return this.confluenceTimeZoneId;
    }

    public void setConfluenceTimeZoneId(String confluenceTimeZoneId) {
        this.confluenceTimeZoneId = confluenceTimeZoneId;
    }
}

