/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.sal.api.websudo.WebSudoRequired;

@ReadOnlyAccessAllowed
@WebSudoRequired
public class ReIndexCalendarContentTypeAction
extends ConfluenceActionSupport {
    public String execute() throws Exception {
        return "success";
    }
}

