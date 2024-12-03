/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 */
package org.apache.velocity.tools.view.servlet;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.tools.view.WebappResourceLoader;

@Deprecated
public class WebappLoader
extends WebappResourceLoader {
    @Override
    public void init(ExtendedProperties configuration) {
        this.log.warn((Object)("WebappLoader is deprecated. Use " + WebappResourceLoader.class.getName() + " instead."));
        super.init(configuration);
    }
}

