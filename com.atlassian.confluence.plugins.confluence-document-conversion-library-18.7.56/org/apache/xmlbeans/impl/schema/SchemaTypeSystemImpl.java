/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.Filer;
import org.apache.xmlbeans.ResourceLoader;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeLoaderException;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.SystemProperties;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.XBeanDebug;
import org.apache.xmlbeans.impl.schema.ClassLoaderResourceLoader;
import org.apache.xmlbeans.impl.schema.SchemaContainer;
import org.apache.xmlbeans.impl.schema.SchemaDependencies;
import org.apache.xmlbeans.impl.schema.SchemaTypeLoaderBase;
import org.apache.xmlbeans.impl.schema.SchemaTypeLoaderImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypePool;
import org.apache.xmlbeans.impl.schema.StscState;
import org.apache.xmlbeans.impl.schema.XsbReader;
import org.apache.xmlbeans.impl.util.FilerImpl;
import org.apache.xmlbeans.impl.util.HexBin;
import org.apache.xmlbeans.impl.util.LongUTFDataInputStream;
import org.apache.xmlbeans.impl.util.LongUTFDataOutputStream;

public class SchemaTypeSystemImpl
extends SchemaTypeLoaderBase
implements SchemaTypeSystem {
    public static final int DATA_BABE = -629491010;
    public static final int MAJOR_VERSION = 2;
    public static final int MINOR_VERSION = 24;
    public static final int RELEASE_NUMBER = 0;
    public static final int FILETYPE_SCHEMAINDEX = 1;
    public static final int FILETYPE_SCHEMATYPE = 2;
    public static final int FILETYPE_SCHEMAELEMENT = 3;
    public static final int FILETYPE_SCHEMAATTRIBUTE = 4;
    public static final int FILETYPE_SCHEMAPOINTER = 5;
    public static final int FILETYPE_SCHEMAMODELGROUP = 6;
    public static final int FILETYPE_SCHEMAATTRIBUTEGROUP = 7;
    public static final int FILETYPE_SCHEMAIDENTITYCONSTRAINT = 8;
    public static final int FLAG_PART_SKIPPABLE = 1;
    public static final int FLAG_PART_FIXED = 4;
    public static final int FLAG_PART_NILLABLE = 8;
    public static final int FLAG_PART_BLOCKEXT = 16;
    public static final int FLAG_PART_BLOCKREST = 32;
    public static final int FLAG_PART_BLOCKSUBST = 64;
    public static final int FLAG_PART_ABSTRACT = 128;
    public static final int FLAG_PART_FINALEXT = 256;
    public static final int FLAG_PART_FINALREST = 512;
    public static final int FLAG_PROP_ISATTR = 1;
    public static final int FLAG_PROP_JAVASINGLETON = 2;
    public static final int FLAG_PROP_JAVAOPTIONAL = 4;
    public static final int FLAG_PROP_JAVAARRAY = 8;
    public static final int FIELD_NONE = 0;
    public static final int FIELD_GLOBAL = 1;
    public static final int FIELD_LOCALATTR = 2;
    public static final int FIELD_LOCALELT = 3;
    static final int FLAG_SIMPLE_TYPE = 1;
    static final int FLAG_DOCUMENT_TYPE = 2;
    static final int FLAG_ORDERED = 4;
    static final int FLAG_BOUNDED = 8;
    static final int FLAG_FINITE = 16;
    static final int FLAG_NUMERIC = 32;
    static final int FLAG_STRINGENUM = 64;
    static final int FLAG_UNION_OF_LISTS = 128;
    static final int FLAG_HAS_PATTERN = 256;
    static final int FLAG_ORDER_SENSITIVE = 512;
    static final int FLAG_TOTAL_ORDER = 1024;
    static final int FLAG_COMPILED = 2048;
    static final int FLAG_BLOCK_EXT = 4096;
    static final int FLAG_BLOCK_REST = 8192;
    static final int FLAG_FINAL_EXT = 16384;
    static final int FLAG_FINAL_REST = 32768;
    static final int FLAG_FINAL_UNION = 65536;
    static final int FLAG_FINAL_LIST = 131072;
    static final int FLAG_ABSTRACT = 262144;
    static final int FLAG_ATTRIBUTE_TYPE = 524288;
    private static final Pattern packPat = Pattern.compile("^(.+)(\\.[^.]+){2}$");
    public static String METADATA_PACKAGE_GEN = "org/apache/xmlbeans/metadata";
    private static final SchemaType[] EMPTY_ST_ARRAY = new SchemaType[0];
    private static final SchemaGlobalElement[] EMPTY_GE_ARRAY = new SchemaGlobalElement[0];
    private static final SchemaGlobalAttribute[] EMPTY_GA_ARRAY = new SchemaGlobalAttribute[0];
    private static final SchemaModelGroup[] EMPTY_MG_ARRAY = new SchemaModelGroup[0];
    private static final SchemaAttributeGroup[] EMPTY_AG_ARRAY = new SchemaAttributeGroup[0];
    private static final SchemaIdentityConstraint[] EMPTY_IC_ARRAY = new SchemaIdentityConstraint[0];
    private static final SchemaAnnotation[] EMPTY_ANN_ARRAY = new SchemaAnnotation[0];
    private final String _name;
    private boolean _incomplete = false;
    private ClassLoader _classloader;
    private ResourceLoader _resourceLoader;
    SchemaTypeLoader _linker;
    private SchemaTypePool _localHandles;
    private Filer _filer;
    private List<SchemaAnnotation> _annotations;
    private Map<String, SchemaContainer> _containers = new HashMap<String, SchemaContainer>();
    private SchemaDependencies _deps;
    private List<SchemaComponent.Ref> _redefinedModelGroups;
    private List<SchemaComponent.Ref> _redefinedAttributeGroups;
    private List<SchemaComponent.Ref> _redefinedGlobalTypes;
    private Map<QName, SchemaComponent.Ref> _globalElements;
    private Map<QName, SchemaComponent.Ref> _globalAttributes;
    private Map<QName, SchemaComponent.Ref> _modelGroups;
    private Map<QName, SchemaComponent.Ref> _attributeGroups;
    private Map<QName, SchemaComponent.Ref> _globalTypes;
    private Map<QName, SchemaComponent.Ref> _documentTypes;
    private Map<QName, SchemaComponent.Ref> _attributeTypes;
    private Map<QName, SchemaComponent.Ref> _identityConstraints = Collections.emptyMap();
    private Map<String, SchemaComponent.Ref> _typeRefsByClassname = new HashMap<String, SchemaComponent.Ref>();
    private Set<String> _namespaces;
    private static Random _random;
    private static final byte[] _mask;
    static final byte[] SINGLE_ZERO_BYTE;
    private final Map<String, SchemaComponent> _resolvedHandles = new HashMap<String, SchemaComponent>();
    private boolean _allNonGroupHandlesResolved = false;

    static String nameToPathString(String nameForSystem) {
        if (!(nameForSystem = nameForSystem.replace('.', '/')).endsWith("/") && nameForSystem.length() > 0) {
            nameForSystem = nameForSystem + "/";
        }
        return nameForSystem;
    }

    protected SchemaTypeSystemImpl() {
        String fullname = this.getClass().getName();
        this._name = fullname.substring(0, fullname.lastIndexOf(46));
        XBeanDebug.LOG.atTrace().log("Loading type system {}", (Object)this._name);
        this._classloader = this.getClass().getClassLoader();
        this._linker = this;
        this._resourceLoader = new ClassLoaderResourceLoader(this._classloader);
        try {
            this.initFromHeader();
        }
        catch (Error | RuntimeException e) {
            XBeanDebug.LOG.atDebug().withThrowable(e).log(e.getMessage());
            throw e;
        }
        XBeanDebug.LOG.atTrace().log("Finished loading type system {}", (Object)this._name);
    }

    public SchemaTypeSystemImpl(Class<?> indexclass) {
        String fullname = indexclass.getName();
        this._name = fullname.substring(0, fullname.lastIndexOf(46));
        XBeanDebug.LOG.atTrace().log("Loading type system {}", (Object)this._name);
        this._classloader = indexclass.getClassLoader();
        this._linker = SchemaTypeLoaderImpl.build(null, null, this._classloader, this.getMetadataPath());
        this._resourceLoader = new ClassLoaderResourceLoader(this._classloader);
        try {
            this.initFromHeader();
        }
        catch (Error | RuntimeException e) {
            XBeanDebug.LOG.atDebug().withThrowable(e).log(e.getMessage());
            throw e;
        }
        XBeanDebug.LOG.atTrace().log("Finished loading type system {}", (Object)this._name);
    }

    public static SchemaTypeSystemImpl forName(String name, ClassLoader loader) {
        try {
            Class<?> c = Class.forName(name + "." + "TypeSystemHolder", true, loader);
            return (SchemaTypeSystemImpl)c.getField("typeSystem").get(null);
        }
        catch (Throwable e) {
            return null;
        }
    }

    public SchemaTypeSystemImpl(ResourceLoader resourceLoader, String name, SchemaTypeLoader linker) {
        this._name = name;
        this._linker = linker;
        this._resourceLoader = resourceLoader;
        try {
            this.initFromHeader();
        }
        catch (Error | RuntimeException e) {
            XBeanDebug.LOG.atDebug().withThrowable(e).log(e.getMessage());
            throw e;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initFromHeader() {
        XBeanDebug.LOG.atTrace().log("Reading unresolved handles for type system {}", (Object)this._name);
        XsbReader reader = null;
        try {
            reader = new XsbReader(this.getTypeSystem(), "index", 1);
            this._localHandles = new SchemaTypePool(this.getTypeSystem());
            this._localHandles.readHandlePool(reader);
            this._globalElements = reader.readQNameRefMap();
            this._globalAttributes = reader.readQNameRefMap();
            this._modelGroups = reader.readQNameRefMap();
            this._attributeGroups = reader.readQNameRefMap();
            this._identityConstraints = reader.readQNameRefMap();
            this._globalTypes = reader.readQNameRefMap();
            this._documentTypes = reader.readQNameRefMap();
            this._attributeTypes = reader.readQNameRefMap();
            this._typeRefsByClassname = reader.readClassnameRefMap();
            this._namespaces = reader.readNamespaces();
            ArrayList<QName> typeNames = new ArrayList<QName>();
            ArrayList<QName> modelGroupNames = new ArrayList<QName>();
            ArrayList<QName> attributeGroupNames = new ArrayList<QName>();
            if (reader.atLeast(2, 15, 0)) {
                this._redefinedGlobalTypes = reader.readQNameRefMapAsList(typeNames);
                this._redefinedModelGroups = reader.readQNameRefMapAsList(modelGroupNames);
                this._redefinedAttributeGroups = reader.readQNameRefMapAsList(attributeGroupNames);
            }
            if (reader.atLeast(2, 19, 0)) {
                this._annotations = reader.readAnnotations();
            }
            this.buildContainers(typeNames, modelGroupNames, attributeGroupNames);
        }
        finally {
            if (reader != null) {
                reader.readEnd();
            }
        }
    }

    void saveIndex() {
        String handle = "index";
        XsbReader saver = new XsbReader(this.getTypeSystem(), handle);
        saver.writeIndexData();
        saver.writeRealHeader(handle, 1);
        saver.writeIndexData();
        saver.writeEnd();
    }

    void savePointers() {
        this.savePointersForComponents(this.globalElements(), this.getMetadataPath() + "/element/");
        this.savePointersForComponents(this.globalAttributes(), this.getMetadataPath() + "/attribute/");
        this.savePointersForComponents(this.modelGroups(), this.getMetadataPath() + "/modelgroup/");
        this.savePointersForComponents(this.attributeGroups(), this.getMetadataPath() + "/attributegroup/");
        this.savePointersForComponents(this.globalTypes(), this.getMetadataPath() + "/type/");
        this.savePointersForComponents(this.identityConstraints(), this.getMetadataPath() + "/identityconstraint/");
        this.savePointersForNamespaces(this._namespaces, this.getMetadataPath() + "/namespace/");
        this.savePointersForClassnames(this._typeRefsByClassname.keySet(), this.getMetadataPath() + "/javaname/");
        this.savePointersForComponents(this.redefinedModelGroups(), this.getMetadataPath() + "/redefinedmodelgroup/");
        this.savePointersForComponents(this.redefinedAttributeGroups(), this.getMetadataPath() + "/redefinedattributegroup/");
        this.savePointersForComponents(this.redefinedGlobalTypes(), this.getMetadataPath() + "/redefinedtype/");
    }

    void savePointersForComponents(SchemaComponent[] components, String dir) {
        for (SchemaComponent component : components) {
            this.savePointerFile(dir + QNameHelper.hexsafedir(component.getName()), this._name);
        }
    }

    void savePointersForClassnames(Set<String> classnames, String dir) {
        for (String classname : classnames) {
            this.savePointerFile(dir + classname.replace('.', '/'), this._name);
        }
    }

    void savePointersForNamespaces(Set<String> namespaces, String dir) {
        for (String ns : namespaces) {
            this.savePointerFile(dir + QNameHelper.hexsafedir(new QName(ns, "xmlns")), this._name);
        }
    }

    void savePointerFile(String filename, String name) {
        XsbReader saver = new XsbReader(this.getTypeSystem(), filename);
        saver.writeString(name);
        saver.writeRealHeader(filename, 5);
        saver.writeString(name);
        saver.writeEnd();
    }

    private Map<String, SchemaComponent.Ref> buildTypeRefsByClassname(Map<String, SchemaType> typesByClassname) {
        LinkedHashMap<String, SchemaComponent.Ref> result = new LinkedHashMap<String, SchemaComponent.Ref>();
        for (String className : typesByClassname.keySet()) {
            result.put(className, typesByClassname.get(className).getRef());
        }
        return result;
    }

    private static Map<QName, SchemaComponent.Ref> buildComponentRefMap(SchemaComponent[] components) {
        return SchemaTypeSystemImpl.buildComponentRefMap(Arrays.asList(components));
    }

    private static Map<QName, SchemaComponent.Ref> buildComponentRefMap(List<? extends SchemaComponent> components) {
        return components.stream().collect(Collectors.toMap(SchemaComponent::getName, SchemaComponent::getComponentRef, (u, v) -> v, LinkedHashMap::new));
    }

    private static List<SchemaComponent.Ref> buildComponentRefList(SchemaComponent[] components) {
        return SchemaTypeSystemImpl.buildComponentRefList(Arrays.asList(components));
    }

    private static List<SchemaComponent.Ref> buildComponentRefList(List<? extends SchemaComponent> components) {
        return components.stream().map(SchemaComponent::getComponentRef).collect(Collectors.toList());
    }

    private static Map<QName, SchemaComponent.Ref> buildDocumentMap(SchemaType[] types) {
        return SchemaTypeSystemImpl.buildDocumentMap(Arrays.asList(types));
    }

    private static Map<QName, SchemaComponent.Ref> buildDocumentMap(List<? extends SchemaComponent> types) {
        LinkedHashMap<QName, SchemaComponent.Ref> result = new LinkedHashMap<QName, SchemaComponent.Ref>();
        for (SchemaComponent schemaComponent : types) {
            SchemaType type = (SchemaType)schemaComponent;
            result.put(type.getDocumentElementName(), type.getRef());
        }
        return result;
    }

    private static Map<QName, SchemaComponent.Ref> buildAttributeTypeMap(SchemaType[] types) {
        LinkedHashMap<QName, SchemaComponent.Ref> result = new LinkedHashMap<QName, SchemaComponent.Ref>();
        for (SchemaType type : types) {
            result.put(type.getAttributeTypeAttributeName(), type.getRef());
        }
        return result;
    }

    private static Map<QName, SchemaComponent.Ref> buildAttributeTypeMap(List<? extends SchemaComponent> types) {
        LinkedHashMap<QName, SchemaComponent.Ref> result = new LinkedHashMap<QName, SchemaComponent.Ref>();
        for (SchemaComponent schemaComponent : types) {
            SchemaType type = (SchemaType)schemaComponent;
            result.put(type.getAttributeTypeAttributeName(), type.getRef());
        }
        return result;
    }

    SchemaContainer getContainer(String namespace) {
        return this._containers.get(namespace);
    }

    private void addContainer(String namespace) {
        SchemaContainer c = new SchemaContainer(namespace);
        c.setTypeSystem(this);
        this._containers.put(namespace, c);
    }

    SchemaContainer getContainerNonNull(String namespace) {
        SchemaContainer result = this.getContainer(namespace);
        if (result == null) {
            this.addContainer(namespace);
            result = this.getContainer(namespace);
        }
        return result;
    }

    private <T extends SchemaComponent.Ref> void buildContainersHelper(Map<QName, SchemaComponent.Ref> elements, BiConsumer<SchemaContainer, T> adder) {
        elements.forEach((k, v) -> adder.accept(this.getContainerNonNull(k.getNamespaceURI()), v));
    }

    private <T extends SchemaComponent.Ref> void buildContainersHelper(List<SchemaComponent.Ref> refs, List<QName> names, BiConsumer<SchemaContainer, T> adder) {
        Iterator<SchemaComponent.Ref> it = refs.iterator();
        Iterator<QName> itname = names.iterator();
        while (it.hasNext()) {
            String ns = itname.next().getNamespaceURI();
            SchemaContainer sc = this.getContainerNonNull(ns);
            adder.accept(sc, (SchemaContainer)((Object)it.next()));
        }
    }

    private void buildContainers(List<QName> redefTypeNames, List<QName> redefModelGroupNames, List<QName> redefAttributeGroupNames) {
        this.buildContainersHelper(this._globalElements, SchemaContainer::addGlobalElement);
        this.buildContainersHelper(this._globalAttributes, SchemaContainer::addGlobalAttribute);
        this.buildContainersHelper(this._modelGroups, SchemaContainer::addModelGroup);
        this.buildContainersHelper(this._attributeGroups, SchemaContainer::addAttributeGroup);
        this.buildContainersHelper(this._identityConstraints, SchemaContainer::addIdentityConstraint);
        this.buildContainersHelper(this._globalTypes, SchemaContainer::addGlobalType);
        this.buildContainersHelper(this._attributeTypes, SchemaContainer::addAttributeType);
        if (this._redefinedGlobalTypes != null && this._redefinedModelGroups != null && this._redefinedAttributeGroups != null) {
            assert (this._redefinedGlobalTypes.size() == redefTypeNames.size());
            this.buildContainersHelper(this._redefinedGlobalTypes, redefTypeNames, SchemaContainer::addRedefinedType);
            this.buildContainersHelper(this._redefinedModelGroups, redefModelGroupNames, SchemaContainer::addRedefinedModelGroup);
            this.buildContainersHelper(this._redefinedAttributeGroups, redefAttributeGroupNames, SchemaContainer::addRedefinedAttributeGroup);
        }
        if (this._annotations != null && !this._annotations.isEmpty()) {
            this._annotations.forEach(this.getContainerNonNull("")::addAnnotation);
        }
        this._containers.values().forEach(SchemaContainer::setImmutable);
    }

    private void fixupContainers() {
        for (SchemaContainer container : this._containers.values()) {
            container.setTypeSystem(this);
            container.setImmutable();
        }
    }

    private void assertContainersHelper(Map<QName, SchemaComponent.Ref> comp, Function<SchemaContainer, List<? extends SchemaComponent>> fun, Function<List<? extends SchemaComponent>, ? extends Map<QName, SchemaComponent.Ref>> fun2) {
        Map<QName, SchemaComponent.Ref> temp = this._containers.values().stream().map(fun).map(fun2 == null ? SchemaTypeSystemImpl::buildComponentRefMap : fun2).map(Map::entrySet).flatMap(Collection::stream).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assert (comp.equals(temp));
    }

    private void assertContainersHelper(List<? extends SchemaComponent.Ref> comp, Function<SchemaContainer, List<? extends SchemaComponent>> fun) {
        Set temp = this._containers.values().stream().map(fun).map(SchemaTypeSystemImpl::buildComponentRefList).flatMap(Collection::stream).collect(Collectors.toSet());
        assert (new HashSet<SchemaComponent.Ref>(comp).equals(temp));
    }

    private void assertContainersSynchronized() {
        boolean assertEnabled = false;
        if (!$assertionsDisabled) {
            assertEnabled = true;
            if (!true) {
                throw new AssertionError();
            }
        }
        if (!assertEnabled) {
            return;
        }
        this.assertContainersHelper(this._globalElements, SchemaContainer::globalElements, null);
        this.assertContainersHelper(this._globalAttributes, SchemaContainer::globalAttributes, null);
        this.assertContainersHelper(this._modelGroups, SchemaContainer::modelGroups, null);
        this.assertContainersHelper(this._modelGroups, SchemaContainer::modelGroups, null);
        this.assertContainersHelper(this._redefinedModelGroups, SchemaContainer::redefinedModelGroups);
        this.assertContainersHelper(this._attributeGroups, SchemaContainer::attributeGroups, null);
        this.assertContainersHelper(this._redefinedAttributeGroups, SchemaContainer::redefinedAttributeGroups);
        this.assertContainersHelper(this._globalTypes, SchemaContainer::globalTypes, null);
        this.assertContainersHelper(this._redefinedGlobalTypes, SchemaContainer::redefinedGlobalTypes);
        this.assertContainersHelper(this._documentTypes, SchemaContainer::documentTypes, SchemaTypeSystemImpl::buildDocumentMap);
        this.assertContainersHelper(this._attributeTypes, SchemaContainer::attributeTypes, SchemaTypeSystemImpl::buildAttributeTypeMap);
        this.assertContainersHelper(this._identityConstraints, SchemaContainer::identityConstraints, null);
        Set temp3 = this._containers.values().stream().map(SchemaContainer::annotations).flatMap(Collection::stream).collect(Collectors.toSet());
        assert (new HashSet<SchemaAnnotation>(this._annotations).equals(temp3));
        Set temp4 = this._containers.values().stream().map(SchemaContainer::getNamespace).collect(Collectors.toSet());
        assert (this._namespaces.equals(temp4));
    }

    private static synchronized void nextBytes(byte[] result) {
        if (_random == null) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                try (LongUTFDataOutputStream daos = new LongUTFDataOutputStream(baos);){
                    String[] props;
                    daos.writeInt(System.identityHashCode(SchemaTypeSystemImpl.class));
                    for (String s : props = new String[]{"user.name", "user.dir", "user.timezone", "user.country", "java.class.path", "java.home", "java.vendor", "java.version", "os.version"}) {
                        String prop = SystemProperties.getProperty(s);
                        if (prop == null) continue;
                        daos.writeUTF(prop);
                        daos.writeInt(System.identityHashCode(prop));
                    }
                    daos.writeLong(Runtime.getRuntime().freeMemory());
                }
                byte[] bytes = baos.toByteArray();
                for (int i = 0; i < bytes.length; ++i) {
                    int j;
                    int n = j = i % _mask.length;
                    _mask[n] = (byte)(_mask[n] * 21);
                    int n2 = j;
                    _mask[n2] = (byte)(_mask[n2] + (byte)i);
                }
            }
            catch (IOException e) {
                XBeanDebug.LOG.atDebug().withThrowable(e).log(e.getMessage());
            }
            _random = new Random(System.currentTimeMillis());
        }
        _random.nextBytes(result);
        int i = 0;
        while (i < result.length) {
            int j = i & _mask.length;
            int n = i++;
            result[n] = (byte)(result[n] ^ _mask[j]);
        }
    }

    public SchemaTypeSystemImpl(String nameForSystem) {
        if (nameForSystem == null) {
            byte[] bytes = new byte[16];
            SchemaTypeSystemImpl.nextBytes(bytes);
            nameForSystem = "s" + new String(HexBin.encode(bytes), StandardCharsets.ISO_8859_1);
        }
        this._name = METADATA_PACKAGE_GEN.replace('/', '.') + ".system." + nameForSystem;
        this._classloader = null;
    }

    public void loadFromStscState(StscState state) {
        assert (this._classloader == null);
        this._localHandles = new SchemaTypePool(this.getTypeSystem());
        this._globalElements = SchemaTypeSystemImpl.buildComponentRefMap(state.globalElements());
        this._globalAttributes = SchemaTypeSystemImpl.buildComponentRefMap(state.globalAttributes());
        this._modelGroups = SchemaTypeSystemImpl.buildComponentRefMap(state.modelGroups());
        this._redefinedModelGroups = SchemaTypeSystemImpl.buildComponentRefList(state.redefinedModelGroups());
        this._attributeGroups = SchemaTypeSystemImpl.buildComponentRefMap(state.attributeGroups());
        this._redefinedAttributeGroups = SchemaTypeSystemImpl.buildComponentRefList(state.redefinedAttributeGroups());
        this._globalTypes = SchemaTypeSystemImpl.buildComponentRefMap(state.globalTypes());
        this._redefinedGlobalTypes = SchemaTypeSystemImpl.buildComponentRefList(state.redefinedGlobalTypes());
        this._documentTypes = SchemaTypeSystemImpl.buildDocumentMap(state.documentTypes());
        this._attributeTypes = SchemaTypeSystemImpl.buildAttributeTypeMap(state.attributeTypes());
        this._typeRefsByClassname = this.buildTypeRefsByClassname(state.typesByClassname());
        this._identityConstraints = SchemaTypeSystemImpl.buildComponentRefMap(state.idConstraints());
        this._annotations = state.annotations();
        this._namespaces = new HashSet<String>(Arrays.asList(state.getNamespaces()));
        this._containers = state.getContainerMap();
        this.fixupContainers();
        this.assertContainersSynchronized();
        this.setDependencies(state.getDependencies());
    }

    final SchemaTypeSystemImpl getTypeSystem() {
        return this;
    }

    void setDependencies(SchemaDependencies deps) {
        this._deps = deps;
    }

    SchemaDependencies getDependencies() {
        return this._deps;
    }

    public boolean isIncomplete() {
        return this._incomplete;
    }

    void setIncomplete(boolean incomplete) {
        this._incomplete = incomplete;
    }

    @Override
    public void saveToDirectory(File classDir) {
        this.save(new FilerImpl(classDir, null, null, false, false));
    }

    @Override
    public void save(Filer filer) {
        if (this._incomplete) {
            throw new IllegalStateException("Incomplete SchemaTypeSystems cannot be saved.");
        }
        if (filer == null) {
            throw new IllegalArgumentException("filer must not be null");
        }
        this._filer = filer;
        this._localHandles.startWriteMode();
        this.saveTypesRecursively(this.globalTypes());
        this.saveTypesRecursively(this.documentTypes());
        this.saveTypesRecursively(this.attributeTypes());
        this.saveGlobalElements(this.globalElements());
        this.saveGlobalAttributes(this.globalAttributes());
        this.saveModelGroups(this.modelGroups());
        this.saveAttributeGroups(this.attributeGroups());
        this.saveIdentityConstraints(this.identityConstraints());
        this.saveTypesRecursively(this.redefinedGlobalTypes());
        this.saveModelGroups(this.redefinedModelGroups());
        this.saveAttributeGroups(this.redefinedAttributeGroups());
        this.saveIndex();
        this.savePointers();
    }

    void saveTypesRecursively(SchemaType[] types) {
        for (SchemaType type : types) {
            if (type.getTypeSystem() != this.getTypeSystem()) continue;
            this.saveType(type);
            this.saveTypesRecursively(type.getAnonymousTypes());
        }
    }

    public void saveGlobalElements(SchemaGlobalElement[] elts) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        for (SchemaGlobalElement elt : elts) {
            this.saveGlobalElement(elt);
        }
    }

    public void saveGlobalAttributes(SchemaGlobalAttribute[] attrs) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        for (SchemaGlobalAttribute attr : attrs) {
            this.saveGlobalAttribute(attr);
        }
    }

    public void saveModelGroups(SchemaModelGroup[] groups) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        for (SchemaModelGroup group : groups) {
            this.saveModelGroup(group);
        }
    }

    public void saveAttributeGroups(SchemaAttributeGroup[] groups) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        for (SchemaAttributeGroup group : groups) {
            this.saveAttributeGroup(group);
        }
    }

    public void saveIdentityConstraints(SchemaIdentityConstraint[] idcs) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        for (SchemaIdentityConstraint idc : idcs) {
            this.saveIdentityConstraint(idc);
        }
    }

    public void saveGlobalElement(SchemaGlobalElement elt) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        String handle = this._localHandles.handleForElement(elt);
        XsbReader saver = new XsbReader(this.getTypeSystem(), handle);
        saver.writeParticleData((SchemaParticle)((Object)elt));
        saver.writeString(elt.getSourceName());
        saver.writeRealHeader(handle, 3);
        saver.writeParticleData((SchemaParticle)((Object)elt));
        saver.writeString(elt.getSourceName());
        saver.writeEnd();
    }

    public void saveGlobalAttribute(SchemaGlobalAttribute attr) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        String handle = this._localHandles.handleForAttribute(attr);
        XsbReader saver = new XsbReader(this.getTypeSystem(), handle);
        saver.writeAttributeData(attr);
        saver.writeString(attr.getSourceName());
        saver.writeRealHeader(handle, 4);
        saver.writeAttributeData(attr);
        saver.writeString(attr.getSourceName());
        saver.writeEnd();
    }

    public void saveModelGroup(SchemaModelGroup grp) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        String handle = this._localHandles.handleForModelGroup(grp);
        XsbReader saver = new XsbReader(this.getTypeSystem(), handle);
        saver.writeModelGroupData(grp);
        saver.writeRealHeader(handle, 6);
        saver.writeModelGroupData(grp);
        saver.writeEnd();
    }

    public void saveAttributeGroup(SchemaAttributeGroup grp) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        String handle = this._localHandles.handleForAttributeGroup(grp);
        XsbReader saver = new XsbReader(this.getTypeSystem(), handle);
        saver.writeAttributeGroupData(grp);
        saver.writeRealHeader(handle, 7);
        saver.writeAttributeGroupData(grp);
        saver.writeEnd();
    }

    public void saveIdentityConstraint(SchemaIdentityConstraint idc) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        String handle = this._localHandles.handleForIdentityConstraint(idc);
        XsbReader saver = new XsbReader(this.getTypeSystem(), handle);
        saver.writeIdConstraintData(idc);
        saver.writeRealHeader(handle, 8);
        saver.writeIdConstraintData(idc);
        saver.writeEnd();
    }

    void saveType(SchemaType type) {
        String handle = this._localHandles.handleForType(type);
        XsbReader saver = new XsbReader(this.getTypeSystem(), handle);
        saver.writeTypeData(type);
        saver.writeRealHeader(handle, 2);
        saver.writeTypeData(type);
        saver.writeEnd();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static String crackPointer(InputStream stream) {
        try (LongUTFDataInputStream input = new LongUTFDataInputStream(stream);){
            short actualfiletype;
            int magic = input.readInt();
            if (magic != -629491010) {
                String string = null;
                return string;
            }
            short majorver = input.readShort();
            short minorver = input.readShort();
            if (majorver != 2) {
                String string = null;
                return string;
            }
            if (minorver > 24) {
                String string = null;
                return string;
            }
            if (minorver >= 18) {
                input.readShort();
            }
            if ((actualfiletype = input.readShort()) != 5) {
                String string = null;
                return string;
            }
            StringPool stringPool = new StringPool("pointer", "unk");
            stringPool.readFrom(input);
            String string = stringPool.stringForCode(input.readShort());
            return string;
        }
        catch (IOException e) {
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SchemaType typeForHandle(String handle) {
        Map<String, SchemaComponent> map = this._resolvedHandles;
        synchronized (map) {
            return (SchemaType)this._resolvedHandles.get(handle);
        }
    }

    @Override
    public SchemaType typeForClassname(String classname) {
        SchemaType.Ref ref = (SchemaType.Ref)this._typeRefsByClassname.get(classname);
        return ref != null ? ref.get() : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SchemaComponent resolveHandle(String handle) {
        SchemaComponent result;
        Map<String, SchemaComponent> map = this._resolvedHandles;
        synchronized (map) {
            result = this._resolvedHandles.get(handle);
        }
        if (result == null) {
            XsbReader reader = new XsbReader(this.getTypeSystem(), handle, 65535);
            int filetype = reader.getActualFiletype();
            switch (filetype) {
                case 2: {
                    XBeanDebug.LOG.atTrace().log("Resolving type for handle {}", (Object)handle);
                    result = reader.finishLoadingType();
                    break;
                }
                case 3: {
                    XBeanDebug.LOG.atTrace().log("Resolving element for handle {}", (Object)handle);
                    result = reader.finishLoadingElement();
                    break;
                }
                case 4: {
                    XBeanDebug.LOG.atTrace().log("Resolving attribute for handle {}", (Object)handle);
                    result = reader.finishLoadingAttribute();
                    break;
                }
                case 6: {
                    XBeanDebug.LOG.atTrace().log("Resolving model group for handle {}", (Object)handle);
                    result = reader.finishLoadingModelGroup();
                    break;
                }
                case 7: {
                    XBeanDebug.LOG.atTrace().log("Resolving attribute group for handle {}", (Object)handle);
                    result = reader.finishLoadingAttributeGroup();
                    break;
                }
                case 8: {
                    XBeanDebug.LOG.atTrace().log("Resolving id constraint for handle {}", (Object)handle);
                    result = reader.finishLoadingIdentityConstraint();
                    break;
                }
                default: {
                    throw new IllegalStateException("Illegal handle type");
                }
            }
            Map<String, SchemaComponent> map2 = this._resolvedHandles;
            synchronized (map2) {
                if (!this._resolvedHandles.containsKey(handle)) {
                    this._resolvedHandles.put(handle, result);
                } else {
                    result = this._resolvedHandles.get(handle);
                }
            }
        }
        return result;
    }

    @Override
    public void resolve() {
        XBeanDebug.LOG.atTrace().log("Resolve called type system {}", (Object)this._name);
        if (this._allNonGroupHandlesResolved) {
            return;
        }
        XBeanDebug.LOG.atTrace().log("Resolving all handles for type system {}", (Object)this._name);
        ArrayList<SchemaComponent.Ref> refs = new ArrayList<SchemaComponent.Ref>();
        refs.addAll(this._globalElements.values());
        refs.addAll(this._globalAttributes.values());
        refs.addAll(this._globalTypes.values());
        refs.addAll(this._documentTypes.values());
        refs.addAll(this._attributeTypes.values());
        refs.addAll(this._identityConstraints.values());
        for (SchemaComponent.Ref ref : refs) {
            ref.getComponent();
        }
        XBeanDebug.LOG.atTrace().log("Finished resolving type system {}", (Object)this._name);
        this._allNonGroupHandlesResolved = true;
    }

    @Override
    public boolean isNamespaceDefined(String namespace) {
        return this._namespaces.contains(namespace);
    }

    @Override
    public SchemaType.Ref findTypeRef(QName name) {
        return (SchemaType.Ref)this._globalTypes.get(name);
    }

    @Override
    public SchemaType.Ref findDocumentTypeRef(QName name) {
        return (SchemaType.Ref)this._documentTypes.get(name);
    }

    @Override
    public SchemaType.Ref findAttributeTypeRef(QName name) {
        return (SchemaType.Ref)this._attributeTypes.get(name);
    }

    @Override
    public SchemaGlobalElement.Ref findElementRef(QName name) {
        return (SchemaGlobalElement.Ref)this._globalElements.get(name);
    }

    @Override
    public SchemaGlobalAttribute.Ref findAttributeRef(QName name) {
        return (SchemaGlobalAttribute.Ref)this._globalAttributes.get(name);
    }

    @Override
    public SchemaModelGroup.Ref findModelGroupRef(QName name) {
        return (SchemaModelGroup.Ref)this._modelGroups.get(name);
    }

    @Override
    public SchemaAttributeGroup.Ref findAttributeGroupRef(QName name) {
        return (SchemaAttributeGroup.Ref)this._attributeGroups.get(name);
    }

    @Override
    public SchemaIdentityConstraint.Ref findIdentityConstraintRef(QName name) {
        return (SchemaIdentityConstraint.Ref)this._identityConstraints.get(name);
    }

    private static <T, U> U[] refHelper(Map<QName, SchemaComponent.Ref> map, Function<T, U> fun, IntFunction<U[]> target, U[] emptyTarget) {
        return SchemaTypeSystemImpl.refHelper(map == null ? null : map.values(), fun, target, emptyTarget);
    }

    private static <T, U> U[] refHelper(Collection<SchemaComponent.Ref> list, Function<T, U> fun, IntFunction<U[]> target, U[] emptyTarget) {
        return list == null || list.isEmpty() ? emptyTarget : list.stream().map(e -> e).map(fun).toArray(target);
    }

    @Override
    public SchemaType[] globalTypes() {
        return SchemaTypeSystemImpl.refHelper(this._globalTypes, SchemaType.Ref::get, SchemaType[]::new, EMPTY_ST_ARRAY);
    }

    public SchemaType[] redefinedGlobalTypes() {
        return SchemaTypeSystemImpl.refHelper(this._redefinedGlobalTypes, SchemaType.Ref::get, SchemaType[]::new, EMPTY_ST_ARRAY);
    }

    @Override
    public InputStream getSourceAsStream(String sourceName) {
        if (!sourceName.startsWith("/")) {
            sourceName = "/" + sourceName;
        }
        return this._resourceLoader.getResourceAsStream(this.getMetadataPath() + "/src" + sourceName);
    }

    SchemaContainer[] containers() {
        return this._containers.values().toArray(new SchemaContainer[0]);
    }

    @Override
    public SchemaType[] documentTypes() {
        return SchemaTypeSystemImpl.refHelper(this._documentTypes, SchemaType.Ref::get, SchemaType[]::new, EMPTY_ST_ARRAY);
    }

    @Override
    public SchemaType[] attributeTypes() {
        return SchemaTypeSystemImpl.refHelper(this._attributeTypes, SchemaType.Ref::get, SchemaType[]::new, EMPTY_ST_ARRAY);
    }

    @Override
    public SchemaGlobalElement[] globalElements() {
        return SchemaTypeSystemImpl.refHelper(this._globalElements, SchemaGlobalElement.Ref::get, SchemaGlobalElement[]::new, EMPTY_GE_ARRAY);
    }

    @Override
    public SchemaGlobalAttribute[] globalAttributes() {
        return SchemaTypeSystemImpl.refHelper(this._globalAttributes, SchemaGlobalAttribute.Ref::get, SchemaGlobalAttribute[]::new, EMPTY_GA_ARRAY);
    }

    @Override
    public SchemaModelGroup[] modelGroups() {
        return SchemaTypeSystemImpl.refHelper(this._modelGroups, SchemaModelGroup.Ref::get, SchemaModelGroup[]::new, EMPTY_MG_ARRAY);
    }

    public SchemaModelGroup[] redefinedModelGroups() {
        return SchemaTypeSystemImpl.refHelper(this._redefinedModelGroups, SchemaModelGroup.Ref::get, SchemaModelGroup[]::new, EMPTY_MG_ARRAY);
    }

    @Override
    public SchemaAttributeGroup[] attributeGroups() {
        return SchemaTypeSystemImpl.refHelper(this._attributeGroups, SchemaAttributeGroup.Ref::get, SchemaAttributeGroup[]::new, EMPTY_AG_ARRAY);
    }

    public SchemaAttributeGroup[] redefinedAttributeGroups() {
        return SchemaTypeSystemImpl.refHelper(this._redefinedAttributeGroups, SchemaAttributeGroup.Ref::get, SchemaAttributeGroup[]::new, EMPTY_AG_ARRAY);
    }

    @Override
    public SchemaAnnotation[] annotations() {
        return this._annotations == null || this._annotations.isEmpty() ? EMPTY_ANN_ARRAY : this._annotations.toArray(EMPTY_ANN_ARRAY);
    }

    public SchemaIdentityConstraint[] identityConstraints() {
        return SchemaTypeSystemImpl.refHelper(this._identityConstraints, SchemaIdentityConstraint.Ref::get, SchemaIdentityConstraint[]::new, EMPTY_IC_ARRAY);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this._classloader;
    }

    public String handleForType(SchemaType type) {
        return this._localHandles.handleForType(type);
    }

    @Override
    public String getName() {
        return this._name;
    }

    public String getMetadataPath() {
        Matcher m = packPat.matcher(this._name);
        String n = m.find() ? m.group(1) : this._name;
        return n.replace('.', '/');
    }

    String getBasePackage() {
        return SchemaTypeSystemImpl.nameToPathString(this._name);
    }

    SchemaTypeLoader getLinker() {
        return this._linker;
    }

    SchemaTypePool getTypePool() {
        return this._localHandles;
    }

    Set<String> getNamespaces() {
        return this._namespaces;
    }

    Map<String, SchemaComponent.Ref> getTypeRefsByClassname() {
        return this._typeRefsByClassname;
    }

    OutputStream getSaverStream(String name, String handle) {
        try {
            return this._filer.createBinaryFile(name);
        }
        catch (IOException e) {
            throw new SchemaTypeLoaderException(e.getMessage(), this.getName(), handle, 9, e);
        }
    }

    InputStream getLoaderStream(String resourcename) {
        return this._resourceLoader.getResourceAsStream(resourcename);
    }

    static {
        _mask = new byte[16];
        SINGLE_ZERO_BYTE = new byte[]{0};
    }

    static class StringPool {
        private final List<String> intsToStrings = new ArrayList<String>();
        private final Map<String, Integer> stringsToInts = new HashMap<String, Integer>();
        private final String _handle;
        private final String _name;

        StringPool(String handle, String name) {
            this._handle = handle;
            this._name = name;
            this.intsToStrings.add(null);
        }

        int codeForString(String str) {
            if (str == null) {
                return 0;
            }
            Integer result = this.stringsToInts.get(str);
            if (result == null) {
                result = this.intsToStrings.size();
                this.intsToStrings.add(str);
                this.stringsToInts.put(str, result);
            }
            return result;
        }

        String stringForCode(int code) {
            return code == 0 ? null : this.intsToStrings.get(code);
        }

        void writeTo(LongUTFDataOutputStream output) {
            try {
                int cnt = this.intsToStrings.size();
                output.writeShortOrInt(cnt);
                boolean isNext = false;
                for (String str : this.intsToStrings) {
                    if (isNext) {
                        output.writeLongUTF(str);
                    }
                    isNext = true;
                }
            }
            catch (IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), this._name, this._handle, 9, e);
            }
        }

        void readFrom(LongUTFDataInputStream input) {
            if (this.intsToStrings.size() != 1 || this.stringsToInts.size() != 0) {
                throw new IllegalStateException();
            }
            try {
                int size = input.readUnsignedShortOrInt();
                for (int i = 1; i < size; ++i) {
                    String str = input.readLongUTF().intern();
                    int code = this.codeForString(str);
                    if (code == i) continue;
                    throw new IllegalStateException();
                }
            }
            catch (IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage() == null ? e.getMessage() : "IO Exception", this._name, this._handle, 9, e);
            }
        }
    }
}

