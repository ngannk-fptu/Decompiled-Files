/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.groovydoc.GroovyAnnotationRef;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyConstructorDoc;
import org.codehaus.groovy.groovydoc.GroovyFieldDoc;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.groovydoc.GroovyPackageDoc;
import org.codehaus.groovy.groovydoc.GroovyType;

public class ExternalGroovyClassDoc
implements GroovyClassDoc {
    private Class externalClass;
    private final List<GroovyAnnotationRef> annotationRefs;

    public ExternalGroovyClassDoc(Class externalClass) {
        this.externalClass = externalClass;
        this.annotationRefs = new ArrayList<GroovyAnnotationRef>();
    }

    @Override
    public boolean isPrimitive() {
        return this.externalClass.isPrimitive();
    }

    @Override
    public GroovyAnnotationRef[] annotations() {
        return this.annotationRefs.toArray(new GroovyAnnotationRef[this.annotationRefs.size()]);
    }

    @Override
    public String qualifiedTypeName() {
        return this.externalClass.getName();
    }

    @Override
    public GroovyClassDoc superclass() {
        Class aClass = this.externalClass.getSuperclass();
        if (aClass != null) {
            return new ExternalGroovyClassDoc(aClass);
        }
        return new ExternalGroovyClassDoc(Object.class);
    }

    public Class externalClass() {
        return this.externalClass;
    }

    public String getTypeSourceDescription() {
        return this.externalClass.isInterface() ? "interface" : "class";
    }

    @Override
    public String simpleTypeName() {
        return this.qualifiedTypeName();
    }

    @Override
    public String typeName() {
        return this.qualifiedTypeName();
    }

    public int hashCode() {
        return this.qualifiedTypeName().hashCode();
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof ExternalGroovyClassDoc)) {
            return false;
        }
        return this.qualifiedTypeName().equals(((ExternalGroovyClassDoc)other).qualifiedTypeName());
    }

    @Override
    public GroovyType superclassType() {
        return null;
    }

    @Override
    public GroovyConstructorDoc[] constructors() {
        return new GroovyConstructorDoc[0];
    }

    @Override
    public GroovyConstructorDoc[] constructors(boolean filter) {
        return new GroovyConstructorDoc[0];
    }

    @Override
    public boolean definesSerializableFields() {
        return false;
    }

    @Override
    public GroovyFieldDoc[] enumConstants() {
        return new GroovyFieldDoc[0];
    }

    @Override
    public GroovyFieldDoc[] fields() {
        return new GroovyFieldDoc[0];
    }

    @Override
    public GroovyFieldDoc[] properties() {
        return new GroovyFieldDoc[0];
    }

    @Override
    public GroovyFieldDoc[] fields(boolean filter) {
        return new GroovyFieldDoc[0];
    }

    @Override
    public GroovyClassDoc findClass(String className) {
        return null;
    }

    @Override
    public GroovyClassDoc[] importedClasses() {
        return new GroovyClassDoc[0];
    }

    @Override
    public GroovyPackageDoc[] importedPackages() {
        return new GroovyPackageDoc[0];
    }

    @Override
    public GroovyClassDoc[] innerClasses() {
        return new GroovyClassDoc[0];
    }

    @Override
    public GroovyClassDoc[] innerClasses(boolean filter) {
        return new GroovyClassDoc[0];
    }

    @Override
    public GroovyClassDoc[] interfaces() {
        return new GroovyClassDoc[0];
    }

    @Override
    public GroovyType[] interfaceTypes() {
        return new GroovyType[0];
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isExternalizable() {
        return false;
    }

    @Override
    public boolean isSerializable() {
        return false;
    }

    @Override
    public GroovyMethodDoc[] methods() {
        return new GroovyMethodDoc[0];
    }

    @Override
    public GroovyMethodDoc[] methods(boolean filter) {
        return new GroovyMethodDoc[0];
    }

    @Override
    public GroovyFieldDoc[] serializableFields() {
        return new GroovyFieldDoc[0];
    }

    @Override
    public GroovyMethodDoc[] serializationMethods() {
        return new GroovyMethodDoc[0];
    }

    @Override
    public boolean subclassOf(GroovyClassDoc gcd) {
        return false;
    }

    @Override
    public String getFullPathName() {
        return null;
    }

    @Override
    public String getRelativeRootPath() {
        return null;
    }

    @Override
    public GroovyClassDoc containingClass() {
        return null;
    }

    @Override
    public GroovyPackageDoc containingPackage() {
        return null;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isPackagePrivate() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public String modifiers() {
        return null;
    }

    @Override
    public int modifierSpecifier() {
        return 0;
    }

    @Override
    public String qualifiedName() {
        return null;
    }

    @Override
    public String commentText() {
        return null;
    }

    @Override
    public String getRawCommentText() {
        return null;
    }

    @Override
    public boolean isAnnotationType() {
        return false;
    }

    @Override
    public boolean isAnnotationTypeElement() {
        return false;
    }

    @Override
    public boolean isClass() {
        return false;
    }

    @Override
    public boolean isConstructor() {
        return false;
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isEnumConstant() {
        return false;
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public boolean isException() {
        return false;
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public boolean isIncluded() {
        return false;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isMethod() {
        return false;
    }

    @Override
    public boolean isOrdinaryClass() {
        return false;
    }

    @Override
    public String name() {
        return this.externalClass.getSimpleName();
    }

    @Override
    public void setRawCommentText(String arg0) {
    }

    @Override
    public String firstSentenceCommentText() {
        return null;
    }

    public int compareTo(Object o) {
        return 0;
    }
}

