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

    @Override
    public Context getInternalUserContext() {
        return this.innerContext.getInternalUserContext();
    }

    @Override
    public InternalContextAdapter getBaseContext() {
        return this.innerContext.getBaseContext();
    }

    @Override
    public Object get(String key) {
        return this.innerContext.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return this.innerContext.put(key, value);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.innerContext.containsKey(key);
    }

    @Override
    public Object[] getKeys() {
        return this.innerContext.getKeys();
    }

    @Override
    public Object remove(Object key) {
        return this.innerContext.remove(key);
    }

    @Override
    public void pushCurrentTemplateName(String s) {
        this.innerContext.pushCurrentTemplateName(s);
    }

    @Override
    public void popCurrentTemplateName() {
        this.innerContext.popCurrentTemplateName();
    }

    @Override
    public String getCurrentTemplateName() {
        return this.innerContext.getCurrentTemplateName();
    }

    @Override
    public Object[] getTemplateNameStack() {
        return this.innerContext.getTemplateNameStack();
    }

    @Override
    public void pushCurrentMacroName(String s) {
        this.innerContext.pushCurrentMacroName(s);
    }

    @Override
    public void popCurrentMacroName() {
        this.innerContext.popCurrentMacroName();
    }

    @Override
    public String getCurrentMacroName() {
        return this.innerContext.getCurrentMacroName();
    }

    @Override
    public int getCurrentMacroCallDepth() {
        return this.innerContext.getCurrentMacroCallDepth();
    }

    @Override
    public Object[] getMacroNameStack() {
        return this.innerContext.getMacroNameStack();
    }

    @Override
    public IntrospectionCacheData icacheGet(Object key) {
        return this.innerContext.icacheGet(key);
    }

    @Override
    public Object localPut(String key, Object value) {
        return this.innerContext.put(key, value);
    }

    @Override
    public void icachePut(Object key, IntrospectionCacheData o) {
        this.innerContext.icachePut(key, o);
    }

    @Override
    public boolean getAllowRendering() {
        return this.innerContext.getAllowRendering();
    }

    @Override
    public void setAllowRendering(boolean v) {
        this.innerContext.setAllowRendering(v);
    }

    @Override
    public void setMacroLibraries(List macroLibraries) {
        this.innerContext.setMacroLibraries(macroLibraries);
    }

    @Override
    public List getMacroLibraries() {
        return this.innerContext.getMacroLibraries();
    }

    @Override
    public EventCartridge attachEventCartridge(EventCartridge ec) {
        return this.innerContext.attachEventCartridge(ec);
    }

    @Override
    public EventCartridge getEventCartridge() {
        return this.innerContext.getEventCartridge();
    }

    @Override
    public void setCurrentResource(Resource r) {
        this.innerContext.setCurrentResource(r);
    }

    @Override
    public Resource getCurrentResource() {
        return this.innerContext.getCurrentResource();
    }
}

