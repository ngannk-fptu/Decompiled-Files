/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.util.JAXBResult
 */
package com.sun.xml.ws.model;

import com.oracle.xmlns.webservices.jaxws_databinding.ExistingAnnotationsType;
import com.oracle.xmlns.webservices.jaxws_databinding.JavaMethod;
import com.oracle.xmlns.webservices.jaxws_databinding.JavaParam;
import com.oracle.xmlns.webservices.jaxws_databinding.JavaWsdlMappingType;
import com.oracle.xmlns.webservices.jaxws_databinding.ObjectFactory;
import com.sun.xml.ws.model.ReflectAnnotationReader;
import com.sun.xml.ws.model.RuntimeModelerException;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ExternalMetadataReader
extends ReflectAnnotationReader {
    private static final String NAMESPACE_WEBLOGIC_WSEE_DATABINDING = "http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding";
    private static final String NAMESPACE_JAXWS_RI_EXTERNAL_METADATA = "http://xmlns.oracle.com/webservices/jaxws-databinding";
    private Map<String, JavaWsdlMappingType> readers = new HashMap<String, JavaWsdlMappingType>();

    public ExternalMetadataReader(Collection<File> files, Collection<String> resourcePaths, ClassLoader classLoader, boolean xsdValidation, boolean disableXmlSecurity) {
        JavaWsdlMappingType externalMapping;
        String namespace;
        if (files != null) {
            for (File file : files) {
                try {
                    namespace = Util.documentRootNamespace(this.newSource(file), disableXmlSecurity);
                    externalMapping = this.parseMetadata(xsdValidation, this.newSource(file), namespace, disableXmlSecurity);
                    this.readers.put(externalMapping.getJavaTypeName(), externalMapping);
                }
                catch (Exception e) {
                    throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", file.getAbsolutePath());
                }
            }
        }
        if (resourcePaths != null) {
            for (String resourcePath : resourcePaths) {
                try {
                    namespace = Util.documentRootNamespace(this.newSource(resourcePath, classLoader), disableXmlSecurity);
                    externalMapping = this.parseMetadata(xsdValidation, this.newSource(resourcePath, classLoader), namespace, disableXmlSecurity);
                    this.readers.put(externalMapping.getJavaTypeName(), externalMapping);
                }
                catch (Exception e) {
                    throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", resourcePath);
                }
            }
        }
    }

    private StreamSource newSource(String resourcePath, ClassLoader classLoader) {
        InputStream is = classLoader.getResourceAsStream(resourcePath);
        return new StreamSource(is);
    }

    private JavaWsdlMappingType parseMetadata(boolean xsdValidation, StreamSource source, String namespace, boolean disableXmlSecurity) throws JAXBException, IOException, TransformerException {
        if (NAMESPACE_WEBLOGIC_WSEE_DATABINDING.equals(namespace)) {
            return Util.transformAndRead(source, disableXmlSecurity);
        }
        if (NAMESPACE_JAXWS_RI_EXTERNAL_METADATA.equals(namespace)) {
            return Util.read(source, xsdValidation, disableXmlSecurity);
        }
        throw new RuntimeModelerException("runtime.modeler.external.metadata.unsupported.schema", namespace, Arrays.asList(NAMESPACE_WEBLOGIC_WSEE_DATABINDING, NAMESPACE_JAXWS_RI_EXTERNAL_METADATA).toString());
    }

    private StreamSource newSource(File file) {
        try {
            return new StreamSource(new FileInputStream(file));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", file.getAbsolutePath());
        }
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annType, Class<?> cls) {
        JavaWsdlMappingType r = this.reader(cls);
        return (A)(r == null ? super.getAnnotation(annType, cls) : (Annotation)Util.annotation(r, annType));
    }

    private JavaWsdlMappingType reader(Class<?> cls) {
        return this.readers.get(cls.getName());
    }

    Annotation[] getAnnotations(List<Object> objects) {
        ArrayList<Annotation> list = new ArrayList<Annotation>();
        for (Object a : objects) {
            if (!Annotation.class.isInstance(a)) continue;
            list.add((Annotation)Annotation.class.cast(a));
        }
        return list.toArray(new Annotation[list.size()]);
    }

    @Override
    public Annotation[] getAnnotations(final Class<?> c) {
        Merger<Annotation[]> merger = new Merger<Annotation[]>(this.reader(c)){

            @Override
            Annotation[] reflection() {
                return ExternalMetadataReader.super.getAnnotations(c);
            }

            @Override
            Annotation[] external() {
                return ExternalMetadataReader.this.getAnnotations(this.reader.getClassAnnotation());
            }
        };
        return (Annotation[])merger.merge();
    }

    @Override
    public Annotation[] getAnnotations(final Method m) {
        Merger<Annotation[]> merger = new Merger<Annotation[]>(this.reader(m.getDeclaringClass())){

            @Override
            Annotation[] reflection() {
                return ExternalMetadataReader.super.getAnnotations(m);
            }

            @Override
            Annotation[] external() {
                JavaMethod jm = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
                return jm == null ? new Annotation[]{} : ExternalMetadataReader.this.getAnnotations(jm.getMethodAnnotation());
            }
        };
        return (Annotation[])merger.merge();
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annType, final Method m) {
        Merger<Annotation> merger = new Merger<Annotation>(this.reader(m.getDeclaringClass())){

            @Override
            Annotation reflection() {
                return ExternalMetadataReader.super.getAnnotation(annType, m);
            }

            @Override
            Annotation external() {
                JavaMethod jm = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
                return (Annotation)Util.annotation(jm, annType);
            }
        };
        return (A)((Annotation)merger.merge());
    }

    @Override
    public Annotation[][] getParameterAnnotations(final Method m) {
        Merger<Annotation[][]> merger = new Merger<Annotation[][]>(this.reader(m.getDeclaringClass())){

            @Override
            Annotation[][] reflection() {
                return ExternalMetadataReader.super.getParameterAnnotations(m);
            }

            @Override
            Annotation[][] external() {
                JavaMethod jm = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
                Annotation[][] a = m.getParameterAnnotations();
                for (int i = 0; i < m.getParameterTypes().length; ++i) {
                    if (jm == null) continue;
                    JavaParam jp = jm.getJavaParams().getJavaParam().get(i);
                    a[i] = ExternalMetadataReader.this.getAnnotations(jp.getParamAnnotation());
                }
                return a;
            }
        };
        return (Annotation[][])merger.merge();
    }

    @Override
    public void getProperties(Map<String, Object> prop, Class<?> cls) {
        JavaWsdlMappingType r = this.reader(cls);
        if (r == null || ExistingAnnotationsType.MERGE.equals((Object)r.getExistingAnnotations())) {
            super.getProperties(prop, cls);
        }
    }

    @Override
    public void getProperties(Map<String, Object> prop, Method m) {
        JavaWsdlMappingType r = this.reader(m.getDeclaringClass());
        if (r == null || ExistingAnnotationsType.MERGE.equals((Object)r.getExistingAnnotations())) {
            super.getProperties(prop, m);
        }
        if (r != null) {
            JavaMethod jm = this.getJavaMethod(m, r);
            Element[] e = Util.annotation(jm);
            prop.put("eclipselink-oxm-xml.xml-element", this.findXmlElement(e));
        }
    }

    @Override
    public void getProperties(Map<String, Object> prop, Method m, int pos) {
        JavaWsdlMappingType r = this.reader(m.getDeclaringClass());
        if (r == null || ExistingAnnotationsType.MERGE.equals((Object)r.getExistingAnnotations())) {
            super.getProperties(prop, m, pos);
        }
        if (r != null) {
            JavaMethod jm = this.getJavaMethod(m, r);
            if (jm == null) {
                return;
            }
            JavaParam jp = jm.getJavaParams().getJavaParam().get(pos);
            Element[] e = Util.annotation(jp);
            prop.put("eclipselink-oxm-xml.xml-element", this.findXmlElement(e));
        }
    }

    JavaMethod getJavaMethod(Method method, JavaWsdlMappingType r) {
        JavaWsdlMappingType.JavaMethods javaMethods = r.getJavaMethods();
        if (javaMethods == null) {
            return null;
        }
        ArrayList<JavaMethod> sameName = new ArrayList<JavaMethod>();
        for (JavaMethod jm : javaMethods.getJavaMethod()) {
            if (!method.getName().equals(jm.getName())) continue;
            sameName.add(jm);
        }
        if (sameName.isEmpty()) {
            return null;
        }
        if (sameName.size() == 1) {
            return (JavaMethod)sameName.get(0);
        }
        Class<?>[] argCls = method.getParameterTypes();
        for (JavaMethod jm : sameName) {
            JavaMethod.JavaParams params = jm.getJavaParams();
            if (params == null || params.getJavaParam() == null || params.getJavaParam().size() != argCls.length) continue;
            int count = 0;
            for (int i = 0; i < argCls.length; ++i) {
                JavaParam jp = params.getJavaParam().get(i);
                if (!argCls[i].getName().equals(jp.getJavaType())) continue;
                ++count;
            }
            if (count != argCls.length) continue;
            return jm;
        }
        return null;
    }

    Element findXmlElement(Element[] xa) {
        if (xa == null) {
            return null;
        }
        for (Element e : xa) {
            if (e.getLocalName().equals("java-type")) {
                return e;
            }
            if (!e.getLocalName().equals("xml-element")) continue;
            return e;
        }
        return null;
    }

    static class Util {
        private static final String DATABINDING_XSD = "jaxws-databinding.xsd";
        private static final String TRANSLATE_NAMESPACES_XSL = "jaxws-databinding-translate-namespaces.xml";
        static Schema schema;
        static JAXBContext jaxbContext;

        Util() {
        }

        private static URL getResource() {
            ClassLoader classLoader = Util.class.getClassLoader();
            return classLoader != null ? classLoader.getResource(DATABINDING_XSD) : ClassLoader.getSystemResource(DATABINDING_XSD);
        }

        private static JAXBContext createJaxbContext(boolean disableXmlSecurity) {
            Class[] cls = new Class[]{ObjectFactory.class};
            try {
                if (disableXmlSecurity) {
                    HashMap<String, Boolean> properties = new HashMap<String, Boolean>();
                    properties.put("com.sun.xml.bind.disableXmlSecurity", disableXmlSecurity);
                    return JAXBContext.newInstance((Class[])cls, properties);
                }
                return JAXBContext.newInstance((Class[])cls);
            }
            catch (JAXBException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static JavaWsdlMappingType read(Source src, boolean xsdValidation, boolean disableXmlSecurity) throws IOException, JAXBException {
            JAXBContext ctx = Util.jaxbContext(disableXmlSecurity);
            try {
                Unmarshaller um = ctx.createUnmarshaller();
                if (xsdValidation) {
                    if (schema == null) {
                        // empty if block
                    }
                    um.setSchema(schema);
                }
                Object o = um.unmarshal(src);
                return Util.getJavaWsdlMapping(o);
            }
            catch (JAXBException e) {
                URL url = new URL(src.getSystemId());
                StreamSource s = new StreamSource(url.openStream());
                Unmarshaller um = ctx.createUnmarshaller();
                if (xsdValidation) {
                    if (schema == null) {
                        // empty if block
                    }
                    um.setSchema(schema);
                }
                Object o = um.unmarshal((Source)s);
                return Util.getJavaWsdlMapping(o);
            }
        }

        private static JAXBContext jaxbContext(boolean disableXmlSecurity) {
            return disableXmlSecurity ? Util.createJaxbContext(true) : jaxbContext;
        }

        public static JavaWsdlMappingType transformAndRead(Source src, boolean disableXmlSecurity) throws TransformerException, JAXBException {
            StreamSource xsl = new StreamSource(Util.class.getResourceAsStream(TRANSLATE_NAMESPACES_XSL));
            JAXBResult result = new JAXBResult(Util.jaxbContext(disableXmlSecurity));
            TransformerFactory tf = XmlUtil.newTransformerFactory(!disableXmlSecurity);
            Transformer transformer = tf.newTemplates(xsl).newTransformer();
            transformer.transform(src, (Result)result);
            return Util.getJavaWsdlMapping(result.getResult());
        }

        static JavaWsdlMappingType getJavaWsdlMapping(Object o) {
            Object val;
            Object object = val = o instanceof JAXBElement ? ((JAXBElement)o).getValue() : o;
            if (val instanceof JavaWsdlMappingType) {
                return (JavaWsdlMappingType)val;
            }
            return null;
        }

        static <T> T findInstanceOf(Class<T> type, List<Object> objects) {
            for (Object o : objects) {
                if (!type.isInstance(o)) continue;
                return type.cast(o);
            }
            return null;
        }

        public static <T> T annotation(JavaWsdlMappingType jwse, Class<T> anntype) {
            if (jwse == null || jwse.getClassAnnotation() == null) {
                return null;
            }
            return Util.findInstanceOf(anntype, jwse.getClassAnnotation());
        }

        public static <T> T annotation(JavaMethod jm, Class<T> anntype) {
            if (jm == null || jm.getMethodAnnotation() == null) {
                return null;
            }
            return Util.findInstanceOf(anntype, jm.getMethodAnnotation());
        }

        public static <T> T annotation(JavaParam jp, Class<T> anntype) {
            if (jp == null || jp.getParamAnnotation() == null) {
                return null;
            }
            return Util.findInstanceOf(anntype, jp.getParamAnnotation());
        }

        public static Element[] annotation(JavaMethod jm) {
            if (jm == null || jm.getMethodAnnotation() == null) {
                return null;
            }
            return Util.findElements(jm.getMethodAnnotation());
        }

        public static Element[] annotation(JavaParam jp) {
            if (jp == null || jp.getParamAnnotation() == null) {
                return null;
            }
            return Util.findElements(jp.getParamAnnotation());
        }

        private static Element[] findElements(List<Object> objects) {
            ArrayList<Element> elems = new ArrayList<Element>();
            for (Object o : objects) {
                if (!(o instanceof Element)) continue;
                elems.add((Element)o);
            }
            return elems.toArray(new Element[elems.size()]);
        }

        static String documentRootNamespace(Source src, boolean disableXmlSecurity) throws XMLStreamException {
            XMLInputFactory factory = XmlUtil.newXMLInputFactory(!disableXmlSecurity);
            XMLStreamReader streamReader = factory.createXMLStreamReader(src);
            XMLStreamReaderUtil.nextElementContent(streamReader);
            String namespaceURI = streamReader.getName().getNamespaceURI();
            XMLStreamReaderUtil.close(streamReader);
            return namespaceURI;
        }

        static {
            SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            try {
                URL xsdUrl = Util.getResource();
                if (xsdUrl != null) {
                    schema = sf.newSchema(xsdUrl);
                }
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
            jaxbContext = Util.createJaxbContext(false);
        }
    }

    static abstract class Merger<T> {
        JavaWsdlMappingType reader;

        Merger(JavaWsdlMappingType r) {
            this.reader = r;
        }

        abstract T reflection();

        abstract T external();

        T merge() {
            T reflection = this.reflection();
            if (this.reader == null) {
                return reflection;
            }
            T external = this.external();
            if (!ExistingAnnotationsType.MERGE.equals((Object)this.reader.getExistingAnnotations())) {
                return external;
            }
            if (reflection instanceof Annotation) {
                return (T)this.doMerge((Annotation)reflection, (Annotation)external);
            }
            if (reflection instanceof Annotation[][]) {
                return (T)this.doMerge((Annotation[][])reflection, (Annotation[][])external);
            }
            return (T)this.doMerge((Annotation[])reflection, (Annotation[])external);
        }

        private Annotation doMerge(Annotation reflection, Annotation external) {
            return external != null ? external : reflection;
        }

        private Annotation[][] doMerge(Annotation[][] reflection, Annotation[][] external) {
            for (int i = 0; i < reflection.length; ++i) {
                reflection[i] = this.doMerge(reflection[i], external.length > i ? external[i] : null);
            }
            return reflection;
        }

        private Annotation[] doMerge(Annotation[] annotations, Annotation[] externalAnnotations) {
            Collection values;
            int size;
            HashMap<String, Annotation> mergeMap = new HashMap<String, Annotation>();
            if (annotations != null) {
                for (Annotation reflectionAnnotation : annotations) {
                    mergeMap.put(reflectionAnnotation.annotationType().getName(), reflectionAnnotation);
                }
            }
            if (externalAnnotations != null) {
                for (Annotation externalAnnotation : externalAnnotations) {
                    mergeMap.put(externalAnnotation.annotationType().getName(), externalAnnotation);
                }
            }
            return (size = (values = mergeMap.values()).size()) == 0 ? null : values.toArray(new Annotation[size]);
        }
    }
}

