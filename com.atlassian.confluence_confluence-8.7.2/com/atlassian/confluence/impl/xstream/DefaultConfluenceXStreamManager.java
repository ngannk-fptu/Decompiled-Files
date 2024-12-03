/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.impl.xstream;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.xstream.security.XStreamSecurityConfigurator;
import com.atlassian.confluence.setup.xstream.XStreamManager;
import java.util.Map;

@Internal
public class DefaultConfluenceXStreamManager
extends XStreamManager {
    public static final String XSTREAM_11_STORAGE_PROPERTY = "xstream.11.storage";
    public static final String XSTREAM_ALLOW_LIST_PROPERTY = "xstream.allowlist.enable";
    public static final String XSTREAM_WHITE_LIST_DEPRECATED_PROPERTY = "xstream.whitelist.enable";

    public DefaultConfluenceXStreamManager(Map<String, String> aliases, ClassLoader classLoader, XStreamSecurityConfigurator securityConfigurator) {
        super(aliases, classLoader, securityConfigurator);
    }
}

