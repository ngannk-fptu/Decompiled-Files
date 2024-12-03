/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0.cfg;

import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.cfg.C3P0ConfigFinder;
import com.mchange.v2.c3p0.cfg.C3P0ConfigUtils;
import com.mchange.v2.c3p0.cfg.C3P0ConfigXmlUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class DefaultC3P0ConfigFinder
implements C3P0ConfigFinder {
    static final String XML_CFG_FILE_KEY = "com.mchange.v2.c3p0.cfg.xml";
    static final String XML_CFG_EXPAND_ENTITY_REFS_KEY = "com.mchange.v2.c3p0.cfg.xml.expandEntityReferences";
    static final String XML_CFG_USE_PERMISSIVE_PARSER_KEY = "com.mchange.v2.c3p0.cfg.xml.usePermissiveParser";
    static final String CLASSLOADER_RESOURCE_PREFIX = "classloader:";
    static final MLogger logger = MLog.getLogger(DefaultC3P0ConfigFinder.class);
    final boolean warn_of_xml_overrides;

    public DefaultC3P0ConfigFinder(boolean warn_of_xml_overrides) {
        this.warn_of_xml_overrides = warn_of_xml_overrides;
    }

    public DefaultC3P0ConfigFinder() {
        this(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public C3P0Config findConfig() throws Exception {
        C3P0Config out;
        HashMap flatDefaults = C3P0ConfigUtils.extractHardcodedC3P0Defaults();
        flatDefaults.putAll(C3P0ConfigUtils.extractC3P0PropertiesResources());
        String cfgFile = C3P0Config.getPropsFileConfigProperty(XML_CFG_FILE_KEY);
        boolean usePermissiveParser = this.findUsePermissiveParser();
        if (cfgFile == null) {
            C3P0Config xmlConfig = C3P0ConfigXmlUtils.extractXmlConfigFromDefaultResource(usePermissiveParser);
            if (xmlConfig != null) {
                this.insertDefaultsUnderNascentConfig(flatDefaults, xmlConfig);
                out = xmlConfig;
                this.mbOverrideWarning("resource", "/c3p0-config.xml");
            } else {
                out = C3P0ConfigUtils.configFromFlatDefaults(flatDefaults);
            }
        } else {
            cfgFile = cfgFile.trim();
            InputStream is = null;
            try {
                if (cfgFile.startsWith(CLASSLOADER_RESOURCE_PREFIX)) {
                    ClassLoader cl = this.getClass().getClassLoader();
                    String rsrcPath = cfgFile.substring(CLASSLOADER_RESOURCE_PREFIX.length());
                    if (rsrcPath.startsWith("/")) {
                        rsrcPath = rsrcPath.substring(1);
                    }
                    if ((is = cl.getResourceAsStream(rsrcPath)) == null) {
                        throw new FileNotFoundException("Specified ClassLoader resource '" + rsrcPath + "' could not be found. [ Found in configuration: " + XML_CFG_FILE_KEY + '=' + cfgFile + " ]");
                    }
                    this.mbOverrideWarning("resource", rsrcPath);
                } else {
                    is = new BufferedInputStream(new FileInputStream(cfgFile));
                    this.mbOverrideWarning("file", cfgFile);
                }
                C3P0Config xmlConfig = C3P0ConfigXmlUtils.extractXmlConfigFromInputStream(is, usePermissiveParser);
                this.insertDefaultsUnderNascentConfig(flatDefaults, xmlConfig);
                out = xmlConfig;
            }
            finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Properties sysPropConfig = C3P0ConfigUtils.findAllC3P0SystemProperties();
        out.defaultConfig.props.putAll(sysPropConfig);
        return out;
    }

    private void insertDefaultsUnderNascentConfig(HashMap flatDefaults, C3P0Config config) {
        flatDefaults.putAll(config.defaultConfig.props);
        config.defaultConfig.props = flatDefaults;
    }

    private void mbOverrideWarning(String srcType, String srcName) {
        if (this.warn_of_xml_overrides && logger.isLoggable(MLevel.WARNING)) {
            logger.log(MLevel.WARNING, "Configuation defined in " + srcType + "'" + srcName + "' overrides all other c3p0 config.");
        }
    }

    private static boolean affirmativelyTrue(String propStr) {
        return propStr != null && propStr.trim().equalsIgnoreCase("true");
    }

    private boolean findUsePermissiveParser() {
        boolean out;
        boolean deprecatedExpandEntityRefs = DefaultC3P0ConfigFinder.affirmativelyTrue(C3P0Config.getPropsFileConfigProperty(XML_CFG_EXPAND_ENTITY_REFS_KEY));
        boolean usePermissiveParser = DefaultC3P0ConfigFinder.affirmativelyTrue(C3P0Config.getPropsFileConfigProperty(XML_CFG_USE_PERMISSIVE_PARSER_KEY));
        boolean bl = out = usePermissiveParser || deprecatedExpandEntityRefs;
        if (out && logger.isLoggable(MLevel.WARNING)) {
            String warningKey;
            if (deprecatedExpandEntityRefs) {
                logger.log(MLevel.WARNING, "You have set the configuration property 'com.mchange.v2.c3p0.cfg.xml.expandEntityReferences', which has been deprecated, to true. Please use 'com.mchange.v2.c3p0.cfg.xml.usePermissiveParser' instead. Please be aware that permissive parsing enables inline document type definitions, XML inclusions, and other fetures!");
                warningKey = usePermissiveParser ? "Configuration property 'com.mchange.v2.c3p0.cfg.xml.usePermissiveParser'" : "Configuration property 'com.mchange.v2.c3p0.cfg.xml.expandEntityReferences' (deprecated)";
            } else {
                warningKey = "Configuration property 'com.mchange.v2.c3p0.cfg.xml.usePermissiveParser'";
            }
            logger.log(MLevel.WARNING, warningKey + " is set to 'true'. Entity references will be resolved in XML c3p0 configuration files, doctypes and xml includes will be permitted, the file will in general be parsed very permissively. This may be a security hazard. Be sure you understand your XML config files, including the full transitive closure of entity references and incusions. See CVE-2018-20433, https://nvd.nist.gov/vuln/detail/CVE-2018-20433 / See also https://github.com/OWASP/CheatSheetSeries/blob/31c94f233c40af4237432008106f42a9c4bff05e/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.md / See also https://vsecurity.com//download/papers/XMLDTDEntityAttacks.pdf");
        }
        return out;
    }
}

