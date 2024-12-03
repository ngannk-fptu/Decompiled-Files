/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.digester.Digester
 *  org.apache.commons.digester.RuleSet
 */
package org.apache.velocity.tools.config;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;
import org.apache.velocity.tools.config.FileFactoryConfiguration;
import org.apache.velocity.tools.config.OldXmlFactoryConfigurationRuleSet;
import org.apache.velocity.tools.config.XmlFactoryConfigurationRuleSet;
import org.xml.sax.SAXException;

public class XmlFactoryConfiguration
extends FileFactoryConfiguration {
    private RuleSet ruleSet;
    private boolean supportOldXml;

    public XmlFactoryConfiguration() {
        this(false, "");
    }

    public XmlFactoryConfiguration(boolean supportOldConfig) {
        this(supportOldConfig, String.valueOf(supportOldConfig));
    }

    public XmlFactoryConfiguration(String id) {
        this(false, id);
    }

    public XmlFactoryConfiguration(boolean supportOldConfig, String id) {
        super(XmlFactoryConfiguration.class, id);
        this.setRuleSet((RuleSet)new XmlFactoryConfigurationRuleSet());
        this.supportOldXml = supportOldConfig;
    }

    public void setRuleSet(RuleSet rules) {
        this.ruleSet = rules;
    }

    public RuleSet getRuleSet() {
        return this.ruleSet;
    }

    @Override
    public void read(InputStream input) throws IOException {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setUseContextClassLoader(true);
        digester.push((Object)this);
        digester.addRuleSet(this.getRuleSet());
        if (this.supportOldXml) {
            digester.addRuleSet((RuleSet)new OldXmlFactoryConfigurationRuleSet());
        }
        try {
            digester.parse(input);
        }
        catch (SAXException saxe) {
            throw new RuntimeException("There was an error while parsing the InputStream", saxe);
        }
    }
}

