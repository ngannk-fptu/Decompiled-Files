/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.addressing.model;

import com.sun.xml.ws.resources.AddressingMessages;
import javax.xml.ws.WebServiceException;

public class ActionNotSupportedException
extends WebServiceException {
    private String action;

    public ActionNotSupportedException(String action) {
        super(AddressingMessages.ACTION_NOT_SUPPORTED_EXCEPTION(action));
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
}

