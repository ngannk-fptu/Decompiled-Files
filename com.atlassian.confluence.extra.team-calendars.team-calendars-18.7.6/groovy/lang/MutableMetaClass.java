/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.MetaBeanProperty;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import java.lang.reflect.Method;

public interface MutableMetaClass
extends MetaClass {
    public boolean isModified();

    public void addNewInstanceMethod(Method var1);

    public void addNewStaticMethod(Method var1);

    public void addMetaMethod(MetaMethod var1);

    public void addMetaBeanProperty(MetaBeanProperty var1);
}

