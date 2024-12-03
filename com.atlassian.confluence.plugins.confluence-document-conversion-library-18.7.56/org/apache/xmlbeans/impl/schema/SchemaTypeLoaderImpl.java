/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.ResourceLoader;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlRuntimeException;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.SystemCache;
import org.apache.xmlbeans.impl.common.XBeanDebug;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.impl.schema.SchemaTypeLoaderBase;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;

public class SchemaTypeLoaderImpl
extends SchemaTypeLoaderBase {
    private final ResourceLoader _resourceLoader;
    private final ClassLoader _classLoader;
    private final SchemaTypeLoader[] _searchPath;
    private Map<String, SchemaTypeSystemImpl> _classpathTypeSystems;
    private Map<String, SchemaTypeSystemImpl> _classLoaderTypeSystems;
    private Map<QName, Object> _elementCache;
    private Map<QName, Object> _attributeCache;
    private Map<QName, Object> _modelGroupCache;
    private Map<QName, Object> _attributeGroupCache;
    private Map<QName, Object> _idConstraintCache;
    private Map<QName, Object> _typeCache;
    private Map<QName, Object> _documentCache;
    private Map<QName, Object> _attributeTypeCache;
    private Map<String, Object> _classnameCache;
    private final String _metadataPath;
    public static String METADATA_PACKAGE_LOAD = SchemaTypeSystemImpl.METADATA_PACKAGE_GEN;
    private static final Object CACHED_NOT_FOUND = new Object();
    private static final String[] basePackage = new String[]{"org.apache.xmlbeans.metadata", "schemaorg_apache_xmlbeans"};
    private static final String[] baseSchemas = new String[]{"sXMLCONFIG", "sXMLLANG", "sXMLSCHEMA", "sXMLTOOLS"};
    private static final SchemaTypeLoader[] EMPTY_SCHEMATYPELOADER_ARRAY = new SchemaTypeLoader[0];

    public static SchemaTypeLoaderImpl getContextTypeLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        SchemaTypeLoaderImpl result = (SchemaTypeLoaderImpl)SystemCache.get().getFromTypeLoaderCache(cl);
        if (result == null) {
            result = new SchemaTypeLoaderImpl(new SchemaTypeLoader[]{BuiltinSchemaTypeSystem.get()}, null, cl, null);
            SystemCache.get().addToTypeLoaderCache(result, cl);
        }
        return result;
    }

    public static SchemaTypeLoader build(SchemaTypeLoader[] searchPath, ResourceLoader resourceLoader, ClassLoader classLoader) {
        return SchemaTypeLoaderImpl.build(searchPath, resourceLoader, classLoader, null);
    }

    public static SchemaTypeLoader build(SchemaTypeLoader[] searchPath, ResourceLoader resourceLoader, ClassLoader classLoader, String metadataPath) {
        SubLoaderList list = new SubLoaderList();
        list.add(searchPath);
        ClassLoader cl = classLoader == null ? SchemaDocument.class.getClassLoader() : classLoader;
        for (String prefix : basePackage) {
            for (String holder : baseSchemas) {
                String clName = prefix + ".system." + holder + ".TypeSystemHolder";
                try {
                    Class<?> cls = Class.forName(clName, true, cl);
                    list.add((SchemaTypeLoader)cls.getDeclaredField("typeSystem").get(null));
                }
                catch (ClassNotFoundException cls) {
                }
                catch (Exception e) {
                    throw new XmlRuntimeException(e);
                }
            }
        }
        return new SchemaTypeLoaderImpl(list.toArray(), resourceLoader, classLoader, metadataPath);
    }

    private SchemaTypeLoaderImpl(SchemaTypeLoader[] searchPath, ResourceLoader resourceLoader, ClassLoader classLoader, String metadataPath) {
        this._searchPath = searchPath == null ? EMPTY_SCHEMATYPELOADER_ARRAY : searchPath;
        this._resourceLoader = resourceLoader;
        this._classLoader = classLoader;
        if (metadataPath != null) {
            this._metadataPath = metadataPath;
        } else {
            String path26 = "schema" + METADATA_PACKAGE_LOAD.replace("/", "_");
            this._metadataPath = SchemaTypeLoaderImpl.isPath30(this._classLoader) ? METADATA_PACKAGE_LOAD : path26;
        }
        this.initCaches();
    }

    private static boolean isPath30(ClassLoader loader) {
        String path30 = METADATA_PACKAGE_LOAD + "/system";
        ClassLoader cl = loader != null ? loader : SchemaDocument.class.getClassLoader();
        return cl.getResource(path30) != null;
    }

    private void initCaches() {
        this._classpathTypeSystems = Collections.synchronizedMap(new HashMap());
        this._classLoaderTypeSystems = Collections.synchronizedMap(new HashMap());
        this._elementCache = Collections.synchronizedMap(new HashMap());
        this._attributeCache = Collections.synchronizedMap(new HashMap());
        this._modelGroupCache = Collections.synchronizedMap(new HashMap());
        this._attributeGroupCache = Collections.synchronizedMap(new HashMap());
        this._idConstraintCache = Collections.synchronizedMap(new HashMap());
        this._typeCache = Collections.synchronizedMap(new HashMap());
        this._documentCache = Collections.synchronizedMap(new HashMap());
        this._attributeTypeCache = Collections.synchronizedMap(new HashMap());
        this._classnameCache = Collections.synchronizedMap(new HashMap());
    }

    SchemaTypeSystemImpl typeSystemForComponent(String searchdir, QName name) {
        String searchfor = searchdir + QNameHelper.hexsafedir(name) + ".xsb";
        String tsname = null;
        if (this._resourceLoader != null) {
            tsname = SchemaTypeLoaderImpl.crackEntry(this._resourceLoader, searchfor);
        }
        if (this._classLoader != null) {
            tsname = SchemaTypeLoaderImpl.crackEntry(this._classLoader, searchfor);
        }
        if (tsname != null) {
            return (SchemaTypeSystemImpl)this.typeSystemForName(tsname);
        }
        return null;
    }

    public SchemaTypeSystem typeSystemForName(String name) {
        SchemaTypeSystemImpl result;
        if (this._resourceLoader != null && (result = this.getTypeSystemOnClasspath(name)) != null) {
            return result;
        }
        if (this._classLoader != null && (result = this.getTypeSystemOnClassloader(name)) != null) {
            return result;
        }
        return null;
    }

    SchemaTypeSystemImpl typeSystemForClassname(String searchdir, String name) {
        String tsname;
        String searchfor = searchdir + name.replace('.', '/') + ".xsb";
        if (this._resourceLoader != null && (tsname = SchemaTypeLoaderImpl.crackEntry(this._resourceLoader, searchfor)) != null) {
            return this.getTypeSystemOnClasspath(tsname);
        }
        if (this._classLoader != null && (tsname = SchemaTypeLoaderImpl.crackEntry(this._classLoader, searchfor)) != null) {
            return this.getTypeSystemOnClassloader(tsname);
        }
        return null;
    }

    SchemaTypeSystemImpl getTypeSystemOnClasspath(String name) {
        return this._classpathTypeSystems.computeIfAbsent(name, n -> new SchemaTypeSystemImpl(this._resourceLoader, (String)n, this));
    }

    SchemaTypeSystemImpl getTypeSystemOnClassloader(String name) {
        XBeanDebug.LOG.atTrace().log("Finding type system {} on classloader", (Object)name);
        SchemaTypeSystemImpl result = this._classLoaderTypeSystems.get(name);
        if (result == null) {
            XBeanDebug.LOG.atTrace().log("Type system {}} not cached - consulting field", (Object)name);
            result = SchemaTypeSystemImpl.forName(name, this._classLoader);
            this._classLoaderTypeSystems.put(name, result);
        }
        return result;
    }

    static String crackEntry(ResourceLoader loader, String searchfor) {
        InputStream is = loader.getResourceAsStream(searchfor);
        return is == null ? null : SchemaTypeLoaderImpl.crackPointer(is);
    }

    static String crackEntry(ClassLoader loader, String searchfor) {
        InputStream stream = loader.getResourceAsStream(searchfor);
        return stream == null ? null : SchemaTypeLoaderImpl.crackPointer(stream);
    }

    static String crackPointer(InputStream stream) {
        return SchemaTypeSystemImpl.crackPointer(stream);
    }

    @Override
    public boolean isNamespaceDefined(String namespace) {
        for (SchemaTypeLoader schemaTypeLoader : this._searchPath) {
            if (!schemaTypeLoader.isNamespaceDefined(namespace)) continue;
            return true;
        }
        SchemaTypeSystemImpl sts = this.typeSystemForComponent(this._metadataPath + "/namespace/", new QName(namespace, "xmlns"));
        return sts != null;
    }

    @Override
    public SchemaType.Ref findTypeRef(QName name) {
        Object cached = this._typeCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType.Ref result = (SchemaType.Ref)cached;
        if (result == null) {
            SchemaTypeSystemImpl ts;
            SchemaTypeLoader schemaTypeLoader;
            SchemaTypeLoader[] schemaTypeLoaderArray = this._searchPath;
            int n = schemaTypeLoaderArray.length;
            for (int i = 0; i < n && null == (result = (schemaTypeLoader = schemaTypeLoaderArray[i]).findTypeRef(name)); ++i) {
            }
            if (result == null && (ts = this.typeSystemForComponent(this._metadataPath + "/type/", name)) != null) {
                result = ts.findTypeRef(name);
                assert (result != null) : "Type system registered type " + QNameHelper.pretty(name) + " but does not return it";
            }
            this._typeCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    @Override
    public SchemaType typeForClassname(String classname) {
        Object cached = this._classnameCache.get(classname = classname.replace('$', '.'));
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType result = (SchemaType)cached;
        if (result == null) {
            SchemaTypeSystemImpl ts;
            SchemaTypeLoader schemaTypeLoader;
            SchemaTypeLoader[] schemaTypeLoaderArray = this._searchPath;
            int n = schemaTypeLoaderArray.length;
            for (int i = 0; i < n && null == (result = (schemaTypeLoader = schemaTypeLoaderArray[i]).typeForClassname(classname)); ++i) {
            }
            if (result == null && (ts = this.typeSystemForClassname(this._metadataPath + "/javaname/", classname)) != null) {
                result = ts.typeForClassname(classname);
                assert (result != null) : "Type system registered type " + classname + " but does not return it";
            }
            this._classnameCache.put(classname, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    @Override
    public SchemaType.Ref findDocumentTypeRef(QName name) {
        Object cached = this._documentCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType.Ref result = (SchemaType.Ref)cached;
        if (result == null) {
            SchemaTypeSystemImpl ts;
            SchemaTypeLoader schemaTypeLoader;
            SchemaTypeLoader[] schemaTypeLoaderArray = this._searchPath;
            int n = schemaTypeLoaderArray.length;
            for (int i = 0; i < n && null == (result = (schemaTypeLoader = schemaTypeLoaderArray[i]).findDocumentTypeRef(name)); ++i) {
            }
            if (result == null && (ts = this.typeSystemForComponent(this._metadataPath + "/element/", name)) != null) {
                result = ts.findDocumentTypeRef(name);
                assert (result != null) : "Type system registered element " + QNameHelper.pretty(name) + " but does not contain document type";
            }
            this._documentCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    @Override
    public SchemaType.Ref findAttributeTypeRef(QName name) {
        Object cached = this._attributeTypeCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType.Ref result = (SchemaType.Ref)cached;
        if (result == null) {
            SchemaTypeSystemImpl ts;
            SchemaTypeLoader schemaTypeLoader;
            SchemaTypeLoader[] schemaTypeLoaderArray = this._searchPath;
            int n = schemaTypeLoaderArray.length;
            for (int i = 0; i < n && null == (result = (schemaTypeLoader = schemaTypeLoaderArray[i]).findAttributeTypeRef(name)); ++i) {
            }
            if (result == null && (ts = this.typeSystemForComponent(this._metadataPath + "/attribute/", name)) != null) {
                result = ts.findAttributeTypeRef(name);
                assert (result != null) : "Type system registered attribute " + QNameHelper.pretty(name) + " but does not contain attribute type";
            }
            this._attributeTypeCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    @Override
    public SchemaGlobalElement.Ref findElementRef(QName name) {
        Object cached = this._elementCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaGlobalElement.Ref result = (SchemaGlobalElement.Ref)cached;
        if (result == null) {
            SchemaTypeSystemImpl ts;
            SchemaTypeLoader schemaTypeLoader;
            SchemaTypeLoader[] schemaTypeLoaderArray = this._searchPath;
            int n = schemaTypeLoaderArray.length;
            for (int i = 0; i < n && null == (result = (schemaTypeLoader = schemaTypeLoaderArray[i]).findElementRef(name)); ++i) {
            }
            if (result == null && (ts = this.typeSystemForComponent(this._metadataPath + "/element/", name)) != null) {
                result = ts.findElementRef(name);
                assert (result != null) : "Type system registered element " + QNameHelper.pretty(name) + " but does not return it";
            }
            this._elementCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    @Override
    public SchemaGlobalAttribute.Ref findAttributeRef(QName name) {
        Object cached = this._attributeCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaGlobalAttribute.Ref result = (SchemaGlobalAttribute.Ref)cached;
        if (result == null) {
            SchemaTypeSystemImpl ts;
            SchemaTypeLoader schemaTypeLoader;
            SchemaTypeLoader[] schemaTypeLoaderArray = this._searchPath;
            int n = schemaTypeLoaderArray.length;
            for (int i = 0; i < n && null == (result = (schemaTypeLoader = schemaTypeLoaderArray[i]).findAttributeRef(name)); ++i) {
            }
            if (result == null && (ts = this.typeSystemForComponent(this._metadataPath + "/attribute/", name)) != null) {
                result = ts.findAttributeRef(name);
                assert (result != null) : "Type system registered attribute " + QNameHelper.pretty(name) + " but does not return it";
            }
            this._attributeCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    @Override
    public SchemaModelGroup.Ref findModelGroupRef(QName name) {
        Object cached = this._modelGroupCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaModelGroup.Ref result = (SchemaModelGroup.Ref)cached;
        if (result == null) {
            SchemaTypeSystemImpl ts;
            SchemaTypeLoader schemaTypeLoader;
            SchemaTypeLoader[] schemaTypeLoaderArray = this._searchPath;
            int n = schemaTypeLoaderArray.length;
            for (int i = 0; i < n && null == (result = (schemaTypeLoader = schemaTypeLoaderArray[i]).findModelGroupRef(name)); ++i) {
            }
            if (result == null && (ts = this.typeSystemForComponent(this._metadataPath + "/modelgroup/", name)) != null) {
                result = ts.findModelGroupRef(name);
                assert (result != null) : "Type system registered model group " + QNameHelper.pretty(name) + " but does not return it";
            }
            this._modelGroupCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    @Override
    public SchemaAttributeGroup.Ref findAttributeGroupRef(QName name) {
        Object cached = this._attributeGroupCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaAttributeGroup.Ref result = (SchemaAttributeGroup.Ref)cached;
        if (result == null) {
            SchemaTypeSystemImpl ts;
            SchemaTypeLoader schemaTypeLoader;
            SchemaTypeLoader[] schemaTypeLoaderArray = this._searchPath;
            int n = schemaTypeLoaderArray.length;
            for (int i = 0; i < n && null == (result = (schemaTypeLoader = schemaTypeLoaderArray[i]).findAttributeGroupRef(name)); ++i) {
            }
            if (result == null && (ts = this.typeSystemForComponent(this._metadataPath + "/attributegroup/", name)) != null) {
                result = ts.findAttributeGroupRef(name);
                assert (result != null) : "Type system registered attribute group " + QNameHelper.pretty(name) + " but does not return it";
            }
            this._attributeGroupCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    @Override
    public SchemaIdentityConstraint.Ref findIdentityConstraintRef(QName name) {
        Object cached = this._idConstraintCache.get(name);
        if (cached == CACHED_NOT_FOUND) {
            return null;
        }
        SchemaIdentityConstraint.Ref result = (SchemaIdentityConstraint.Ref)cached;
        if (result == null) {
            SchemaTypeSystemImpl ts;
            SchemaTypeLoader schemaTypeLoader;
            SchemaTypeLoader[] schemaTypeLoaderArray = this._searchPath;
            int n = schemaTypeLoaderArray.length;
            for (int i = 0; i < n && null == (result = (schemaTypeLoader = schemaTypeLoaderArray[i]).findIdentityConstraintRef(name)); ++i) {
            }
            if (result == null && (ts = this.typeSystemForComponent(this._metadataPath + "/identityconstraint/", name)) != null) {
                result = ts.findIdentityConstraintRef(name);
                assert (result != null) : "Type system registered identity constraint " + QNameHelper.pretty(name) + " but does not return it";
            }
            this._idConstraintCache.put(name, result == null ? CACHED_NOT_FOUND : result);
        }
        return result;
    }

    @Override
    public InputStream getSourceAsStream(String sourceName) {
        InputStream result = null;
        if (!sourceName.startsWith("/")) {
            sourceName = "/" + sourceName;
        }
        if (this._resourceLoader != null) {
            result = this._resourceLoader.getResourceAsStream(this._metadataPath + "/src" + sourceName);
        }
        if (result == null && this._classLoader != null) {
            return this._classLoader.getResourceAsStream(this._metadataPath + "/src" + sourceName);
        }
        return result;
    }

    static {
        SystemCache.set(new SchemaTypeLoaderCache());
    }

    private static class SubLoaderList {
        private final List<SchemaTypeLoader> theList = new ArrayList<SchemaTypeLoader>();
        private final Map<SchemaTypeLoader, Object> seen = new IdentityHashMap<SchemaTypeLoader, Object>();

        private SubLoaderList() {
        }

        void add(SchemaTypeLoader[] searchPath) {
            if (searchPath == null) {
                return;
            }
            for (SchemaTypeLoader stl : searchPath) {
                if (stl instanceof SchemaTypeLoaderImpl) {
                    SchemaTypeLoaderImpl sub = (SchemaTypeLoaderImpl)stl;
                    if (sub._classLoader != null || sub._resourceLoader != null) {
                        this.add(sub);
                        continue;
                    }
                    this.add(sub._searchPath);
                    continue;
                }
                this.add(stl);
            }
        }

        void add(SchemaTypeLoader loader) {
            if (loader != null && !this.seen.containsKey(loader)) {
                this.theList.add(loader);
                this.seen.put(loader, null);
            }
        }

        SchemaTypeLoader[] toArray() {
            return this.theList.toArray(EMPTY_SCHEMATYPELOADER_ARRAY);
        }
    }

    private static class SchemaTypeLoaderCache
    extends SystemCache {
        private final ThreadLocal<List<SoftReference<SchemaTypeLoaderImpl>>> _cachedTypeSystems = ThreadLocal.withInitial(ArrayList::new);

        private SchemaTypeLoaderCache() {
        }

        @Override
        public void clearThreadLocals() {
            this._cachedTypeSystems.remove();
            super.clearThreadLocals();
        }

        @Override
        public SchemaTypeLoader getFromTypeLoaderCache(ClassLoader cl) {
            List<SoftReference<SchemaTypeLoaderImpl>> a = this._cachedTypeSystems.get();
            int candidate = -1;
            SchemaTypeLoaderImpl result = null;
            for (int i = 0; i < a.size(); ++i) {
                SchemaTypeLoaderImpl tl = a.get(i).get();
                if (tl == null) {
                    a.remove(i--);
                    continue;
                }
                if (tl._classLoader != cl) continue;
                candidate = i;
                result = tl;
                break;
            }
            if (candidate > 0) {
                SoftReference<SchemaTypeLoaderImpl> t = a.get(0);
                a.set(0, a.get(candidate));
                a.set(candidate, t);
            }
            return result;
        }

        @Override
        public void addToTypeLoaderCache(SchemaTypeLoader stl, ClassLoader cl) {
            assert (stl instanceof SchemaTypeLoaderImpl && ((SchemaTypeLoaderImpl)stl)._classLoader == cl);
            List<SoftReference<SchemaTypeLoaderImpl>> a = this._cachedTypeSystems.get();
            if (a.size() > 0) {
                SoftReference<SchemaTypeLoaderImpl> t = a.get(0);
                a.set(0, new SoftReference<SchemaTypeLoaderImpl>((SchemaTypeLoaderImpl)stl));
                a.add(t);
            } else {
                a.add(new SoftReference<SchemaTypeLoaderImpl>((SchemaTypeLoaderImpl)stl));
            }
        }
    }
}

