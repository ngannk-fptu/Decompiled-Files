/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.support.http;

import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.support.http.Request;
import com.atlassian.plugin.webresource.impl.support.http.Response;

public class BaseController {
    protected final Globals globals;
    protected final Request request;
    protected final Response response;

    public BaseController(Globals globals, Request request, Response response) {
        this.globals = globals;
        this.request = request;
        this.response = response;
    }
}

