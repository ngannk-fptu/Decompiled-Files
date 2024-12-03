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
    private List macroLibraries = null;

    InternalContextBase() {
    }

    public void pushCurrentTemplateName(String s) {
        this.templateNameStack.push(s);
    }

    public void popCurrentTemplateName() {
        this.templateNameStack.pop();
    }

    public String getCurrentTemplateName() {
        if (this.templateNameStack.empty()) {
            return "<undef>";
        }
        return (String)this.templateNameStack.peek();
    }

    public Object[] getTemplateNameStack() {
        return this.templateNameStack.toArray();
    }

    public void pushCurrentMacroName(String s) {
        this.macroNameStack.push(s);
    }

    public void popCurrentMacroName() {
        this.macroNameStack.pop();
    }

    public String getCurrentMacroName() {
        if (this.macroNameStack.empty()) {
            return "<undef>";
        }
        return (String)this.macroNameStack.peek();
    }

    public int getCurrentMacroCallDepth() {
        return this.macroNameStack.size();
    }

    public Object[] getMacroNameStack() {
        return this.macroNameStack.toArray();
    }

    public IntrospectionCacheData icacheGet(Object key) {
        return (IntrospectionCacheData)this.introspectionCache.get(key);
    }

    public void icachePut(Object key, IntrospectionCacheData o) {
        this.introspectionCache.put(key, o);
    }

    public void setCurrentResource(Resource r) {
        this.currentResource = r;
    }

    public Resource getCurrentResource() {
        return this.currentResource;
    }

    public void setMacroLibraries(List macroLibraries) {
        this.macroLibraries = macroLibraries;
    }

    public List getMacroLibraries() {
        return this.macroLibraries;
    }

    public EventCartridge attachEventCartridge(EventCartridge ec) {
        EventCartridge temp = this.eventCartridge;
        this.eventCartridge = ec;
        return temp;
    }

    public EventCartridge getEventCartridge() {
        return this.eventCartridge;
    }
}

