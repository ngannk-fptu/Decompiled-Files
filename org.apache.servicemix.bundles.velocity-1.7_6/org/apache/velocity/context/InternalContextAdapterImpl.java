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

    public void pushCurrentTemplateName(String s) {
        this.icb.pushCurrentTemplateName(s);
    }

    public void popCurrentTemplateName() {
        this.icb.popCurrentTemplateName();
    }

    public String getCurrentTemplateName() {
        return this.icb.getCurrentTemplateName();
    }

    public Object[] getTemplateNameStack() {
        return this.icb.getTemplateNameStack();
    }

    public void pushCurrentMacroName(String s) {
        this.icb.pushCurrentMacroName(s);
    }

    public void popCurrentMacroName() {
        this.icb.popCurrentMacroName();
    }

    public String getCurrentMacroName() {
        return this.icb.getCurrentMacroName();
    }

    public int getCurrentMacroCallDepth() {
        return this.icb.getCurrentMacroCallDepth();
    }

    public Object[] getMacroNameStack() {
        return this.icb.getMacroNameStack();
    }

    public IntrospectionCacheData icacheGet(Object key) {
        return this.icb.icacheGet(key);
    }

    public void icachePut(Object key, IntrospectionCacheData o) {
        this.icb.icachePut(key, o);
    }

    public void setCurrentResource(Resource r) {
        this.icb.setCurrentResource(r);
    }

    public Resource getCurrentResource() {
        return this.icb.getCurrentResource();
    }

    public void setMacroLibraries(List macroLibraries) {
        this.icb.setMacroLibraries(macroLibraries);
    }

    public List getMacroLibraries() {
        return this.icb.getMacroLibraries();
    }

    public Object put(String key, Object value) {
        return this.context.put(key, value);
    }

    public Object localPut(String key, Object value) {
        return this.put(key, value);
    }

    public Object get(String key) {
        return this.context.get(key);
    }

    public boolean containsKey(Object key) {
        return this.context.containsKey(key);
    }

    public Object[] getKeys() {
        return this.context.getKeys();
    }

    public Object remove(Object key) {
        return this.context.remove(key);
    }

    public Context getInternalUserContext() {
        return this.context;
    }

    public InternalContextAdapter getBaseContext() {
        return this;
    }

    public EventCartridge attachEventCartridge(EventCartridge ec) {
        if (this.iec != null) {
            return this.iec.attachEventCartridge(ec);
        }
        return null;
    }

    public EventCartridge getEventCartridge() {
        if (this.iec != null) {
            return this.iec.getEventCartridge();
        }
        return null;
    }
}

