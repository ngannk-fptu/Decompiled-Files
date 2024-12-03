/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import groovy.util.BuilderSupport;
import groovy.xml.NamespaceBuilderSupport;
import java.util.Map;

public class NamespaceBuilder {
    private BuilderSupport builder;

    public static NamespaceBuilderSupport newInstance(BuilderSupport builder, String uri) {
        return new NamespaceBuilder(builder).namespace(uri);
    }

    public static NamespaceBuilderSupport newInstance(BuilderSupport builder) {
        return new NamespaceBuilderSupport(builder);
    }

    public static NamespaceBuilderSupport newInstance(BuilderSupport builder, String uri, String prefix) {
        return new NamespaceBuilder(builder).namespace(uri, prefix);
    }

    public static NamespaceBuilderSupport newInstance(Map nsMap, BuilderSupport builder) {
        return new NamespaceBuilder(builder).declareNamespace(nsMap);
    }

    public NamespaceBuilder(BuilderSupport builder) {
        this.builder = builder;
    }

    public NamespaceBuilderSupport namespace(String uri) {
        return this.namespace(uri, "");
    }

    public NamespaceBuilderSupport namespace(String uri, String prefix) {
        return new NamespaceBuilderSupport(this.builder, uri, prefix);
    }

    public NamespaceBuilderSupport declareNamespace(Map ns) {
        return new NamespaceBuilderSupport(this.builder, ns);
    }
}

