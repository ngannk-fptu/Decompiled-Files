/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResourceType
extends AbstractDavProperty<Set<XmlSerializable>> {
    public static final int DEFAULT_RESOURCE = 0;
    public static final int COLLECTION = 1;
    public static final int VERSION_HISTORY = 2;
    public static final int ACTIVITY = 3;
    public static final int BASELINE = 4;
    private static final List<TypeName> NAMES = new ArrayList<TypeName>();
    private final int[] resourceTypes;

    public ResourceType(int resourceType) {
        this(new int[]{resourceType});
    }

    public ResourceType(int[] resourceTypes) {
        super(DavPropertyName.RESOURCETYPE, false);
        for (int resourceType : resourceTypes) {
            if (ResourceType.isValidResourceType(resourceType)) continue;
            throw new IllegalArgumentException("Invalid resource type '" + resourceType + "'.");
        }
        this.resourceTypes = resourceTypes;
    }

    @Override
    public Set<XmlSerializable> getValue() {
        HashSet<XmlSerializable> rTypes = new HashSet<XmlSerializable>();
        for (int resourceType : this.resourceTypes) {
            TypeName n = NAMES.get(resourceType);
            if (n == null) continue;
            rTypes.add(n);
        }
        return rTypes;
    }

    public int[] getResourceTypes() {
        return this.resourceTypes;
    }

    private static boolean isValidResourceType(int resourceType) {
        return resourceType >= 0 && resourceType <= NAMES.size() - 1;
    }

    public static int registerResourceType(String name, Namespace namespace) {
        if (name == null || namespace == null) {
            throw new IllegalArgumentException("Cannot register a <null> resourcetype");
        }
        TypeName tn = new TypeName(name, namespace);
        if (NAMES.contains(tn)) {
            return NAMES.indexOf(tn);
        }
        if (NAMES.add(tn)) {
            return NAMES.size() - 1;
        }
        throw new IllegalArgumentException("Could not register resourcetype " + namespace.getPrefix() + name);
    }

    static {
        NAMES.add(null);
        NAMES.add(new TypeName("collection", NAMESPACE));
        NAMES.add(new TypeName("version-history", DeltaVConstants.NAMESPACE));
        NAMES.add(new TypeName("activity", DeltaVConstants.NAMESPACE));
        NAMES.add(new TypeName("baseline", DeltaVConstants.NAMESPACE));
    }

    private static class TypeName
    implements XmlSerializable {
        private final String localName;
        private final Namespace namespace;
        private final int hashCode;

        private TypeName(String localName, Namespace namespace) {
            this.localName = localName;
            this.namespace = namespace;
            this.hashCode = DomUtil.getExpandedName(localName, namespace).hashCode();
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object o) {
            if (o instanceof TypeName) {
                return this.hashCode == ((TypeName)o).hashCode;
            }
            return false;
        }

        @Override
        public Element toXml(Document document) {
            return DomUtil.createElement(document, this.localName, this.namespace);
        }
    }
}

