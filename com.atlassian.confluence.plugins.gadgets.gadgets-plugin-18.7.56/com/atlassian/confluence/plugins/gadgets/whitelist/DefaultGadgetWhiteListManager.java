/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetSpecUriNotAllowedException
 *  com.atlassian.gadgets.GadgetSpecUrlChecker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gadgets.whitelist;

import com.atlassian.confluence.plugins.gadgets.whitelist.GadgetWhiteListManager;
import com.atlassian.gadgets.GadgetSpecUriNotAllowedException;
import com.atlassian.gadgets.GadgetSpecUrlChecker;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultGadgetWhiteListManager
implements GadgetWhiteListManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultGadgetWhiteListManager.class);
    private final GadgetSpecUrlChecker gadgetSpecUrlChecker;

    public DefaultGadgetWhiteListManager(GadgetSpecUrlChecker gadgetSpecUrlChecker) {
        this.gadgetSpecUrlChecker = gadgetSpecUrlChecker;
    }

    @Override
    public boolean isAllowedGadgetUri(URI uri) {
        try {
            this.gadgetSpecUrlChecker.assertRenderable(uri.toString());
            return true;
        }
        catch (GadgetSpecUriNotAllowedException e) {
            log.warn("GadgetSpecUriNotAllowed: {}", (Object)e.getMessage());
            return false;
        }
    }
}

