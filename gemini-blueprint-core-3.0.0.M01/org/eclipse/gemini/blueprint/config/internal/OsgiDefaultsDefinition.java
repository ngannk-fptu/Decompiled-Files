/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.config.internal;

import org.eclipse.gemini.blueprint.config.internal.util.ReferenceParsingUtil;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OsgiDefaultsDefinition {
    private static final String SDM_NS = "http://www.springframework.org/schema/osgi";
    private static final String EGB_NS = "http://www.eclipse.org/gemini/blueprint/schema/blueprint";
    private static final String DEFAULT_TIMEOUT = "default-timeout";
    private static final String DEFAULT_AVAILABILITY = "default-availability";
    private static final String DEFAULT_CARDINALITY = "default-cardinality";
    private static final String TIMEOUT_DEFAULT = "300000";
    private String timeout = "300000";
    private Availability availability = Availability.MANDATORY;

    public OsgiDefaultsDefinition(Document document, ParserContext parserContext) {
        Assert.notNull((Object)document);
        Element root = document.getDocumentElement();
        ReferenceParsingUtil.checkAvailabilityAndCardinalityDuplication(root, DEFAULT_AVAILABILITY, DEFAULT_CARDINALITY, parserContext);
        this.parseDefaults(root, EGB_NS);
        this.parseDefaults(root, SDM_NS);
    }

    private void parseDefaults(Element root, String namespace) {
        String cardinality;
        String availability;
        String timeout = this.getAttribute(root, namespace, DEFAULT_TIMEOUT);
        if (StringUtils.hasText((String)timeout)) {
            this.setTimeout(timeout);
        }
        if (StringUtils.hasText((String)(availability = this.getAttribute(root, namespace, DEFAULT_AVAILABILITY)))) {
            this.setAvailability(ReferenceParsingUtil.determineAvailability(availability));
        }
        if (StringUtils.hasText((String)(cardinality = this.getAttribute(root, namespace, DEFAULT_CARDINALITY)))) {
            this.setAvailability(ReferenceParsingUtil.determineAvailabilityFromCardinality(cardinality));
        }
    }

    public String getTimeout() {
        return this.timeout;
    }

    protected void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public Availability getAvailability() {
        return this.availability;
    }

    protected void setAvailability(Availability availability) {
        this.availability = availability;
    }

    protected String getAttribute(Element root, String ns, String attributeName) {
        String value = root.getAttributeNS(ns, attributeName);
        return !StringUtils.hasText((String)value) ? root.getAttribute(attributeName) : value;
    }
}

