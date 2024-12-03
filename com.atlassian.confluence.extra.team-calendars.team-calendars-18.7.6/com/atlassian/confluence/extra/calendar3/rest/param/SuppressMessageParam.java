/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import javax.ws.rs.FormParam;

public class SuppressMessageParam {
    @FormParam(value="messageKey")
    String messageKey;

    public String getMessageKey() {
        return this.messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}

