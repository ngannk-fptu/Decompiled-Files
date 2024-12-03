/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyAnnotationRef;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyConstructorDoc;
import org.codehaus.groovy.groovydoc.GroovyFieldDoc;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.groovydoc.GroovyPackageDoc;
import org.codehaus.groovy.groovydoc.GroovyType;

public class ArrayClassDocWrapper
implements GroovyClassDoc {
    private final GroovyClassDoc delegate;

    public ArrayClassDocWrapper(GroovyClassDoc delegate) {
        this.delegate = delegate;
    }

    public GroovyClassDoc getDelegate() {
        return this.delegate;
    }

    @Override
    public GroovyConstructorDoc[] constructors() {
        return this.delegate.constructors();
    }

    @Override
    public GroovyConstructorDoc[] constructors(boolean filter) {
        return this.delegate.constructors(filter);
    }

    @Override
    public boolean definesSerializableFields() {
        return this.delegate.definesSerializableFields();
    }

    @Override
    public GroovyFieldDoc[] enumConstants() {
        return this.delegate.enumConstants();
    }

    @Override
    public GroovyFieldDoc[] fields() {
        return this.delegate.fields();
    }

    @Override
    public GroovyFieldDoc[] properties() {
        return this.delegate.properties();
    }

    @Override
    public GroovyFieldDoc[] fields(boolean filter) {
        return this.delegate.fields(filter);
    }

    @Override
    public GroovyClassDoc findClass(String className) {
        return this.delegate.findClass(className);
    }

    @Override
    public GroovyClassDoc[] importedClasses() {
        return this.delegate.importedClasses();
    }

    @Override
    public GroovyPackageDoc[] importedPackages() {
        return this.delegate.importedPackages();
    }

    @Override
    public GroovyClassDoc[] innerClasses() {
        return this.delegate.innerClasses();
    }

    @Override
    public GroovyClassDoc[] innerClasses(boolean filter) {
        return this.delegate.innerClasses(filter);
    }

    @Override
    public GroovyClassDoc[] interfaces() {
        return this.delegate.interfaces();
    }

    @Override
    public GroovyType[] interfaceTypes() {
        return this.delegate.interfaceTypes();
    }

    @Override
    public boolean isAbstract() {
        return this.delegate.isAbstract();
    }

    @Override
    public boolean isExternalizable() {
        return this.delegate.isExternalizable();
    }

    @Override
    public boolean isSerializable() {
        return this.delegate.isSerializable();
    }

    @Override
    public GroovyMethodDoc[] methods() {
        return this.delegate.methods();
    }

    @Override
    public GroovyMethodDoc[] methods(boolean filter) {
        return this.delegate.methods(filter);
    }

    @Override
    public GroovyFieldDoc[] serializableFields() {
        return this.delegate.serializableFields();
    }

    @Override
    public GroovyMethodDoc[] serializationMethods() {
        return this.delegate.serializationMethods();
    }

    @Override
    public boolean subclassOf(GroovyClassDoc gcd) {
        return this.delegate.subclassOf(gcd);
    }

    @Override
    public GroovyClassDoc superclass() {
        return this.delegate.superclass();
    }

    @Override
    public GroovyType superclassType() {
        return this.delegate.superclassType();
    }

    @Override
    public String getFullPathName() {
        return this.delegate.getFullPathName();
    }

    @Override
    public String getRelativeRootPath() {
        return this.delegate.getRelativeRootPath();
    }

    @Override
    public boolean isPrimitive() {
        return this.delegate.isPrimitive();
    }

    @Override
    public String qualifiedTypeName() {
        return this.delegate.qualifiedTypeName();
    }

    @Override
    public String simpleTypeName() {
        return this.delegate.simpleTypeName();
    }

    @Override
    public String typeName() {
        return this.delegate.typeName();
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public GroovyAnnotationRef[] annotations() {
        return this.delegate.annotations();
    }

    @Override
    public GroovyClassDoc containingClass() {
        return this.delegate.containingClass();
    }

    @Override
    public GroovyPackageDoc containingPackage() {
        return this.delegate.containingPackage();
    }

    @Override
    public boolean isFinal() {
        return this.delegate.isFinal();
    }

    @Override
    public boolean isPackagePrivate() {
        return this.delegate.isPackagePrivate();
    }

    @Override
    public boolean isPrivate() {
        return this.delegate.isPrivate();
    }

    @Override
    public boolean isProtected() {
        return this.delegate.isProtected();
    }

    @Override
    public boolean isPublic() {
        return this.delegate.isPublic();
    }

    @Override
    public boolean isStatic() {
        return this.delegate.isStatic();
    }

    @Override
    public String modifiers() {
        return this.delegate.modifiers();
    }

    @Override
    public int modifierSpecifier() {
        return this.delegate.modifierSpecifier();
    }

    @Override
    public String qualifiedName() {
        return this.delegate.qualifiedName();
    }

    @Override
    public String commentText() {
        return this.delegate.commentText();
    }

    @Override
    public String getRawCommentText() {
        return this.delegate.getRawCommentText();
    }

    @Override
    public boolean isAnnotationType() {
        return this.delegate.isAnnotationType();
    }

    @Override
    public boolean isAnnotationTypeElement() {
        return this.delegate.isAnnotationTypeElement();
    }

    @Override
    public boolean isClass() {
        return this.delegate.isClass();
    }

    @Override
    public boolean isConstructor() {
        return this.delegate.isConstructor();
    }

    @Override
    public boolean isDeprecated() {
        return this.delegate.isDeprecated();
    }

    @Override
    public boolean isEnum() {
        return this.delegate.isEnum();
    }

    @Override
    public boolean isEnumConstant() {
        return this.delegate.isEnumConstant();
    }

    @Override
    public boolean isError() {
        return this.delegate.isError();
    }

    @Override
    public boolean isException() {
        return this.delegate.isException();
    }

    @Override
    public boolean isField() {
        return this.delegate.isField();
    }

    @Override
    public boolean isIncluded() {
        return this.delegate.isIncluded();
    }

    @Override
    public boolean isInterface() {
        return this.delegate.isInterface();
    }

    @Override
    public boolean isMethod() {
        return this.delegate.isMethod();
    }

    @Override
    public boolean isOrdinaryClass() {
        return this.delegate.isOrdinaryClass();
    }

    @Override
    public String name() {
        return this.delegate.name();
    }

    @Override
    public void setRawCommentText(String arg0) {
        this.delegate.setRawCommentText(arg0);
    }

    @Override
    public String firstSentenceCommentText() {
        return this.delegate.firstSentenceCommentText();
    }

    public int compareTo(Object o) {
        return this.delegate.compareTo(o);
    }
}

