/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.sling.scripting.jsp;

import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletContext;
import org.apache.sling.scripting.jsp.jasper.IOProvider;
import org.apache.sling.scripting.jsp.jasper.Options;
import org.apache.sling.scripting.jsp.jasper.compiler.JspConfig;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.compiler.TagPluginManager;
import org.apache.sling.scripting.jsp.jasper.compiler.TldLocationsCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JspServletOptions
implements Options {
    private static final Logger log = LoggerFactory.getLogger(JspServletOptions.class);
    public static final String AUTOMATIC_VERSION = "auto";
    private final Map<String, String> settings = new TreeMap<String, String>();
    public boolean fork = true;
    private boolean keepGenerated = true;
    private boolean trimSpaces = false;
    private boolean isPoolingEnabled = true;
    private boolean mappedFile = true;
    private boolean sendErrorToClient = false;
    private boolean classDebugInfo = true;
    private boolean isSmapSuppressed = false;
    private boolean isSmapDumped = false;
    private boolean genStringAsCharArray = false;
    private boolean errorOnUseBeanInvalidClassAttribute = true;
    private boolean defaultIsSession = false;
    private String ieClassId = "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";
    private String compilerTargetVM;
    private String compilerSourceVM;
    private TldLocationsCache tldLocationsCache;
    private JspConfig jspConfig;
    private TagPluginManager tagPluginManager;
    private String javaEncoding = "UTF8";
    private boolean xpoweredBy;
    private boolean displaySourceFragments = false;

    private String getProperty(String name) {
        return this.settings.get(name);
    }

    private void setProperty(String name, String value) {
        this.settings.put(name, value);
    }

    public Map<String, String> getProperties() {
        return this.settings;
    }

    @Override
    public boolean getKeepGenerated() {
        return this.keepGenerated;
    }

    @Override
    public boolean getTrimSpaces() {
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
    public boolean getSendErrorToClient() {
        return this.sendErrorToClient;
    }

    @Override
    public boolean getClassDebugInfo() {
        return this.classDebugInfo;
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
    public String getIeClassId() {
        return this.ieClassId;
    }

    @Override
    public boolean isXpoweredBy() {
        return this.xpoweredBy;
    }

    @Override
    public String getCompiler() {
        return null;
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
    public boolean getErrorOnUseBeanInvalidClassAttribute() {
        return this.errorOnUseBeanInvalidClassAttribute;
    }

    public void setErrorOnUseBeanInvalidClassAttribute(boolean b) {
        this.errorOnUseBeanInvalidClassAttribute = b;
    }

    @Override
    public TldLocationsCache getTldLocationsCache() {
        return this.tldLocationsCache;
    }

    public void setTldLocationsCache(TldLocationsCache tldC) {
        this.tldLocationsCache = tldC;
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
    public boolean getDisplaySourceFragment() {
        return this.displaySourceFragments;
    }

    @Override
    public String getCompilerClassName() {
        return null;
    }

    @Override
    public boolean isDefaultSession() {
        return this.defaultIsSession;
    }

    public JspServletOptions(ServletContext servletContext, IOProvider ioProvider, Map<String, Object> config, TldLocationsCache tldLocationsCache, boolean defaultIsSession) {
        String xpoweredBy;
        String fork;
        String targetVM;
        String ieClassId;
        String errBeanClass;
        String genCharArray;
        String dumpSmap;
        String suppressSmap;
        String debugInfo;
        String senderr;
        String mapFile;
        String dpsFrags;
        String trimsp;
        this.defaultIsSession = defaultIsSession;
        this.compilerTargetVM = this.compilerSourceVM = System.getProperty("java.specification.version");
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String strValue;
            Object value;
            String key = entry.getKey();
            if (!key.startsWith("jasper.") || (value = entry.getValue()) == null || (strValue = String.valueOf(value).trim()).length() <= 0) continue;
            this.setProperty(key.substring("jasper.".length()), strValue);
        }
        String keepgen = this.getProperty("keepgenerated");
        if (keepgen != null) {
            if (keepgen.equalsIgnoreCase("true")) {
                this.keepGenerated = true;
            } else if (keepgen.equalsIgnoreCase("false")) {
                this.keepGenerated = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.keepgen"));
            }
        }
        if ((trimsp = this.getProperty("trimSpaces")) != null) {
            if (trimsp.equalsIgnoreCase("true")) {
                this.trimSpaces = true;
            } else if (trimsp.equalsIgnoreCase("false")) {
                this.trimSpaces = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.trimspaces"));
            }
        }
        if ((dpsFrags = this.getProperty("displaySourceFragments")) != null) {
            if (dpsFrags.equalsIgnoreCase("true")) {
                this.displaySourceFragments = true;
            } else if (dpsFrags.equalsIgnoreCase("false")) {
                this.displaySourceFragments = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.displaySourceFragment"));
            }
        }
        this.isPoolingEnabled = true;
        String poolingEnabledParam = this.getProperty("enablePooling");
        if (poolingEnabledParam != null && !poolingEnabledParam.equalsIgnoreCase("true")) {
            if (poolingEnabledParam.equalsIgnoreCase("false")) {
                this.isPoolingEnabled = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.enablePooling"));
            }
        }
        if ((mapFile = this.getProperty("mappedfile")) != null) {
            if (mapFile.equalsIgnoreCase("true")) {
                this.mappedFile = true;
            } else if (mapFile.equalsIgnoreCase("false")) {
                this.mappedFile = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.mappedFile"));
            }
        }
        if ((senderr = this.getProperty("sendErrToClient")) != null) {
            if (senderr.equalsIgnoreCase("true")) {
                this.sendErrorToClient = true;
            } else if (senderr.equalsIgnoreCase("false")) {
                this.sendErrorToClient = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.sendErrToClient"));
            }
        }
        if ((debugInfo = this.getProperty("classdebuginfo")) != null) {
            if (debugInfo.equalsIgnoreCase("true")) {
                this.classDebugInfo = true;
            } else if (debugInfo.equalsIgnoreCase("false")) {
                this.classDebugInfo = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.classDebugInfo"));
            }
        }
        if ((suppressSmap = this.getProperty("suppressSmap")) != null) {
            if (suppressSmap.equalsIgnoreCase("true")) {
                this.isSmapSuppressed = true;
            } else if (suppressSmap.equalsIgnoreCase("false")) {
                this.isSmapSuppressed = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.suppressSmap"));
            }
        }
        if ((dumpSmap = this.getProperty("dumpSmap")) != null) {
            if (dumpSmap.equalsIgnoreCase("true")) {
                this.isSmapDumped = true;
            } else if (dumpSmap.equalsIgnoreCase("false")) {
                this.isSmapDumped = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.dumpSmap"));
            }
        }
        if ((genCharArray = this.getProperty("genStrAsCharArray")) != null) {
            if (genCharArray.equalsIgnoreCase("true")) {
                this.genStringAsCharArray = true;
            } else if (genCharArray.equalsIgnoreCase("false")) {
                this.genStringAsCharArray = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.genchararray"));
            }
        }
        if ((errBeanClass = this.getProperty("errorOnUseBeanInvalidClassAttribute")) != null) {
            if (errBeanClass.equalsIgnoreCase("true")) {
                this.errorOnUseBeanInvalidClassAttribute = true;
            } else if (errBeanClass.equalsIgnoreCase("false")) {
                this.errorOnUseBeanInvalidClassAttribute = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.errBean"));
            }
        }
        if ((ieClassId = this.getProperty("ieClassId")) != null) {
            this.ieClassId = ieClassId;
        }
        if ((targetVM = this.getProperty("compilerTargetVM")) != null && !AUTOMATIC_VERSION.equalsIgnoreCase(targetVM)) {
            this.compilerTargetVM = targetVM;
        }
        this.setProperty("compilerTargetVM", this.compilerTargetVM);
        String sourceVM = this.getProperty("compilerSourceVM");
        if (sourceVM != null && !AUTOMATIC_VERSION.equalsIgnoreCase(sourceVM)) {
            this.compilerSourceVM = sourceVM;
        }
        this.setProperty("compilerSourceVM", this.compilerSourceVM);
        String javaEncoding = this.getProperty("javaEncoding");
        if (javaEncoding != null) {
            this.javaEncoding = javaEncoding;
        }
        if ((fork = this.getProperty("fork")) != null) {
            if (fork.equalsIgnoreCase("true")) {
                this.fork = true;
            } else if (fork.equalsIgnoreCase("false")) {
                this.fork = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.fork"));
            }
        }
        if ((xpoweredBy = this.getProperty("xpoweredBy")) != null) {
            if (xpoweredBy.equalsIgnoreCase("true")) {
                this.xpoweredBy = true;
            } else if (xpoweredBy.equalsIgnoreCase("false")) {
                this.xpoweredBy = false;
            } else if (log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jsp.warning.xpoweredBy"));
            }
        }
        this.tldLocationsCache = tldLocationsCache;
        this.jspConfig = new JspConfig(servletContext);
        this.tagPluginManager = new TagPluginManager(servletContext);
    }

    @Override
    public String getScratchDir() {
        return ":";
    }
}

