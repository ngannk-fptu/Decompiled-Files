/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.BindingConfig;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SystemProperties;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ResolverUtil;
import org.apache.xmlbeans.impl.schema.SchemaAnnotationImpl;
import org.apache.xmlbeans.impl.schema.SchemaAttributeGroupImpl;
import org.apache.xmlbeans.impl.schema.SchemaContainer;
import org.apache.xmlbeans.impl.schema.SchemaDependencies;
import org.apache.xmlbeans.impl.schema.SchemaGlobalAttributeImpl;
import org.apache.xmlbeans.impl.schema.SchemaGlobalElementImpl;
import org.apache.xmlbeans.impl.schema.SchemaIdentityConstraintImpl;
import org.apache.xmlbeans.impl.schema.SchemaModelGroupImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;
import org.apache.xmlbeans.impl.schema.XmlValueRef;
import org.apache.xmlbeans.impl.util.HexBin;
import org.apache.xmlbeans.impl.values.XmlStringImpl;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.xml.sax.EntityResolver;

public class StscState {
    private static final XmlValueRef XMLSTR_PRESERVE = StscState.buildString("preserve");
    private static final XmlValueRef XMLSTR_REPLACE = StscState.buildString("preserve");
    private static final XmlValueRef XMLSTR_COLLAPSE = StscState.buildString("preserve");
    static final SchemaType[] EMPTY_ST_ARRAY = new SchemaType[0];
    private static final XmlValueRef[] FACETS_NONE = new XmlValueRef[12];
    private static final boolean[] FIXED_FACETS_NONE = new boolean[12];
    private static final boolean[] FIXED_FACETS_WS = new boolean[12];
    private static final XmlValueRef[] FACETS_WS_COLLAPSE = new XmlValueRef[]{null, null, null, null, null, null, null, null, null, StscState.build_wsstring(3), null, null};
    static final XmlValueRef[] FACETS_UNION = FACETS_NONE;
    static final boolean[] FIXED_FACETS_UNION = FIXED_FACETS_NONE;
    static final XmlValueRef[] FACETS_LIST = FACETS_WS_COLLAPSE;
    static final boolean[] FIXED_FACETS_LIST = FIXED_FACETS_WS;
    private static final ThreadLocal<StscStack> tl_stscStack = new ThreadLocal();
    private static final String PROJECT_URL_PREFIX = "project://local";
    private String _givenStsName;
    private Collection<XmlError> _errorListener;
    private SchemaTypeSystemImpl _target;
    private BindingConfig _config;
    private Map<QName, QName> _compatMap;
    private boolean _doingDownloads;
    private byte[] _digest = null;
    private boolean _noDigest = false;
    private boolean _allowPartial = false;
    private int _recoveredErrors = 0;
    private SchemaTypeLoader _importingLoader;
    private final Map<String, SchemaContainer> _containers = new LinkedHashMap<String, SchemaContainer>();
    private SchemaDependencies _dependencies;
    private final Map<SchemaTypeImpl, SchemaTypeImpl> _redefinedGlobalTypes = new LinkedHashMap<SchemaTypeImpl, SchemaTypeImpl>();
    private final Map<SchemaModelGroupImpl, SchemaModelGroupImpl> _redefinedModelGroups = new LinkedHashMap<SchemaModelGroupImpl, SchemaModelGroupImpl>();
    private final Map<SchemaAttributeGroupImpl, SchemaAttributeGroupImpl> _redefinedAttributeGroups = new LinkedHashMap<SchemaAttributeGroupImpl, SchemaAttributeGroupImpl>();
    private final Map<QName, SchemaType> _globalTypes = new LinkedHashMap<QName, SchemaType>();
    private final Map<QName, SchemaGlobalElement> _globalElements = new LinkedHashMap<QName, SchemaGlobalElement>();
    private final Map<QName, SchemaGlobalAttribute> _globalAttributes = new LinkedHashMap<QName, SchemaGlobalAttribute>();
    private final Map<QName, SchemaModelGroup> _modelGroups = new LinkedHashMap<QName, SchemaModelGroup>();
    private final Map<QName, SchemaAttributeGroup> _attributeGroups = new LinkedHashMap<QName, SchemaAttributeGroup>();
    private final Map<QName, SchemaType> _documentTypes = new LinkedHashMap<QName, SchemaType>();
    private final Map<QName, SchemaType> _attributeTypes = new LinkedHashMap<QName, SchemaType>();
    private final Map<String, SchemaType> _typesByClassname = new LinkedHashMap<String, SchemaType>();
    private final Map<String, SchemaComponent> _misspelledNames = new HashMap<String, SchemaComponent>();
    private final Set<SchemaComponent> _processingGroups = new LinkedHashSet<SchemaComponent>();
    private final Map<QName, SchemaIdentityConstraint> _idConstraints = new LinkedHashMap<QName, SchemaIdentityConstraint>();
    private final Set<String> _namespaces = new HashSet<String>();
    private final List<SchemaAnnotation> _annotations = new ArrayList<SchemaAnnotation>();
    private boolean _noUpa;
    private boolean _noPvr;
    private boolean _noAnn;
    private boolean _mdefAll;
    private final Set<String> _mdefNamespaces = StscState.buildDefaultMdefNamespaces();
    private EntityResolver _entityResolver;
    private File _schemasDir;
    private final Map<String, String> _sourceForUri = new HashMap<String, String>();
    private URI _baseURI = URI.create("project://local/");
    private final SchemaTypeLoader _s4sloader = XmlBeans.typeLoaderForClassLoader(SchemaDocument.class.getClassLoader());

    private static Set<String> buildDefaultMdefNamespaces() {
        return new HashSet<String>(Collections.singletonList("http://www.openuri.org/2002/04/soap/conversation/"));
    }

    private StscState() {
    }

    public void initFromTypeSystem(SchemaTypeSystemImpl system, Set<String> newNamespaces) {
        SchemaContainer[] containers;
        for (SchemaContainer container : containers = system.containers()) {
            if (newNamespaces.contains(container.getNamespace())) continue;
            this.addContainer(container);
        }
    }

    void addNewContainer(String namespace) {
        if (this._containers.containsKey(namespace)) {
            return;
        }
        SchemaContainer container = new SchemaContainer(namespace);
        container.setTypeSystem(this.sts());
        this.addNamespace(namespace);
        this._containers.put(namespace, container);
    }

    private void addContainer(SchemaContainer container) {
        this._containers.put(container.getNamespace(), container);
        container.globalElements().forEach(g -> this._globalElements.put(g.getName(), (SchemaGlobalElement)g));
        container.globalAttributes().forEach(g -> this._globalAttributes.put(g.getName(), (SchemaGlobalAttribute)g));
        container.modelGroups().forEach(g -> this._modelGroups.put(g.getName(), (SchemaModelGroup)g));
        container.attributeGroups().forEach(g -> this._attributeGroups.put(g.getName(), (SchemaAttributeGroup)g));
        container.globalTypes().forEach(this.mapTypes(this._globalTypes, false));
        container.documentTypes().forEach(this.mapTypes(this._documentTypes, true));
        container.attributeTypes().forEach(this.mapTypes(this._attributeTypes, true));
        container.identityConstraints().forEach(g -> this._idConstraints.put(g.getName(), (SchemaIdentityConstraint)g));
        this._annotations.addAll(container.annotations());
        this._namespaces.add(container.getNamespace());
        container.unsetImmutable();
    }

    private Consumer<SchemaType> mapTypes(Map<QName, SchemaType> map, boolean useProperties) {
        return t -> {
            QName name = useProperties ? t.getProperties()[0].getName() : t.getName();
            map.put(name, (SchemaType)t);
            if (t.getFullJavaName() != null) {
                this.addClassname(t.getFullJavaName(), (SchemaType)t);
            }
        };
    }

    SchemaContainer getContainer(String namespace) {
        return this._containers.get(namespace);
    }

    Map<String, SchemaContainer> getContainerMap() {
        return Collections.unmodifiableMap(this._containers);
    }

    void registerDependency(String sourceNs, String targetNs) {
        this._dependencies.registerDependency(sourceNs, targetNs);
    }

    void registerContribution(String ns, String fileUrl) {
        this._dependencies.registerContribution(ns, fileUrl);
    }

    SchemaDependencies getDependencies() {
        return this._dependencies;
    }

    void setDependencies(SchemaDependencies deps) {
        this._dependencies = deps;
    }

    boolean isFileProcessed(String url) {
        return this._dependencies.isFileRepresented(url);
    }

    public void setImportingTypeLoader(SchemaTypeLoader loader) {
        this._importingLoader = loader;
    }

    public void setErrorListener(Collection<XmlError> errorListener) {
        this._errorListener = errorListener;
    }

    public void error(String message, int code, XmlObject loc) {
        StscState.addError(this._errorListener, message, code, loc);
    }

    public void error(String code, Object[] args, XmlObject loc) {
        StscState.addError(this._errorListener, code, args, loc);
    }

    public void recover(String code, Object[] args, XmlObject loc) {
        StscState.addError(this._errorListener, code, args, loc);
        ++this._recoveredErrors;
    }

    public void warning(String message, int code, XmlObject loc) {
        StscState.addWarning(this._errorListener, message, code, loc);
    }

    public void warning(String code, Object[] args, XmlObject loc) {
        if ("reserved-type-name".equals(code) && loc.documentProperties().getSourceName() != null && loc.documentProperties().getSourceName().indexOf("XMLSchema.xsd") > 0) {
            return;
        }
        StscState.addWarning(this._errorListener, code, args, loc);
    }

    public void info(String message) {
        StscState.addInfo(this._errorListener, message);
    }

    public void info(String code, Object[] args) {
        StscState.addInfo(this._errorListener, code, args);
    }

    public static void addError(Collection<XmlError> errorListener, String message, int code, XmlObject location) {
        XmlError err = XmlError.forObject(message, 0, location);
        errorListener.add(err);
    }

    public static void addError(Collection<XmlError> errorListener, String code, Object[] args, XmlObject location) {
        XmlError err = XmlError.forObject(code, args, 0, location);
        errorListener.add(err);
    }

    public static void addError(Collection<XmlError> errorListener, String code, Object[] args, File location) {
        XmlError err = XmlError.forLocation(code, args, 0, location.toURI().toString(), 0, 0, 0);
        errorListener.add(err);
    }

    public static void addError(Collection<XmlError> errorListener, String code, Object[] args, URL location) {
        XmlError err = XmlError.forLocation(code, args, 0, location.toString(), 0, 0, 0);
        errorListener.add(err);
    }

    public static void addWarning(Collection<XmlError> errorListener, String message, int code, XmlObject location) {
        XmlError err = XmlError.forObject(message, 1, location);
        errorListener.add(err);
    }

    public static void addWarning(Collection<XmlError> errorListener, String code, Object[] args, XmlObject location) {
        XmlError err = XmlError.forObject(code, args, 1, location);
        errorListener.add(err);
    }

    public static void addInfo(Collection<XmlError> errorListener, String message) {
        XmlError err = XmlError.forMessage(message, 2);
        errorListener.add(err);
    }

    public static void addInfo(Collection<XmlError> errorListener, String code, Object[] args) {
        XmlError err = XmlError.forMessage(code, args, 2);
        errorListener.add(err);
    }

    public void setGivenTypeSystemName(String name) {
        this._givenStsName = name;
    }

    public void setTargetSchemaTypeSystem(SchemaTypeSystemImpl target) {
        this._target = target;
    }

    public void addSchemaDigest(byte[] digest) {
        int len;
        if (this._noDigest) {
            return;
        }
        if (digest == null) {
            this._noDigest = true;
            this._digest = null;
            return;
        }
        if (this._digest == null) {
            this._digest = new byte[16];
        }
        if (digest.length < (len = this._digest.length)) {
            len = digest.length;
        }
        for (int i = 0; i < len; ++i) {
            int n = i;
            this._digest[n] = (byte)(this._digest[n] ^ digest[i]);
        }
    }

    public SchemaTypeSystemImpl sts() {
        if (this._target != null) {
            return this._target;
        }
        String name = this._givenStsName;
        if (name == null && this._digest != null) {
            name = "s" + new String(HexBin.encode(this._digest), StandardCharsets.ISO_8859_1);
        }
        this._target = new SchemaTypeSystemImpl(name);
        return this._target;
    }

    public boolean shouldDownloadURI(String uriString) {
        if (this._doingDownloads) {
            return true;
        }
        if (uriString == null) {
            return false;
        }
        try {
            URI uri = new URI(uriString);
            if (uri.getScheme().equalsIgnoreCase("jar") || uri.getScheme().equalsIgnoreCase("zip")) {
                String s = uri.getSchemeSpecificPart();
                int i = s.lastIndexOf(33);
                return this.shouldDownloadURI(i > 0 ? s.substring(0, i) : s);
            }
            return uri.getScheme().equalsIgnoreCase("file");
        }
        catch (Exception e) {
            return false;
        }
    }

    public void setOptions(XmlOptions options) {
        Set<String> mdef;
        if (options == null) {
            return;
        }
        this._allowPartial = options.isCompilePartialTypesystem();
        this._compatMap = options.getCompileSubstituteNames();
        this._noUpa = options.isCompileNoUpaRule() || !"true".equals(SystemProperties.getProperty("xmlbean.uniqueparticleattribution", "true"));
        this._noPvr = options.isCompileNoPvrRule() || !"true".equals(SystemProperties.getProperty("xmlbean.particlerestriction", "true"));
        this._noAnn = options.isCompileNoAnnotations() || !"true".equals(SystemProperties.getProperty("xmlbean.schemaannotations", "true"));
        this._doingDownloads = options.isCompileDownloadUrls() || "true".equals(SystemProperties.getProperty("xmlbean.downloadurls", "false"));
        this._entityResolver = options.getEntityResolver();
        if (this._entityResolver == null) {
            this._entityResolver = ResolverUtil.getGlobalEntityResolver();
        }
        if (this._entityResolver != null) {
            this._doingDownloads = true;
        }
        if ((mdef = options.getCompileMdefNamespaces()) != null) {
            this._mdefNamespaces.addAll(mdef);
            String local = "##local";
            String any = "##any";
            if (this._mdefNamespaces.contains(local)) {
                this._mdefNamespaces.remove(local);
                this._mdefNamespaces.add("");
            }
            if (this._mdefNamespaces.contains(any)) {
                this._mdefNamespaces.remove(any);
                this._mdefAll = true;
            }
        }
    }

    public EntityResolver getEntityResolver() {
        return this._entityResolver;
    }

    public boolean noUpa() {
        return this._noUpa;
    }

    public boolean noPvr() {
        return this._noPvr;
    }

    public boolean noAnn() {
        return this._noAnn;
    }

    public boolean allowPartial() {
        return this._allowPartial;
    }

    public int getRecovered() {
        return this._recoveredErrors;
    }

    private QName compatName(QName name, String chameleonNamespace) {
        if (name.getNamespaceURI().length() == 0 && chameleonNamespace != null && chameleonNamespace.length() > 0) {
            name = new QName(chameleonNamespace, name.getLocalPart());
        }
        if (this._compatMap == null) {
            return name;
        }
        QName subst = this._compatMap.get(name);
        if (subst == null) {
            return name;
        }
        return subst;
    }

    public void setBindingConfig(BindingConfig config) throws IllegalArgumentException {
        this._config = config;
    }

    public BindingConfig getBindingConfig() throws IllegalArgumentException {
        return this._config;
    }

    public String getPackageOverride(String namespace) {
        if (this._config == null) {
            return null;
        }
        return this._config.lookupPackageForNamespace(namespace);
    }

    public String getJavaPrefix(String namespace) {
        if (this._config == null) {
            return null;
        }
        return this._config.lookupPrefixForNamespace(namespace);
    }

    public String getJavaSuffix(String namespace) {
        if (this._config == null) {
            return null;
        }
        return this._config.lookupSuffixForNamespace(namespace);
    }

    public String getJavaname(QName qname, int kind) {
        if (this._config == null) {
            return null;
        }
        return this._config.lookupJavanameForQName(qname, kind);
    }

    private static String crunchName(QName name) {
        return name.getLocalPart().toLowerCase(Locale.ROOT);
    }

    void addSpelling(QName name, SchemaComponent comp) {
        this._misspelledNames.put(StscState.crunchName(name), comp);
    }

    SchemaComponent findSpelling(QName name) {
        return this._misspelledNames.get(StscState.crunchName(name));
    }

    void addNamespace(String targetNamespace) {
        this._namespaces.add(targetNamespace);
    }

    String[] getNamespaces() {
        return this._namespaces.toArray(new String[0]);
    }

    boolean linkerDefinesNamespace(String namespace) {
        return this._importingLoader.isNamespaceDefined(namespace);
    }

    SchemaTypeImpl findGlobalType(QName name, String chameleonNamespace, String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaTypeImpl result = (SchemaTypeImpl)this._globalTypes.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaTypeImpl)this._importingLoader.findType(name);
            boolean bl = foundOnLoader = result != null;
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }

    SchemaTypeImpl findRedefinedGlobalType(QName name, String chameleonNamespace, SchemaTypeImpl redefinedBy) {
        QName redefinedName = redefinedBy.getName();
        if ((name = this.compatName(name, chameleonNamespace)).equals(redefinedName)) {
            return this._redefinedGlobalTypes.get(redefinedBy);
        }
        SchemaTypeImpl result = (SchemaTypeImpl)this._globalTypes.get(name);
        if (result == null) {
            result = (SchemaTypeImpl)this._importingLoader.findType(name);
        }
        return result;
    }

    void addGlobalType(SchemaTypeImpl type, SchemaTypeImpl redefined) {
        if (type != null) {
            QName name = type.getName();
            SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert (container != null && container == type.getContainer());
            if (redefined != null) {
                if (this._redefinedGlobalTypes.containsKey(redefined)) {
                    if (!this.ignoreMdef(name)) {
                        if (this._mdefAll) {
                            this.warning("sch-props-correct.2", new Object[]{"global type", QNameHelper.pretty(name), this._redefinedGlobalTypes.get(redefined).getSourceName()}, type.getParseObject());
                        } else {
                            this.error("sch-props-correct.2", new Object[]{"global type", QNameHelper.pretty(name), this._redefinedGlobalTypes.get(redefined).getSourceName()}, type.getParseObject());
                        }
                    }
                } else {
                    this._redefinedGlobalTypes.put(redefined, type);
                    container.addRedefinedType(type.getRef());
                }
            } else if (this._globalTypes.containsKey(name)) {
                if (!this.ignoreMdef(name)) {
                    if (this._mdefAll) {
                        this.warning("sch-props-correct.2", new Object[]{"global type", QNameHelper.pretty(name), this._globalTypes.get(name).getSourceName()}, type.getParseObject());
                    } else {
                        this.error("sch-props-correct.2", new Object[]{"global type", QNameHelper.pretty(name), this._globalTypes.get(name).getSourceName()}, type.getParseObject());
                    }
                }
            } else {
                this._globalTypes.put(name, type);
                container.addGlobalType(type.getRef());
                this.addSpelling(name, type);
            }
        }
    }

    private boolean ignoreMdef(QName name) {
        return this._mdefNamespaces.contains(name.getNamespaceURI());
    }

    SchemaType[] globalTypes() {
        return this._globalTypes.values().toArray(new SchemaType[0]);
    }

    SchemaType[] redefinedGlobalTypes() {
        return this._redefinedGlobalTypes.values().toArray(new SchemaType[0]);
    }

    SchemaTypeImpl findDocumentType(QName name, String chameleonNamespace, String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaTypeImpl result = (SchemaTypeImpl)this._documentTypes.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaTypeImpl)this._importingLoader.findDocumentType(name);
            boolean bl = foundOnLoader = result != null;
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }

    void addDocumentType(SchemaTypeImpl type, QName name) {
        if (this._documentTypes.containsKey(name)) {
            if (!this.ignoreMdef(name)) {
                if (this._mdefAll) {
                    this.warning("sch-props-correct.2", new Object[]{"global element", QNameHelper.pretty(name), this._documentTypes.get(name).getSourceName()}, type.getParseObject());
                } else {
                    this.error("sch-props-correct.2", new Object[]{"global element", QNameHelper.pretty(name), this._documentTypes.get(name).getSourceName()}, type.getParseObject());
                }
            }
        } else {
            this._documentTypes.put(name, type);
            SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert (container != null && container == type.getContainer());
            container.addDocumentType(type.getRef());
        }
    }

    SchemaType[] documentTypes() {
        return this._documentTypes.values().toArray(new SchemaType[0]);
    }

    SchemaTypeImpl findAttributeType(QName name, String chameleonNamespace, String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaTypeImpl result = (SchemaTypeImpl)this._attributeTypes.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaTypeImpl)this._importingLoader.findAttributeType(name);
            boolean bl = foundOnLoader = result != null;
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }

    void addAttributeType(SchemaTypeImpl type, QName name) {
        if (this._attributeTypes.containsKey(name)) {
            if (!this.ignoreMdef(name)) {
                if (this._mdefAll) {
                    this.warning("sch-props-correct.2", new Object[]{"global attribute", QNameHelper.pretty(name), this._attributeTypes.get(name).getSourceName()}, type.getParseObject());
                } else {
                    this.error("sch-props-correct.2", new Object[]{"global attribute", QNameHelper.pretty(name), this._attributeTypes.get(name).getSourceName()}, type.getParseObject());
                }
            }
        } else {
            this._attributeTypes.put(name, type);
            SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert (container != null && container == type.getContainer());
            container.addAttributeType(type.getRef());
        }
    }

    SchemaType[] attributeTypes() {
        return this._attributeTypes.values().toArray(new SchemaType[0]);
    }

    SchemaGlobalAttributeImpl findGlobalAttribute(QName name, String chameleonNamespace, String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaGlobalAttributeImpl result = (SchemaGlobalAttributeImpl)this._globalAttributes.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaGlobalAttributeImpl)this._importingLoader.findAttribute(name);
            boolean bl = foundOnLoader = result != null;
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }

    void addGlobalAttribute(SchemaGlobalAttributeImpl attribute) {
        if (attribute != null) {
            QName name = attribute.getName();
            this._globalAttributes.put(name, attribute);
            this.addSpelling(name, attribute);
            SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert (container != null && container == attribute.getContainer());
            container.addGlobalAttribute(attribute.getRef());
        }
    }

    SchemaGlobalAttribute[] globalAttributes() {
        return this._globalAttributes.values().toArray(new SchemaGlobalAttribute[0]);
    }

    SchemaGlobalElementImpl findGlobalElement(QName name, String chameleonNamespace, String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaGlobalElementImpl result = (SchemaGlobalElementImpl)this._globalElements.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaGlobalElementImpl)this._importingLoader.findElement(name);
            boolean bl = foundOnLoader = result != null;
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }

    void addGlobalElement(SchemaGlobalElementImpl element) {
        if (element != null) {
            QName name = element.getName();
            this._globalElements.put(name, element);
            SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert (container != null && container == element.getContainer());
            container.addGlobalElement(element.getRef());
            this.addSpelling(name, element);
        }
    }

    SchemaGlobalElement[] globalElements() {
        return this._globalElements.values().toArray(new SchemaGlobalElement[0]);
    }

    SchemaAttributeGroupImpl findAttributeGroup(QName name, String chameleonNamespace, String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaAttributeGroupImpl result = (SchemaAttributeGroupImpl)this._attributeGroups.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaAttributeGroupImpl)this._importingLoader.findAttributeGroup(name);
            boolean bl = foundOnLoader = result != null;
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }

    SchemaAttributeGroupImpl findRedefinedAttributeGroup(QName name, String chameleonNamespace, SchemaAttributeGroupImpl redefinedBy) {
        QName redefinitionFor = redefinedBy.getName();
        if ((name = this.compatName(name, chameleonNamespace)).equals(redefinitionFor)) {
            return this._redefinedAttributeGroups.get(redefinedBy);
        }
        SchemaAttributeGroupImpl result = (SchemaAttributeGroupImpl)this._attributeGroups.get(name);
        if (result == null) {
            result = (SchemaAttributeGroupImpl)this._importingLoader.findAttributeGroup(name);
        }
        return result;
    }

    void addAttributeGroup(SchemaAttributeGroupImpl attributeGroup, SchemaAttributeGroupImpl redefined) {
        if (attributeGroup != null) {
            QName name = attributeGroup.getName();
            SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert (container != null && container == attributeGroup.getContainer());
            if (redefined != null) {
                if (this._redefinedAttributeGroups.containsKey(redefined)) {
                    if (!this.ignoreMdef(name)) {
                        if (this._mdefAll) {
                            this.warning("sch-props-correct.2", new Object[]{"attribute group", QNameHelper.pretty(name), this._redefinedAttributeGroups.get(redefined).getSourceName()}, attributeGroup.getParseObject());
                        } else {
                            this.error("sch-props-correct.2", new Object[]{"attribute group", QNameHelper.pretty(name), this._redefinedAttributeGroups.get(redefined).getSourceName()}, attributeGroup.getParseObject());
                        }
                    }
                } else {
                    this._redefinedAttributeGroups.put(redefined, attributeGroup);
                    container.addRedefinedAttributeGroup(attributeGroup.getRef());
                }
            } else if (this._attributeGroups.containsKey(name)) {
                if (!this.ignoreMdef(name)) {
                    if (this._mdefAll) {
                        this.warning("sch-props-correct.2", new Object[]{"attribute group", QNameHelper.pretty(name), this._attributeGroups.get(name).getSourceName()}, attributeGroup.getParseObject());
                    } else {
                        this.error("sch-props-correct.2", new Object[]{"attribute group", QNameHelper.pretty(name), this._attributeGroups.get(name).getSourceName()}, attributeGroup.getParseObject());
                    }
                }
            } else {
                this._attributeGroups.put(attributeGroup.getName(), attributeGroup);
                this.addSpelling(attributeGroup.getName(), attributeGroup);
                container.addAttributeGroup(attributeGroup.getRef());
            }
        }
    }

    SchemaAttributeGroup[] attributeGroups() {
        return this._attributeGroups.values().toArray(new SchemaAttributeGroup[0]);
    }

    SchemaAttributeGroup[] redefinedAttributeGroups() {
        return this._redefinedAttributeGroups.values().toArray(new SchemaAttributeGroup[0]);
    }

    SchemaModelGroupImpl findModelGroup(QName name, String chameleonNamespace, String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaModelGroupImpl result = (SchemaModelGroupImpl)this._modelGroups.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaModelGroupImpl)this._importingLoader.findModelGroup(name);
            boolean bl = foundOnLoader = result != null;
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }

    SchemaModelGroupImpl findRedefinedModelGroup(QName name, String chameleonNamespace, SchemaModelGroupImpl redefinedBy) {
        QName redefinitionFor = redefinedBy.getName();
        if ((name = this.compatName(name, chameleonNamespace)).equals(redefinitionFor)) {
            return this._redefinedModelGroups.get(redefinedBy);
        }
        SchemaModelGroupImpl result = (SchemaModelGroupImpl)this._modelGroups.get(name);
        if (result == null) {
            result = (SchemaModelGroupImpl)this._importingLoader.findModelGroup(name);
        }
        return result;
    }

    void addModelGroup(SchemaModelGroupImpl modelGroup, SchemaModelGroupImpl redefined) {
        if (modelGroup != null) {
            QName name = modelGroup.getName();
            SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert (container != null && container == modelGroup.getContainer());
            if (redefined != null) {
                if (this._redefinedModelGroups.containsKey(redefined)) {
                    if (!this.ignoreMdef(name)) {
                        if (this._mdefAll) {
                            this.warning("sch-props-correct.2", new Object[]{"model group", QNameHelper.pretty(name), ((SchemaComponent)this._redefinedModelGroups.get(redefined)).getSourceName()}, modelGroup.getParseObject());
                        } else {
                            this.error("sch-props-correct.2", new Object[]{"model group", QNameHelper.pretty(name), ((SchemaComponent)this._redefinedModelGroups.get(redefined)).getSourceName()}, modelGroup.getParseObject());
                        }
                    }
                } else {
                    this._redefinedModelGroups.put(redefined, modelGroup);
                    container.addRedefinedModelGroup(modelGroup.getRef());
                }
            } else if (this._modelGroups.containsKey(name)) {
                if (!this.ignoreMdef(name)) {
                    if (this._mdefAll) {
                        this.warning("sch-props-correct.2", new Object[]{"model group", QNameHelper.pretty(name), this._modelGroups.get(name).getSourceName()}, modelGroup.getParseObject());
                    } else {
                        this.error("sch-props-correct.2", new Object[]{"model group", QNameHelper.pretty(name), this._modelGroups.get(name).getSourceName()}, modelGroup.getParseObject());
                    }
                }
            } else {
                this._modelGroups.put(modelGroup.getName(), modelGroup);
                this.addSpelling(modelGroup.getName(), modelGroup);
                container.addModelGroup(modelGroup.getRef());
            }
        }
    }

    SchemaModelGroup[] modelGroups() {
        return this._modelGroups.values().toArray(new SchemaModelGroup[0]);
    }

    SchemaModelGroup[] redefinedModelGroups() {
        return this._redefinedModelGroups.values().toArray(new SchemaModelGroup[0]);
    }

    SchemaIdentityConstraintImpl findIdConstraint(QName name, String chameleonNamespace, String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        if (sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return (SchemaIdentityConstraintImpl)this._idConstraints.get(name);
    }

    void addIdConstraint(SchemaIdentityConstraintImpl idc) {
        if (idc != null) {
            QName name = idc.getName();
            SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert (container != null && container == idc.getContainer());
            if (this._idConstraints.containsKey(name)) {
                if (!this.ignoreMdef(name)) {
                    this.warning("sch-props-correct.2", new Object[]{"identity constraint", QNameHelper.pretty(name), this._idConstraints.get(name).getSourceName()}, idc.getParseObject());
                }
            } else {
                this._idConstraints.put(name, idc);
                this.addSpelling(idc.getName(), idc);
                container.addIdentityConstraint(idc.getRef());
            }
        }
    }

    SchemaIdentityConstraintImpl[] idConstraints() {
        return this._idConstraints.values().toArray(new SchemaIdentityConstraintImpl[0]);
    }

    void addAnnotation(SchemaAnnotationImpl ann, String targetNamespace) {
        if (ann != null) {
            SchemaContainer container = this.getContainer(targetNamespace);
            assert (container != null && container == ann.getContainer());
            this._annotations.add(ann);
            container.addAnnotation(ann);
        }
    }

    List<SchemaAnnotation> annotations() {
        return this._annotations;
    }

    boolean isProcessing(SchemaComponent obj) {
        return this._processingGroups.contains(obj);
    }

    void startProcessing(SchemaComponent obj) {
        assert (!this._processingGroups.contains(obj));
        this._processingGroups.add(obj);
    }

    void finishProcessing(SchemaComponent obj) {
        assert (this._processingGroups.contains(obj));
        this._processingGroups.remove(obj);
    }

    SchemaComponent[] getCurrentProcessing() {
        return this._processingGroups.toArray(new SchemaComponent[0]);
    }

    Map<String, SchemaType> typesByClassname() {
        return Collections.unmodifiableMap(this._typesByClassname);
    }

    void addClassname(String classname, SchemaType type) {
        this._typesByClassname.put(classname, type);
    }

    public static void clearThreadLocals() {
        tl_stscStack.remove();
    }

    public static StscState start() {
        StscStack stscStack = tl_stscStack.get();
        if (stscStack == null) {
            stscStack = new StscStack();
            tl_stscStack.set(stscStack);
        }
        return stscStack.push();
    }

    public static StscState get() {
        return StscState.tl_stscStack.get().current;
    }

    public static void end() {
        StscStack stscStack = tl_stscStack.get();
        stscStack.pop();
        if (stscStack.stack.size() == 0) {
            tl_stscStack.remove();
        }
    }

    static XmlValueRef build_wsstring(int wsr) {
        switch (wsr) {
            case 1: {
                return XMLSTR_PRESERVE;
            }
            case 2: {
                return XMLSTR_REPLACE;
            }
            case 3: {
                return XMLSTR_COLLAPSE;
            }
        }
        return null;
    }

    static XmlValueRef buildString(String str) {
        if (str == null) {
            return null;
        }
        try {
            XmlStringImpl i = new XmlStringImpl();
            i.setStringValue(str);
            i.setImmutable();
            return new XmlValueRef(i);
        }
        catch (XmlValueOutOfRangeException e) {
            return null;
        }
    }

    public void notFoundError(QName itemName, int code, XmlObject loc, boolean recovered) {
        QName name;
        String expected;
        String expectedName = QNameHelper.pretty(itemName);
        String found = null;
        String foundName = null;
        String sourceName = null;
        if (recovered) {
            ++this._recoveredErrors;
        }
        switch (code) {
            case 0: {
                expected = "type";
                break;
            }
            case 1: {
                expected = "element";
                break;
            }
            case 3: {
                expected = "attribute";
                break;
            }
            case 6: {
                expected = "model group";
                break;
            }
            case 4: {
                expected = "attribute group";
                break;
            }
            case 5: {
                expected = "identity constraint";
                break;
            }
            default: {
                assert (false);
                expected = "definition";
            }
        }
        SchemaComponent foundComponent = this.findSpelling(itemName);
        if (foundComponent != null && (name = foundComponent.getName()) != null) {
            switch (foundComponent.getComponentType()) {
                case 0: {
                    found = "type";
                    sourceName = foundComponent.getSourceName();
                    break;
                }
                case 1: {
                    found = "element";
                    sourceName = foundComponent.getSourceName();
                    break;
                }
                case 3: {
                    found = "attribute";
                    sourceName = foundComponent.getSourceName();
                    break;
                }
                case 4: {
                    found = "attribute group";
                    break;
                }
                case 6: {
                    found = "model group";
                }
            }
            if (sourceName != null) {
                sourceName = sourceName.substring(sourceName.lastIndexOf(47) + 1);
            }
            if (!name.equals(itemName)) {
                foundName = QNameHelper.pretty(name);
            }
        }
        if (found == null) {
            this.error("src-resolve", new Object[]{expected, expectedName}, loc);
        } else {
            this.error("src-resolve.a", new Object[]{expected, expectedName, found, foundName == null ? 0 : 1, foundName, sourceName == null ? 0 : 1, sourceName}, loc);
        }
    }

    public String sourceNameForUri(String uri) {
        return this._sourceForUri.get(uri);
    }

    public Map<String, String> sourceCopyMap() {
        return Collections.unmodifiableMap(this._sourceForUri);
    }

    public void setBaseUri(URI uri) {
        this._baseURI = uri;
    }

    public String relativize(String uri) {
        return this.relativize(uri, false);
    }

    public String computeSavedFilename(String uri) {
        return this.relativize(uri, true);
    }

    private String relativize(String uri, boolean forSavedFilename) {
        if (uri == null) {
            return null;
        }
        if (uri.startsWith("/")) {
            uri = PROJECT_URL_PREFIX + uri.replace('\\', '/');
        } else {
            int colon = uri.indexOf(58);
            if (colon <= 1 || !uri.substring(0, colon).matches("^\\w+$")) {
                uri = "project://local/" + uri.replace('\\', '/');
            }
        }
        if (this._baseURI != null) {
            try {
                URI relative = this._baseURI.relativize(new URI(uri));
                if (!relative.isAbsolute()) {
                    return relative.toString();
                }
                uri = relative.toString();
            }
            catch (URISyntaxException relative) {
                // empty catch block
            }
        }
        if (!forSavedFilename) {
            return uri;
        }
        int lastslash = uri.lastIndexOf(47);
        String dir = QNameHelper.hexsafe(lastslash == -1 ? "" : uri.substring(0, lastslash));
        int question = uri.indexOf(63, lastslash + 1);
        if (question == -1) {
            return dir + "/" + uri.substring(lastslash + 1);
        }
        String query = QNameHelper.hexsafe(uri.substring(question));
        if (query.startsWith("URI_SHA_1_")) {
            return dir + "/" + uri.substring(lastslash + 1, question);
        }
        return dir + "/" + uri.substring(lastslash + 1, question) + query;
    }

    public void addSourceUri(String uri, String nameToUse) {
        if (uri == null) {
            return;
        }
        if (nameToUse == null) {
            nameToUse = this.computeSavedFilename(uri);
        }
        this._sourceForUri.put(uri, nameToUse);
    }

    public Collection<XmlError> getErrorListener() {
        return this._errorListener;
    }

    public SchemaTypeLoader getS4SLoader() {
        return this._s4sloader;
    }

    public File getSchemasDir() {
        return this._schemasDir;
    }

    public void setSchemasDir(File _schemasDir) {
        this._schemasDir = _schemasDir;
    }

    private static final class StscStack {
        StscState current;
        List<StscState> stack = new ArrayList<StscState>();

        private StscStack() {
        }

        final StscState push() {
            this.stack.add(this.current);
            this.current = new StscState();
            return this.current;
        }

        final void pop() {
            this.current = this.stack.get(this.stack.size() - 1);
            this.stack.remove(this.stack.size() - 1);
        }
    }
}

