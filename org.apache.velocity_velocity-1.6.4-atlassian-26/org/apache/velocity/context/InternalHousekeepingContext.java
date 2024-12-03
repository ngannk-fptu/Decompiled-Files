/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

import java.util.List;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.util.introspection.IntrospectionCacheData;

interface InternalHousekeepingContext {
    public void pushCurrentTemplateName(String var1);

    public void popCurrentTemplateName();

    public String getCurrentTemplateName();

    public Object[] getTemplateNameStack();

    public void pushCurrentMacroName(String var1);

    public void popCurrentMacroName();

    public String getCurrentMacroName();

    public int getCurrentMacroCallDepth();

    public Object[] getMacroNameStack();

    public IntrospectionCacheData icacheGet(Object var1);

    public void icachePut(Object var1, IntrospectionCacheData var2);

    public Resource getCurrentResource();

    public void setCurrentResource(Resource var1);

    public boolean getAllowRendering();

    public void setAllowRendering(boolean var1);

    public void setMacroLibraries(List var1);

    public List getMacroLibraries();
}

