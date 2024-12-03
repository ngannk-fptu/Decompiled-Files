/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.wadl.generators.resourcedoc;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.AnnotationDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ClassDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.MethodDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.NamedValueType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ParamDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.RepresentationDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ResourceDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ResponseDocType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ResourceDocAccessor {
    private ResourceDocType _resourceDoc;

    public ResourceDocAccessor(ResourceDocType resourceDoc) {
        this._resourceDoc = resourceDoc;
    }

    public ClassDocType getClassDoc(Class<?> resourceClass) {
        for (ClassDocType classDocType : this._resourceDoc.getDocs()) {
            if (!resourceClass.getName().equals(classDocType.getClassName())) continue;
            return classDocType;
        }
        return null;
    }

    public MethodDocType getMethodDoc(Class<?> resourceClass, Method method) {
        ClassDocType classDoc = this.getClassDoc(resourceClass);
        if (classDoc != null) {
            for (MethodDocType methodDocType : classDoc.getMethodDocs()) {
                if (method == null || !method.getName().equals(methodDocType.getMethodName())) continue;
                return methodDocType;
            }
        }
        return null;
    }

    public ParamDocType getParamDoc(Class<?> resourceClass, Method method, Parameter p) {
        MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
        if (methodDoc != null) {
            for (ParamDocType paramDocType : methodDoc.getParamDocs()) {
                for (AnnotationDocType annotationDocType : paramDocType.getAnnotationDocs()) {
                    String sourceName;
                    Class<? extends Annotation> annotationType = p.getAnnotation().annotationType();
                    if (annotationType == null || (sourceName = this.getSourceName(annotationDocType)) == null || !sourceName.equals(p.getSourceName())) continue;
                    return paramDocType;
                }
            }
        }
        return null;
    }

    public RepresentationDocType getRequestRepresentation(Class<?> resourceClass, Method method, String mediaType) {
        if (mediaType == null) {
            return null;
        }
        MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
        return methodDoc != null && methodDoc.getRequestDoc() != null && methodDoc.getRequestDoc().getRepresentationDoc() != null ? methodDoc.getRequestDoc().getRepresentationDoc() : null;
    }

    public ResponseDocType getResponse(Class<?> resourceClass, Method method) {
        MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
        return methodDoc != null && methodDoc.getResponseDoc() != null ? methodDoc.getResponseDoc() : null;
    }

    private String getSourceName(AnnotationDocType annotationDocType) {
        if (annotationDocType.hasAttributeDocs()) {
            for (NamedValueType namedValueType : annotationDocType.getAttributeDocs()) {
                if (!"value".equals(namedValueType.getName())) continue;
                return namedValueType.getValue();
            }
        }
        return null;
    }
}

