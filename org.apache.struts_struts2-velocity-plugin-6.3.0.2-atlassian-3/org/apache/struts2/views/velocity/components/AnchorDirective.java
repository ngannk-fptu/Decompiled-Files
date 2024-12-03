/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.util.ValueStack
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.components.Anchor
 *  org.apache.struts2.components.Component
 */
package org.apache.struts2.views.velocity.components;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Anchor;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.velocity.components.AbstractDirective;

public class AnchorDirective
extends AbstractDirective {
    @Override
    public String getBeanName() {
        return "a";
    }

    @Override
    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Anchor(stack, req, res);
    }

    @Override
    public int getType() {
        return 1;
    }
}

