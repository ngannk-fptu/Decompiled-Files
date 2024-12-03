/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins;

import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.plugins.Declaration;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.PluginInvalidInputException;
import org.apache.commons.digester.plugins.PluginManager;
import org.apache.commons.digester.plugins.PluginRules;
import org.xml.sax.Attributes;

public class PluginDeclarationRule
extends Rule {
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        int nAttrs = attributes.getLength();
        Properties props = new Properties();
        for (int i = 0; i < nAttrs; ++i) {
            String key = attributes.getLocalName(i);
            if (key == null || key.length() == 0) {
                key = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            props.setProperty(key, value);
        }
        try {
            PluginDeclarationRule.declarePlugin(this.digester, props);
        }
        catch (PluginInvalidInputException ex) {
            throw new PluginInvalidInputException("Error on element [" + this.digester.getMatch() + "]: " + ex.getMessage());
        }
    }

    public static void declarePlugin(Digester digester, Properties props) throws PluginException {
        String id = props.getProperty("id");
        String pluginClassName = props.getProperty("class");
        if (id == null) {
            throw new PluginInvalidInputException("mandatory attribute id not present on plugin declaration");
        }
        if (pluginClassName == null) {
            throw new PluginInvalidInputException("mandatory attribute class not present on plugin declaration");
        }
        Declaration newDecl = new Declaration(pluginClassName);
        newDecl.setId(id);
        newDecl.setProperties(props);
        PluginRules rc = (PluginRules)digester.getRules();
        PluginManager pm = rc.getPluginManager();
        newDecl.init(digester, pm);
        pm.addDeclaration(newDecl);
    }
}

