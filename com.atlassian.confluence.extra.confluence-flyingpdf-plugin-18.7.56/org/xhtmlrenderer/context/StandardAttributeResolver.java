/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.context;

import org.w3c.dom.Element;
import org.xhtmlrenderer.css.extend.AttributeResolver;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.extend.UserInterface;

public class StandardAttributeResolver
implements AttributeResolver {
    private NamespaceHandler nsh;
    private UserAgentCallback uac;
    private UserInterface ui;

    public StandardAttributeResolver(NamespaceHandler nsh, UserAgentCallback uac, UserInterface ui) {
        this.nsh = nsh;
        this.uac = uac;
        this.ui = ui;
    }

    @Override
    public String getAttributeValue(Object e, String attrName) {
        return this.nsh.getAttributeValue((Element)e, attrName);
    }

    @Override
    public String getAttributeValue(Object e, String namespaceURI, String attrName) {
        return this.nsh.getAttributeValue((Element)e, namespaceURI, attrName);
    }

    @Override
    public String getClass(Object e) {
        return this.nsh.getClass((Element)e);
    }

    @Override
    public String getID(Object e) {
        return this.nsh.getID((Element)e);
    }

    @Override
    public String getNonCssStyling(Object e) {
        return this.nsh.getNonCssStyling((Element)e);
    }

    @Override
    public String getElementStyling(Object e) {
        return this.nsh.getElementStyling((Element)e);
    }

    @Override
    public String getLang(Object e) {
        return this.nsh.getLang((Element)e);
    }

    @Override
    public boolean isLink(Object e) {
        return this.nsh.getLinkUri((Element)e) != null;
    }

    @Override
    public boolean isVisited(Object e) {
        return this.isLink(e) && this.uac.isVisited(this.nsh.getLinkUri((Element)e));
    }

    @Override
    public boolean isHover(Object e) {
        return this.ui.isHover((Element)e);
    }

    @Override
    public boolean isActive(Object e) {
        return this.ui.isActive((Element)e);
    }

    @Override
    public boolean isFocus(Object e) {
        return this.ui.isFocus((Element)e);
    }
}

