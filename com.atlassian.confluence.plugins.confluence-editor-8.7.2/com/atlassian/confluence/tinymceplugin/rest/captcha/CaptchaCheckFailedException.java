/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.tinymceplugin.rest.captcha;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class CaptchaCheckFailedException
extends WebApplicationException {
    public CaptchaCheckFailedException() {
        super(Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)"Captcha check failed").build());
    }
}

