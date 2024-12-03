/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.blueprint.config.internal;

import org.eclipse.gemini.blueprint.config.internal.OsgiDefaultsDefinition;
import org.eclipse.gemini.blueprint.config.internal.util.ReferenceParsingUtil;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BlueprintDefaultsDefinition
extends OsgiDefaultsDefinition {
    private static final String BLUEPRINT_NS = "http://www.osgi.org/xmlns/blueprint/v1.0.0";
    private static final String DEFAULT_TIMEOUT = "default-timeout";
    private static final String DEFAULT_AVAILABILITY = "default-availability";
    private static final String TIMEOUT_DEFAULT = "300000";
    private static final String DEFAULT_INITIALIZATION = "default-activation";
    private static final String LAZY_INITIALIZATION = "lazy";
    private static final boolean INITIALIZATION_DEFAULT = false;
    private boolean defaultInitialization;

    public BlueprintDefaultsDefinition(Document doc, ParserContext parserContext) {
        super(doc, parserContext);
        String initialization;
        Element root = doc.getDocumentElement();
        String timeout = this.getAttribute(root, BLUEPRINT_NS, DEFAULT_TIMEOUT);
        this.setTimeout(StringUtils.hasText((String)timeout) ? timeout.trim() : TIMEOUT_DEFAULT);
        String availability = this.getAttribute(root, BLUEPRINT_NS, DEFAULT_AVAILABILITY);
        if (StringUtils.hasText((String)availability)) {
            Availability avail = ReferenceParsingUtil.determineAvailability(availability);
            this.setAvailability(avail);
        }
        this.defaultInitialization = StringUtils.hasText((String)(initialization = this.getAttribute(root, BLUEPRINT_NS, DEFAULT_INITIALIZATION))) ? initialization.trim().equalsIgnoreCase(LAZY_INITIALIZATION) : false;
    }

    public boolean getDefaultInitialization() {
        return this.defaultInitialization;
    }
}

