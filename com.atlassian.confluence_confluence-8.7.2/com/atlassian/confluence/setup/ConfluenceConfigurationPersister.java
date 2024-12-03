/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.xml.DefaultDom4jXmlConfigurationPersister
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.xml.DefaultDom4jXmlConfigurationPersister;

public class ConfluenceConfigurationPersister
extends DefaultDom4jXmlConfigurationPersister {
    public String getRootName() {
        return "confluence-configuration";
    }
}

