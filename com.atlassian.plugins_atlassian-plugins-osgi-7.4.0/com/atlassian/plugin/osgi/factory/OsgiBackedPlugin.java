/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  org.osgi.framework.Bundle
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.Plugin;
import org.osgi.framework.Bundle;

public interface OsgiBackedPlugin
extends Plugin {
    public Bundle getBundle();
}

