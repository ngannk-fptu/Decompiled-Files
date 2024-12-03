/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;

public class ParserConfigurationSettings
implements XMLComponentManager {
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected ArrayList fRecognizedProperties;
    protected HashMap fProperties;
    protected ArrayList fRecognizedFeatures = new ArrayList();
    protected HashMap fFeatures;
    protected XMLComponentManager fParentSettings;

    public ParserConfigurationSettings() {
        this(null);
    }

    public ParserConfigurationSettings(XMLComponentManager xMLComponentManager) {
        this.fRecognizedProperties = new ArrayList();
        this.fFeatures = new HashMap();
        this.fProperties = new HashMap();
        this.fParentSettings = xMLComponentManager;
    }

    public void addRecognizedFeatures(String[] stringArray) {
        int n = stringArray != null ? stringArray.length : 0;
        for (int i = 0; i < n; ++i) {
            String string = stringArray[i];
            if (this.fRecognizedFeatures.contains(string)) continue;
            this.fRecognizedFeatures.add(string);
        }
    }

    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        this.checkFeature(string);
        this.fFeatures.put(string, bl ? Boolean.TRUE : Boolean.FALSE);
    }

    public void addRecognizedProperties(String[] stringArray) {
        int n = stringArray != null ? stringArray.length : 0;
        for (int i = 0; i < n; ++i) {
            String string = stringArray[i];
            if (this.fRecognizedProperties.contains(string)) continue;
            this.fRecognizedProperties.add(string);
        }
    }

    public void setProperty(String string, Object object) throws XMLConfigurationException {
        this.checkProperty(string);
        this.fProperties.put(string, object);
    }

    @Override
    public boolean getFeature(String string) throws XMLConfigurationException {
        Boolean bl = (Boolean)this.fFeatures.get(string);
        if (bl == null) {
            this.checkFeature(string);
            return false;
        }
        return bl;
    }

    @Override
    public Object getProperty(String string) throws XMLConfigurationException {
        Object v = this.fProperties.get(string);
        if (v == null) {
            this.checkProperty(string);
        }
        return v;
    }

    protected void checkFeature(String string) throws XMLConfigurationException {
        if (!this.fRecognizedFeatures.contains(string)) {
            if (this.fParentSettings != null) {
                this.fParentSettings.getFeature(string);
            } else {
                short s = 0;
                throw new XMLConfigurationException(s, string);
            }
        }
    }

    protected void checkProperty(String string) throws XMLConfigurationException {
        if (!this.fRecognizedProperties.contains(string)) {
            if (this.fParentSettings != null) {
                this.fParentSettings.getProperty(string);
            } else {
                short s = 0;
                throw new XMLConfigurationException(s, string);
            }
        }
    }
}

