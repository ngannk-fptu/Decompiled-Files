/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.views.TagLibraryModelProvider;
import org.apache.struts2.views.freemarker.tags.StrutsModels;

public class DefaultTagLibrary
implements TagLibraryModelProvider {
    @Override
    public Object getModels(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new StrutsModels(stack, req, res);
    }

    public Object getFreemarkerModels(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return this.getModels(stack, req, res);
    }
}

