/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator.test;

import com.mchange.v1.identicator.IdWeakHashMap;
import com.mchange.v1.identicator.Identicator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TestIdWeakHashMap {
    static final Identicator id = new Identicator(){

        @Override
        public boolean identical(Object object, Object object2) {
            return ((String)object).charAt(0) == ((String)object2).charAt(0);
        }

        @Override
        public int hash(Object object) {
            return ((String)object).charAt(0);
        }
    };
    static final Map weak = new IdWeakHashMap(id);

    public static void main(String[] stringArray) {
        TestIdWeakHashMap.doAdds();
        System.gc();
        TestIdWeakHashMap.show();
        TestIdWeakHashMap.setRemoveHi();
        System.gc();
        TestIdWeakHashMap.show();
    }

    static void setRemoveHi() {
        String string = new String("bye");
        weak.put(string, "");
        Set set = weak.keySet();
        set.remove("hi");
        TestIdWeakHashMap.show();
    }

    static void doAdds() {
        String string = "hi";
        String string2 = new String("hello");
        String string3 = new String("yoohoo");
        String string4 = new String("poop");
        weak.put(string, "");
        weak.put(string2, "");
        weak.put(string3, "");
        weak.put(string4, "");
        TestIdWeakHashMap.show();
    }

    static void show() {
        System.out.println("elements:");
        Iterator iterator = weak.keySet().iterator();
        while (iterator.hasNext()) {
            System.out.println("\t" + iterator.next());
        }
        System.out.println("size: " + weak.size());
    }
}

