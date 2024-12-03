/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 */
package org.apache.jasper;

import java.io.File;
import java.util.Map;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import org.apache.jasper.TrimSpacesOption;
import org.apache.jasper.compiler.JspConfig;
import org.apache.jasper.compiler.TagPluginManager;
import org.apache.jasper.compiler.TldCache;

public interface Options {
    public boolean getErrorOnUseBeanInvalidClassAttribute();

    public boolean getKeepGenerated();

    public boolean isPoolingEnabled();

    public boolean getMappedFile();

    public boolean getClassDebugInfo();

    public int getCheckInterval();

    public boolean getDevelopment();

    public boolean getDisplaySourceFragment();

    public boolean isSmapSuppressed();

    public boolean isSmapDumped();

    public TrimSpacesOption getTrimSpaces();

    @Deprecated
    public String getIeClassId();

    public File getScratchDir();

    public String getClassPath();

    public String getCompiler();

    public String getCompilerTargetVM();

    public String getCompilerSourceVM();

    public String getCompilerClassName();

    public TldCache getTldCache();

    public String getJavaEncoding();

    public boolean getFork();

    public JspConfig getJspConfig();

    public boolean isXpoweredBy();

    public TagPluginManager getTagPluginManager();

    public boolean genStringAsCharArray();

    public int getModificationTestInterval();

    public boolean getRecompileOnFail();

    public boolean isCaching();

    public Map<String, TagLibraryInfo> getCache();

    public int getMaxLoadedJsps();

    public int getJspIdleTimeout();

    public boolean getStrictQuoteEscaping();

    public boolean getQuoteAttributeEL();

    default public boolean getGeneratedJavaAddTimestamp() {
        return true;
    }
}

