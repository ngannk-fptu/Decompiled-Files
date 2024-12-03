/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.osuser.config.xml;

import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.impl.osuser.config.xml.DefaultOSUConfigurationLoader;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultOSUConfigurationHandler
extends DefaultHandler {
    private static final Logger log = Logger.getLogger(DefaultOSUConfigurationHandler.class);
    private DefaultOSUConfigurationLoader configLoader;
    private String currentClass;
    private Properties currentProperties;
    private String _currentPropertyName;
    private StringBuffer _currentPropertyValue;

    public DefaultOSUConfigurationHandler(DefaultOSUConfigurationLoader configLoader) {
        this.configLoader = configLoader;
    }

    public void characters(char[] chars, int offset, int len) throws SAXException {
        if (this._currentPropertyValue != null) {
            this._currentPropertyValue.append(chars, offset, len);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("provider")) {
            try {
                this.configLoader.addProvider(this.currentClass, this.currentProperties);
            }
            catch (ConfigurationException e) {
                log.error((Object)e);
            }
            this.currentProperties = null;
            this.currentClass = null;
        } else if (qName.equals("authenticator")) {
            this.currentProperties = null;
            this.currentClass = null;
        } else if (qName.equals("property")) {
            this.currentProperties.put(this._currentPropertyName, this._currentPropertyValue.toString());
            this._currentPropertyName = null;
            this._currentPropertyValue = null;
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("provider") || qName.equals("authenticator")) {
            this.currentClass = attributes.getValue("class");
            this.currentProperties = new Properties();
        } else if (qName.equals("property")) {
            this._currentPropertyName = attributes.getValue("name");
            this._currentPropertyValue = new StringBuffer();
        }
    }
}

