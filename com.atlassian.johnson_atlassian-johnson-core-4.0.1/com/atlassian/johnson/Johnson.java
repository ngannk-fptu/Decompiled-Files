/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.ServletContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.johnson;

import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.config.ConfigurationJohnsonException;
import com.atlassian.johnson.config.DefaultJohnsonConfig;
import com.atlassian.johnson.config.JohnsonConfig;
import com.atlassian.johnson.config.XmlJohnsonConfig;
import com.atlassian.johnson.event.ApplicationEventCheck;
import com.atlassian.johnson.setup.ContainerFactory;
import com.atlassian.johnson.util.StringUtils;
import com.google.common.base.Preconditions;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Johnson {
    public static final String ATTR_CONFIG = Johnson.class.getName() + ":Config";
    public static final String ATTR_EVENT_CONTAINER = Johnson.class.getName() + ":EventContainer";
    public static final String PARAM_CONFIG_LOCATION = "johnsonConfigLocation";
    private static final Logger LOG = LoggerFactory.getLogger(Johnson.class);
    private static JohnsonConfig config;
    private static JohnsonEventContainer eventContainer;

    private Johnson() {
        throw new UnsupportedOperationException(this.getClass().getName() + " should not be instantiated");
    }

    @Nonnull
    public static JohnsonConfig getConfig() {
        Preconditions.checkState((boolean)Johnson.isInitialized(), (Object)"Johnson.getConfig() was called before initialisation");
        return config;
    }

    @Nonnull
    public static JohnsonConfig getConfig(@Nonnull ServletContext context) {
        Object attribute = ((ServletContext)Preconditions.checkNotNull((Object)context, (Object)"context")).getAttribute(ATTR_CONFIG);
        if (attribute != null) {
            return (JohnsonConfig)attribute;
        }
        return Johnson.getConfig();
    }

    @Nonnull
    public static JohnsonEventContainer getEventContainer() {
        Preconditions.checkState((boolean)Johnson.isInitialized(), (Object)"Johnson.getEventContainer() was called before initialisation");
        return eventContainer;
    }

    @Nonnull
    public static JohnsonEventContainer getEventContainer(@Nonnull ServletContext context) {
        Object attribute = ((ServletContext)Preconditions.checkNotNull((Object)context, (Object)"context")).getAttribute(ATTR_EVENT_CONTAINER);
        if (attribute != null) {
            return (JohnsonEventContainer)attribute;
        }
        return Johnson.getEventContainer();
    }

    public static void initialize(@Nonnull ServletContext context) {
        String location = StringUtils.defaultIfEmpty(((ServletContext)Preconditions.checkNotNull((Object)context, (Object)"context")).getInitParameter(PARAM_CONFIG_LOCATION), "johnson-config.xml");
        Johnson.initialize(location);
        context.setAttribute(ATTR_CONFIG, (Object)config);
        context.setAttribute(ATTR_EVENT_CONTAINER, (Object)eventContainer);
        List<ApplicationEventCheck> checks = config.getApplicationEventChecks();
        for (ApplicationEventCheck check : checks) {
            check.check(eventContainer, context);
        }
    }

    public static void initialize(@Nullable String location) {
        location = StringUtils.defaultIfEmpty(location, "johnson-config.xml");
        LOG.debug("Initialising Johnson with configuration from [{}]", (Object)location);
        try {
            config = XmlJohnsonConfig.fromFile(location);
        }
        catch (ConfigurationJohnsonException e) {
            LOG.warn("Failed to load configuration from [" + location + "]", (Throwable)e);
            config = DefaultJohnsonConfig.getInstance();
        }
        ContainerFactory containerFactory = config.getContainerFactory();
        eventContainer = containerFactory.create();
    }

    public static boolean isInitialized() {
        return config != null && eventContainer != null;
    }

    public static void terminate() {
        config = null;
        eventContainer = null;
    }

    public static void terminate(@Nonnull ServletContext context) {
        Preconditions.checkNotNull((Object)context, (Object)"context");
        Johnson.terminate();
        context.removeAttribute(ATTR_CONFIG);
        context.removeAttribute(ATTR_EVENT_CONTAINER);
    }
}

