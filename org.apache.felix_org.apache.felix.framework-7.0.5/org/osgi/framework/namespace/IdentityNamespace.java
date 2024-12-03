/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework.namespace;

import org.osgi.resource.Namespace;

public final class IdentityNamespace
extends Namespace {
    public static final String IDENTITY_NAMESPACE = "osgi.identity";
    public static final String CAPABILITY_SINGLETON_DIRECTIVE = "singleton";
    public static final String CAPABILITY_VERSION_ATTRIBUTE = "version";
    public static final String CAPABILITY_TYPE_ATTRIBUTE = "type";
    public static final String TYPE_BUNDLE = "osgi.bundle";
    public static final String TYPE_FRAGMENT = "osgi.fragment";
    public static final String TYPE_UNKNOWN = "unknown";
    public static final String CAPABILITY_TAGS_ATTRIBUTE = "tags";
    public static final String CAPABILITY_COPYRIGHT_ATTRIBUTE = "copyright";
    public static final String CAPABILITY_DESCRIPTION_ATTRIBUTE = "description";
    public static final String CAPABILITY_DOCUMENTATION_ATTRIBUTE = "documentation";
    public static final String CAPABILITY_LICENSE_ATTRIBUTE = "license";
    public static final String REQUIREMENT_CLASSIFIER_DIRECTIVE = "classifier";
    public static final String CLASSIFIER_SOURCES = "sources";
    public static final String CLASSIFIER_JAVADOC = "javadoc";

    private IdentityNamespace() {
    }
}

