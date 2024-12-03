/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.descriptor.tagplugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.DigesterFactory;
import org.apache.tomcat.util.descriptor.XmlErrorHandler;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TagPluginParser {
    private final Log log = LogFactory.getLog(TagPluginParser.class);
    private static final String PREFIX = "tag-plugins/tag-plugin";
    private final Digester digester;
    private final Map<String, String> plugins = new HashMap<String, String>();

    public TagPluginParser(ServletContext context, boolean blockExternal) {
        this.digester = DigesterFactory.newDigester(false, false, new TagPluginRuleSet(), blockExternal);
        this.digester.setClassLoader(context.getClassLoader());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void parse(URL url) throws IOException, SAXException {
        try (InputStream is = url.openStream();){
            XmlErrorHandler handler = new XmlErrorHandler();
            this.digester.setErrorHandler(handler);
            this.digester.push(this);
            InputSource source = new InputSource(url.toExternalForm());
            source.setByteStream(is);
            this.digester.parse(source);
            if (!handler.getWarnings().isEmpty() || !handler.getErrors().isEmpty()) {
                handler.logFindings(this.log, source.getSystemId());
                if (!handler.getErrors().isEmpty()) {
                    throw handler.getErrors().iterator().next();
                }
            }
        }
        finally {
            this.digester.reset();
        }
    }

    public void addPlugin(String tagClass, String pluginClass) {
        this.plugins.put(tagClass, pluginClass);
    }

    public Map<String, String> getPlugins() {
        return this.plugins;
    }

    private static class TagPluginRuleSet
    implements RuleSet {
        private TagPluginRuleSet() {
        }

        @Override
        public void addRuleInstances(Digester digester) {
            digester.addCallMethod(TagPluginParser.PREFIX, "addPlugin", 2);
            digester.addCallParam("tag-plugins/tag-plugin/tag-class", 0);
            digester.addCallParam("tag-plugins/tag-plugin/plugin-class", 1);
        }
    }
}

