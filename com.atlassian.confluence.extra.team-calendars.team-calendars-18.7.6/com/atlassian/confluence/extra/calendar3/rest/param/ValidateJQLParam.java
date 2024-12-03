/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import javax.ws.rs.FormParam;

public class ValidateJQLParam {
    @FormParam(value="jql")
    String jql;

    public String getJql() {
        return this.jql;
    }

    public void setJql(String jql) {
        this.jql = jql;
    }
}

