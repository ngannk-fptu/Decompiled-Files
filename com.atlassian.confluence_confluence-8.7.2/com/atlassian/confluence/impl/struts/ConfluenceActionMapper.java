/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts2.dispatcher.mapper.DefaultActionMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.struts;

import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceActionMapper
extends DefaultActionMapper {
    private static final Logger LOG = LoggerFactory.getLogger(ConfluenceActionMapper.class);

    protected String cleanupActionName(String rawActionName) {
        if (!this.allowedActionNames.matcher(rawActionName).matches()) {
            LOG.debug("{} did not match allowed action names {} - default action {} will be used!", new Object[]{rawActionName, this.allowedActionNames, this.defaultActionName});
            return this.defaultActionName;
        }
        return rawActionName;
    }

    protected String cleanupNamespaceName(String rawNamespace) {
        if (!this.allowedNamespaceNames.matcher(rawNamespace).matches()) {
            LOG.debug("{} did not match allowed namespace names {} - default namespace {} will be used!", new Object[]{rawNamespace, this.allowedNamespaceNames, this.defaultNamespaceName});
            return this.defaultNamespaceName;
        }
        return rawNamespace;
    }
}

