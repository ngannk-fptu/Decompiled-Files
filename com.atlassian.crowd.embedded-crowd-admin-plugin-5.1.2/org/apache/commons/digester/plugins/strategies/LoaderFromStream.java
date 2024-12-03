/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.commons.digester.plugins.strategies;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.digester.xmlrules.FromXmlRuleSet;
import org.apache.commons.logging.Log;
import org.xml.sax.InputSource;

public class LoaderFromStream
extends RuleLoader {
    private byte[] input;
    private FromXmlRuleSet ruleSet;

    public LoaderFromStream(InputStream s) throws Exception {
        this.load(s);
    }

    private void load(InputStream s) throws IOException {
        int i;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[256];
        while ((i = s.read(buf)) != -1) {
            baos.write(buf, 0, i);
        }
        this.input = baos.toByteArray();
    }

    public void addRules(Digester d, String path) throws PluginException {
        Log log = d.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("LoaderFromStream: loading rules for plugin at path [" + path + "]"));
        }
        InputSource source = new InputSource(new ByteArrayInputStream(this.input));
        FromXmlRuleSet ruleSet = new FromXmlRuleSet(source);
        ruleSet.addRuleInstances(d, path);
    }
}

