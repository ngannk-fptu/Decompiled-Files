/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.ext.license;

import java.util.List;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Source;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class LicenseHelper {
    public static final String UNSPECIFIED_LICENSE = "http://purl.org/atompub/license#unspecified";

    LicenseHelper() {
    }

    public static List<Link> getLicense(Base base, boolean inherited) {
        List<Link> links = null;
        if (base instanceof Source) {
            links = ((Source)base).getLinks("license");
        } else if (base instanceof Entry) {
            Entry entry = (Entry)base;
            Source source = entry.getSource();
            Object parent = entry.getParentElement();
            links = entry.getLinks("license");
            if (inherited && (links == null || links.size() == 0) && source != null) {
                links = LicenseHelper.getLicense(source, false);
            }
            if (inherited && (links == null || links.size() == 0) && parent != null) {
                links = LicenseHelper.getLicense(parent, false);
            }
        }
        return links;
    }

    public static List<Link> getLicense(Base base) {
        return LicenseHelper.getLicense(base, true);
    }

    public static boolean hasUnspecifiedLicense(Base base, boolean inherited) {
        return LicenseHelper.hasLicense(base, UNSPECIFIED_LICENSE, inherited);
    }

    public static boolean hasUnspecifiedLicense(Base base) {
        return LicenseHelper.hasUnspecifiedLicense(base, true);
    }

    public static boolean hasLicense(Base base, String iri, boolean inherited) {
        List<Link> links = LicenseHelper.getLicense(base, inherited);
        IRI check = new IRI(iri);
        boolean answer = false;
        if (links != null) {
            for (Link link : links) {
                if (!link.getResolvedHref().equals(check)) continue;
                answer = true;
                break;
            }
        }
        return answer;
    }

    public static boolean hasLicense(Base base, String iri) {
        return LicenseHelper.hasLicense(base, iri, true);
    }

    public static boolean hasLicense(Base base, boolean inherited) {
        List<Link> links = LicenseHelper.getLicense(base, inherited);
        return links != null && links.size() > 0;
    }

    public static boolean hasLicense(Base base) {
        return LicenseHelper.hasLicense(base, true);
    }

    public static Link addUnspecifiedLicense(Base base) {
        if (LicenseHelper.hasUnspecifiedLicense(base, false)) {
            throw new IllegalStateException("Unspecified license already added");
        }
        if (LicenseHelper.hasLicense(base, false)) {
            throw new IllegalStateException("Other licenses are already added.");
        }
        return LicenseHelper.addLicense(base, UNSPECIFIED_LICENSE);
    }

    public static Link addLicense(Base base, String iri) {
        return LicenseHelper.addLicense(base, iri, null, null, null);
    }

    public static Link addLicense(Base base, String iri, String title) {
        return LicenseHelper.addLicense(base, iri, null, title, null);
    }

    public static Link addLicense(Base base, String iri, String type, String title, String hreflang) {
        if (LicenseHelper.hasLicense(base, iri, false)) {
            throw new IllegalStateException("License '" + iri + "' has already been added");
        }
        if (LicenseHelper.hasUnspecifiedLicense(base, false)) {
            throw new IllegalStateException("Unspecified license already added");
        }
        if (base instanceof Source) {
            return ((Source)base).addLink(new IRI(iri).toString(), "license", type, title, hreflang, -1L);
        }
        if (base instanceof Entry) {
            return ((Entry)base).addLink(new IRI(iri).toString(), "license", type, title, hreflang, -1L);
        }
        return null;
    }
}

