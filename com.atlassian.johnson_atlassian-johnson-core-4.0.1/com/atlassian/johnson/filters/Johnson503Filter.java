/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.johnson.filters;

import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.filters.AbstractJohnsonFilter;
import java.io.IOException;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Johnson503Filter
extends AbstractJohnsonFilter {
    private static final Logger log = LoggerFactory.getLogger(Johnson503Filter.class);

    @Override
    protected void handleError(JohnsonEventContainer appEventContainer, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        log.info("The application is unavailable, or there are errors.  Returning a temporarily unavailable status.");
        servletResponse.setStatus(503);
        if (Johnson503Filter.hasOnlyWarnings(appEventContainer)) {
            servletResponse.setHeader("Retry-After", "30");
        }
        servletResponse.getWriter().flush();
    }

    @Override
    protected void handleNotSetup(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        log.info("The application is not setup.  Returning a temporarily unavailable status.");
        servletResponse.setStatus(503);
        servletResponse.getWriter().flush();
    }

    private static boolean hasOnlyWarnings(JohnsonEventContainer eventContainer) {
        return eventContainer != null && eventContainer.stream().map(Event::getLevel).map(EventLevel::getLevel).allMatch(Predicate.isEqual("warning"));
    }
}

