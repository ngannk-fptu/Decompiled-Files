/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper;

import org.apache.sling.scripting.jsp.jasper.compiler.JspConfig;
import org.apache.sling.scripting.jsp.jasper.compiler.TagPluginManager;
import org.apache.sling.scripting.jsp.jasper.compiler.TldLocationsCache;

public interface Options {
    public boolean getErrorOnUseBeanInvalidClassAttribute();

    public boolean getKeepGenerated();

    public boolean isPoolingEnabled();

    public boolean getMappedFile();

    public boolean getSendErrorToClient();

    public boolean getClassDebugInfo();

    public boolean getDisplaySourceFragment();

    public boolean isSmapSuppressed();

    public boolean isSmapDumped();

    public boolean getTrimSpaces();

    public String getIeClassId();

    public String getScratchDir();

    public String getCompiler();

    public String getCompilerTargetVM();

    public String getCompilerSourceVM();

    public String getCompilerClassName();

    public TldLocationsCache getTldLocationsCache();

    public String getJavaEncoding();

    public boolean getFork();

    public JspConfig getJspConfig();

    public boolean isXpoweredBy();

    public TagPluginManager getTagPluginManager();

    public boolean genStringAsCharArray();

    public boolean isDefaultSession();
}

