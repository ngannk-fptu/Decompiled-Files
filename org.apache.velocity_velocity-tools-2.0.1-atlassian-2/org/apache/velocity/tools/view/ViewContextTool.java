/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package org.apache.velocity.tools.view;

import java.util.Enumeration;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.velocity.tools.generic.ContextTool;
import org.apache.velocity.tools.generic.ValueParser;

public class ViewContextTool
extends ContextTool {
    protected HttpServletRequest request;
    protected HttpSession session;
    protected ServletContext application;

    @Override
    protected void configure(ValueParser parser) {
        super.configure(parser);
        this.request = (HttpServletRequest)parser.getValue("request");
        this.session = this.request.getSession(false);
        this.application = (ServletContext)parser.getValue("servletContext");
    }

    @Override
    protected void fillKeyset(Set keys) {
        super.fillKeyset(keys);
        Enumeration e = this.request.getAttributeNames();
        while (e.hasMoreElements()) {
            keys.add(e.nextElement());
        }
        if (this.session != null) {
            e = this.session.getAttributeNames();
            while (e.hasMoreElements()) {
                keys.add(e.nextElement());
            }
        }
        e = this.application.getAttributeNames();
        while (e.hasMoreElements()) {
            keys.add(e.nextElement());
        }
    }
}

