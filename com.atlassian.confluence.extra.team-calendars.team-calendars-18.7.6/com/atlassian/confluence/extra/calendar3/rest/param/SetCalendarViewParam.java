/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import javax.ws.rs.FormParam;

public class SetCalendarViewParam {
    @FormParam(value="view")
    String view;

    public String getView() {
        return this.view;
    }

    public void setView(String view) {
        this.view = view;
    }
}

