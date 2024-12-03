/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.IOException;
import java.net.URL;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.DigesterFactory;
import org.apache.tomcat.util.descriptor.InputSourceUtil;
import org.apache.tomcat.util.descriptor.XmlErrorHandler;
import org.apache.tomcat.util.descriptor.web.Constants;
import org.apache.tomcat.util.descriptor.web.WebRuleSet;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class WebXmlParser {
    private final Log log = LogFactory.getLog(WebXmlParser.class);
    private static final StringManager sm = StringManager.getManager((String)Constants.PACKAGE_NAME);
    private final Digester webDigester;
    private final WebRuleSet webRuleSet = new WebRuleSet(false);
    private final Digester webFragmentDigester;
    private final WebRuleSet webFragmentRuleSet;

    public WebXmlParser(boolean namespaceAware, boolean validation, boolean blockExternal) {
        this.webDigester = DigesterFactory.newDigester(validation, namespaceAware, this.webRuleSet, blockExternal);
        this.webDigester.getParser();
        this.webFragmentRuleSet = new WebRuleSet(true);
        this.webFragmentDigester = DigesterFactory.newDigester(validation, namespaceAware, this.webFragmentRuleSet, blockExternal);
        this.webFragmentDigester.getParser();
    }

    public boolean parseWebXml(URL url, WebXml dest, boolean fragment) throws IOException {
        if (url == null) {
            return true;
        }
        InputSource source = new InputSource(url.toExternalForm());
        source.setByteStream(url.openStream());
        return this.parseWebXml(source, dest, fragment);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean parseWebXml(InputSource source, WebXml dest, boolean fragment) {
        WebRuleSet ruleSet;
        Digester digester;
        boolean ok = true;
        if (source == null) {
            return ok;
        }
        XmlErrorHandler handler = new XmlErrorHandler();
        if (fragment) {
            digester = this.webFragmentDigester;
            ruleSet = this.webFragmentRuleSet;
        } else {
            digester = this.webDigester;
            ruleSet = this.webRuleSet;
        }
        digester.push(dest);
        digester.setErrorHandler(handler);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("webXmlParser.applicationStart", new Object[]{source.getSystemId()}));
        }
        try {
            digester.parse(source);
            if (handler.getWarnings().size() > 0 || handler.getErrors().size() > 0) {
                ok = false;
                handler.logFindings(this.log, source.getSystemId());
            }
        }
        catch (SAXParseException e) {
            this.log.error((Object)sm.getString("webXmlParser.applicationParse", new Object[]{source.getSystemId()}), (Throwable)e);
            this.log.error((Object)sm.getString("webXmlParser.applicationPosition", new Object[]{"" + e.getLineNumber(), "" + e.getColumnNumber()}));
            ok = false;
        }
        catch (Exception e) {
            this.log.error((Object)sm.getString("webXmlParser.applicationParse", new Object[]{source.getSystemId()}), (Throwable)e);
            ok = false;
        }
        finally {
            InputSourceUtil.close(source);
            digester.reset();
            ruleSet.recycle();
        }
        return ok;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.webDigester.setClassLoader(classLoader);
        this.webFragmentDigester.setClassLoader(classLoader);
    }
}

