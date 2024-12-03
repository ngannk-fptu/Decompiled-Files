/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.addressing;

import com.sun.xml.ws.api.model.CheckedException;
import com.sun.xml.ws.api.model.JavaMethod;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class WsaActionUtil {
    private static final Logger LOGGER = Logger.getLogger(WsaActionUtil.class.getName());

    public static final String getDefaultFaultAction(JavaMethod method, CheckedException ce) {
        String delim;
        String tns = method.getOwner().getTargetNamespace();
        if (tns.endsWith(delim = WsaActionUtil.getDelimiter(tns))) {
            tns = tns.substring(0, tns.length() - 1);
        }
        return tns + delim + method.getOwner().getPortTypeName().getLocalPart() + delim + method.getOperationName() + delim + "Fault" + delim + ce.getExceptionClass().getSimpleName();
    }

    private static String getDelimiter(String tns) {
        String delim = "/";
        try {
            URI uri = new URI(tns);
            if (uri.getScheme() != null && uri.getScheme().equalsIgnoreCase("urn")) {
                delim = ":";
            }
        }
        catch (URISyntaxException e) {
            LOGGER.warning("TargetNamespace of WebService is not a valid URI");
        }
        return delim;
    }
}

