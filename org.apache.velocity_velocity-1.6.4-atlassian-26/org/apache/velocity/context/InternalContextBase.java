/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.InternalEventContext;
import org.apache.velocity.context.InternalHousekeepingContext;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.util.introspection.IntrospectionCacheData;

class InternalContextBase
implements InternalHousekeepingContext,
InternalEventContext {
    private static final long serialVersionUID = -245905472770843470L;
    private HashMap introspectionCache = new HashMap(33);
    private Stack templateNameStack = new Stack();
    private Stack macroNameStack = new Stack();
    private EventCartridge eventCartridge = null;
    private Resource currentResource = null;
    private boolean allowRendering = true;
    private List macroLibraries = null;

    InternalContextBase() {
    }

    @Override
    public void pushCurrentTemplateName(String s) {
        this.templateNameStack.push(s);
    }

    @Override
    public void popCurrentTemplateName() {
        this.templateNameStack.pop();
    }

    @Override
    public String getCurrentTemplateName() {
        if (this.templateNameStack.empty()) {
            return "<undef>";
        }
        return (String)this.templateNameStack.peek();
    }

    @Override
    public Object[] getTemplateNameStack() {
        return this.templateNameStack.toArray();
    }

    @Override
    public void pushCurrentMacroName(String s) {
        this.macroNameStack.push(s);
    }

    @Override
    public void popCurrentMacroName() {
        this.macroNameStack.pop();
    }

    @Override
    public String getCurrentMacroName() {
        if (this.macroNameStack.empty()) {
            return "<undef>";
        }
        return (String)this.macroNameStack.peek();
    }

    @Override
    public int getCurrentMacroCallDepth() {
        return this.macroNameStack.size();
    }

    @Override
    public Object[] getMacroNameStack() {
        return this.macroNameStack.toArray();
    }

    @Override
    public IntrospectionCacheData icacheGet(Object key) {
        return (IntrospectionCacheData)this.introspectionCache.get(key);
    }

    @Override
    public void icachePut(Object key, IntrospectionCacheData o) {
        this.introspectionCache.put(key, o);
    }

    @Override
    public void setCurrentResource(Resource r) {
        this.currentResource = r;
    }

    @Override
    public Resource getCurrentResource() {
        return this.currentResource;
    }

    @Override
    public boolean getAllowRendering() {
        return this.allowRendering;
    }

    @Override
    public void setAllowRendering(boolean v) {
        this.allowRendering = v;
    }

    @Override
    public void setMacroLibraries(List macroLibraries) {
        this.macroLibraries = macroLibraries;
    }

    @Override
    public List getMacroLibraries() {
        return this.macroLibraries;
    }

    @Override
    public EventCartridge attachEventCartridge(EventCartridge ec) {
        EventCartridge temp = this.eventCartridge;
        this.eventCartridge = ec;
        return temp;
    }

    @Override
    public EventCartridge getEventCartridge() {
        return this.eventCartridge;
    }
}

