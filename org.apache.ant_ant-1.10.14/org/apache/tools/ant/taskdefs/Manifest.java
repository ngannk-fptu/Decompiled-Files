/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.util.StreamUtils;

public class Manifest {
    public static final String ATTRIBUTE_MANIFEST_VERSION = "Manifest-Version";
    public static final String ATTRIBUTE_SIGNATURE_VERSION = "Signature-Version";
    public static final String ATTRIBUTE_NAME = "Name";
    public static final String ATTRIBUTE_FROM = "From";
    public static final String ATTRIBUTE_CLASSPATH = "Class-Path";
    public static final String DEFAULT_MANIFEST_VERSION = "1.0";
    public static final int MAX_LINE_LENGTH = 72;
    public static final int MAX_SECTION_LENGTH = 70;
    public static final String EOL = "\r\n";
    public static final String ERROR_FROM_FORBIDDEN = "Manifest attributes should not start with \"From\" in \"";
    public static final Charset JAR_CHARSET = StandardCharsets.UTF_8;
    @Deprecated
    public static final String JAR_ENCODING = JAR_CHARSET.name();
    private static final String ATTRIBUTE_MANIFEST_VERSION_LC = "Manifest-Version".toLowerCase(Locale.ENGLISH);
    private static final String ATTRIBUTE_NAME_LC = "Name".toLowerCase(Locale.ENGLISH);
    private static final String ATTRIBUTE_FROM_LC = "From".toLowerCase(Locale.ENGLISH);
    private static final String ATTRIBUTE_CLASSPATH_LC = "Class-Path".toLowerCase(Locale.ENGLISH);
    private String manifestVersion = "1.0";
    private Section mainSection = new Section();
    private Map<String, Section> sections = new LinkedHashMap<String, Section>();

    public static Manifest getDefaultManifest() throws BuildException {
        Manifest manifest;
        block11: {
            String defManifest = "/org/apache/tools/ant/defaultManifest.mf";
            InputStream in = Manifest.class.getResourceAsStream(defManifest);
            try {
                if (in == null) {
                    throw new BuildException("Could not find default manifest: %s", defManifest);
                }
                Manifest defaultManifest = new Manifest(new InputStreamReader(in, JAR_CHARSET));
                String version = System.getProperty("java.runtime.version");
                if (version == null) {
                    version = System.getProperty("java.vm.version");
                }
                Attribute createdBy = new Attribute("Created-By", version + " (" + System.getProperty("java.vm.vendor") + ")");
                defaultManifest.getMainSection().storeAttribute(createdBy);
                manifest = defaultManifest;
                if (in == null) break block11;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (ManifestException e) {
                    throw new BuildException("Default manifest is invalid !!", e);
                }
                catch (IOException e) {
                    throw new BuildException("Unable to read default manifest", e);
                }
            }
            in.close();
        }
        return manifest;
    }

    public Manifest() {
        this.manifestVersion = null;
    }

    public Manifest(Reader r) throws ManifestException, IOException {
        String line;
        BufferedReader reader = new BufferedReader(r);
        String nextSectionName = this.mainSection.read(reader);
        String readManifestVersion = this.mainSection.getAttributeValue(ATTRIBUTE_MANIFEST_VERSION);
        if (readManifestVersion != null) {
            this.manifestVersion = readManifestVersion;
            this.mainSection.removeAttribute(ATTRIBUTE_MANIFEST_VERSION);
        }
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) continue;
            Section section = new Section();
            if (nextSectionName == null) {
                Attribute sectionName = new Attribute(line);
                if (!ATTRIBUTE_NAME.equalsIgnoreCase(sectionName.getName())) {
                    throw new ManifestException("Manifest sections should start with a \"Name\" attribute and not \"" + sectionName.getName() + "\"");
                }
                nextSectionName = sectionName.getValue();
            } else {
                Attribute firstAttribute = new Attribute(line);
                section.addAttributeAndCheck(firstAttribute);
            }
            section.setName(nextSectionName);
            nextSectionName = section.read(reader);
            this.addConfiguredSection(section);
        }
    }

    public void addConfiguredSection(Section section) throws ManifestException {
        String sectionName = section.getName();
        if (sectionName == null) {
            throw new BuildException("Sections must have a name");
        }
        this.sections.put(sectionName, section);
    }

    public void addConfiguredAttribute(Attribute attribute) throws ManifestException {
        if (attribute.getKey() == null || attribute.getValue() == null) {
            throw new BuildException("Attributes must have name and value");
        }
        if (ATTRIBUTE_MANIFEST_VERSION_LC.equals(attribute.getKey())) {
            this.manifestVersion = attribute.getValue();
        } else {
            this.mainSection.addConfiguredAttribute(attribute);
        }
    }

    public void merge(Manifest other) throws ManifestException {
        this.merge(other, false);
    }

    public void merge(Manifest other, boolean overwriteMain) throws ManifestException {
        this.merge(other, overwriteMain, false);
    }

    public void merge(Manifest other, boolean overwriteMain, boolean mergeClassPaths) throws ManifestException {
        if (other != null) {
            if (overwriteMain) {
                this.mainSection = (Section)other.mainSection.clone();
            } else {
                this.mainSection.merge(other.mainSection, mergeClassPaths);
            }
            if (other.manifestVersion != null) {
                this.manifestVersion = other.manifestVersion;
            }
            for (String sectionName : Collections.list(other.getSectionNames())) {
                Section ourSection = this.sections.get(sectionName);
                Section otherSection = other.sections.get(sectionName);
                if (ourSection == null) {
                    if (otherSection == null) continue;
                    this.addConfiguredSection((Section)otherSection.clone());
                    continue;
                }
                ourSection.merge(otherSection, mergeClassPaths);
            }
        }
    }

    public void write(PrintWriter writer) throws IOException {
        this.write(writer, false);
    }

    public void write(PrintWriter writer, boolean flatten) throws IOException {
        writer.print("Manifest-Version: " + this.manifestVersion + EOL);
        String signatureVersion = this.mainSection.getAttributeValue(ATTRIBUTE_SIGNATURE_VERSION);
        if (signatureVersion != null) {
            writer.print("Signature-Version: " + signatureVersion + EOL);
            this.mainSection.removeAttribute(ATTRIBUTE_SIGNATURE_VERSION);
        }
        this.mainSection.write(writer, flatten);
        if (signatureVersion != null) {
            try {
                Attribute svAttr = new Attribute(ATTRIBUTE_SIGNATURE_VERSION, signatureVersion);
                this.mainSection.addConfiguredAttribute(svAttr);
            }
            catch (ManifestException manifestException) {
                // empty catch block
            }
        }
        for (String sectionName : this.sections.keySet()) {
            Section section = this.getSection(sectionName);
            section.write(writer, flatten);
        }
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        try {
            this.write(new PrintWriter(sw));
        }
        catch (IOException e) {
            return "";
        }
        return sw.toString();
    }

    public Enumeration<String> getWarnings() {
        ArrayList<String> warnings = Collections.list(this.mainSection.getWarnings());
        this.sections.values().stream().map(section -> Collections.list(section.getWarnings())).forEach(warnings::addAll);
        return Collections.enumeration(warnings);
    }

    public int hashCode() {
        int hashCode = 0;
        if (this.manifestVersion != null) {
            hashCode += this.manifestVersion.hashCode();
        }
        hashCode += this.mainSection.hashCode();
        return hashCode += this.sections.hashCode();
    }

    public boolean equals(Object rhs) {
        if (rhs == null || rhs.getClass() != this.getClass()) {
            return false;
        }
        if (rhs == this) {
            return true;
        }
        Manifest rhsManifest = (Manifest)rhs;
        if (this.manifestVersion == null ? rhsManifest.manifestVersion != null : !this.manifestVersion.equals(rhsManifest.manifestVersion)) {
            return false;
        }
        return this.mainSection.equals(rhsManifest.mainSection) && this.sections.equals(rhsManifest.sections);
    }

    public String getManifestVersion() {
        return this.manifestVersion;
    }

    public Section getMainSection() {
        return this.mainSection;
    }

    public Section getSection(String name) {
        return this.sections.get(name);
    }

    public Enumeration<String> getSectionNames() {
        return Collections.enumeration(this.sections.keySet());
    }

    public static class Attribute {
        private static final int MAX_NAME_VALUE_LENGTH = 68;
        private static final int MAX_NAME_LENGTH = 70;
        private String name = null;
        private Vector<String> values = new Vector();
        private int currentIndex = 0;

        public Attribute() {
        }

        public Attribute(String line) throws ManifestException {
            this.parse(line);
        }

        public Attribute(String name, String value) {
            this.name = name;
            this.setValue(value);
        }

        public int hashCode() {
            return Objects.hash(this.getKey(), this.values);
        }

        public boolean equals(Object rhs) {
            if (rhs == null || rhs.getClass() != this.getClass()) {
                return false;
            }
            if (rhs == this) {
                return true;
            }
            Attribute rhsAttribute = (Attribute)rhs;
            String lhsKey = this.getKey();
            String rhsKey = rhsAttribute.getKey();
            return !(lhsKey == null && rhsKey != null || lhsKey != null && !lhsKey.equals(rhsKey) || !this.values.equals(rhsAttribute.values));
        }

        public void parse(String line) throws ManifestException {
            int index = line.indexOf(": ");
            if (index == -1) {
                throw new ManifestException("Manifest line \"" + line + "\" is not valid as it does not contain a name and a value separated by ': '");
            }
            this.name = line.substring(0, index);
            this.setValue(line.substring(index + 2));
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String getKey() {
            return this.name == null ? null : this.name.toLowerCase(Locale.ENGLISH);
        }

        public void setValue(String value) {
            if (this.currentIndex >= this.values.size()) {
                this.values.addElement(value);
                this.currentIndex = this.values.size() - 1;
            } else {
                this.values.setElementAt(value, this.currentIndex);
            }
        }

        public String getValue() {
            return this.values.isEmpty() ? null : String.join((CharSequence)" ", this.values);
        }

        public void addValue(String value) {
            ++this.currentIndex;
            this.setValue(value);
        }

        public Enumeration<String> getValues() {
            return this.values.elements();
        }

        public void addContinuation(String line) {
            this.setValue(this.values.elementAt(this.currentIndex) + line.substring(1));
        }

        public void write(PrintWriter writer) throws IOException {
            this.write(writer, false);
        }

        public void write(PrintWriter writer, boolean flatten) throws IOException {
            if (flatten) {
                this.writeValue(writer, this.getValue());
            } else {
                for (String value : this.values) {
                    this.writeValue(writer, value);
                }
            }
        }

        private void writeValue(PrintWriter writer, String value) throws IOException {
            String line;
            int nameLength = this.name.getBytes(JAR_CHARSET).length;
            if (nameLength > 68) {
                if (nameLength > 70) {
                    throw new IOException("Unable to write manifest line " + this.name + ": " + value);
                }
                writer.print(this.name + ": " + Manifest.EOL);
                line = " " + value;
            } else {
                line = this.name + ": " + value;
            }
            while (line.getBytes(JAR_CHARSET).length > 70) {
                int breakIndex = 70;
                if (breakIndex >= line.length()) {
                    breakIndex = line.length() - 1;
                }
                String section = line.substring(0, breakIndex);
                while (section.getBytes(JAR_CHARSET).length > 70 && breakIndex > 0) {
                    section = line.substring(0, --breakIndex);
                }
                if (breakIndex == 0) {
                    throw new IOException("Unable to write manifest line " + this.name + ": " + value);
                }
                writer.print(section + Manifest.EOL);
                line = " " + line.substring(breakIndex);
            }
            writer.print(line + Manifest.EOL);
        }
    }

    public static class Section {
        private List<String> warnings = new Vector<String>();
        private String name = null;
        private Map<String, Attribute> attributes = new LinkedHashMap<String, Attribute>();

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String read(BufferedReader reader) throws ManifestException, IOException {
            String nameReadAhead;
            Attribute attribute = null;
            while (true) {
                String line;
                if ((line = reader.readLine()) == null || line.isEmpty()) {
                    return null;
                }
                if (line.charAt(0) == ' ') {
                    if (attribute == null) {
                        if (this.name == null) {
                            throw new ManifestException("Can't start an attribute with a continuation line " + line);
                        }
                        this.name = this.name + line.substring(1);
                        continue;
                    }
                    attribute.addContinuation(line);
                    continue;
                }
                attribute = new Attribute(line);
                nameReadAhead = this.addAttributeAndCheck(attribute);
                attribute = this.getAttribute(attribute.getKey());
                if (nameReadAhead != null) break;
            }
            return nameReadAhead;
        }

        public void merge(Section section) throws ManifestException {
            this.merge(section, false);
        }

        public void merge(Section section, boolean mergeClassPaths) throws ManifestException {
            if (this.name == null && section.getName() != null || this.name != null && section.getName() != null && !this.name.toLowerCase(Locale.ENGLISH).equals(section.getName().toLowerCase(Locale.ENGLISH))) {
                throw new ManifestException("Unable to merge sections with different names");
            }
            Attribute classpathAttribute = null;
            for (String attributeName : Collections.list(section.getAttributeKeys())) {
                Attribute attribute = section.getAttribute(attributeName);
                if (Manifest.ATTRIBUTE_CLASSPATH.equalsIgnoreCase(attributeName)) {
                    if (classpathAttribute == null) {
                        classpathAttribute = new Attribute();
                        classpathAttribute.setName(Manifest.ATTRIBUTE_CLASSPATH);
                    }
                    Collections.list(attribute.getValues()).forEach(classpathAttribute::addValue);
                    continue;
                }
                this.storeAttribute(attribute);
            }
            if (classpathAttribute != null) {
                Attribute currentCp;
                if (mergeClassPaths && (currentCp = this.getAttribute(Manifest.ATTRIBUTE_CLASSPATH)) != null) {
                    Collections.list(currentCp.getValues()).forEach(classpathAttribute::addValue);
                }
                this.storeAttribute(classpathAttribute);
            }
            this.warnings.addAll(section.warnings);
        }

        public void write(PrintWriter writer) throws IOException {
            this.write(writer, false);
        }

        public void write(PrintWriter writer, boolean flatten) throws IOException {
            if (this.name != null) {
                Attribute nameAttr = new Attribute(Manifest.ATTRIBUTE_NAME, this.name);
                nameAttr.write(writer);
            }
            for (String key : Collections.list(this.getAttributeKeys())) {
                this.getAttribute(key).write(writer, flatten);
            }
            writer.print(Manifest.EOL);
        }

        public Attribute getAttribute(String attributeName) {
            return this.attributes.get(attributeName.toLowerCase(Locale.ENGLISH));
        }

        public Enumeration<String> getAttributeKeys() {
            return Collections.enumeration(this.attributes.keySet());
        }

        public String getAttributeValue(String attributeName) {
            Attribute attribute = this.getAttribute(attributeName.toLowerCase(Locale.ENGLISH));
            return attribute == null ? null : attribute.getValue();
        }

        public void removeAttribute(String attributeName) {
            String key = attributeName.toLowerCase(Locale.ENGLISH);
            this.attributes.remove(key);
        }

        public void addConfiguredAttribute(Attribute attribute) throws ManifestException {
            String check = this.addAttributeAndCheck(attribute);
            if (check != null) {
                throw new BuildException("Specify the section name using the \"name\" attribute of the <section> element rather than using a \"Name\" manifest attribute");
            }
        }

        public String addAttributeAndCheck(Attribute attribute) throws ManifestException {
            if (attribute.getName() == null || attribute.getValue() == null) {
                throw new BuildException("Attributes must have name and value");
            }
            String attributeKey = attribute.getKey();
            if (attributeKey.equals(ATTRIBUTE_NAME_LC)) {
                this.warnings.add("\"Name\" attributes should not occur in the main section and must be the first element in all other sections: \"" + attribute.getName() + ": " + attribute.getValue() + "\"");
                return attribute.getValue();
            }
            if (attributeKey.startsWith(ATTRIBUTE_FROM_LC)) {
                this.warnings.add(Manifest.ERROR_FROM_FORBIDDEN + attribute.getName() + ": " + attribute.getValue() + "\"");
            } else if (attributeKey.equals(ATTRIBUTE_CLASSPATH_LC)) {
                Attribute classpathAttribute = this.attributes.get(attributeKey);
                if (classpathAttribute == null) {
                    this.storeAttribute(attribute);
                } else {
                    this.warnings.add("Multiple Class-Path attributes are supported but violate the Jar specification and may not be correctly processed in all environments");
                    Collections.list(attribute.getValues()).forEach(classpathAttribute::addValue);
                }
            } else {
                if (this.attributes.containsKey(attributeKey)) {
                    throw new ManifestException("The attribute \"" + attribute.getName() + "\" may not occur more than once in the same section");
                }
                this.storeAttribute(attribute);
            }
            return null;
        }

        public Object clone() {
            Section cloned = new Section();
            cloned.setName(this.name);
            StreamUtils.enumerationAsStream(this.getAttributeKeys()).map(key -> new Attribute(this.getAttribute((String)key).getName(), this.getAttribute((String)key).getValue())).forEach(cloned::storeAttribute);
            return cloned;
        }

        private void storeAttribute(Attribute attribute) {
            if (attribute == null) {
                return;
            }
            this.attributes.put(attribute.getKey(), attribute);
        }

        public Enumeration<String> getWarnings() {
            return Collections.enumeration(this.warnings);
        }

        public int hashCode() {
            return this.attributes.hashCode();
        }

        public boolean equals(Object rhs) {
            if (rhs == null || rhs.getClass() != this.getClass()) {
                return false;
            }
            if (rhs == this) {
                return true;
            }
            Section rhsSection = (Section)rhs;
            return this.attributes.equals(rhsSection.attributes);
        }
    }
}

