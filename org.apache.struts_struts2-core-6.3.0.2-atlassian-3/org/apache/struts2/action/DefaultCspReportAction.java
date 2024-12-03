/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.action.CspReportAction;

public class DefaultCspReportAction
extends CspReportAction {
    protected static final Logger LOG = LogManager.getLogger(DefaultCspReportAction.class);

    @Override
    void processReport(String jsonCspReport) {
        LOG.error(jsonCspReport);
    }
}

