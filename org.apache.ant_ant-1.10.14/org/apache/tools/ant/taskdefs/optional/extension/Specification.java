/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import org.apache.tools.ant.taskdefs.optional.extension.Compatibility;
import org.apache.tools.ant.util.DeweyDecimal;

public final class Specification {
    private static final String MISSING = "Missing ";
    public static final Attributes.Name SPECIFICATION_TITLE = Attributes.Name.SPECIFICATION_TITLE;
    public static final Attributes.Name SPECIFICATION_VERSION = Attributes.Name.SPECIFICATION_VERSION;
    public static final Attributes.Name SPECIFICATION_VENDOR = Attributes.Name.SPECIFICATION_VENDOR;
    public static final Attributes.Name IMPLEMENTATION_TITLE = Attributes.Name.IMPLEMENTATION_TITLE;
    public static final Attributes.Name IMPLEMENTATION_VERSION = Attributes.Name.IMPLEMENTATION_VERSION;
    public static final Attributes.Name IMPLEMENTATION_VENDOR = Attributes.Name.IMPLEMENTATION_VENDOR;
    public static final Compatibility COMPATIBLE = new Compatibility("COMPATIBLE");
    public static final Compatibility REQUIRE_SPECIFICATION_UPGRADE = new Compatibility("REQUIRE_SPECIFICATION_UPGRADE");
    public static final Compatibility REQUIRE_VENDOR_SWITCH = new Compatibility("REQUIRE_VENDOR_SWITCH");
    public static final Compatibility REQUIRE_IMPLEMENTATION_CHANGE = new Compatibility("REQUIRE_IMPLEMENTATION_CHANGE");
    public static final Compatibility INCOMPATIBLE = new Compatibility("INCOMPATIBLE");
    private String specificationTitle;
    private DeweyDecimal specificationVersion;
    private String specificationVendor;
    private String implementationTitle;
    private String implementationVendor;
    private String implementationVersion;
    private String[] sections;

    public static Specification[] getSpecifications(Manifest manifest) throws ParseException {
        if (null == manifest) {
            return new Specification[0];
        }
        ArrayList<Specification> results = new ArrayList<Specification>();
        for (Map.Entry<String, Attributes> e : manifest.getEntries().entrySet()) {
            Optional.ofNullable(Specification.getSpecification(e.getKey(), e.getValue())).ifPresent(results::add);
        }
        return Specification.removeDuplicates(results).toArray(new Specification[0]);
    }

    public Specification(String specificationTitle, String specificationVersion, String specificationVendor, String implementationTitle, String implementationVersion, String implementationVendor) {
        this(specificationTitle, specificationVersion, specificationVendor, implementationTitle, implementationVersion, implementationVendor, null);
    }

    public Specification(String specificationTitle, String specificationVersion, String specificationVendor, String implementationTitle, String implementationVersion, String implementationVendor, String[] sections) {
        this.specificationTitle = specificationTitle;
        this.specificationVendor = specificationVendor;
        if (null != specificationVersion) {
            try {
                this.specificationVersion = new DeweyDecimal(specificationVersion);
            }
            catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Bad specification version format '" + specificationVersion + "' in '" + specificationTitle + "'. (Reason: " + nfe + ")");
            }
        }
        this.implementationTitle = implementationTitle;
        this.implementationVendor = implementationVendor;
        this.implementationVersion = implementationVersion;
        if (null == this.specificationTitle) {
            throw new NullPointerException("specificationTitle");
        }
        this.sections = sections == null ? null : (String[])sections.clone();
    }

    public String getSpecificationTitle() {
        return this.specificationTitle;
    }

    public String getSpecificationVendor() {
        return this.specificationVendor;
    }

    public String getImplementationTitle() {
        return this.implementationTitle;
    }

    public DeweyDecimal getSpecificationVersion() {
        return this.specificationVersion;
    }

    public String getImplementationVendor() {
        return this.implementationVendor;
    }

    public String getImplementationVersion() {
        return this.implementationVersion;
    }

    public String[] getSections() {
        return this.sections == null ? null : (String[])this.sections.clone();
    }

    public Compatibility getCompatibilityWith(Specification other) {
        if (!this.specificationTitle.equals(other.getSpecificationTitle())) {
            return INCOMPATIBLE;
        }
        DeweyDecimal otherSpecificationVersion = other.getSpecificationVersion();
        if (!(null == this.specificationVersion || null != otherSpecificationVersion && this.isCompatible(this.specificationVersion, otherSpecificationVersion))) {
            return REQUIRE_SPECIFICATION_UPGRADE;
        }
        if (null != this.implementationVendor && !this.implementationVendor.equals(other.getImplementationVendor())) {
            return REQUIRE_VENDOR_SWITCH;
        }
        if (null != this.implementationVersion && !this.implementationVersion.equals(other.getImplementationVersion())) {
            return REQUIRE_IMPLEMENTATION_CHANGE;
        }
        return COMPATIBLE;
    }

    public boolean isCompatibleWith(Specification other) {
        return COMPATIBLE == this.getCompatibilityWith(other);
    }

    public String toString() {
        String format = "%s: %s%n";
        StringBuilder sb = new StringBuilder(String.format("%s: %s%n", SPECIFICATION_TITLE, this.specificationTitle));
        if (null != this.specificationVersion) {
            sb.append(String.format("%s: %s%n", SPECIFICATION_VERSION, this.specificationVersion));
        }
        if (null != this.specificationVendor) {
            sb.append(String.format("%s: %s%n", SPECIFICATION_VENDOR, this.specificationVendor));
        }
        if (null != this.implementationTitle) {
            sb.append(String.format("%s: %s%n", IMPLEMENTATION_TITLE, this.implementationTitle));
        }
        if (null != this.implementationVersion) {
            sb.append(String.format("%s: %s%n", IMPLEMENTATION_VERSION, this.implementationVersion));
        }
        if (null != this.implementationVendor) {
            sb.append(String.format("%s: %s%n", IMPLEMENTATION_VENDOR, this.implementationVendor));
        }
        return sb.toString();
    }

    private boolean isCompatible(DeweyDecimal first, DeweyDecimal second) {
        return first.isGreaterThanOrEqual(second);
    }

    private static List<Specification> removeDuplicates(List<Specification> list) {
        ArrayList<Specification> results = new ArrayList<Specification>();
        ArrayList<String> sections = new ArrayList<String>();
        while (!list.isEmpty()) {
            Specification specification = list.remove(0);
            Iterator<Specification> iterator = list.iterator();
            while (iterator.hasNext()) {
                Specification other = iterator.next();
                if (!Specification.isEqual(specification, other)) continue;
                Optional.ofNullable(other.getSections()).ifPresent(s -> Collections.addAll(sections, s));
                iterator.remove();
            }
            results.add(Specification.mergeInSections(specification, sections));
            sections.clear();
        }
        return results;
    }

    private static boolean isEqual(Specification specification, Specification other) {
        return specification.getSpecificationTitle().equals(other.getSpecificationTitle()) && specification.getSpecificationVersion().isEqual(other.getSpecificationVersion()) && specification.getSpecificationVendor().equals(other.getSpecificationVendor()) && specification.getImplementationTitle().equals(other.getImplementationTitle()) && specification.getImplementationVersion().equals(other.getImplementationVersion()) && specification.getImplementationVendor().equals(other.getImplementationVendor());
    }

    private static Specification mergeInSections(Specification specification, List<String> sectionsToAdd) {
        if (sectionsToAdd.isEmpty()) {
            return specification;
        }
        Stream sections = Stream.concat(Optional.ofNullable(specification.getSections()).map(Stream::of).orElse(Stream.empty()), sectionsToAdd.stream());
        return new Specification(specification.getSpecificationTitle(), specification.getSpecificationVersion().toString(), specification.getSpecificationVendor(), specification.getImplementationTitle(), specification.getImplementationVersion(), specification.getImplementationVendor(), (String[])sections.toArray(String[]::new));
    }

    private static String getTrimmedString(String value) {
        return value == null ? null : value.trim();
    }

    private static Specification getSpecification(String section, Attributes attributes) throws ParseException {
        String name = Specification.getTrimmedString(attributes.getValue(SPECIFICATION_TITLE));
        if (null == name) {
            return null;
        }
        String specVendor = Specification.getTrimmedString(attributes.getValue(SPECIFICATION_VENDOR));
        if (null == specVendor) {
            throw new ParseException(MISSING + SPECIFICATION_VENDOR, 0);
        }
        String specVersion = Specification.getTrimmedString(attributes.getValue(SPECIFICATION_VERSION));
        if (null == specVersion) {
            throw new ParseException(MISSING + SPECIFICATION_VERSION, 0);
        }
        String impTitle = Specification.getTrimmedString(attributes.getValue(IMPLEMENTATION_TITLE));
        if (null == impTitle) {
            throw new ParseException(MISSING + IMPLEMENTATION_TITLE, 0);
        }
        String impVersion = Specification.getTrimmedString(attributes.getValue(IMPLEMENTATION_VERSION));
        if (null == impVersion) {
            throw new ParseException(MISSING + IMPLEMENTATION_VERSION, 0);
        }
        String impVendor = Specification.getTrimmedString(attributes.getValue(IMPLEMENTATION_VENDOR));
        if (null == impVendor) {
            throw new ParseException(MISSING + IMPLEMENTATION_VENDOR, 0);
        }
        return new Specification(name, specVersion, specVendor, impTitle, impVersion, impVendor, new String[]{section});
    }
}

