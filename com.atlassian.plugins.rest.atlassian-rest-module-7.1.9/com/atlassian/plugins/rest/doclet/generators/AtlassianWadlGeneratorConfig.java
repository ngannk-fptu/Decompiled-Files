/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.doclet.generators;

import com.atlassian.plugins.rest.doclet.generators.grammars.WadlGrammarsAdaptor;
import com.atlassian.plugins.rest.doclet.generators.resourcedoc.AtlassianRuntimeWadlGeneratorResourceDocSupport;
import com.sun.jersey.api.wadl.config.WadlGeneratorConfig;
import com.sun.jersey.api.wadl.config.WadlGeneratorDescription;
import com.sun.jersey.server.wadl.generators.WadlGeneratorApplicationDoc;
import java.util.List;

public class AtlassianWadlGeneratorConfig
extends WadlGeneratorConfig {
    public static final String APPLICATION_XML = "application-doc.xml";
    public static final String GRAMMARS_XML = "application-grammars.xml";
    public static final String RESOURCE_XML = "resourcedoc.xml";

    @Override
    public List<WadlGeneratorDescription> configure() {
        return AtlassianWadlGeneratorConfig.generator(WadlGeneratorApplicationDoc.class).prop("applicationDocsStream", APPLICATION_XML).generator(WadlGrammarsAdaptor.class).prop("grammarsStream", GRAMMARS_XML).generator(AtlassianRuntimeWadlGeneratorResourceDocSupport.class).prop("resourceDocStream", RESOURCE_XML).descriptions();
    }
}

