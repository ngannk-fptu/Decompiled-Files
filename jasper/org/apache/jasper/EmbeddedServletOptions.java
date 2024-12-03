/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.jasper;

import java.io.File;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import org.apache.jasper.Constants;
import org.apache.jasper.Options;
import org.apache.jasper.TrimSpacesOption;
import org.apache.jasper.compiler.JspConfig;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.TagPluginManager;
import org.apache.jasper.compiler.TldCache;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class EmbeddedServletOptions
implements Options {
    private final Log log;
    private Properties settings;
    private boolean development;
    public boolean fork;
    private boolean keepGenerated;
    private TrimSpacesOption trimSpaces;
    private boolean isPoolingEnabled;
    private boolean mappedFile;
    private boolean classDebugInfo;
    private int checkInterval;
    private boolean isSmapSuppressed;
    private boolean isSmapDumped;
    private boolean genStringAsCharArray;
    private boolean errorOnUseBeanInvalidClassAttribute;
    private File scratchDir;
    private String ieClassId;
    private String classpath;
    private String compiler;
    private String compilerTargetVM;
    private String compilerSourceVM;
    private String compilerClassName;
    private TldCache tldCache;
    private JspConfig jspConfig;
    private TagPluginManager tagPluginManager;
    private String javaEncoding;
    private int modificationTestInterval;
    private boolean recompileOnFail;
    private boolean xpoweredBy;
    private boolean displaySourceFragment;
    private int maxLoadedJsps;
    private int jspIdleTimeout;
    private boolean strictQuoteEscaping;
    private boolean quoteAttributeEL;

    public String getProperty(String name) {
        return this.settings.getProperty(name);
    }

    public void setProperty(String name, String value) {
        if (name != null && value != null) {
            this.settings.setProperty(name, value);
        }
    }

    public void setQuoteAttributeEL(boolean b) {
        this.quoteAttributeEL = b;
    }

    @Override
    public boolean getQuoteAttributeEL() {
        return this.quoteAttributeEL;
    }

    @Override
    public boolean getKeepGenerated() {
        return this.keepGenerated;
    }

    @Override
    public TrimSpacesOption getTrimSpaces() {
        return this.trimSpaces;
    }

    @Override
    public boolean isPoolingEnabled() {
        return this.isPoolingEnabled;
    }

    @Override
    public boolean getMappedFile() {
        return this.mappedFile;
    }

    @Override
    public boolean getClassDebugInfo() {
        return this.classDebugInfo;
    }

    @Override
    public int getCheckInterval() {
        return this.checkInterval;
    }

    @Override
    public int getModificationTestInterval() {
        return this.modificationTestInterval;
    }

    @Override
    public boolean getRecompileOnFail() {
        return this.recompileOnFail;
    }

    @Override
    public boolean getDevelopment() {
        return this.development;
    }

    @Override
    public boolean isSmapSuppressed() {
        return this.isSmapSuppressed;
    }

    @Override
    public boolean isSmapDumped() {
        return this.isSmapDumped;
    }

    @Override
    public boolean genStringAsCharArray() {
        return this.genStringAsCharArray;
    }

    @Override
    @Deprecated
    public String getIeClassId() {
        return this.ieClassId;
    }

    @Override
    public File getScratchDir() {
        return this.scratchDir;
    }

    @Override
    public String getClassPath() {
        return this.classpath;
    }

    @Override
    public boolean isXpoweredBy() {
        return this.xpoweredBy;
    }

    @Override
    public String getCompiler() {
        return this.compiler;
    }

    @Override
    public String getCompilerTargetVM() {
        return this.compilerTargetVM;
    }

    @Override
    public String getCompilerSourceVM() {
        return this.compilerSourceVM;
    }

    @Override
    public String getCompilerClassName() {
        return this.compilerClassName;
    }

    @Override
    public boolean getErrorOnUseBeanInvalidClassAttribute() {
        return this.errorOnUseBeanInvalidClassAttribute;
    }

    public void setErrorOnUseBeanInvalidClassAttribute(boolean b) {
        this.errorOnUseBeanInvalidClassAttribute = b;
    }

    @Override
    public TldCache getTldCache() {
        return this.tldCache;
    }

    public void setTldCache(TldCache tldCache) {
        this.tldCache = tldCache;
    }

    @Override
    public String getJavaEncoding() {
        return this.javaEncoding;
    }

    @Override
    public boolean getFork() {
        return this.fork;
    }

    @Override
    public JspConfig getJspConfig() {
        return this.jspConfig;
    }

    @Override
    public TagPluginManager getTagPluginManager() {
        return this.tagPluginManager;
    }

    @Override
    public boolean isCaching() {
        return false;
    }

    @Override
    public Map<String, TagLibraryInfo> getCache() {
        return null;
    }

    @Override
    public boolean getDisplaySourceFragment() {
        return this.displaySourceFragment;
    }

    @Override
    public int getMaxLoadedJsps() {
        return this.maxLoadedJsps;
    }

    @Override
    public int getJspIdleTimeout() {
        return this.jspIdleTimeout;
    }

    @Override
    public boolean getStrictQuoteEscaping() {
        return this.strictQuoteEscaping;
    }

    public EmbeddedServletOptions(ServletConfig config, ServletContext context) {
        String quoteAttributeEL;
        String strictQuoteEscaping;
        block117: {
            String jspIdleTimeout;
            block116: {
                String maxLoadedJsps;
                String displaySourceFragment;
                String xpoweredBy;
                String fork;
                String compilerClassName;
                String javaEncoding;
                String compilerSourceVM;
                String dir;
                String classpath;
                String ieClassId;
                String errBeanClass;
                String genCharArray;
                String dumpSmap;
                String suppressSmap;
                String development;
                String recompileOnFail;
                block115: {
                    String modificationTestInterval;
                    block114: {
                        String checkInterval;
                        String debugInfo;
                        String mapFile;
                        block113: {
                            String trimsp;
                            this.log = LogFactory.getLog(EmbeddedServletOptions.class);
                            this.settings = new Properties();
                            this.development = true;
                            this.fork = true;
                            this.keepGenerated = true;
                            this.trimSpaces = TrimSpacesOption.FALSE;
                            this.isPoolingEnabled = true;
                            this.mappedFile = true;
                            this.classDebugInfo = true;
                            this.checkInterval = 0;
                            this.isSmapSuppressed = false;
                            this.isSmapDumped = false;
                            this.genStringAsCharArray = false;
                            this.errorOnUseBeanInvalidClassAttribute = true;
                            this.ieClassId = "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";
                            this.classpath = null;
                            this.compiler = null;
                            this.compilerTargetVM = "1.8";
                            this.compilerSourceVM = "1.8";
                            this.compilerClassName = null;
                            this.tldCache = null;
                            this.jspConfig = null;
                            this.tagPluginManager = null;
                            this.javaEncoding = "UTF-8";
                            this.modificationTestInterval = 4;
                            this.recompileOnFail = false;
                            this.displaySourceFragment = true;
                            this.maxLoadedJsps = -1;
                            this.jspIdleTimeout = -1;
                            this.strictQuoteEscaping = true;
                            this.quoteAttributeEL = true;
                            Enumeration enumeration = config.getInitParameterNames();
                            while (enumeration.hasMoreElements()) {
                                String k = (String)enumeration.nextElement();
                                String v = config.getInitParameter(k);
                                this.setProperty(k, v);
                            }
                            String keepgen = config.getInitParameter("keepgenerated");
                            if (keepgen != null) {
                                if (keepgen.equalsIgnoreCase("true")) {
                                    this.keepGenerated = true;
                                } else if (keepgen.equalsIgnoreCase("false")) {
                                    this.keepGenerated = false;
                                } else if (this.log.isWarnEnabled()) {
                                    this.log.warn((Object)Localizer.getMessage("jsp.warning.keepgen"));
                                }
                            }
                            if ((trimsp = config.getInitParameter("trimSpaces")) != null) {
                                try {
                                    this.trimSpaces = TrimSpacesOption.valueOf(trimsp.toUpperCase());
                                }
                                catch (IllegalArgumentException iae) {
                                    if (!this.log.isWarnEnabled()) break block113;
                                    this.log.warn((Object)Localizer.getMessage("jsp.warning.trimspaces"), (Throwable)iae);
                                }
                            }
                        }
                        this.isPoolingEnabled = true;
                        String poolingEnabledParam = config.getInitParameter("enablePooling");
                        if (poolingEnabledParam != null && !poolingEnabledParam.equalsIgnoreCase("true")) {
                            if (poolingEnabledParam.equalsIgnoreCase("false")) {
                                this.isPoolingEnabled = false;
                            } else if (this.log.isWarnEnabled()) {
                                this.log.warn((Object)Localizer.getMessage("jsp.warning.enablePooling"));
                            }
                        }
                        if ((mapFile = config.getInitParameter("mappedfile")) != null) {
                            if (mapFile.equalsIgnoreCase("true")) {
                                this.mappedFile = true;
                            } else if (mapFile.equalsIgnoreCase("false")) {
                                this.mappedFile = false;
                            } else if (this.log.isWarnEnabled()) {
                                this.log.warn((Object)Localizer.getMessage("jsp.warning.mappedFile"));
                            }
                        }
                        if ((debugInfo = config.getInitParameter("classdebuginfo")) != null) {
                            if (debugInfo.equalsIgnoreCase("true")) {
                                this.classDebugInfo = true;
                            } else if (debugInfo.equalsIgnoreCase("false")) {
                                this.classDebugInfo = false;
                            } else if (this.log.isWarnEnabled()) {
                                this.log.warn((Object)Localizer.getMessage("jsp.warning.classDebugInfo"));
                            }
                        }
                        if ((checkInterval = config.getInitParameter("checkInterval")) != null) {
                            try {
                                this.checkInterval = Integer.parseInt(checkInterval);
                            }
                            catch (NumberFormatException ex) {
                                if (!this.log.isWarnEnabled()) break block114;
                                this.log.warn((Object)Localizer.getMessage("jsp.warning.checkInterval"));
                            }
                        }
                    }
                    if ((modificationTestInterval = config.getInitParameter("modificationTestInterval")) != null) {
                        try {
                            this.modificationTestInterval = Integer.parseInt(modificationTestInterval);
                        }
                        catch (NumberFormatException ex) {
                            if (!this.log.isWarnEnabled()) break block115;
                            this.log.warn((Object)Localizer.getMessage("jsp.warning.modificationTestInterval"));
                        }
                    }
                }
                if ((recompileOnFail = config.getInitParameter("recompileOnFail")) != null) {
                    if (recompileOnFail.equalsIgnoreCase("true")) {
                        this.recompileOnFail = true;
                    } else if (recompileOnFail.equalsIgnoreCase("false")) {
                        this.recompileOnFail = false;
                    } else if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.recompileOnFail"));
                    }
                }
                if ((development = config.getInitParameter("development")) != null) {
                    if (development.equalsIgnoreCase("true")) {
                        this.development = true;
                    } else if (development.equalsIgnoreCase("false")) {
                        this.development = false;
                    } else if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.development"));
                    }
                }
                if ((suppressSmap = config.getInitParameter("suppressSmap")) != null) {
                    if (suppressSmap.equalsIgnoreCase("true")) {
                        this.isSmapSuppressed = true;
                    } else if (suppressSmap.equalsIgnoreCase("false")) {
                        this.isSmapSuppressed = false;
                    } else if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.suppressSmap"));
                    }
                }
                if ((dumpSmap = config.getInitParameter("dumpSmap")) != null) {
                    if (dumpSmap.equalsIgnoreCase("true")) {
                        this.isSmapDumped = true;
                    } else if (dumpSmap.equalsIgnoreCase("false")) {
                        this.isSmapDumped = false;
                    } else if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.dumpSmap"));
                    }
                }
                if ((genCharArray = config.getInitParameter("genStringAsCharArray")) != null) {
                    if (genCharArray.equalsIgnoreCase("true")) {
                        this.genStringAsCharArray = true;
                    } else if (genCharArray.equalsIgnoreCase("false")) {
                        this.genStringAsCharArray = false;
                    } else if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.genchararray"));
                    }
                }
                if ((errBeanClass = config.getInitParameter("errorOnUseBeanInvalidClassAttribute")) != null) {
                    if (errBeanClass.equalsIgnoreCase("true")) {
                        this.errorOnUseBeanInvalidClassAttribute = true;
                    } else if (errBeanClass.equalsIgnoreCase("false")) {
                        this.errorOnUseBeanInvalidClassAttribute = false;
                    } else if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.errBean"));
                    }
                }
                if ((ieClassId = config.getInitParameter("ieClassId")) != null) {
                    this.ieClassId = ieClassId;
                }
                if ((classpath = config.getInitParameter("classpath")) != null) {
                    this.classpath = classpath;
                }
                if ((dir = config.getInitParameter("scratchdir")) != null && Constants.IS_SECURITY_ENABLED) {
                    this.log.info((Object)Localizer.getMessage("jsp.info.ignoreSetting", "scratchdir", dir));
                    dir = null;
                }
                this.scratchDir = dir != null ? new File(dir) : (File)context.getAttribute("javax.servlet.context.tempdir");
                if (this.scratchDir == null) {
                    this.log.fatal((Object)Localizer.getMessage("jsp.error.no.scratch.dir"));
                    return;
                }
                if (!(this.scratchDir.exists() && this.scratchDir.canRead() && this.scratchDir.canWrite() && this.scratchDir.isDirectory())) {
                    this.log.fatal((Object)Localizer.getMessage("jsp.error.bad.scratch.dir", this.scratchDir.getAbsolutePath()));
                }
                this.compiler = config.getInitParameter("compiler");
                String compilerTargetVM = config.getInitParameter("compilerTargetVM");
                if (compilerTargetVM != null) {
                    this.compilerTargetVM = compilerTargetVM;
                }
                if ((compilerSourceVM = config.getInitParameter("compilerSourceVM")) != null) {
                    this.compilerSourceVM = compilerSourceVM;
                }
                if ((javaEncoding = config.getInitParameter("javaEncoding")) != null) {
                    this.javaEncoding = javaEncoding;
                }
                if ((compilerClassName = config.getInitParameter("compilerClassName")) != null) {
                    this.compilerClassName = compilerClassName;
                }
                if ((fork = config.getInitParameter("fork")) != null) {
                    if (fork.equalsIgnoreCase("true")) {
                        this.fork = true;
                    } else if (fork.equalsIgnoreCase("false")) {
                        this.fork = false;
                    } else if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.fork"));
                    }
                }
                if ((xpoweredBy = config.getInitParameter("xpoweredBy")) != null) {
                    if (xpoweredBy.equalsIgnoreCase("true")) {
                        this.xpoweredBy = true;
                    } else if (xpoweredBy.equalsIgnoreCase("false")) {
                        this.xpoweredBy = false;
                    } else if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.xpoweredBy"));
                    }
                }
                if ((displaySourceFragment = config.getInitParameter("displaySourceFragment")) != null) {
                    if (displaySourceFragment.equalsIgnoreCase("true")) {
                        this.displaySourceFragment = true;
                    } else if (displaySourceFragment.equalsIgnoreCase("false")) {
                        this.displaySourceFragment = false;
                    } else if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.displaySourceFragment"));
                    }
                }
                if ((maxLoadedJsps = config.getInitParameter("maxLoadedJsps")) != null) {
                    try {
                        this.maxLoadedJsps = Integer.parseInt(maxLoadedJsps);
                    }
                    catch (NumberFormatException ex) {
                        if (!this.log.isWarnEnabled()) break block116;
                        this.log.warn((Object)Localizer.getMessage("jsp.warning.maxLoadedJsps", "" + this.maxLoadedJsps));
                    }
                }
            }
            if ((jspIdleTimeout = config.getInitParameter("jspIdleTimeout")) != null) {
                try {
                    this.jspIdleTimeout = Integer.parseInt(jspIdleTimeout);
                }
                catch (NumberFormatException ex) {
                    if (!this.log.isWarnEnabled()) break block117;
                    this.log.warn((Object)Localizer.getMessage("jsp.warning.jspIdleTimeout", "" + this.jspIdleTimeout));
                }
            }
        }
        if ((strictQuoteEscaping = config.getInitParameter("strictQuoteEscaping")) != null) {
            if (strictQuoteEscaping.equalsIgnoreCase("true")) {
                this.strictQuoteEscaping = true;
            } else if (strictQuoteEscaping.equalsIgnoreCase("false")) {
                this.strictQuoteEscaping = false;
            } else if (this.log.isWarnEnabled()) {
                this.log.warn((Object)Localizer.getMessage("jsp.warning.strictQuoteEscaping"));
            }
        }
        if ((quoteAttributeEL = config.getInitParameter("quoteAttributeEL")) != null) {
            if (quoteAttributeEL.equalsIgnoreCase("true")) {
                this.quoteAttributeEL = true;
            } else if (quoteAttributeEL.equalsIgnoreCase("false")) {
                this.quoteAttributeEL = false;
            } else if (this.log.isWarnEnabled()) {
                this.log.warn((Object)Localizer.getMessage("jsp.warning.quoteAttributeEL"));
            }
        }
        this.tldCache = TldCache.getInstance(context);
        this.jspConfig = new JspConfig(context);
        this.tagPluginManager = new TagPluginManager(context);
    }
}

