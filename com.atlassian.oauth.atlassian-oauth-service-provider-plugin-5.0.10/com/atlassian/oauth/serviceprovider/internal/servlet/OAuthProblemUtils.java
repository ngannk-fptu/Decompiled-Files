/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  net.oauth.OAuthMessage
 *  net.oauth.OAuthProblemException
 *  org.slf4j.Logger
 */
package com.atlassian.oauth.serviceprovider.internal.servlet;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import org.slf4j.Logger;

public class OAuthProblemUtils {
    public static void logOAuthProblem(OAuthMessage message, OAuthProblemException ope, Logger logger) {
        if ("timestamp_refused".equals(ope.getProblem())) {
            logger.warn("Rejecting OAuth request for url \"{}\" due to invalid timestamp ({}). This is most likely due to our system clock not being synchronized with the consumer's clock.", new Object[]{message.URL, ope.getParameters()});
        } else if (logger.isDebugEnabled()) {
            logger.warn("Problem encountered authenticating OAuth client request for url \"" + message.URL + "\", error was \"" + ope.getProblem() + "\", with parameters \"" + ope.getParameters() + "\"", (Throwable)ope);
        } else {
            logger.warn("Problem encountered authenticating OAuth client for url \"{}\", error was \"{}\", with parameters \"{}\"", new Object[]{message.URL, ope.getProblem(), ope.getParameters()});
        }
    }

    public static void logOAuthRequest(HttpServletRequest request, String message, Logger logger) {
        if (logger.isDebugEnabled()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(message);
            buffer.append(" Headers: [");
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = (String)headerNames.nextElement();
                buffer.append(headerName);
                buffer.append(" = ");
                buffer.append(request.getHeader(headerName));
                buffer.append(", ");
            }
            buffer.append("]");
            logger.debug(buffer.toString());
        }
    }
}

