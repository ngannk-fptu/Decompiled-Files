/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package org.osgi.service.obr;

import java.net.URL;
import java.util.Map;
import org.osgi.framework.Version;
import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.Requirement;

public interface Resource {
    public static final String LICENSE_URL = "license";
    public static final String DESCRIPTION = "description";
    public static final String DOCUMENTATION_URL = "documentation";
    public static final String COPYRIGHT = "copyright";
    public static final String SOURCE_URL = "source";
    public static final String SYMBOLIC_NAME = "symbolicname";
    public static final String PRESENTATION_NAME = "presentationname";
    public static final String ID = "id";
    public static final String VERSION = "version";
    public static final String URL = "url";
    public static final String SIZE = "size";
    public static final String[] KEYS = new String[]{"description", "size", "id", "license", "documentation", "copyright", "source", "presentationname", "symbolicname", "version", "url"};

    public Map getProperties();

    public String getSymbolicName();

    public String getPresentationName();

    public Version getVersion();

    public String getId();

    public URL getURL();

    public Requirement[] getRequirements();

    public Capability[] getCapabilities();

    public String[] getCategories();

    public Repository getRepository();
}

