/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package com.atlassian.plugin.spring.scanner.runtime;

import com.atlassian.plugin.spring.scanner.runtime.impl.AtlassianScannerBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class AtlassianScannerNamespaceHandler
extends NamespaceHandlerSupport {
    public void init() {
        this.registerBeanDefinitionParser("scan-indexes", new AtlassianScannerBeanDefinitionParser());
    }
}

