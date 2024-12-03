/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.bind.v2.model.annotation.AnnotationReader
 *  com.sun.xml.bind.v2.model.nav.Navigator
 *  javax.jws.WebParam
 *  javax.jws.WebParam$Mode
 *  javax.jws.WebResult
 *  javax.xml.bind.annotation.XmlAttachmentRef
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlList
 *  javax.xml.bind.annotation.XmlMimeType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.model;

import com.sun.istack.NotNull;
import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.ws.spi.db.BindingHelper;
import com.sun.xml.ws.util.StringUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.WebServiceException;

public abstract class AbstractWrapperBeanGenerator<T, C, M, A extends Comparable> {
    private static final Logger LOGGER = Logger.getLogger(AbstractWrapperBeanGenerator.class.getName());
    private static final String RETURN = "return";
    private static final String EMTPY_NAMESPACE_ID = "";
    private static final Class[] jaxbAnns = new Class[]{XmlAttachmentRef.class, XmlMimeType.class, XmlJavaTypeAdapter.class, XmlList.class, XmlElement.class};
    private static final Set<String> skipProperties = new HashSet<String>();
    private final AnnotationReader<T, C, ?, M> annReader;
    private final Navigator<T, C, ?, M> nav;
    private final BeanMemberFactory<T, A> factory;
    private final boolean JAXB_ALLOWNONNILLABLEARRAY = AbstractWrapperBeanGenerator.getBooleanSystemProperty("com.sun.xml.ws.jaxb.allowNonNillableArray");
    private static final Map<String, String> reservedWords;

    protected AbstractWrapperBeanGenerator(AnnotationReader<T, C, ?, M> annReader, Navigator<T, C, ?, M> nav, BeanMemberFactory<T, A> factory) {
        this.annReader = annReader;
        this.nav = nav;
        this.factory = factory;
    }

    private List<Annotation> collectJAXBAnnotations(M method) {
        ArrayList<Annotation> jaxbAnnotation = new ArrayList<Annotation>();
        for (Class jaxbClass : jaxbAnns) {
            Annotation ann = this.annReader.getMethodAnnotation(jaxbClass, method, null);
            if (ann == null) continue;
            jaxbAnnotation.add(ann);
        }
        return jaxbAnnotation;
    }

    private List<Annotation> collectJAXBAnnotations(M method, int paramIndex) {
        ArrayList<Annotation> jaxbAnnotation = new ArrayList<Annotation>();
        for (Class jaxbClass : jaxbAnns) {
            Annotation ann = this.annReader.getMethodParameterAnnotation(jaxbClass, method, paramIndex, null);
            if (ann == null) continue;
            jaxbAnnotation.add(ann);
        }
        return jaxbAnnotation;
    }

    protected abstract T getSafeType(T var1);

    protected abstract T getHolderValueType(T var1);

    protected abstract boolean isVoidType(T var1);

    public List<A> collectRequestBeanMembers(M method) {
        ArrayList<Comparable> requestMembers = new ArrayList<Comparable>();
        int paramIndex = -1;
        for (Object param : this.nav.getMethodParameters(method)) {
            WebParam webParam;
            if ((webParam = (WebParam)this.annReader.getMethodParameterAnnotation(WebParam.class, method, ++paramIndex, null)) != null && (webParam.header() || webParam.mode().equals((Object)WebParam.Mode.OUT))) continue;
            Object holderType = this.getHolderValueType(param);
            Object paramType = holderType != null ? holderType : this.getSafeType(param);
            String paramName = webParam != null && webParam.name().length() > 0 ? webParam.name() : "arg" + paramIndex;
            String paramNamespace = webParam != null && webParam.targetNamespace().length() > 0 ? webParam.targetNamespace() : EMTPY_NAMESPACE_ID;
            List<Annotation> jaxbAnnotation = this.collectJAXBAnnotations(method, paramIndex);
            this.processXmlElement(jaxbAnnotation, paramName, paramNamespace, paramType);
            Comparable member = (Comparable)this.factory.createWrapperBeanMember(paramType, AbstractWrapperBeanGenerator.getPropertyName(paramName), jaxbAnnotation);
            requestMembers.add(member);
        }
        return requestMembers;
    }

    public List<A> collectResponseBeanMembers(M method) {
        Object returnType;
        ArrayList<Comparable> responseMembers = new ArrayList<Comparable>();
        String responseElementName = RETURN;
        String responseNamespace = EMTPY_NAMESPACE_ID;
        boolean isResultHeader = false;
        WebResult webResult = (WebResult)this.annReader.getMethodAnnotation(WebResult.class, method, null);
        if (webResult != null) {
            if (webResult.name().length() > 0) {
                responseElementName = webResult.name();
            }
            if (webResult.targetNamespace().length() > 0) {
                responseNamespace = webResult.targetNamespace();
            }
            isResultHeader = webResult.header();
        }
        if (!this.isVoidType(returnType = this.getSafeType(this.nav.getReturnType(method))) && !isResultHeader) {
            List<Annotation> jaxbRespAnnotations = this.collectJAXBAnnotations(method);
            this.processXmlElement(jaxbRespAnnotations, responseElementName, responseNamespace, returnType);
            responseMembers.add((Comparable)this.factory.createWrapperBeanMember(returnType, AbstractWrapperBeanGenerator.getPropertyName(responseElementName), jaxbRespAnnotations));
        }
        int paramIndex = -1;
        for (Object param : this.nav.getMethodParameters(method)) {
            Object paramType = this.getHolderValueType(param);
            WebParam webParam = (WebParam)this.annReader.getMethodParameterAnnotation(WebParam.class, method, ++paramIndex, null);
            if (paramType == null || webParam != null && webParam.header()) continue;
            String paramName = webParam != null && webParam.name().length() > 0 ? webParam.name() : "arg" + paramIndex;
            String paramNamespace = webParam != null && webParam.targetNamespace().length() > 0 ? webParam.targetNamespace() : EMTPY_NAMESPACE_ID;
            List<Annotation> jaxbAnnotation = this.collectJAXBAnnotations(method, paramIndex);
            this.processXmlElement(jaxbAnnotation, paramName, paramNamespace, paramType);
            Comparable member = (Comparable)this.factory.createWrapperBeanMember(paramType, AbstractWrapperBeanGenerator.getPropertyName(paramName), jaxbAnnotation);
            responseMembers.add(member);
        }
        return responseMembers;
    }

    private void processXmlElement(List<Annotation> jaxb, String elemName, String elemNS, T type) {
        XmlElement elemAnn = null;
        for (Annotation a : jaxb) {
            if (a.annotationType() != XmlElement.class) continue;
            elemAnn = (XmlElement)a;
            jaxb.remove(a);
            break;
        }
        String name = elemAnn != null && !elemAnn.name().equals("##default") ? elemAnn.name() : elemName;
        String ns = elemAnn != null && !elemAnn.namespace().equals("##default") ? elemAnn.namespace() : elemNS;
        boolean nillable = this.nav.isArray(type) && !this.JAXB_ALLOWNONNILLABLEARRAY || this.nav.isArray(type) && elemAnn == null || elemAnn != null && elemAnn.nillable();
        boolean required = elemAnn != null && elemAnn.required();
        XmlElementHandler handler = new XmlElementHandler(name, ns, nillable, required);
        XmlElement elem = (XmlElement)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{XmlElement.class}, (InvocationHandler)handler);
        jaxb.add((Annotation)elem);
    }

    public Collection<A> collectExceptionBeanMembers(C exception) {
        return this.collectExceptionBeanMembers(exception, true);
    }

    public Collection<A> collectExceptionBeanMembers(C exception, boolean decapitalize) {
        String[] propOrder;
        TreeMap fields = new TreeMap();
        this.getExceptionProperties(exception, fields, decapitalize);
        XmlType xmlType = (XmlType)this.annReader.getClassAnnotation(XmlType.class, exception, null);
        if (xmlType != null && (propOrder = xmlType.propOrder()).length > 0 && propOrder[0].length() != 0) {
            ArrayList<Comparable> list = new ArrayList<Comparable>();
            for (String prop : propOrder) {
                Comparable a = (Comparable)fields.get(prop);
                if (a == null) {
                    throw new WebServiceException("Exception " + exception + " has @XmlType and its propOrder contains unknown property " + prop);
                }
                list.add(a);
            }
            return list;
        }
        return fields.values();
    }

    private void getExceptionProperties(C exception, TreeMap<String, A> fields, boolean decapitalize) {
        Object sc = this.nav.getSuperClass(exception);
        if (sc != null) {
            this.getExceptionProperties(sc, fields, decapitalize);
        }
        Collection methods = this.nav.getDeclaredMethods(exception);
        for (Object method : methods) {
            String fieldName;
            String name;
            if (!this.nav.isPublicMethod(method) || this.nav.isStaticMethod(method) && this.nav.isFinalMethod(method) || !this.nav.isPublicMethod(method) || !(name = this.nav.getMethodName(method)).startsWith("get") && !name.startsWith("is") || skipProperties.contains(name) || name.equals("get") || name.equals("is")) continue;
            Object returnType = this.getSafeType(this.nav.getReturnType(method));
            if (this.nav.getMethodParameters(method).length != 0) continue;
            String string = fieldName = name.startsWith("get") ? name.substring(3) : name.substring(2);
            if (decapitalize) {
                fieldName = StringUtils.decapitalize(fieldName);
            }
            fields.put(fieldName, (Comparable)this.factory.createWrapperBeanMember(returnType, fieldName, Collections.emptyList()));
        }
    }

    private static String getPropertyName(String name) {
        String propertyName = BindingHelper.mangleNameToVariableName(name);
        return AbstractWrapperBeanGenerator.getJavaReservedVarialbeName(propertyName);
    }

    @NotNull
    private static String getJavaReservedVarialbeName(@NotNull String name) {
        String reservedName = reservedWords.get(name);
        return reservedName == null ? name : reservedName;
    }

    private static Boolean getBooleanSystemProperty(final String prop) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

            @Override
            public Boolean run() {
                return Boolean.getBoolean(prop);
            }
        });
    }

    static {
        skipProperties.add("getCause");
        skipProperties.add("getLocalizedMessage");
        skipProperties.add("getClass");
        skipProperties.add("getStackTrace");
        skipProperties.add("getSuppressed");
        reservedWords = new HashMap<String, String>();
        reservedWords.put("abstract", "_abstract");
        reservedWords.put("assert", "_assert");
        reservedWords.put("boolean", "_boolean");
        reservedWords.put("break", "_break");
        reservedWords.put("byte", "_byte");
        reservedWords.put("case", "_case");
        reservedWords.put("catch", "_catch");
        reservedWords.put("char", "_char");
        reservedWords.put("class", "_class");
        reservedWords.put("const", "_const");
        reservedWords.put("continue", "_continue");
        reservedWords.put("default", "_default");
        reservedWords.put("do", "_do");
        reservedWords.put("double", "_double");
        reservedWords.put("else", "_else");
        reservedWords.put("extends", "_extends");
        reservedWords.put("false", "_false");
        reservedWords.put("final", "_final");
        reservedWords.put("finally", "_finally");
        reservedWords.put("float", "_float");
        reservedWords.put("for", "_for");
        reservedWords.put("goto", "_goto");
        reservedWords.put("if", "_if");
        reservedWords.put("implements", "_implements");
        reservedWords.put("import", "_import");
        reservedWords.put("instanceof", "_instanceof");
        reservedWords.put("int", "_int");
        reservedWords.put("interface", "_interface");
        reservedWords.put("long", "_long");
        reservedWords.put("native", "_native");
        reservedWords.put("new", "_new");
        reservedWords.put("null", "_null");
        reservedWords.put("package", "_package");
        reservedWords.put("private", "_private");
        reservedWords.put("protected", "_protected");
        reservedWords.put("public", "_public");
        reservedWords.put(RETURN, "_return");
        reservedWords.put("short", "_short");
        reservedWords.put("static", "_static");
        reservedWords.put("strictfp", "_strictfp");
        reservedWords.put("super", "_super");
        reservedWords.put("switch", "_switch");
        reservedWords.put("synchronized", "_synchronized");
        reservedWords.put("this", "_this");
        reservedWords.put("throw", "_throw");
        reservedWords.put("throws", "_throws");
        reservedWords.put("transient", "_transient");
        reservedWords.put("true", "_true");
        reservedWords.put("try", "_try");
        reservedWords.put("void", "_void");
        reservedWords.put("volatile", "_volatile");
        reservedWords.put("while", "_while");
        reservedWords.put("enum", "_enum");
    }

    private static class XmlElementHandler
    implements InvocationHandler {
        private String name;
        private String namespace;
        private boolean nillable;
        private boolean required;

        XmlElementHandler(String name, String namespace, boolean nillable, boolean required) {
            this.name = name;
            this.namespace = namespace;
            this.nillable = nillable;
            this.required = required;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (methodName.equals("name")) {
                return this.name;
            }
            if (methodName.equals("namespace")) {
                return this.namespace;
            }
            if (methodName.equals("nillable")) {
                return this.nillable;
            }
            if (methodName.equals("required")) {
                return this.required;
            }
            throw new WebServiceException("Not handling " + methodName);
        }
    }

    public static interface BeanMemberFactory<T, A> {
        public A createWrapperBeanMember(T var1, String var2, List<Annotation> var3);
    }
}

