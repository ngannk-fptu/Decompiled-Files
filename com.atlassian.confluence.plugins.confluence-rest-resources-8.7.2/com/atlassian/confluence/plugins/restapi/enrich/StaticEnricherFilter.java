/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.rest.navigation.impl.RestNavigationImpl
 *  com.atlassian.confluence.rest.serialization.DefaultRestEntityFactory
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.plugins.rest.navigation.impl.RestNavigationImpl;
import com.atlassian.confluence.plugins.restapi.enrich.DefaultRestEntityEnrichmentManager;
import com.atlassian.confluence.plugins.restapi.enrich.VisitorWrapper;
import com.atlassian.confluence.plugins.restapi.enrich.documentation.RequestEntityEnricher;
import com.atlassian.confluence.rest.serialization.DefaultRestEntityFactory;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import java.util.List;
import java.util.Map;

public class StaticEnricherFilter {
    private static final String DOC_BASE_URL = System.getProperty("confluence.restdoc.static.baseurl", "http://myhost:8080/confluence");
    private static final String CONTEXT_PATH = System.getProperty("confluence.restdoc.static.context", "/confluence");
    private static final DefaultRestEntityEnrichmentManager restEntityEnricher = new DefaultRestEntityEnrichmentManager(() -> new RestNavigationImpl(DOC_BASE_URL, CONTEXT_PATH), null, new DefaultRestEntityFactory());

    public static Object enrichResponse(Object obj) {
        return StaticEnricherFilter.enrichResponseInternal(obj);
    }

    public static <T> List<T> enrichResponse(List<T> obj) {
        return (List)StaticEnricherFilter.enrichResponseInternal(obj);
    }

    public static Map enrichResponse(Map obj) {
        return (Map)StaticEnricherFilter.enrichResponseInternal(obj);
    }

    private static Object enrichResponseInternal(Object obj) {
        return restEntityEnricher.convertAndEnrich(obj, SchemaType.REST);
    }

    public static Object enrichRequest(Object obj) {
        Object converted = StaticEnricherFilter.enrichResponseInternal(obj);
        VisitorWrapper.newTreeFilter(new RequestEntityEnricher()).enrich(converted, SchemaType.REST);
        return converted;
    }
}

