/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

import java.util.List;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.context.InternalContextBase;
import org.apache.velocity.context.InternalEventContext;
import org.apache.velocity.context.InternalHousekeepingContext;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.util.introspection.IntrospectionCacheData;

public final class InternalContextAdapterImpl
implements InternalContextAdapter {
    Context context = null;
    InternalHousekeepingContext icb = null;
    InternalEventContext iec = null;

    public InternalContextAdapterImpl(Context c) {
        this.context = c;
        this.icb = !(c instanceof InternalHousekeepingContext) ? new InternalContextBase() : (InternalHousekeepingContext)((Object)this.context);
        if (c instanceof InternalEventContext) {
            this.iec = (InternalEventContext)((Object)this.context);
        }
    }

    @Override
    public void pushCurrentTemplateName(String s) {
        this.icb.pushCurrentTemplateName(s);
    }

    @Override
    public void popCurrentTemplateName() {
        this.icb.popCurrentTemplateName();
    }

    @Override
    public String getCurrentTemplateName() {
        return this.icb.getCurrentTemplateName();
    }

    @Override
    public Object[] getTemplateNameStack() {
        return this.icb.getTemplateNameStack();
    }

    @Override
    public void pushCurrentMacroName(String s) {
        this.icb.pushCurrentMacroName(s);
    }

    @Override
    public void popCurrentMacroName() {
        this.icb.popCurrentMacroName();
    }

    @Override
    public String getCurrentMacroName() {
        return this.icb.getCurrentMacroName();
    }

    @Override
    public int getCurrentMacroCallDepth() {
        return this.icb.getCurrentMacroCallDepth();
    }

    @Override
    public Object[] getMacroNameStack() {
        return this.icb.getMacroNameStack();
    }

    @Override
    public IntrospectionCacheData icacheGet(Object key) {
        return this.icb.icacheGet(key);
    }

    @Override
    public void icachePut(Object key, IntrospectionCacheData o) {
        this.icb.icachePut(key, o);
    }

    @Override
    public void setCurrentResource(Resource r) {
        this.icb.setCurrentResource(r);
    }

    @Override
    public Resource getCurrentResource() {
        return this.icb.getCurrentResource();
    }

    @Override
    public boolean getAllowRendering() {
        return this.icb.getAllowRendering();
    }

    @Override
    public void setAllowRendering(boolean v) {
        this.icb.setAllowRendering(v);
    }

    @Override
    public void setMacroLibraries(List macroLibraries) {
        this.icb.setMacroLibraries(macroLibraries);
    }

    @Override
    public List getMacroLibraries() {
        return this.icb.getMacroLibraries();
    }

    @Override
    public Object put(String key, Object value) {
        return this.context.put(key, value);
    }

    @Override
    public Object localPut(String key, Object value) {
        return this.put(key, value);
    }

    @Override
    public Object get(String key) {
        return this.context.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.context.containsKey(key);
    }

    @Override
    public Object[] getKeys() {
        return this.context.getKeys();
    }

    @Override
    public Object remove(Object key) {
        return this.context.remove(key);
    }

    @Override
    public Context getInternalUserContext() {
        return this.context;
    }

    @Override
    public InternalContextAdapter getBaseContext() {
        return this;
    }

    @Override
    public EventCartridge attachEventCartridge(EventCartridge ec) {
        if (this.iec != null) {
            return this.iec.attachEventCartridge(ec);
        }
        return null;
    }

    @Override
    public EventCartridge getEventCartridge() {
        if (this.iec != null) {
            return this.iec.getEventCartridge();
        }
        return null;
    }
}

