/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import org.apache.tools.ant.taskdefs.optional.extension.Compatibility;
import org.apache.tools.ant.util.DeweyDecimal;

public final class Extension {
    public static final Attributes.Name EXTENSION_LIST = new Attributes.Name("Extension-List");
    public static final Attributes.Name OPTIONAL_EXTENSION_LIST = new Attributes.Name("Optional-Extension-List");
    public static final Attributes.Name EXTENSION_NAME = new Attributes.Name("Extension-Name");
    public static final Attributes.Name SPECIFICATION_VERSION = Attributes.Name.SPECIFICATION_VERSION;
    public static final Attributes.Name SPECIFICATION_VENDOR = Attributes.Name.SPECIFICATION_VENDOR;
    public static final Attributes.Name IMPLEMENTATION_VERSION = Attributes.Name.IMPLEMENTATION_VERSION;
    public static final Attributes.Name IMPLEMENTATION_VENDOR = Attributes.Name.IMPLEMENTATION_VENDOR;
    public static final Attributes.Name IMPLEMENTATION_URL = new Attributes.Name("Implementation-URL");
    public static final Attributes.Name IMPLEMENTATION_VENDOR_ID = new Attributes.Name("Implementation-Vendor-Id");
    public static final Compatibility COMPATIBLE = new Compatibility("COMPATIBLE");
    public static final Compatibility REQUIRE_SPECIFICATION_UPGRADE = new Compatibility("REQUIRE_SPECIFICATION_UPGRADE");
    public static final Compatibility REQUIRE_VENDOR_SWITCH = new Compatibility("REQUIRE_VENDOR_SWITCH");
    public static final Compatibility REQUIRE_IMPLEMENTATION_UPGRADE = new Compatibility("REQUIRE_IMPLEMENTATION_UPGRADE");
    public static final Compatibility INCOMPATIBLE = new Compatibility("INCOMPATIBLE");
    private String extensionName;
    private DeweyDecimal specificationVersion;
    private String specificationVendor;
    private String implementationVendorID;
    private String implementationVendor;
    private DeweyDecimal implementationVersion;
    private String implementationURL;

    public static Extension[] getAvailable(Manifest manifest) {
        if (null == manifest) {
            return new Extension[0];
        }
        return (Extension[])Stream.concat(Optional.ofNullable(manifest.getMainAttributes()).map(Stream::of).orElse(Stream.empty()), manifest.getEntries().values().stream()).map(attrs -> Extension.getExtension("", attrs)).filter(Objects::nonNull).toArray(Extension[]::new);
    }

    public static Extension[] getRequired(Manifest manifest) {
        return Extension.getListed(manifest, Attributes.Name.EXTENSION_LIST);
    }

    public static Extension[] getOptions(Manifest manifest) {
        return Extension.getListed(manifest, OPTIONAL_EXTENSION_LIST);
    }

    public static void addExtension(Extension extension, Attributes attributes) {
        Extension.addExtension(extension, "", attributes);
    }

    public static void addExtension(Extension extension, String prefix, Attributes attributes) {
        String implementationURL;
        DeweyDecimal implementationVersion;
        String implementationVendor;
        String implementationVendorID;
        DeweyDecimal specificationVersion;
        attributes.putValue(prefix + EXTENSION_NAME, extension.getExtensionName());
        String specificationVendor = extension.getSpecificationVendor();
        if (null != specificationVendor) {
            attributes.putValue(prefix + SPECIFICATION_VENDOR, specificationVendor);
        }
        if (null != (specificationVersion = extension.getSpecificationVersion())) {
            attributes.putValue(prefix + SPECIFICATION_VERSION, specificationVersion.toString());
        }
        if (null != (implementationVendorID = extension.getImplementationVendorID())) {
            attributes.putValue(prefix + IMPLEMENTATION_VENDOR_ID, implementationVendorID);
        }
        if (null != (implementationVendor = extension.getImplementationVendor())) {
            attributes.putValue(prefix + IMPLEMENTATION_VENDOR, implementationVendor);
        }
        if (null != (implementationVersion = extension.getImplementationVersion())) {
            attributes.putValue(prefix + IMPLEMENTATION_VERSION, implementationVersion.toString());
        }
        if (null != (implementationURL = extension.getImplementationURL())) {
            attributes.putValue(prefix + IMPLEMENTATION_URL, implementationURL);
        }
    }

    public Extension(String extensionName, String specificationVersion, String specificationVendor, String implementationVersion, String implementationVendor, String implementationVendorId, String implementationURL) {
        this.extensionName = extensionName;
        this.specificationVendor = specificationVendor;
        if (null != specificationVersion) {
            try {
                this.specificationVersion = new DeweyDecimal(specificationVersion);
            }
            catch (NumberFormatException nfe) {
                String error = "Bad specification version format '" + specificationVersion + "' in '" + extensionName + "'. (Reason: " + nfe + ")";
                throw new IllegalArgumentException(error);
            }
        }
        this.implementationURL = implementationURL;
        this.implementationVendor = implementationVendor;
        this.implementationVendorID = implementationVendorId;
        if (null != implementationVersion) {
            try {
                this.implementationVersion = new DeweyDecimal(implementationVersion);
            }
            catch (NumberFormatException nfe) {
                String error = "Bad implementation version format '" + implementationVersion + "' in '" + extensionName + "'. (Reason: " + nfe + ")";
                throw new IllegalArgumentException(error);
            }
        }
        if (null == this.extensionName) {
            throw new NullPointerException("extensionName property is null");
        }
    }

    public String getExtensionName() {
        return this.extensionName;
    }

    public String getSpecificationVendor() {
        return this.specificationVendor;
    }

    public DeweyDecimal getSpecificationVersion() {
        return this.specificationVersion;
    }

    public String getImplementationURL() {
        return this.implementationURL;
    }

    public String getImplementationVendor() {
        return this.implementationVendor;
    }

    public String getImplementationVendorID() {
        return this.implementationVendorID;
    }

    public DeweyDecimal getImplementationVersion() {
        return this.implementationVersion;
    }

    public Compatibility getCompatibilityWith(Extension required) {
        if (!this.extensionName.equals(required.getExtensionName())) {
            return INCOMPATIBLE;
        }
        DeweyDecimal requiredSpecificationVersion = required.getSpecificationVersion();
        if (!(null == requiredSpecificationVersion || null != this.specificationVersion && this.isCompatible(this.specificationVersion, requiredSpecificationVersion))) {
            return REQUIRE_SPECIFICATION_UPGRADE;
        }
        String requiredImplementationVendorID = required.getImplementationVendorID();
        if (!(null == requiredImplementationVendorID || null != this.implementationVendorID && this.implementationVendorID.equals(requiredImplementationVendorID))) {
            return REQUIRE_VENDOR_SWITCH;
        }
        DeweyDecimal requiredImplementationVersion = required.getImplementationVersion();
        if (!(null == requiredImplementationVersion || null != this.implementationVersion && this.isCompatible(this.implementationVersion, requiredImplementationVersion))) {
            return REQUIRE_IMPLEMENTATION_UPGRADE;
        }
        return COMPATIBLE;
    }

    public boolean isCompatibleWith(Extension required) {
        return COMPATIBLE == this.getCompatibilityWith(required);
    }

    public String toString() {
        String format = "%s: %s%n";
        StringBuilder sb = new StringBuilder(String.format("%s: %s%n", EXTENSION_NAME, this.extensionName));
        if (null != this.specificationVersion) {
            sb.append(String.format("%s: %s%n", SPECIFICATION_VERSION, this.specificationVersion));
        }
        if (null != this.specificationVendor) {
            sb.append(String.format("%s: %s%n", SPECIFICATION_VENDOR, this.specificationVendor));
        }
        if (null != this.implementationVersion) {
            sb.append(String.format("%s: %s%n", IMPLEMENTATION_VERSION, this.implementationVersion));
        }
        if (null != this.implementationVendorID) {
            sb.append(String.format("%s: %s%n", IMPLEMENTATION_VENDOR_ID, this.implementationVendorID));
        }
        if (null != this.implementationVendor) {
            sb.append(String.format("%s: %s%n", IMPLEMENTATION_VENDOR, this.implementationVendor));
        }
        if (null != this.implementationURL) {
            sb.append(String.format("%s: %s%n", IMPLEMENTATION_URL, this.implementationURL));
        }
        return sb.toString();
    }

    private boolean isCompatible(DeweyDecimal first, DeweyDecimal second) {
        return first.isGreaterThanOrEqual(second);
    }

    private static Extension[] getListed(Manifest manifest, Attributes.Name listKey) {
        ArrayList<Extension> results = new ArrayList<Extension>();
        Attributes mainAttributes = manifest.getMainAttributes();
        if (null != mainAttributes) {
            Extension.getExtension(mainAttributes, results, listKey);
        }
        manifest.getEntries().values().forEach(attributes -> Extension.getExtension(attributes, results, listKey));
        return results.toArray(new Extension[0]);
    }

    private static void getExtension(Attributes attributes, List<Extension> required, Attributes.Name listKey) {
        String names = attributes.getValue(listKey);
        if (null == names) {
            return;
        }
        for (String prefix : Extension.split(names, " ")) {
            Extension extension = Extension.getExtension(prefix + "-", attributes);
            if (null == extension) continue;
            required.add(extension);
        }
    }

    private static String[] split(String string, String onToken) {
        StringTokenizer tokenizer = new StringTokenizer(string, onToken);
        String[] result = new String[tokenizer.countTokens()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = tokenizer.nextToken();
        }
        return result;
    }

    private static Extension getExtension(String prefix, Attributes attributes) {
        String nameKey = prefix + EXTENSION_NAME;
        String name = Extension.getTrimmedString(attributes.getValue(nameKey));
        if (null == name) {
            return null;
        }
        String specVendorKey = prefix + SPECIFICATION_VENDOR;
        String specVendor = Extension.getTrimmedString(attributes.getValue(specVendorKey));
        String specVersionKey = prefix + SPECIFICATION_VERSION;
        String specVersion = Extension.getTrimmedString(attributes.getValue(specVersionKey));
        String impVersionKey = prefix + IMPLEMENTATION_VERSION;
        String impVersion = Extension.getTrimmedString(attributes.getValue(impVersionKey));
        String impVendorKey = prefix + IMPLEMENTATION_VENDOR;
        String impVendor = Extension.getTrimmedString(attributes.getValue(impVendorKey));
        String impVendorIDKey = prefix + IMPLEMENTATION_VENDOR_ID;
        String impVendorId = Extension.getTrimmedString(attributes.getValue(impVendorIDKey));
        String impURLKey = prefix + IMPLEMENTATION_URL;
        String impURL = Extension.getTrimmedString(attributes.getValue(impURLKey));
        return new Extension(name, specVersion, specVendor, impVersion, impVendor, impVendorId, impURL);
    }

    private static String getTrimmedString(String value) {
        return null == value ? null : value.trim();
    }
}

