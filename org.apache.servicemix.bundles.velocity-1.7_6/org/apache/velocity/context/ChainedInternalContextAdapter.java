/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

import java.util.List;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.util.introspection.IntrospectionCacheData;

public abstract class ChainedInternalContextAdapter
implements InternalContextAdapter {
    protected InternalContextAdapter innerContext = null;

    public ChainedInternalContextAdapter(InternalContextAdapter inner) {
        this.innerContext = inner;
    }

    public Context getInternalUserContext() {
        return this.innerContext.getInternalUserContext();
    }

    public InternalContextAdapter getBaseContext() {
        return this.innerContext.getBaseContext();
    }

    public Object get(String key) {
        return this.innerContext.get(key);
    }

    public Object put(String key, Object value) {
        return this.innerContext.put(key, value);
    }

    public boolean containsKey(Object key) {
        return this.innerContext.containsKey(key);
    }

    public Object[] getKeys() {
        return this.innerContext.getKeys();
    }

    public Object remove(Object key) {
        return this.innerContext.remove(key);
    }

    public void pushCurrentTemplateName(String s) {
        this.innerContext.pushCurrentTemplateName(s);
    }

    public void popCurrentTemplateName() {
        this.innerContext.popCurrentTemplateName();
    }

    public String getCurrentTemplateName() {
        return this.innerContext.getCurrentTemplateName();
    }

    public Object[] getTemplateNameStack() {
        return this.innerContext.getTemplateNameStack();
    }

    public void pushCurrentMacroName(String s) {
        this.innerContext.pushCurrentMacroName(s);
    }

    public void popCurrentMacroName() {
        this.innerContext.popCurrentMacroName();
    }

    public String getCurrentMacroName() {
        return this.innerContext.getCurrentMacroName();
    }

    public int getCurrentMacroCallDepth() {
        return this.innerContext.getCurrentMacroCallDepth();
    }

    public Object[] getMacroNameStack() {
        return this.innerContext.getMacroNameStack();
    }

    public IntrospectionCacheData icacheGet(Object key) {
        return this.innerContext.icacheGet(key);
    }

    public Object localPut(String key, Object value) {
        return this.innerContext.put(key, value);
    }

    public void icachePut(Object key, IntrospectionCacheData o) {
        this.innerContext.icachePut(key, o);
    }

    public void setMacroLibraries(List macroLibraries) {
        this.innerContext.setMacroLibraries(macroLibraries);
    }

    public List getMacroLibraries() {
        return this.innerContext.getMacroLibraries();
    }

    public EventCartridge attachEventCartridge(EventCartridge ec) {
        return this.innerContext.attachEventCartridge(ec);
    }

    public EventCartridge getEventCartridge() {
        return this.innerContext.getEventCartridge();
    }

    public void setCurrentResource(Resource r) {
        this.innerContext.setCurrentResource(r);
    }

    public Resource getCurrentResource() {
        return this.innerContext.getCurrentResource();
    }
}

