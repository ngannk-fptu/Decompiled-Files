/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.util;

import java.util.ArrayList;
import java.util.HashMap;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLComponentManager;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;

public class ParserConfigurationSettings
implements XMLComponentManager {
    private final ArrayList<String> fRecognizedProperties_;
    private final HashMap<String, Object> fProperties_;
    private final ArrayList<String> fRecognizedFeatures_ = new ArrayList();
    private final HashMap<String, Boolean> fFeatures_;

    public ParserConfigurationSettings() {
        this.fRecognizedProperties_ = new ArrayList();
        this.fFeatures_ = new HashMap();
        this.fProperties_ = new HashMap();
    }

    public void addRecognizedFeatures(String[] featureIds) {
        int featureIdsCount = featureIds != null ? featureIds.length : 0;
        for (int i = 0; i < featureIdsCount; ++i) {
            String featureId = featureIds[i];
            if (this.fRecognizedFeatures_.contains(featureId)) continue;
            this.fRecognizedFeatures_.add(featureId);
        }
    }

    public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
        this.checkFeature(featureId);
        this.fFeatures_.put(featureId, state ? Boolean.TRUE : Boolean.FALSE);
    }

    public void addRecognizedProperties(String[] propertyIds) {
        int propertyIdsCount = propertyIds != null ? propertyIds.length : 0;
        for (int i = 0; i < propertyIdsCount; ++i) {
            String propertyId = propertyIds[i];
            if (this.fRecognizedProperties_.contains(propertyId)) continue;
            this.fRecognizedProperties_.add(propertyId);
        }
    }

    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
        this.checkProperty(propertyId);
        this.fProperties_.put(propertyId, value);
    }

    @Override
    public boolean getFeature(String featureId) throws XMLConfigurationException {
        Boolean state = this.fFeatures_.get(featureId);
        if (state == null) {
            this.checkFeature(featureId);
            return false;
        }
        return state;
    }

    @Override
    public Object getProperty(String propertyId) throws XMLConfigurationException {
        Object propertyValue = this.fProperties_.get(propertyId);
        if (propertyValue == null) {
            this.checkProperty(propertyId);
        }
        return propertyValue;
    }

    protected void checkFeature(String featureId) throws XMLConfigurationException {
        if (!this.fRecognizedFeatures_.contains(featureId)) {
            boolean type = false;
            throw new XMLConfigurationException(0, featureId);
        }
    }

    protected void checkProperty(String propertyId) throws XMLConfigurationException {
        if (!this.fRecognizedProperties_.contains(propertyId)) {
            boolean type = false;
            throw new XMLConfigurationException(0, propertyId);
        }
    }
}

