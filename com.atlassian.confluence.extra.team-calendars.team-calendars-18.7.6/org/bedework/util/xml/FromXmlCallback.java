/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.w3c.dom.Element;

public class FromXmlCallback {
    private final Map<String, Class> classForName = new HashMap<String, Class>();
    private final Set<String> skipThese = new TreeSet<String>();
    private final Map<String, String> mapFields = new HashMap<String, String>();

    public Class forElement(Element el) throws Throwable {
        return this.classForName.get(el.getTagName());
    }

    public void addClassForName(String name, Class cl) {
        this.classForName.put(name, cl);
    }

    public void addMapField(String from, String to) {
        this.mapFields.put(from, to);
    }

    public String mappedField(String from) {
        return this.mapFields.get(from);
    }

    public Object simpleValue(Class cl, String val) throws Throwable {
        return null;
    }

    public boolean skipElement(Element el) throws Throwable {
        return this.skipThese.contains(el.getTagName());
    }

    public void addSkips(String ... names) {
        Collections.addAll(this.skipThese, names);
    }

    public String getFieldlName(Element el) throws Throwable {
        return this.mappedField(el.getNodeName());
    }

    public boolean save(Element el, Object theObject, Object theValue) throws Throwable {
        return false;
    }
}

