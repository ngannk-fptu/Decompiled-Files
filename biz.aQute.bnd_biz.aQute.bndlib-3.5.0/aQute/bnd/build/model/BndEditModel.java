/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.build.model.EE;
import aQute.bnd.build.model.clauses.ExportedPackage;
import aQute.bnd.build.model.clauses.HeaderClause;
import aQute.bnd.build.model.clauses.ImportPattern;
import aQute.bnd.build.model.clauses.ServiceComponent;
import aQute.bnd.build.model.clauses.VersionedClause;
import aQute.bnd.build.model.conversions.CollectionFormatter;
import aQute.bnd.build.model.conversions.Converter;
import aQute.bnd.build.model.conversions.DefaultBooleanFormatter;
import aQute.bnd.build.model.conversions.DefaultFormatter;
import aQute.bnd.build.model.conversions.EEConverter;
import aQute.bnd.build.model.conversions.EEFormatter;
import aQute.bnd.build.model.conversions.HeaderClauseFormatter;
import aQute.bnd.build.model.conversions.HeaderClauseListConverter;
import aQute.bnd.build.model.conversions.MapFormatter;
import aQute.bnd.build.model.conversions.NewlineEscapedStringFormatter;
import aQute.bnd.build.model.conversions.NoopConverter;
import aQute.bnd.build.model.conversions.PropertiesConverter;
import aQute.bnd.build.model.conversions.PropertiesEntryFormatter;
import aQute.bnd.build.model.conversions.RequirementFormatter;
import aQute.bnd.build.model.conversions.RequirementListConverter;
import aQute.bnd.build.model.conversions.SimpleListConverter;
import aQute.bnd.build.model.conversions.VersionedClauseConverter;
import aQute.bnd.osgi.Processor;
import aQute.bnd.properties.IDocument;
import aQute.bnd.properties.IRegion;
import aQute.bnd.properties.LineType;
import aQute.bnd.properties.PropertiesLineReader;
import aQute.bnd.version.Version;
import aQute.lib.io.IO;
import aQute.lib.utf8properties.UTF8Properties;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.osgi.resource.Requirement;

public class BndEditModel {
    public static final String NEWLINE_LINE_SEPARATOR = "\\n\\\n\t";
    public static final String LIST_SEPARATOR = ",\\\n\t";
    private static String[] KNOWN_PROPERTIES = new String[]{"Bundle-License", "Bundle-Category", "Bundle-Name", "Bundle-Description", "Bundle-Copyright", "Bundle-UpdateLocation", "Bundle-Vendor", "Bundle-ContactAddress", "Bundle-DocURL", "Bundle-SymbolicName", "Bundle-Version", "Bundle-Activator", "Export-Package", "Import-Package", "Private-Package", "-sources", "Service-Component", "-classpath", "-buildpath", "-runbundles", "-runproperties", "-sub", "-runframework", "-runfw", "-runvm", "-runprogramargs", "-distro", "Test-Cases", "-plugin", "-pluginpath", "-runrepos", "-runrequires", "-runee", "-runblacklist", "Bundle-Blueprint", "Include-Resource", "-standalone"};
    public static final String PROP_WORKSPACE = "_workspace";
    public static final String BUNDLE_VERSION_MACRO = "${Bundle-Version}";
    private final Map<String, Converter<? extends Object, String>> converters = new HashMap<String, Converter<? extends Object, String>>();
    private final Map<String, Converter<String, ? extends Object>> formatters = new HashMap<String, Converter<String, ? extends Object>>();
    private File bndResource;
    private String bndResourceName;
    private final PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
    private Properties properties = new UTF8Properties();
    private final Map<String, Object> objectProperties = new HashMap<String, Object>();
    private final Map<String, String> changesToSave = new TreeMap<String, String>();
    private Project project;
    private Converter<List<VersionedClause>, String> buildPathConverter = new HeaderClauseListConverter<VersionedClause>(new Converter<VersionedClause, HeaderClause>(){

        @Override
        public VersionedClause convert(HeaderClause input) throws IllegalArgumentException {
            if (input == null) {
                return null;
            }
            return new VersionedClause(input.getName(), input.getAttribs());
        }

        @Override
        public VersionedClause error(String msg) {
            return null;
        }
    });
    private Converter<List<VersionedClause>, String> buildPackagesConverter = new HeaderClauseListConverter<VersionedClause>(new Converter<VersionedClause, HeaderClause>(){

        @Override
        public VersionedClause convert(HeaderClause input) throws IllegalArgumentException {
            if (input == null) {
                return null;
            }
            return new VersionedClause(input.getName(), input.getAttribs());
        }

        @Override
        public VersionedClause error(String msg) {
            return VersionedClause.error(msg);
        }
    });
    private Converter<List<VersionedClause>, String> clauseListConverter = new HeaderClauseListConverter<VersionedClause>(new VersionedClauseConverter());
    private Converter<String, String> stringConverter = new NoopConverter<String>();
    private Converter<Boolean, String> includedSourcesConverter = new Converter<Boolean, String>(){

        @Override
        public Boolean convert(String string) throws IllegalArgumentException {
            return Boolean.valueOf(string);
        }

        @Override
        public Boolean error(String msg) {
            return Boolean.FALSE;
        }
    };
    private Converter<List<String>, String> listConverter = SimpleListConverter.create();
    private Converter<List<HeaderClause>, String> headerClauseListConverter = new HeaderClauseListConverter<HeaderClause>(new NoopConverter());
    private Converter<List<ExportedPackage>, String> exportPackageConverter = new HeaderClauseListConverter<ExportedPackage>(new Converter<ExportedPackage, HeaderClause>(){

        @Override
        public ExportedPackage convert(HeaderClause input) {
            if (input == null) {
                return null;
            }
            return new ExportedPackage(input.getName(), input.getAttribs());
        }

        @Override
        public ExportedPackage error(String msg) {
            return ExportedPackage.error(msg);
        }
    });
    private Converter<List<ServiceComponent>, String> serviceComponentConverter = new HeaderClauseListConverter<ServiceComponent>(new Converter<ServiceComponent, HeaderClause>(){

        @Override
        public ServiceComponent convert(HeaderClause input) throws IllegalArgumentException {
            if (input == null) {
                return null;
            }
            return new ServiceComponent(input.getName(), input.getAttribs());
        }

        @Override
        public ServiceComponent error(String msg) {
            return ServiceComponent.error(msg);
        }
    });
    private Converter<List<ImportPattern>, String> importPatternConverter = new HeaderClauseListConverter<ImportPattern>(new Converter<ImportPattern, HeaderClause>(){

        @Override
        public ImportPattern convert(HeaderClause input) throws IllegalArgumentException {
            if (input == null) {
                return null;
            }
            return new ImportPattern(input.getName(), input.getAttribs());
        }

        @Override
        public ImportPattern error(String msg) {
            return ImportPattern.error(msg);
        }
    });
    private Converter<Map<String, String>, String> propertiesConverter = new PropertiesConverter();
    private Converter<List<Requirement>, String> requirementListConverter = new RequirementListConverter();
    private Converter<EE, String> eeConverter = new EEConverter();
    private Converter<String, String> newlineEscapeFormatter = new NewlineEscapedStringFormatter();
    private Converter<String, Boolean> defaultFalseBoolFormatter = new DefaultBooleanFormatter(false);
    private Converter<String, Collection<?>> stringListFormatter = new CollectionFormatter(",\\\n\t", (String)null);
    private Converter<String, Collection<? extends HeaderClause>> headerClauseListFormatter = new CollectionFormatter<HeaderClause>(",\\\n\t", new HeaderClauseFormatter(), null);
    private Converter<String, Map<String, String>> propertiesFormatter = new MapFormatter(",\\\n\t", new PropertiesEntryFormatter(), null);
    private Converter<String, Collection<? extends Requirement>> requirementListFormatter = new CollectionFormatter<Requirement>(",\\\n\t", new RequirementFormatter(), null);
    private Converter<String, Collection<? extends HeaderClause>> standaloneLinkListFormatter = new CollectionFormatter<HeaderClause>(",\\\n\t", new HeaderClauseFormatter(), "");
    private Converter<String, EE> eeFormatter = new EEFormatter();
    private Converter<String, Collection<? extends String>> runReposFormatter = new CollectionFormatter<String>(",\\\n\t", "<<EMPTY>>");
    private Workspace workspace;

    public BndEditModel() {
        this.converters.put("Bundle-License", this.stringConverter);
        this.converters.put("Bundle-Category", this.stringConverter);
        this.converters.put("Bundle-Name", this.stringConverter);
        this.converters.put("Bundle-Description", this.stringConverter);
        this.converters.put("Bundle-Copyright", this.stringConverter);
        this.converters.put("Bundle-UpdateLocation", this.stringConverter);
        this.converters.put("Bundle-Vendor", this.stringConverter);
        this.converters.put("Bundle-ContactAddress", this.stringConverter);
        this.converters.put("Bundle-DocURL", this.stringConverter);
        this.converters.put("-buildpath", this.buildPathConverter);
        this.converters.put("-buildpackages", this.buildPackagesConverter);
        this.converters.put("-runbundles", this.clauseListConverter);
        this.converters.put("Bundle-SymbolicName", this.stringConverter);
        this.converters.put("Bundle-Version", this.stringConverter);
        this.converters.put("Bundle-Activator", this.stringConverter);
        this.converters.put("-output", this.stringConverter);
        this.converters.put("-sources", this.includedSourcesConverter);
        this.converters.put("Private-Package", this.listConverter);
        this.converters.put("-classpath", this.listConverter);
        this.converters.put("Export-Package", this.exportPackageConverter);
        this.converters.put("Service-Component", this.serviceComponentConverter);
        this.converters.put("Import-Package", this.importPatternConverter);
        this.converters.put("-runframework", this.stringConverter);
        this.converters.put("-runfw", this.stringConverter);
        this.converters.put("-sub", this.listConverter);
        this.converters.put("-runproperties", this.propertiesConverter);
        this.converters.put("-runvm", this.stringConverter);
        this.converters.put("-runprogramargs", this.stringConverter);
        this.converters.put("Test-Suites", this.listConverter);
        this.converters.put("Test-Cases", this.listConverter);
        this.converters.put("-plugin", this.headerClauseListConverter);
        this.converters.put("-runrequires", this.requirementListConverter);
        this.converters.put("-runee", this.eeConverter);
        this.converters.put("-runrepos", this.listConverter);
        this.converters.put("Bundle-Blueprint", this.headerClauseListConverter);
        this.converters.put("Include-Resource", this.listConverter);
        this.converters.put("-standalone", this.headerClauseListConverter);
        this.formatters.put("Bundle-License", this.newlineEscapeFormatter);
        this.formatters.put("Bundle-Category", this.newlineEscapeFormatter);
        this.formatters.put("Bundle-Name", this.newlineEscapeFormatter);
        this.formatters.put("Bundle-Description", this.newlineEscapeFormatter);
        this.formatters.put("Bundle-Copyright", this.newlineEscapeFormatter);
        this.formatters.put("Bundle-UpdateLocation", this.newlineEscapeFormatter);
        this.formatters.put("Bundle-Vendor", this.newlineEscapeFormatter);
        this.formatters.put("Bundle-ContactAddress", this.newlineEscapeFormatter);
        this.formatters.put("Bundle-DocURL", this.newlineEscapeFormatter);
        this.formatters.put("-buildpath", this.headerClauseListFormatter);
        this.formatters.put("-buildpackages", this.headerClauseListFormatter);
        this.formatters.put("-runbundles", this.headerClauseListFormatter);
        this.formatters.put("Bundle-SymbolicName", this.newlineEscapeFormatter);
        this.formatters.put("Bundle-Version", this.newlineEscapeFormatter);
        this.formatters.put("Bundle-Activator", this.newlineEscapeFormatter);
        this.formatters.put("-output", this.newlineEscapeFormatter);
        this.formatters.put("-sources", this.defaultFalseBoolFormatter);
        this.formatters.put("Private-Package", this.stringListFormatter);
        this.formatters.put("-classpath", this.stringListFormatter);
        this.formatters.put("Export-Package", this.headerClauseListFormatter);
        this.formatters.put("Service-Component", this.headerClauseListFormatter);
        this.formatters.put("Import-Package", this.headerClauseListFormatter);
        this.formatters.put("-runframework", this.newlineEscapeFormatter);
        this.formatters.put("-runfw", this.newlineEscapeFormatter);
        this.formatters.put("-sub", this.stringListFormatter);
        this.formatters.put("-runproperties", this.propertiesFormatter);
        this.formatters.put("-runvm", this.newlineEscapeFormatter);
        this.formatters.put("-runprogramargs", this.newlineEscapeFormatter);
        this.formatters.put("Test-Cases", this.stringListFormatter);
        this.formatters.put("-plugin", this.headerClauseListFormatter);
        this.formatters.put("-runrequires", this.requirementListFormatter);
        this.formatters.put("-runee", this.eeFormatter);
        this.formatters.put("-runrepos", this.runReposFormatter);
        this.formatters.put("Bundle-Blueprint", this.headerClauseListFormatter);
        this.formatters.put("Include-Resource", this.stringListFormatter);
        this.formatters.put("-standalone", this.standaloneLinkListFormatter);
    }

    public BndEditModel(BndEditModel model) {
        this();
        this.bndResource = model.bndResource;
        this.workspace = model.workspace;
        this.properties.putAll((Map<?, ?>)model.properties);
        this.changesToSave.putAll(model.changesToSave);
    }

    public BndEditModel(Workspace workspace) {
        this();
        this.workspace = workspace;
    }

    public void loadFrom(IDocument document) throws IOException {
        try (InputStream in = this.toEscaped(document.get());){
            this.loadFrom(in);
        }
    }

    public InputStream toEscaped(String text) throws IOException {
        int c;
        StringReader unicode = new StringReader(text);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        while ((c = unicode.read()) >= 0) {
            if (c >= 127) {
                bout.write(String.format("\\u%04X", c).getBytes());
                continue;
            }
            bout.write((char)c);
        }
        return new ByteArrayInputStream(bout.toByteArray());
    }

    public InputStream toAsciiStream(IDocument doc) throws IOException {
        this.saveChangesTo(doc);
        return this.toEscaped(doc.get());
    }

    public void loadFrom(File file) throws IOException {
        this.loadFrom(IO.stream(file));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadFrom(InputStream inputStream) throws IOException {
        try {
            if (this.workspace != null) {
                this.properties = (Properties)this.workspace.getProperties().clone();
            } else {
                this.properties.clear();
            }
            this.properties.load(inputStream);
            this.objectProperties.clear();
            this.changesToSave.clear();
            for (String prop : KNOWN_PROPERTIES) {
                this.propChangeSupport.firePropertyChange(prop, null, null);
            }
        }
        finally {
            inputStream.close();
        }
    }

    public void saveChangesTo(IDocument document) {
        Iterator<Map.Entry<String, String>> iter = this.changesToSave.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String propertyName = entry.getKey();
            String stringValue = entry.getValue();
            BndEditModel.updateDocument(document, propertyName, stringValue);
            String value = this.cleanup(stringValue);
            if (value == null) {
                value = "";
            }
            if (propertyName != null) {
                this.properties.setProperty(propertyName, value);
            }
            iter.remove();
        }
    }

    private static IRegion findEntry(IDocument document, String name) throws Exception {
        PropertiesLineReader reader = new PropertiesLineReader(document);
        LineType type = reader.next();
        while (type != LineType.eof) {
            String key;
            if (type == LineType.entry && name.equals(key = reader.key())) {
                return reader.region();
            }
            type = reader.next();
        }
        return null;
    }

    private static void updateDocument(IDocument document, String name, String value) {
        String newEntry;
        if (value != null) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(name).append(": ").append(value);
            newEntry = buffer.toString();
        } else {
            newEntry = "";
        }
        try {
            IRegion region = BndEditModel.findEntry(document, name);
            if (region != null) {
                int offset = region.getOffset();
                int length = region.getLength();
                if (newEntry.length() == 0 && offset + length + 1 < document.getLength()) {
                    ++length;
                }
                document.replace(offset, length, newEntry);
            } else if (newEntry.length() > 0) {
                if (document.getLength() > 0 && document.getChar(document.getLength() - 1) != '\n') {
                    newEntry = "\n" + newEntry;
                }
                document.replace(document.getLength(), 0, newEntry);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllPropertyNames() {
        ArrayList<String> result = new ArrayList<String>(this.properties.size());
        Enumeration<?> names = this.properties.propertyNames();
        while (names.hasMoreElements()) {
            result.add((String)names.nextElement());
        }
        return result;
    }

    public Converter<Object, String> lookupConverter(String propertyName) {
        Converter<Object, String> converter = this.converters.get(propertyName);
        return converter;
    }

    public Converter<String, Object> lookupFormatter(String propertyName) {
        Converter<String, Object> formatter = this.formatters.get(propertyName);
        return formatter;
    }

    public Object genericGet(String propertyName) {
        Converter<? extends Object, String> converter = this.converters.get(propertyName);
        if (converter == null) {
            converter = new NoopConverter<String>();
        }
        return this.doGetObject(propertyName, converter);
    }

    public void genericSet(String propertyName, Object value) {
        Object oldValue = this.genericGet(propertyName);
        DefaultFormatter formatter = this.formatters.get(propertyName);
        if (formatter == null) {
            formatter = new DefaultFormatter();
        }
        this.doSetObject(propertyName, oldValue, value, formatter);
    }

    public String getBundleLicense() {
        return this.doGetObject("Bundle-License", this.stringConverter);
    }

    public void setBundleLicense(String bundleLicense) {
        this.doSetObject("Bundle-License", this.getBundleLicense(), bundleLicense, this.newlineEscapeFormatter);
    }

    public String getBundleCategory() {
        return this.doGetObject("Bundle-Category", this.stringConverter);
    }

    public void setBundleCategory(String bundleCategory) {
        this.doSetObject("Bundle-Category", this.getBundleCategory(), bundleCategory, this.newlineEscapeFormatter);
    }

    public String getBundleName() {
        return this.doGetObject("Bundle-Name", this.stringConverter);
    }

    public void setBundleName(String bundleName) {
        this.doSetObject("Bundle-Name", this.getBundleName(), bundleName, this.newlineEscapeFormatter);
    }

    public String getBundleDescription() {
        return this.doGetObject("Bundle-Description", this.stringConverter);
    }

    public void setBundleDescription(String bundleDescription) {
        this.doSetObject("Bundle-Description", this.getBundleDescription(), bundleDescription, this.newlineEscapeFormatter);
    }

    public String getBundleCopyright() {
        return this.doGetObject("Bundle-Copyright", this.stringConverter);
    }

    public void setBundleCopyright(String bundleCopyright) {
        this.doSetObject("Bundle-Copyright", this.getBundleCopyright(), bundleCopyright, this.newlineEscapeFormatter);
    }

    public String getBundleUpdateLocation() {
        return this.doGetObject("Bundle-UpdateLocation", this.stringConverter);
    }

    public void setBundleUpdateLocation(String bundleUpdateLocation) {
        this.doSetObject("Bundle-UpdateLocation", this.getBundleUpdateLocation(), bundleUpdateLocation, this.newlineEscapeFormatter);
    }

    public String getBundleVendor() {
        return this.doGetObject("Bundle-Vendor", this.stringConverter);
    }

    public void setBundleVendor(String bundleVendor) {
        this.doSetObject("Bundle-Vendor", this.getBundleVendor(), bundleVendor, this.newlineEscapeFormatter);
    }

    public String getBundleContactAddress() {
        return this.doGetObject("Bundle-ContactAddress", this.stringConverter);
    }

    public void setBundleContactAddress(String bundleContactAddress) {
        this.doSetObject("Bundle-ContactAddress", this.getBundleContactAddress(), bundleContactAddress, this.newlineEscapeFormatter);
    }

    public String getBundleDocUrl() {
        return this.doGetObject("Bundle-DocURL", this.stringConverter);
    }

    public void setBundleDocUrl(String bundleDocUrl) {
        this.doSetObject("Bundle-DocURL", this.getBundleDocUrl(), bundleDocUrl, this.newlineEscapeFormatter);
    }

    public String getBundleSymbolicName() {
        return this.doGetObject("Bundle-SymbolicName", this.stringConverter);
    }

    public void setBundleSymbolicName(String bundleSymbolicName) {
        this.doSetObject("Bundle-SymbolicName", this.getBundleSymbolicName(), bundleSymbolicName, this.newlineEscapeFormatter);
    }

    public String getBundleVersionString() {
        return this.doGetObject("Bundle-Version", this.stringConverter);
    }

    public void setBundleVersion(String bundleVersion) {
        this.doSetObject("Bundle-Version", this.getBundleVersionString(), bundleVersion, this.newlineEscapeFormatter);
    }

    public String getBundleActivator() {
        return this.doGetObject("Bundle-Activator", this.stringConverter);
    }

    public void setBundleActivator(String bundleActivator) {
        this.doSetObject("Bundle-Activator", this.getBundleActivator(), bundleActivator, this.newlineEscapeFormatter);
    }

    public String getOutputFile() {
        return this.doGetObject("-output", this.stringConverter);
    }

    public void setOutputFile(String name) {
        this.doSetObject("-output", this.getOutputFile(), name, this.newlineEscapeFormatter);
    }

    public boolean isIncludeSources() {
        return this.doGetObject("-sources", this.includedSourcesConverter);
    }

    public void setIncludeSources(boolean includeSources) {
        boolean oldValue = this.isIncludeSources();
        this.doSetObject("-sources", oldValue, includeSources, this.defaultFalseBoolFormatter);
    }

    public List<String> getPrivatePackages() {
        return this.doGetObject("Private-Package", this.listConverter);
    }

    public void setPrivatePackages(List<? extends String> packages) {
        List<String> oldPackages = this.getPrivatePackages();
        this.doSetObject("Private-Package", oldPackages, packages, this.stringListFormatter);
    }

    public List<ExportedPackage> getSystemPackages() {
        return this.doGetObject("-runsystempackages", this.exportPackageConverter);
    }

    public void setSystemPackages(List<? extends ExportedPackage> packages) {
        List<ExportedPackage> oldPackages = this.getSystemPackages();
        this.doSetObject("-runsystempackages", oldPackages, packages, this.headerClauseListFormatter);
    }

    public List<String> getClassPath() {
        return this.doGetObject("-classpath", this.listConverter);
    }

    public void addPrivatePackage(String packageName) {
        List<String> packages = this.getPrivatePackages();
        packages = packages == null ? new ArrayList<String>() : new ArrayList<String>(packages);
        packages.add(packageName);
        this.setPrivatePackages(packages);
    }

    public void setClassPath(List<? extends String> classPath) {
        List<String> oldClassPath = this.getClassPath();
        this.doSetObject("-classpath", oldClassPath, classPath, this.stringListFormatter);
    }

    public List<ExportedPackage> getExportedPackages() {
        return this.doGetObject("Export-Package", this.exportPackageConverter);
    }

    public void setExportedPackages(List<? extends ExportedPackage> exports) {
        boolean referencesBundleVersion = false;
        if (exports != null) {
            for (ExportedPackage exportedPackage : exports) {
                String versionString = exportedPackage.getVersionString();
                if (versionString == null || versionString.indexOf(BUNDLE_VERSION_MACRO) <= -1) continue;
                referencesBundleVersion = true;
            }
        }
        List<ExportedPackage> oldValue = this.getExportedPackages();
        this.doSetObject("Export-Package", oldValue, exports, this.headerClauseListFormatter);
        if (referencesBundleVersion && this.getBundleVersionString() == null) {
            this.setBundleVersion(Version.emptyVersion.toString());
        }
    }

    public void addExportedPackage(ExportedPackage export) {
        List<ExportedPackage> exports = this.getExportedPackages();
        exports = exports == null ? new ArrayList<ExportedPackage>() : new ArrayList<ExportedPackage>(exports);
        exports.add(export);
        this.setExportedPackages(exports);
    }

    public List<String> getDSAnnotationPatterns() {
        return this.doGetObject("-dsannotations", this.listConverter);
    }

    public void setDSAnnotationPatterns(List<? extends String> patterns) {
        List<String> oldValue = this.getDSAnnotationPatterns();
        this.doSetObject("-dsannotations", oldValue, patterns, this.stringListFormatter);
    }

    public List<ServiceComponent> getServiceComponents() {
        return this.doGetObject("Service-Component", this.serviceComponentConverter);
    }

    public void setServiceComponents(List<? extends ServiceComponent> components) {
        List<ServiceComponent> oldValue = this.getServiceComponents();
        this.doSetObject("Service-Component", oldValue, components, this.headerClauseListFormatter);
    }

    public List<ImportPattern> getImportPatterns() {
        return this.doGetObject("Import-Package", this.importPatternConverter);
    }

    public void setImportPatterns(List<? extends ImportPattern> patterns) {
        List<ImportPattern> oldValue = this.getImportPatterns();
        this.doSetObject("Import-Package", oldValue, patterns, this.headerClauseListFormatter);
    }

    public List<VersionedClause> getBuildPath() {
        return this.doGetObject("-buildpath", this.buildPathConverter);
    }

    public void setBuildPath(List<? extends VersionedClause> paths) {
        List<VersionedClause> oldValue = this.getBuildPath();
        this.doSetObject("-buildpath", oldValue, paths, this.headerClauseListFormatter);
    }

    @Deprecated
    public List<VersionedClause> getBuildPackages() {
        return this.doGetObject("-buildpackages", this.buildPackagesConverter);
    }

    @Deprecated
    public void setBuildPackages(List<? extends VersionedClause> paths) {
        List<VersionedClause> oldValue = this.getBuildPackages();
        this.doSetObject("-buildpackages", oldValue, paths, this.headerClauseListFormatter);
    }

    public List<VersionedClause> getRunBundles() {
        return this.doGetObject("-runbundles", this.clauseListConverter);
    }

    public void setRunBundles(List<? extends VersionedClause> paths) {
        List<VersionedClause> oldValue = this.getRunBundles();
        this.doSetObject("-runbundles", oldValue, paths, this.headerClauseListFormatter);
    }

    public boolean isIncludedPackage(String packageName) {
        List<String> privatePackages = this.getPrivatePackages();
        if (privatePackages != null && privatePackages.contains(packageName)) {
            return true;
        }
        List<ExportedPackage> exportedPackages = this.getExportedPackages();
        if (exportedPackages != null) {
            for (ExportedPackage pkg : exportedPackages) {
                if (!packageName.equals(pkg.getName())) continue;
                return true;
            }
        }
        return false;
    }

    public List<String> getSubBndFiles() {
        return this.doGetObject("-sub", this.listConverter);
    }

    public void setSubBndFiles(List<String> subBndFiles) {
        List<String> oldValue = this.getSubBndFiles();
        this.doSetObject("-sub", oldValue, subBndFiles, this.stringListFormatter);
    }

    public Map<String, String> getRunProperties() {
        return this.doGetObject("-runproperties", this.propertiesConverter);
    }

    public void setRunProperties(Map<String, String> props) {
        Map<String, String> old = this.getRunProperties();
        this.doSetObject("-runproperties", old, props, this.propertiesFormatter);
    }

    public String getRunVMArgs() {
        return this.doGetObject("-runvm", this.stringConverter);
    }

    public void setRunVMArgs(String args) {
        String old = this.getRunVMArgs();
        this.doSetObject("-runvm", old, args, this.newlineEscapeFormatter);
    }

    public String getRunProgramArgs() {
        return this.doGetObject("-runprogramargs", this.stringConverter);
    }

    public void setRunProgramArgs(String args) {
        String old = this.getRunProgramArgs();
        this.doSetObject("-runprogramargs", old, args, this.newlineEscapeFormatter);
    }

    public List<String> getTestSuites() {
        List<String> testCases = this.doGetObject("Test-Cases", this.listConverter);
        testCases = testCases != null ? testCases : Collections.emptyList();
        List<String> testSuites = this.doGetObject("Test-Suites", this.listConverter);
        testSuites = testSuites != null ? testSuites : Collections.emptyList();
        ArrayList<String> result = new ArrayList<String>(testCases.size() + testSuites.size());
        result.addAll(testCases);
        result.addAll(testSuites);
        return result;
    }

    public void setTestSuites(List<String> suites) {
        List<String> old = this.getTestSuites();
        this.doSetObject("Test-Cases", old, suites, this.stringListFormatter);
        this.doSetObject("Test-Suites", null, null, this.stringListFormatter);
    }

    public List<HeaderClause> getPlugins() {
        return this.doGetObject("-plugin", this.headerClauseListConverter);
    }

    public void setPlugins(List<HeaderClause> plugins) {
        List<HeaderClause> old = this.getPlugins();
        this.doSetObject("-plugin", old, plugins, this.headerClauseListFormatter);
    }

    public List<String> getPluginPath() {
        return this.doGetObject("-pluginpath", this.listConverter);
    }

    public void setPluginPath(List<String> pluginPath) {
        List<String> old = this.getPluginPath();
        this.doSetObject("-pluginpath", old, pluginPath, this.stringListFormatter);
    }

    public List<String> getDistro() {
        return this.doGetObject("-distro", this.listConverter);
    }

    public void setDistro(List<String> distros) {
        List<String> old = this.getPluginPath();
        this.doSetObject("-distro", old, distros, this.stringListFormatter);
    }

    public List<String> getRunRepos() {
        return this.doGetObject("-runrepos", this.listConverter);
    }

    public void setRunRepos(List<String> repos) {
        List<String> old = this.getRunRepos();
        this.doSetObject("-runrepos", old, repos, this.runReposFormatter);
    }

    public String getRunFramework() {
        return this.doGetObject("-runframework", this.stringConverter);
    }

    public String getRunFw() {
        return this.doGetObject("-runfw", this.stringConverter);
    }

    public EE getEE() {
        return this.doGetObject("-runee", this.eeConverter);
    }

    public void setEE(EE ee) {
        EE old = this.getEE();
        this.doSetObject("-runee", old, ee, this.eeFormatter);
    }

    public void setRunFramework(String clause) {
        assert ("services".equals(clause.toLowerCase().trim()) || "none".equals(clause.toLowerCase().trim()));
        String oldValue = this.getRunFramework();
        this.doSetObject("-runframework", oldValue, clause, this.newlineEscapeFormatter);
    }

    public void setRunFw(String clause) {
        String oldValue = this.getRunFw();
        this.doSetObject("-runfw", oldValue, clause, this.newlineEscapeFormatter);
    }

    public List<Requirement> getRunRequires() {
        return this.doGetObject("-runrequires", this.requirementListConverter);
    }

    public void setRunRequires(List<Requirement> requires) {
        List<Requirement> oldValue = this.getRunRequires();
        this.doSetObject("-runrequires", oldValue, requires, this.requirementListFormatter);
    }

    public List<Requirement> getRunBlacklist() {
        return this.doGetObject("-runblacklist", this.requirementListConverter);
    }

    public void setRunBlacklist(List<Requirement> requires) {
        List<Requirement> oldValue = this.getRunBlacklist();
        this.doSetObject("-runblacklist", oldValue, requires, this.requirementListFormatter);
    }

    public List<HeaderClause> getStandaloneLinks() {
        return this.doGetObject("-standalone", this.headerClauseListConverter);
    }

    public void setStandaloneLinks(List<HeaderClause> headers) {
        List<HeaderClause> old = this.getStandaloneLinks();
        this.doSetObject("-standalone", old, headers, this.standaloneLinkListFormatter);
    }

    public List<HeaderClause> getIgnoreStandalone() {
        List<HeaderClause> v = this.doGetObject("-ignore-standalone", this.headerClauseListConverter);
        if (v != null) {
            return v;
        }
        v = this.doGetObject("x-ignore-standalone", this.headerClauseListConverter);
        if (v == null) {
            return null;
        }
        this.setIgnoreStandalone(v);
        this.doSetObject("x-ignore-standalone", v, null, this.standaloneLinkListFormatter);
        return this.doGetObject("-ignore-standalone", this.headerClauseListConverter);
    }

    public void setIgnoreStandalone(List<HeaderClause> headers) {
        List<HeaderClause> old = this.getIgnoreStandalone();
        this.doSetObject("-ignore-standalone", old, headers, this.standaloneLinkListFormatter);
    }

    private <R> R doGetObject(String name, Converter<? extends R, ? super String> converter) {
        try {
            Object result;
            if (this.objectProperties.containsKey(name)) {
                Object temp;
                result = temp = this.objectProperties.get(name);
            } else if (this.changesToSave.containsKey(name)) {
                result = converter.convert(this.changesToSave.get(name));
                this.objectProperties.put(name, result);
            } else if (this.properties.containsKey(name)) {
                result = converter.convert(this.properties.getProperty(name));
                this.objectProperties.put(name, result);
            } else {
                result = converter.convert(null);
            }
            return (R)result;
        }
        catch (Exception e) {
            return converter.error(e.getMessage());
        }
    }

    private <T> void doSetObject(String name, T oldValue, T newValue, Converter<String, ? super T> formatter) {
        this.objectProperties.put(name, newValue);
        String v = formatter.convert(newValue);
        this.changesToSave.put(name, v);
        this.propChangeSupport.firePropertyChange(name, oldValue, newValue);
    }

    public boolean isProjectFile() {
        return "bnd.bnd".equals(this.getBndResourceName());
    }

    public boolean isBndrun() {
        return this.getBndResourceName().endsWith(".bndrun");
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.propChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.propChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public void setBndResource(File bndResource) {
        this.bndResource = bndResource;
    }

    public File getBndResource() {
        return this.bndResource;
    }

    public String getBndResourceName() {
        if (this.bndResourceName == null) {
            return "";
        }
        return this.bndResourceName;
    }

    public void setBndResourceName(String bndResourceName) {
        this.bndResourceName = bndResourceName;
    }

    public List<HeaderClause> getBundleBlueprint() {
        return this.doGetObject("Bundle-Blueprint", this.headerClauseListConverter);
    }

    public void setBundleBlueprint(List<HeaderClause> bundleBlueprint) {
        List<HeaderClause> old = this.getPlugins();
        this.doSetObject("Bundle-Blueprint", old, bundleBlueprint, this.headerClauseListFormatter);
    }

    public void addBundleBlueprint(String location) {
        List<HeaderClause> bpLocations = this.getBundleBlueprint();
        bpLocations = bpLocations == null ? new ArrayList<HeaderClause>() : new ArrayList<HeaderClause>(bpLocations);
        bpLocations.add(new HeaderClause(location, null));
        this.setBundleBlueprint(bpLocations);
    }

    public List<String> getIncludeResource() {
        return this.doGetObject("Include-Resource", this.listConverter);
    }

    public void setIncludeResource(List<String> includeResource) {
        List<String> old = this.getIncludeResource();
        this.doSetObject("Include-Resource", old, includeResource, this.stringListFormatter);
    }

    public void addIncludeResource(String resource) {
        List<String> includeResource = this.getIncludeResource();
        includeResource = includeResource == null ? new ArrayList<String>() : new ArrayList<String>(includeResource);
        includeResource.add(resource);
        this.setIncludeResource(includeResource);
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }

    public Workspace getWorkspace() {
        return this.workspace;
    }

    public void setWorkspace(Workspace workspace) {
        Workspace old = this.workspace;
        this.workspace = workspace;
        this.propChangeSupport.firePropertyChange(PROP_WORKSPACE, old, workspace);
    }

    public String getGenericString(String name) {
        return this.doGetObject(name, this.stringConverter);
    }

    public void setGenericString(String name, String value) {
        this.doSetObject(name, this.getGenericString(name), value, this.stringConverter);
    }

    public Processor getProperties() throws Exception {
        Processor parent = null;
        if (this.isProjectFile() && this.project != null) {
            parent = this.project;
        } else if (this.getBndResource() != null && (parent = Workspace.getRun(this.getBndResource())) == null) {
            parent = new Processor();
            parent.setProperties(this.getBndResource(), this.getBndResource().getParentFile());
        }
        Processor result = parent == null ? new Processor() : new Processor(parent);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : this.changesToSave.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n\n");
        }
        UTF8Properties p = new UTF8Properties();
        p.load(new StringReader(sb.toString()));
        result.getProperties().putAll((Map<?, ?>)this.properties);
        result.getProperties().putAll((Map<?, ?>)p);
        return result;
    }

    private String cleanup(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\\\\n", "");
    }

    public Map<String, String> getDocumentChanges() {
        return this.changesToSave;
    }
}

