/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.groovydoc;

public interface GroovyDoc
extends Comparable {
    public String commentText();

    public String getRawCommentText();

    public boolean isAnnotationType();

    public boolean isAnnotationTypeElement();

    public boolean isClass();

    public boolean isConstructor();

    public boolean isDeprecated();

    public boolean isEnum();

    public boolean isEnumConstant();

    public boolean isError();

    public boolean isException();

    public boolean isField();

    public boolean isIncluded();

    public boolean isInterface();

    public boolean isMethod();

    public boolean isOrdinaryClass();

    public String name();

    public void setRawCommentText(String var1);

    public String firstSentenceCommentText();
}

