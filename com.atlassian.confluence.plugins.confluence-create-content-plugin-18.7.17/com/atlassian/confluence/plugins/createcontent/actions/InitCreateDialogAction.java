/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.xwork.FlashScope
 *  com.atlassian.core.filters.ServletContextThreadLocal
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.core.filters.ServletContextThreadLocal;
import java.util.HashMap;

public class InitCreateDialogAction
extends ConfluenceActionSupport {
    public String execute() throws Exception {
        FlashScope.put((String)"createDialogInitParams", new HashMap(ServletContextThreadLocal.getRequest().getParameterMap()));
        return super.execute();
    }
}

