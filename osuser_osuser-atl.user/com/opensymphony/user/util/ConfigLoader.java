/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.ClassLoaderUtil
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.util;

import com.opensymphony.user.UserManager;
import com.opensymphony.user.authenticator.Authenticator;
import com.opensymphony.user.provider.UserProvider;
import com.opensymphony.util.ClassLoaderUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigLoader
extends DefaultHandler {
    private static final Log logger = LogFactory.getLog((Class)(class$com$opensymphony$user$util$ConfigLoader == null ? (class$com$opensymphony$user$util$ConfigLoader = ConfigLoader.class$("com.opensymphony.user.util.ConfigLoader")) : class$com$opensymphony$user$util$ConfigLoader));
    protected Properties currentProperties;
    protected String currentClass;
    protected UserManager userManager;
    static /* synthetic */ Class class$com$opensymphony$user$util$ConfigLoader;

    public synchronized void load(InputStream in, UserManager userManager) {
        this.userManager = userManager;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)"Loading config");
            }
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(in, (DefaultHandler)new ConfigHandler());
        }
        catch (SAXException e) {
            logger.error((Object)"Could not parse config XML", (Throwable)e);
            throw new RuntimeException(e.getMessage());
        }
        catch (IOException e) {
            logger.error((Object)"Could not read config from stream", (Throwable)e);
            throw new RuntimeException(e.getMessage());
        }
        catch (ParserConfigurationException e) {
            logger.fatal((Object)"Could not obtain SAX parser", (Throwable)e);
            throw new RuntimeException(e.getMessage());
        }
        catch (RuntimeException e) {
            logger.fatal((Object)"RuntimeException", (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            logger.fatal((Object)"Exception", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private void addAuthenticator() {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Authenticator class = " + this.currentClass + " " + this.currentProperties));
        }
        if (this.userManager != null) {
            try {
                Authenticator authenticator = (Authenticator)ClassLoaderUtil.loadClass((String)this.currentClass, this.getClass()).newInstance();
                if (!authenticator.init(this.currentProperties)) {
                    logger.error((Object)("Could not initialize authenticator " + this.currentClass));
                    throw new RuntimeException("Could not initialize authenticator " + this.currentClass);
                }
                this.userManager.setAuthenticator(authenticator);
            }
            catch (Exception e) {
                logger.error((Object)"Could not create instance of authenticator ", (Throwable)e);
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private void addProvider() {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("UserProvider class = " + this.currentClass + " " + this.currentProperties));
        }
        if (this.userManager != null) {
            try {
                UserProvider provider = (UserProvider)ClassLoaderUtil.loadClass((String)this.currentClass, this.getClass()).newInstance();
                if (!provider.init(this.currentProperties)) {
                    logger.error((Object)("Could not initialize provider " + this.currentClass));
                    throw new RuntimeException("Could not initialize provider " + this.currentClass);
                }
                this.userManager.addProvider(provider);
            }
            catch (Exception e) {
                logger.error((Object)"Could not create instance of provider", (Throwable)e);
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private class ConfigHandler
    extends DefaultHandler {
        private String _currentPropertyName;
        private StringBuffer _currentPropertyValue;

        private ConfigHandler() {
        }

        public void characters(char[] chars, int offset, int len) throws SAXException {
            if (this._currentPropertyValue != null) {
                this._currentPropertyValue.append(chars, offset, len);
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equals("provider")) {
                ConfigLoader.this.addProvider();
                ConfigLoader.this.currentProperties = null;
                ConfigLoader.this.currentClass = null;
            } else if (qName.equals("authenticator")) {
                ConfigLoader.this.addAuthenticator();
                ConfigLoader.this.currentProperties = null;
                ConfigLoader.this.currentClass = null;
            } else if (qName.equals("property")) {
                ConfigLoader.this.currentProperties.put(this._currentPropertyName, this._currentPropertyValue.toString());
                this._currentPropertyName = null;
                this._currentPropertyValue = null;
            }
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("provider") || qName.equals("authenticator")) {
                ConfigLoader.this.currentClass = attributes.getValue("class");
                ConfigLoader.this.currentProperties = new Properties();
            } else if (qName.equals("property")) {
                this._currentPropertyName = attributes.getValue("name");
                this._currentPropertyValue = new StringBuffer();
            }
        }
    }
}

