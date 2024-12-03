/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.xmlattribute;

import aQute.bnd.annotation.xml.XMLAttribute;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class XMLAttributeFinder
extends ClassDataCollector {
    private final Analyzer analyzer;
    Map<Descriptors.TypeRef, XMLAttribute> annoCache = new HashMap<Descriptors.TypeRef, XMLAttribute>();
    Map<Descriptors.TypeRef, Map<String, String>> defaultsCache = new HashMap<Descriptors.TypeRef, Map<String, String>>();
    XMLAttribute xmlAttr;

    public XMLAttributeFinder(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public synchronized XMLAttribute getXMLAttribute(Annotation a) throws Exception {
        Descriptors.TypeRef name = a.getName();
        if (this.annoCache.containsKey(name)) {
            return this.annoCache.get(name);
        }
        Clazz clazz = this.analyzer.findClass(name);
        if (clazz != null) {
            this.xmlAttr = null;
            clazz.parseClassFileWithCollector(this);
            this.annoCache.put(name, this.xmlAttr);
            return this.xmlAttr;
        }
        return null;
    }

    @Override
    public void annotation(Annotation annotation) throws Exception {
        Object a = annotation.getAnnotation();
        if (a instanceof XMLAttribute) {
            this.xmlAttr = (XMLAttribute)a;
        }
    }

    public Map<String, String> getDefaults(Annotation a) {
        Descriptors.TypeRef name = a.getName();
        Map<String, String> defaults = this.defaultsCache.get(name);
        if (defaults == null) {
            defaults = this.extractDefaults(name, this.analyzer);
        }
        if (defaults == null) {
            return new LinkedHashMap<String, String>();
        }
        return new LinkedHashMap<String, String>(defaults);
    }

    private Map<String, String> extractDefaults(Descriptors.TypeRef name, final Analyzer analyzer) {
        try {
            Clazz clazz = analyzer.findClass(name);
            final LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
            clazz.parseClassFileWithCollector(new ClassDataCollector(){

                @Override
                public void annotationDefault(Clazz.MethodDef defined) {
                    Object value = defined.getConstant();
                    boolean isClass = false;
                    Descriptors.TypeRef type = defined.getType().getClassRef();
                    if (!type.isPrimitive()) {
                        if (Class.class.getName().equals(type.getFQN())) {
                            isClass = true;
                        } else {
                            try {
                                Clazz r = analyzer.findClass(type);
                                if (r.isAnnotation()) {
                                    analyzer.warning("Nested annotation type found in field %s, %s", defined.getName(), type.getFQN());
                                    return;
                                }
                            }
                            catch (Exception e) {
                                analyzer.exception(e, "Exception extracting annotation defaults for type %s", type);
                                return;
                            }
                        }
                    }
                    if (value != null) {
                        String name = defined.getName();
                        if (value.getClass().isArray()) {
                            StringBuilder sb = new StringBuilder();
                            String sep = "";
                            for (int i = 0; i < Array.getLength(value); ++i) {
                                Object element = Array.get(value, i);
                                sb.append(sep).append(this.convert(element, isClass));
                                sep = " ";
                            }
                            props.put(name, sb.toString());
                        } else {
                            props.put(name, this.convert(value, isClass));
                        }
                    }
                }

                private String convert(Object value, boolean isClass) {
                    if (isClass) {
                        return ((Descriptors.TypeRef)value).getFQN();
                    }
                    return String.valueOf(value);
                }
            });
            this.defaultsCache.put(name, props);
            return props;
        }
        catch (Exception e) {
            analyzer.exception(e, "Exception extracting annotation defaults for type %s", name);
            return null;
        }
    }
}

