/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import org.osgi.framework.Constants;

public interface FelixConstants
extends Constants {
    public static final String SYSTEM_BUNDLE_SYMBOLICNAME = "org.apache.felix.framework";
    public static final String FRAMEWORK_VERSION_VALUE = "1.9";
    public static final String FRAMEWORK_VENDOR_VALUE = "Apache Software Foundation";
    public static final String FELIX_VERSION_PROPERTY = "felix.version";
    public static final String DIRECTIVE_SEPARATOR = ":=";
    public static final String ATTRIBUTE_SEPARATOR = "=";
    public static final String CLASS_PATH_SEPARATOR = ",";
    public static final String CLASS_PATH_DOT = ".";
    public static final String PACKAGE_SEPARATOR = ";";
    public static final String VERSION_SEGMENT_SEPARATOR = ".";
    public static final int VERSION_SEGMENT_COUNT = 3;
    public static final String BUNDLE_NATIVECODE_OPTIONAL = "*";
    public static final String BUNDLE_URL_PROTOCOL = "bundle";
    public static final String FRAMEWORK_BUNDLECACHE_IMPL = "felix.bundlecache.impl";
    public static final String LOG_LEVEL_PROP = "felix.log.level";
    public static final String LOG_LOGGER_PROP = "felix.log.logger";
    public static final String SYSTEMBUNDLE_ACTIVATORS_PROP = "felix.systembundle.activators";
    public static final String BUNDLE_STARTLEVEL_PROP = "felix.startlevel.bundle";
    public static final String SERVICE_URLHANDLERS_PROP = "felix.service.urlhandlers";
    public static final String IMPLICIT_BOOT_DELEGATION_PROP = "felix.bootdelegation.implicit";
    public static final String BOOT_CLASSLOADERS_PROP = "felix.bootdelegation.classloaders";
    public static final String USE_LOCALURLS_PROP = "felix.jarurls";
    public static final String NATIVE_OS_NAME_ALIAS_PREFIX = "felix.native.osname.alias";
    public static final String NATIVE_PROC_NAME_ALIAS_PREFIX = "felix.native.processor.alias";
    public static final String USE_CACHEDURLS_PROPS = "felix.bundlecodesource.usecachedurls";
    public static final String RESOLVER_PARALLELISM = "felix.resolver.parallelism";
    public static final String USE_PROPERTY_SUBSTITUTION_IN_SYSTEMPACKAGES = "felix.systempackages.substitution";
    public static final String RESOLUTION_DYNAMIC = "dynamic";
    public static final int FRAMEWORK_INACTIVE_STARTLEVEL = 0;
    public static final int FRAMEWORK_DEFAULT_STARTLEVEL = 1;
    public static final int SYSTEMBUNDLE_DEFAULT_STARTLEVEL = 0;
    public static final int BUNDLE_DEFAULT_STARTLEVEL = 1;
    public static final String FAKE_URL_PROTOCOL_VALUE = "location:";
    public static final String FELIX_EXTENSION_ACTIVATOR = "Felix-Activator";
    public static final String SECURITY_DEFAULT_POLICY = "felix.security.defaultpolicy";
    public static final String FELIX_EXTENSIONS_DISABLE = "felix.extensions.disable";
    public static final String FRAMEWORK_UUID_SECURE = "felix.uuid.secure";
    public static final String CALCULATE_SYSTEMPACKAGES_USES = "felix.systempackages.calculate.uses";
}

