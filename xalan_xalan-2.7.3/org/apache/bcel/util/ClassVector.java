/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.bcel.classfile.JavaClass;

@Deprecated
public class ClassVector
implements Serializable {
    private static final long serialVersionUID = 5600397075672780806L;
    @Deprecated
    protected List<JavaClass> vec = new ArrayList<JavaClass>();

    public void addElement(JavaClass clazz) {
        this.vec.add(clazz);
    }

    public JavaClass elementAt(int index) {
        return this.vec.get(index);
    }

    public void removeElementAt(int index) {
        this.vec.remove(index);
    }

    public JavaClass[] toArray() {
        return this.vec.toArray(JavaClass.EMPTY_ARRAY);
    }
}

