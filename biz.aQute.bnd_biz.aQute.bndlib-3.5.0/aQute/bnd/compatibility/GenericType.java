/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.compatibility;

import aQute.bnd.compatibility.Scope;

public class GenericType {
    static final GenericType[] EMPTY = new GenericType[0];
    Scope reference;
    GenericType[] a;
    GenericType[] b;
    int array;
    Scope scope;

    public GenericType(Class<Object> class1) {
    }

    public static class GenericArray
    extends GenericType {
        public GenericArray(Class<Object> class1) {
            super(class1);
        }
    }

    public static class GenericWildcard
    extends GenericType {
        public GenericWildcard(Class<Object> class1) {
            super(class1);
        }
    }
}

