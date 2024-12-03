/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.bridge.SLF4JBridgeHandler
 */
package com.atlassian.plugins.rest.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public final class Slf4jBridge {
    private static final Logger log = LoggerFactory.getLogger(Slf4jBridge.class);

    public static Helper createHelper() {
        try {
            Class.forName("org.slf4j.bridge.SLF4JBridgeHandler");
            return new BridgePresentHelper();
        }
        catch (ClassNotFoundException e) {
            return new BridgeMissingHelper();
        }
    }

    private static class BridgePresentHelper
    implements Helper {
        private BridgePresentHelper() {
        }

        @Override
        public void install() {
            log.debug("Installing SLF4JBridgeHandler for {}.", (Object)Thread.currentThread().getContextClassLoader());
            SLF4JBridgeHandler.install();
        }

        @Override
        public void uninstall() {
            SLF4JBridgeHandler.uninstall();
            log.debug("Uninstalled SLF4JBridgeHandler for {}.", (Object)Thread.currentThread().getContextClassLoader());
        }
    }

    private static class BridgeMissingHelper
    implements Helper {
        private BridgeMissingHelper() {
        }

        @Override
        public void install() {
            log.debug("Skipping installation of SLF4JBridgeHandler for {}. Have you provided jcl-over-slf4j.jar?", (Object)Thread.currentThread().getContextClassLoader());
        }

        @Override
        public void uninstall() {
        }
    }

    public static interface Helper {
        public void install();

        public void uninstall();
    }
}

