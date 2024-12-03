/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 */
package org.apache.velocity.tools.view.tools;

import javax.servlet.ServletRequest;
import org.apache.velocity.tools.view.ParameterTool;
import org.apache.velocity.tools.view.ViewContext;

@Deprecated
public class ParameterParser
extends ParameterTool {
    public ParameterParser() {
    }

    public ParameterParser(ServletRequest request) {
        this.setRequest(request);
    }

    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            this.setRequest((ServletRequest)((ViewContext)obj).getRequest());
        }
    }
}

