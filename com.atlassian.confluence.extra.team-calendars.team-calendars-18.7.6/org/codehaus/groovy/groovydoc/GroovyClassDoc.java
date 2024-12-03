/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyConstructorDoc;
import org.codehaus.groovy.groovydoc.GroovyFieldDoc;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.groovydoc.GroovyPackageDoc;
import org.codehaus.groovy.groovydoc.GroovyProgramElementDoc;
import org.codehaus.groovy.groovydoc.GroovyType;

public interface GroovyClassDoc
extends GroovyType,
GroovyProgramElementDoc {
    public GroovyConstructorDoc[] constructors();

    public GroovyConstructorDoc[] constructors(boolean var1);

    public boolean definesSerializableFields();

    public GroovyFieldDoc[] enumConstants();

    public GroovyFieldDoc[] fields();

    public GroovyFieldDoc[] properties();

    public GroovyFieldDoc[] fields(boolean var1);

    public GroovyClassDoc findClass(String var1);

    public GroovyClassDoc[] importedClasses();

    public GroovyPackageDoc[] importedPackages();

    public GroovyClassDoc[] innerClasses();

    public GroovyClassDoc[] innerClasses(boolean var1);

    public GroovyClassDoc[] interfaces();

    public GroovyType[] interfaceTypes();

    public boolean isAbstract();

    public boolean isExternalizable();

    public boolean isSerializable();

    public GroovyMethodDoc[] methods();

    public GroovyMethodDoc[] methods(boolean var1);

    public GroovyFieldDoc[] serializableFields();

    public GroovyMethodDoc[] serializationMethods();

    public boolean subclassOf(GroovyClassDoc var1);

    public GroovyClassDoc superclass();

    public GroovyType superclassType();

    public String getFullPathName();

    public String getRelativeRootPath();
}

