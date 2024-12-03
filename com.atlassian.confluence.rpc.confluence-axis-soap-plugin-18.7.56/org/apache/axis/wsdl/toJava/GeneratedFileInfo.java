/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GeneratedFileInfo {
    protected ArrayList list = new ArrayList();

    public List getList() {
        return this.list;
    }

    public void add(String name, String className, String type) {
        this.list.add(new Entry(name, className, type));
    }

    public List findType(String type) {
        ArrayList<Entry> ret = null;
        Iterator i = this.list.iterator();
        while (i.hasNext()) {
            Entry e = (Entry)i.next();
            if (!e.type.equals(type)) continue;
            if (ret == null) {
                ret = new ArrayList<Entry>();
            }
            ret.add(e);
        }
        return ret;
    }

    public Entry findName(String fileName) {
        Iterator i = this.list.iterator();
        while (i.hasNext()) {
            Entry e = (Entry)i.next();
            if (!e.fileName.equals(fileName)) continue;
            return e;
        }
        return null;
    }

    public Entry findClass(String className) {
        Iterator i = this.list.iterator();
        while (i.hasNext()) {
            Entry e = (Entry)i.next();
            if (!e.className.equals(className)) continue;
            return e;
        }
        return null;
    }

    public List getClassNames() {
        ArrayList<String> ret = new ArrayList<String>(this.list.size());
        Iterator i = this.list.iterator();
        while (i.hasNext()) {
            Entry e = (Entry)i.next();
            ret.add(e.className);
        }
        return ret;
    }

    public List getFileNames() {
        ArrayList<String> ret = new ArrayList<String>(this.list.size());
        Iterator i = this.list.iterator();
        while (i.hasNext()) {
            Entry e = (Entry)i.next();
            ret.add(e.fileName);
        }
        return ret;
    }

    public String toString() {
        String s = "";
        Iterator i = this.list.iterator();
        while (i.hasNext()) {
            Entry entry = (Entry)i.next();
            s = s + entry.toString() + "\n";
        }
        return s;
    }

    public class Entry {
        public String fileName;
        public String className;
        public String type;

        public Entry(String name, String className, String type) {
            this.fileName = name;
            this.className = className;
            this.type = type;
        }

        public String toString() {
            return "Name: " + this.fileName + " Class: " + this.className + " Type: " + this.type;
        }
    }
}

