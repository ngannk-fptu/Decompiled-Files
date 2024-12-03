/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.security.PrivilegedGetTccl
 *  org.apache.tomcat.util.security.PrivilegedSetTccl
 */
package org.apache.tomcat.util.descriptor.tld;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.Constants;
import org.apache.tomcat.util.descriptor.DigesterFactory;
import org.apache.tomcat.util.descriptor.XmlErrorHandler;
import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.apache.tomcat.util.descriptor.tld.TldRuleSet;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TldParser {
    private final Log log = LogFactory.getLog(TldParser.class);
    private final Digester digester;

    public TldParser(boolean namespaceAware, boolean validation, boolean blockExternal) {
        this(namespaceAware, validation, new TldRuleSet(), blockExternal);
    }

    public TldParser(boolean namespaceAware, boolean validation, RuleSet ruleSet, boolean blockExternal) {
        this.digester = DigesterFactory.newDigester(validation, namespaceAware, ruleSet, blockExternal);
    }

    public TaglibXml parse(TldResourcePath path) throws IOException, SAXException {
        ClassLoader original;
        Thread currentThread = Thread.currentThread();
        if (Constants.IS_SECURITY_ENABLED) {
            PrivilegedGetTccl pa = new PrivilegedGetTccl(currentThread);
            original = (ClassLoader)AccessController.doPrivileged(pa);
        } else {
            original = currentThread.getContextClassLoader();
        }
        try {
            TaglibXml taglibXml;
            block17: {
                InputStream is = path.openStream();
                try {
                    if (Constants.IS_SECURITY_ENABLED) {
                        PrivilegedSetTccl pa = new PrivilegedSetTccl(currentThread, TldParser.class.getClassLoader());
                        AccessController.doPrivileged(pa);
                    } else {
                        currentThread.setContextClassLoader(TldParser.class.getClassLoader());
                    }
                    XmlErrorHandler handler = new XmlErrorHandler();
                    this.digester.setErrorHandler(handler);
                    TaglibXml taglibXml2 = new TaglibXml();
                    this.digester.push(taglibXml2);
                    InputSource source = new InputSource(path.toExternalForm());
                    source.setByteStream(is);
                    this.digester.parse(source);
                    if (!handler.getWarnings().isEmpty() || !handler.getErrors().isEmpty()) {
                        handler.logFindings(this.log, source.getSystemId());
                        if (!handler.getErrors().isEmpty()) {
                            throw handler.getErrors().iterator().next();
                        }
                    }
                    taglibXml = taglibXml2;
                    if (is == null) break block17;
                }
                catch (Throwable throwable) {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                is.close();
            }
            return taglibXml;
        }
        finally {
            this.digester.reset();
            if (Constants.IS_SECURITY_ENABLED) {
                PrivilegedSetTccl pa = new PrivilegedSetTccl(currentThread, original);
                AccessController.doPrivileged(pa);
            } else {
                currentThread.setContextClassLoader(original);
            }
        }
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.digester.setClassLoader(classLoader);
    }
}

