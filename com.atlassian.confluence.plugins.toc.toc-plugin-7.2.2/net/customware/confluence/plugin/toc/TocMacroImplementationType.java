/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.renderer.PageContext
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.customware.confluence.plugin.toc;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.renderer.PageContext;
import javax.annotation.Nonnull;
import net.customware.confluence.plugin.toc.TOCMacro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum TocMacroImplementationType {
    CLIENT,
    SERVER;

    static final String MOBILE_OUTPUT_DEVICE_TYPE = "mobile";
    static final String CONTAINED_RENDER = "containedRender";
    private static final Logger log;

    @Nonnull
    static TocMacroImplementationType selectImplementation(ConversionContext conversionContext) {
        if (!ConversionContextOutputType.DISPLAY.value().equals(conversionContext.getOutputType())) {
            return SERVER;
        }
        if (MOBILE_OUTPUT_DEVICE_TYPE.equals(conversionContext.getOutputDeviceType())) {
            return SERVER;
        }
        return TocMacroImplementationType.useClientMode(conversionContext) ? CLIENT : SERVER;
    }

    private static boolean useClientMode(ConversionContext conversionContext) {
        boolean oldContextCheck;
        boolean isContainedRender = (Boolean)conversionContext.getProperty(CONTAINED_RENDER, (Object)false);
        if (isContainedRender) {
            return false;
        }
        PageContext pageContext = conversionContext.getPageContext();
        boolean bl = oldContextCheck = pageContext.getOriginalContext() == pageContext;
        if (!oldContextCheck) {
            log.debug("Using deprecated pageContext.getOriginalContext() to determine render mode for TOC macro, please use CONTAINED_RENDER property in ConversionContext");
        }
        return oldContextCheck;
    }

    ImplementationTypeSelectionEvent createEvent() {
        return new ImplementationTypeSelectionEvent(this);
    }

    static {
        log = LoggerFactory.getLogger(TOCMacro.class);
    }

    @EventName(value="confluence.toc-macro.implementation")
    public static class ImplementationTypeSelectionEvent {
        private final TocMacroImplementationType implementationType;

        ImplementationTypeSelectionEvent(TocMacroImplementationType implementationType) {
            this.implementationType = implementationType;
        }

        public String getImplementationType() {
            return this.implementationType.toString();
        }
    }
}

