/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.BindingConfig;
import org.apache.xmlbeans.Filer;
import org.apache.xmlbeans.QNameCache;
import org.apache.xmlbeans.ResourceLoader;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.impl.schema.PathResourceLoader;
import org.apache.xmlbeans.impl.schema.SchemaTypeLoaderImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemCompiler;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.w3c.dom.Node;

public final class XmlBeans {
    private static final String HOLDER_CLASS_NAME = "TypeSystemHolder";
    private static final String TYPE_SYSTEM_FIELD = "typeSystem";
    private static String XMLBEANS_TITLE = "org.apache.xmlbeans";
    private static String XMLBEANS_VERSION = "unknown";
    private static String XMLBEANS_VENDOR = "Apache Software Foundation";
    private static final ThreadLocal _threadLocalLoaderQNameCache;
    public static final SchemaType NO_TYPE;

    public static String getTitle() {
        return XMLBEANS_TITLE;
    }

    public static String getVendor() {
        return XMLBEANS_VENDOR;
    }

    public static String getVersion() {
        return XMLBEANS_VERSION;
    }

    public static void clearThreadLocals() {
        _threadLocalLoaderQNameCache.remove();
    }

    public static QNameCache getQNameCache() {
        SoftReference softRef = (SoftReference)_threadLocalLoaderQNameCache.get();
        QNameCache qnameCache = (QNameCache)softRef.get();
        if (qnameCache == null) {
            qnameCache = new QNameCache(32);
            _threadLocalLoaderQNameCache.set(new SoftReference<QNameCache>(qnameCache));
        }
        return qnameCache;
    }

    public static QName getQName(String localPart) {
        return XmlBeans.getQNameCache().getName("", localPart);
    }

    public static QName getQName(String namespaceUri, String localPart) {
        return XmlBeans.getQNameCache().getName(namespaceUri, localPart);
    }

    private static RuntimeException causedException(RuntimeException e, Throwable cause) {
        e.initCause(cause);
        return e;
    }

    public static String compilePath(String pathExpr) throws XmlException {
        return XmlBeans.compilePath(pathExpr, null);
    }

    public static String compilePath(String pathExpr, XmlOptions options) throws XmlException {
        return XmlBeans.getContextTypeLoader().compilePath(pathExpr, options);
    }

    public static String compileQuery(String queryExpr) throws XmlException {
        return XmlBeans.compileQuery(queryExpr, null);
    }

    public static String compileQuery(String queryExpr, XmlOptions options) throws XmlException {
        return XmlBeans.getContextTypeLoader().compileQuery(queryExpr, options);
    }

    public static SchemaTypeLoader getContextTypeLoader() {
        return SchemaTypeLoaderImpl.getContextTypeLoader();
    }

    public static SchemaTypeSystem getBuiltinTypeSystem() {
        return BuiltinSchemaTypeSystem.get();
    }

    public static XmlCursor nodeToCursor(Node n) {
        return Locale.nodeToCursor(n);
    }

    public static XmlObject nodeToXmlObject(Node n) {
        return Locale.nodeToXmlObject(n);
    }

    public static XMLStreamReader nodeToXmlStreamReader(Node n) {
        return Locale.nodeToXmlStream(n);
    }

    public static Node streamToNode(XMLStreamReader xs) {
        return Locale.streamToNode(xs);
    }

    public static SchemaTypeLoader loadXsd(XmlObject ... schemas) throws XmlException {
        return XmlBeans.loadXsd(schemas, null);
    }

    public static SchemaTypeLoader loadXsd(XmlObject[] schemas, XmlOptions options) throws XmlException {
        SchemaTypeSystemImpl sts = SchemaTypeSystemCompiler.compile(null, null, schemas, null, XmlBeans.getContextTypeLoader(), null, options);
        return sts == null ? null : XmlBeans.typeLoaderUnion(sts, XmlBeans.getContextTypeLoader());
    }

    public static SchemaTypeSystem compileXsd(XmlObject[] schemas, SchemaTypeLoader typepath, XmlOptions options) throws XmlException {
        return XmlBeans.compileXmlBeans(null, null, schemas, null, typepath, null, options);
    }

    public static SchemaTypeSystem compileXsd(SchemaTypeSystem system, XmlObject[] schemas, SchemaTypeLoader typepath, XmlOptions options) throws XmlException {
        return XmlBeans.compileXmlBeans(null, system, schemas, null, typepath, null, options);
    }

    public static SchemaTypeSystem compileXmlBeans(String name, SchemaTypeSystem system, XmlObject[] schemas, BindingConfig config, SchemaTypeLoader typepath, Filer filer, XmlOptions options) throws XmlException {
        return SchemaTypeSystemCompiler.compile(name, system, schemas, config, typepath != null ? typepath : XmlBeans.getContextTypeLoader(), filer, options);
    }

    public static SchemaTypeLoader typeLoaderUnion(SchemaTypeLoader ... typeLoaders) {
        return typeLoaders.length == 1 ? typeLoaders[0] : SchemaTypeLoaderImpl.build(typeLoaders, null, null);
    }

    public static SchemaTypeLoader typeLoaderForClassLoader(ClassLoader loader) {
        return SchemaTypeLoaderImpl.build(null, null, loader);
    }

    public static SchemaTypeLoader typeLoaderForResource(ResourceLoader resourceLoader) {
        return SchemaTypeLoaderImpl.build(null, resourceLoader, null);
    }

    public static SchemaTypeSystem typeSystemForClassLoader(ClassLoader loader, String stsName) {
        try {
            ClassLoader cl = loader == null ? Thread.currentThread().getContextClassLoader() : loader;
            Class<?> clazz = cl.loadClass(stsName + "." + HOLDER_CLASS_NAME);
            SchemaTypeSystem sts = (SchemaTypeSystem)clazz.getDeclaredField(TYPE_SYSTEM_FIELD).get(null);
            if (sts == null) {
                throw new RuntimeException("SchemaTypeSystem is null for field typeSystem on class with name " + stsName + "." + HOLDER_CLASS_NAME + ". Please verify the version of xmlbeans.jar is correct.");
            }
            return sts;
        }
        catch (ClassNotFoundException e) {
            throw XmlBeans.causedException(new RuntimeException("Cannot load SchemaTypeSystem. Unable to load class with name " + stsName + "." + HOLDER_CLASS_NAME + ". Make sure the generated binary files are on the classpath."), e);
        }
        catch (NoSuchFieldException e) {
            throw XmlBeans.causedException(new RuntimeException("Cannot find field typeSystem on class " + stsName + "." + HOLDER_CLASS_NAME + ". Please verify the version of xmlbeans.jar is correct."), e);
        }
        catch (IllegalAccessException e) {
            throw XmlBeans.causedException(new RuntimeException("Field typeSystem on class " + stsName + "." + HOLDER_CLASS_NAME + "is not accessible. Please verify the version of xmlbeans.jar is correct."), e);
        }
    }

    public static ResourceLoader resourceLoaderForPath(File[] path) {
        return new PathResourceLoader(path);
    }

    public static SchemaType typeForClass(Class c) {
        if (c == null || !XmlObject.class.isAssignableFrom(c)) {
            return null;
        }
        try {
            Field typeField = c.getField("type");
            if (typeField == null) {
                return null;
            }
            return (SchemaType)typeField.get(null);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static SchemaType getNoType() {
        return BuiltinSchemaTypeSystem.getNoType();
    }

    private XmlBeans() {
    }

    static {
        Package pkg = XmlBeans.class.getPackage();
        if (pkg != null && pkg.getImplementationVersion() != null) {
            XMLBEANS_TITLE = pkg.getImplementationTitle();
            XMLBEANS_VERSION = pkg.getImplementationVersion();
            XMLBEANS_VENDOR = pkg.getImplementationVendor();
        }
        _threadLocalLoaderQNameCache = new ThreadLocal(){

            protected Object initialValue() {
                return new SoftReference<QNameCache>(new QNameCache(32));
            }
        };
        NO_TYPE = XmlBeans.getNoType();
    }
}

