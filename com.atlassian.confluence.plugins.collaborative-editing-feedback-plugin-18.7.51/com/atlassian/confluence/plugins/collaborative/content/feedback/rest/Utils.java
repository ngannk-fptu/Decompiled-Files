/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  javax.ws.rs.core.Response
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.rest;

import com.atlassian.confluence.plugins.collaborative.content.feedback.exception.DataFetchException;
import com.atlassian.sal.api.permission.AuthorisationException;
import java.util.concurrent.Callable;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Utils() {
    }

    public static Response executeAndRespond(Callable<Object> action) {
        try {
            return Response.ok().entity((Object)objectMapper.writeValueAsString(action.call())).build();
        }
        catch (AuthorisationException e) {
            logger.warn("Not enough permissions to complete this request");
            return Response.status((int)403).build();
        }
        catch (DataFetchException e) {
            logger.error("Error handling request", (Throwable)e);
            return Response.status((int)500).entity((Object)("{ \"message\": \"" + e.getMessage() + "\", \"code\": " + e.getError() + "}")).build();
        }
        catch (Exception e) {
            logger.error("Error handling request", (Throwable)e);
            return Response.status((int)500).entity((Object)("{ \"message\": \"" + e.getMessage() + "\"}")).build();
        }
    }

    public static Response executeAndWrapExceptions(Callable<Response> action) {
        try {
            return action.call();
        }
        catch (Exception e) {
            logger.error("Error handling request", (Throwable)e);
            return Response.status((int)500).entity((Object)("{ \"message\": \"" + e.getMessage() + "\"}")).build();
        }
    }
}

