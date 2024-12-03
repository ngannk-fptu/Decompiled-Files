/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.v2.model.annotation.AnnotationReader
 *  com.sun.xml.bind.v2.model.annotation.RuntimeInlineAnnotationReader
 *  com.sun.xml.bind.v2.model.nav.Navigator
 *  javax.xml.bind.annotation.XmlAttachmentRef
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlList
 *  javax.xml.bind.annotation.XmlMimeType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *  javax.xml.ws.Holder
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.model;

import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.ws.model.AbstractWrapperBeanGenerator;
import com.sun.xml.ws.model.FieldSignature;
import com.sun.xml.ws.model.Injector;
import com.sun.xml.ws.model.Utils;
import com.sun.xml.ws.org.objectweb.asm.AnnotationVisitor;
import com.sun.xml.ws.org.objectweb.asm.ClassWriter;
import com.sun.xml.ws.org.objectweb.asm.FieldVisitor;
import com.sun.xml.ws.org.objectweb.asm.MethodVisitor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public class WrapperBeanGenerator {
    private static final Logger LOGGER = Logger.getLogger(WrapperBeanGenerator.class.getName());
    private static final FieldFactory FIELD_FACTORY = new FieldFactory();
    private static final AbstractWrapperBeanGenerator RUNTIME_GENERATOR = new RuntimeWrapperBeanGenerator((AnnotationReader<Type, Class, ?, Method>)new RuntimeInlineAnnotationReader(), Utils.REFLECTION_NAVIGATOR, FIELD_FACTORY);

    private static byte[] createBeanImage(String className, String rootName, String rootNS, String typeName, String typeNS, Collection<Field> fields) throws Exception {
        XmlElement xmlElem;
        ClassWriter cw = new ClassWriter(0);
        cw.visit(52, 33, WrapperBeanGenerator.replaceDotWithSlash(className), null, "java/lang/Object", null);
        AnnotationVisitor root = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlRootElement;", true);
        root.visit("name", rootName);
        root.visit("namespace", rootNS);
        root.visitEnd();
        AnnotationVisitor type = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlType;", true);
        type.visit("name", typeName);
        type.visit("namespace", typeNS);
        if (fields.size() > 1) {
            AnnotationVisitor propVisitor = type.visitArray("propOrder");
            for (Field field : fields) {
                propVisitor.visit("propOrder", field.fieldName);
            }
            propVisitor.visitEnd();
        }
        type.visitEnd();
        for (Field field : fields) {
            FieldVisitor fv = cw.visitField(1, field.fieldName, field.asmType.getDescriptor(), field.getSignature(), null);
            for (Annotation ann : field.jaxbAnnotations) {
                if (ann instanceof XmlMimeType) {
                    AnnotationVisitor mime = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlMimeType;", true);
                    mime.visit("value", ((XmlMimeType)ann).value());
                    mime.visitEnd();
                    continue;
                }
                if (ann instanceof XmlJavaTypeAdapter) {
                    AnnotationVisitor ada = fv.visitAnnotation("Ljavax/xml/bind/annotation/adapters/XmlJavaTypeAdapter;", true);
                    ada.visit("value", WrapperBeanGenerator.getASMType(((XmlJavaTypeAdapter)ann).value()));
                    ada.visitEnd();
                    continue;
                }
                if (ann instanceof XmlAttachmentRef) {
                    AnnotationVisitor att = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlAttachmentRef;", true);
                    att.visitEnd();
                    continue;
                }
                if (ann instanceof XmlList) {
                    AnnotationVisitor list = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlList;", true);
                    list.visitEnd();
                    continue;
                }
                if (ann instanceof XmlElement) {
                    AnnotationVisitor elem = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlElement;", true);
                    xmlElem = (XmlElement)ann;
                    elem.visit("name", xmlElem.name());
                    elem.visit("namespace", xmlElem.namespace());
                    if (xmlElem.nillable()) {
                        elem.visit("nillable", true);
                    }
                    if (xmlElem.required()) {
                        elem.visit("required", true);
                    }
                    elem.visitEnd();
                    continue;
                }
                throw new WebServiceException("Unknown JAXB annotation " + ann);
            }
            fv.visitEnd();
        }
        MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(177);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        cw.visitEnd();
        if (LOGGER.isLoggable(Level.FINE)) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("@XmlRootElement(name=").append(rootName).append(", namespace=").append(rootNS).append(")");
            sb.append("\n");
            sb.append("@XmlType(name=").append(typeName).append(", namespace=").append(typeNS);
            if (fields.size() > 1) {
                sb.append(", propOrder={");
                for (Field field : fields) {
                    sb.append(" ");
                    sb.append(field.fieldName);
                }
                sb.append(" }");
            }
            sb.append(")");
            sb.append("\n");
            sb.append("public class ").append(className).append(" {");
            for (Field field : fields) {
                sb.append("\n");
                for (Annotation ann : field.jaxbAnnotations) {
                    sb.append("\n    ");
                    if (ann instanceof XmlMimeType) {
                        sb.append("@XmlMimeType(value=").append(((XmlMimeType)ann).value()).append(")");
                        continue;
                    }
                    if (ann instanceof XmlJavaTypeAdapter) {
                        sb.append("@XmlJavaTypeAdapter(value=").append(WrapperBeanGenerator.getASMType(((XmlJavaTypeAdapter)ann).value())).append(")");
                        continue;
                    }
                    if (ann instanceof XmlAttachmentRef) {
                        sb.append("@XmlAttachmentRef");
                        continue;
                    }
                    if (ann instanceof XmlList) {
                        sb.append("@XmlList");
                        continue;
                    }
                    if (ann instanceof XmlElement) {
                        xmlElem = (XmlElement)ann;
                        sb.append("\n    ");
                        sb.append("@XmlElement(name=").append(xmlElem.name()).append(", namespace=").append(xmlElem.namespace());
                        if (xmlElem.nillable()) {
                            sb.append(", nillable=true");
                        }
                        if (xmlElem.required()) {
                            sb.append(", required=true");
                        }
                        sb.append(")");
                        continue;
                    }
                    throw new WebServiceException("Unknown JAXB annotation " + ann);
                }
                sb.append("\n    ");
                sb.append("public ");
                if (field.getSignature() == null) {
                    sb.append(field.asmType.getDescriptor());
                } else {
                    sb.append(field.getSignature());
                }
                sb.append(" ");
                sb.append(field.fieldName);
            }
            sb.append("\n\n}");
            LOGGER.fine(sb.toString());
        }
        return cw.toByteArray();
    }

    private static String replaceDotWithSlash(String name) {
        return name.replace('.', '/');
    }

    static Class createRequestWrapperBean(String className, Method method, QName reqElemName, ClassLoader cl) {
        byte[] image;
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Request Wrapper Class : {0}", className);
        }
        List<Field> requestMembers = RUNTIME_GENERATOR.collectRequestBeanMembers(method);
        try {
            image = WrapperBeanGenerator.createBeanImage(className, reqElemName.getLocalPart(), reqElemName.getNamespaceURI(), reqElemName.getLocalPart(), reqElemName.getNamespaceURI(), requestMembers);
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
        return Injector.inject(cl, className, image);
    }

    static Class createResponseWrapperBean(String className, Method method, QName resElemName, ClassLoader cl) {
        byte[] image;
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Response Wrapper Class : {0}", className);
        }
        List<Field> responseMembers = RUNTIME_GENERATOR.collectResponseBeanMembers(method);
        try {
            image = WrapperBeanGenerator.createBeanImage(className, resElemName.getLocalPart(), resElemName.getNamespaceURI(), resElemName.getLocalPart(), resElemName.getNamespaceURI(), responseMembers);
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
        return Injector.inject(cl, className, image);
    }

    private static com.sun.xml.ws.org.objectweb.asm.Type getASMType(Type t) {
        TypeVariable tv;
        ParameterizedType pt;
        assert (t != null);
        if (t instanceof Class) {
            return com.sun.xml.ws.org.objectweb.asm.Type.getType((Class)t);
        }
        if (t instanceof ParameterizedType && (pt = (ParameterizedType)t).getRawType() instanceof Class) {
            return com.sun.xml.ws.org.objectweb.asm.Type.getType((Class)pt.getRawType());
        }
        if (t instanceof GenericArrayType) {
            return com.sun.xml.ws.org.objectweb.asm.Type.getType(FieldSignature.vms(t));
        }
        if (t instanceof WildcardType) {
            return com.sun.xml.ws.org.objectweb.asm.Type.getType(FieldSignature.vms(t));
        }
        if (t instanceof TypeVariable && (tv = (TypeVariable)t).getBounds()[0] instanceof Class) {
            return com.sun.xml.ws.org.objectweb.asm.Type.getType((Class)tv.getBounds()[0]);
        }
        throw new IllegalArgumentException("Not creating ASM Type for type = " + t);
    }

    static Class createExceptionBean(String className, Class exception, String typeNS, String elemName, String elemNS, ClassLoader cl) {
        return WrapperBeanGenerator.createExceptionBean(className, exception, typeNS, elemName, elemNS, cl, true);
    }

    static Class createExceptionBean(String className, Class exception, String typeNS, String elemName, String elemNS, ClassLoader cl, boolean decapitalizeExceptionBeanProperties) {
        byte[] image;
        Collection<Field> fields = RUNTIME_GENERATOR.collectExceptionBeanMembers(exception, decapitalizeExceptionBeanProperties);
        try {
            image = WrapperBeanGenerator.createBeanImage(className, elemName, elemNS, exception.getSimpleName(), typeNS, fields);
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
        return Injector.inject(cl, className, image);
    }

    static void write(byte[] b, String className) {
        className = className.substring(className.lastIndexOf(".") + 1);
        try (FileOutputStream fo = new FileOutputStream(className + ".class");){
            fo.write(b);
            fo.flush();
        }
        catch (IOException e) {
            LOGGER.log(Level.INFO, "Error Writing class", e);
        }
    }

    private static class Field
    implements Comparable<Field> {
        private final Type reflectType;
        private final com.sun.xml.ws.org.objectweb.asm.Type asmType;
        private final String fieldName;
        private final List<Annotation> jaxbAnnotations;

        Field(String paramName, Type paramType, com.sun.xml.ws.org.objectweb.asm.Type asmType, List<Annotation> jaxbAnnotations) {
            this.reflectType = paramType;
            this.asmType = asmType;
            this.fieldName = paramName;
            this.jaxbAnnotations = jaxbAnnotations;
        }

        String getSignature() {
            if (this.reflectType instanceof Class) {
                return null;
            }
            if (this.reflectType instanceof TypeVariable) {
                return null;
            }
            return FieldSignature.vms(this.reflectType);
        }

        @Override
        public int compareTo(Field o) {
            return this.fieldName.compareTo(o.fieldName);
        }
    }

    private static final class FieldFactory
    implements AbstractWrapperBeanGenerator.BeanMemberFactory<Type, Field> {
        private FieldFactory() {
        }

        @Override
        public Field createWrapperBeanMember(Type paramType, String paramName, List<Annotation> jaxb) {
            return new Field(paramName, paramType, WrapperBeanGenerator.getASMType(paramType), jaxb);
        }
    }

    private static final class RuntimeWrapperBeanGenerator
    extends AbstractWrapperBeanGenerator<Type, Class, Method, Field> {
        protected RuntimeWrapperBeanGenerator(AnnotationReader<Type, Class, ?, Method> annReader, Navigator<Type, Class, ?, Method> nav, AbstractWrapperBeanGenerator.BeanMemberFactory<Type, Field> beanMemberFactory) {
            super(annReader, nav, beanMemberFactory);
        }

        @Override
        protected Type getSafeType(Type type) {
            return type;
        }

        @Override
        protected Type getHolderValueType(Type paramType) {
            ParameterizedType p;
            if (paramType instanceof ParameterizedType && (p = (ParameterizedType)paramType).getRawType().equals(Holder.class)) {
                return p.getActualTypeArguments()[0];
            }
            return null;
        }

        @Override
        protected boolean isVoidType(Type type) {
            return type == Void.TYPE;
        }
    }
}

