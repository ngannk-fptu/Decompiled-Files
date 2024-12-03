/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractModelComponent;
import com.sun.jersey.api.model.AbstractModelVisitor;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.Parameterized;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;

public class AbstractResourceMethod
extends AbstractMethod
implements Parameterized,
AbstractModelComponent {
    private String httpMethod;
    private List<MediaType> consumeMimeList;
    private List<MediaType> produceMimeList;
    private List<Parameter> parameters;
    private Class returnType;
    private Type genericReturnType;
    private boolean isConsumesDeclared;
    private boolean isProducesDeclared;

    public AbstractResourceMethod(AbstractResource resource, Method method, Class returnType, Type genericReturnType, String httpMethod, Annotation[] annotations) {
        super(resource, method, annotations);
        this.httpMethod = httpMethod.toUpperCase();
        this.consumeMimeList = new ArrayList<MediaType>();
        this.produceMimeList = new ArrayList<MediaType>();
        this.returnType = returnType;
        this.genericReturnType = genericReturnType;
        this.parameters = new ArrayList<Parameter>();
    }

    public AbstractResource getDeclaringResource() {
        return this.getResource();
    }

    public Class getReturnType() {
        return this.returnType;
    }

    public Type getGenericReturnType() {
        return this.genericReturnType;
    }

    public List<MediaType> getSupportedInputTypes() {
        return this.consumeMimeList;
    }

    public void setAreInputTypesDeclared(boolean declared) {
        this.isConsumesDeclared = declared;
    }

    public boolean areInputTypesDeclared() {
        return this.isConsumesDeclared;
    }

    public List<MediaType> getSupportedOutputTypes() {
        return this.produceMimeList;
    }

    public void setAreOutputTypesDeclared(boolean declared) {
        this.isProducesDeclared = declared;
    }

    public boolean areOutputTypesDeclared() {
        return this.isProducesDeclared;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public boolean hasEntity() {
        for (Parameter p : this.getParameters()) {
            if (Parameter.Source.ENTITY != p.getSource()) continue;
            return true;
        }
        return false;
    }

    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public void accept(AbstractModelVisitor visitor) {
        visitor.visitAbstractResourceMethod(this);
    }

    @Override
    public List<AbstractModelComponent> getComponents() {
        return null;
    }

    public String toString() {
        return "AbstractResourceMethod(" + this.getMethod().getDeclaringClass().getSimpleName() + "#" + this.getMethod().getName() + ")";
    }
}

