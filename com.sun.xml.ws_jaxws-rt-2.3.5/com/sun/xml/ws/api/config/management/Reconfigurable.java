/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.config.management;

import javax.xml.ws.WebServiceException;

public interface Reconfigurable {
    public void reconfigure() throws WebServiceException;
}

