/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.logging.Logger
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.config.metro.util;

import com.sun.istack.logging.Logger;
import javax.xml.ws.WebServiceException;

public class ParserUtil {
    private static final Logger LOGGER = Logger.getLogger(ParserUtil.class);

    private ParserUtil() {
    }

    public static boolean parseBooleanValue(String value) throws WebServiceException {
        if ("true".equals(value) || "1".equals(value)) {
            return true;
        }
        if ("false".equals(value) || "0".equals(value)) {
            return false;
        }
        throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException("invalid boolean value"));
    }
}

