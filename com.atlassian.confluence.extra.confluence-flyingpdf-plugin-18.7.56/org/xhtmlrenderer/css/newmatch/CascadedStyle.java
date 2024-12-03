/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.newmatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class CascadedStyle {
    private Map cascadedProperties;
    private String fingerprint;
    public static final CascadedStyle emptyCascadedStyle = new CascadedStyle();

    public static CascadedStyle createAnonymousStyle(IdentValue display) {
        PropertyValue val = new PropertyValue(display);
        List<PropertyDeclaration> props = Collections.singletonList(new PropertyDeclaration(CSSName.DISPLAY, val, true, 1));
        return new CascadedStyle(props.iterator());
    }

    public static CascadedStyle createLayoutStyle(PropertyDeclaration[] decls) {
        return new CascadedStyle(Arrays.asList(decls).iterator());
    }

    public static CascadedStyle createLayoutStyle(List decls) {
        return new CascadedStyle(decls.iterator());
    }

    public static CascadedStyle createLayoutStyle(CascadedStyle startingPoint, PropertyDeclaration[] decls) {
        return new CascadedStyle(startingPoint, Arrays.asList(decls).iterator());
    }

    public static PropertyDeclaration createLayoutPropertyDeclaration(CSSName cssName, IdentValue display) {
        PropertyValue val = new PropertyValue(display);
        return new PropertyDeclaration(cssName, val, true, 1);
    }

    CascadedStyle(Iterator iter) {
        this();
        this.addProperties(iter);
    }

    private void addProperties(Iterator iter) {
        int i;
        List[] buckets = new List[6];
        for (i = 0; i < buckets.length; ++i) {
            buckets[i] = new LinkedList();
        }
        while (iter.hasNext()) {
            PropertyDeclaration prop = (PropertyDeclaration)iter.next();
            buckets[prop.getImportanceAndOrigin()].add(prop);
        }
        for (i = 0; i < buckets.length; ++i) {
            for (PropertyDeclaration prop : buckets[i]) {
                this.cascadedProperties.put(prop.getCSSName(), prop);
            }
        }
    }

    private CascadedStyle(CascadedStyle startingPoint, Iterator props) {
        this.cascadedProperties = new TreeMap(startingPoint.cascadedProperties);
        this.addProperties(props);
    }

    private CascadedStyle() {
        this.cascadedProperties = new TreeMap();
    }

    public boolean hasProperty(CSSName cssName) {
        return this.cascadedProperties.get(cssName) != null;
    }

    public PropertyDeclaration propertyByName(CSSName cssName) {
        PropertyDeclaration prop = (PropertyDeclaration)this.cascadedProperties.get(cssName);
        return prop;
    }

    public IdentValue getIdent(CSSName cssName) {
        PropertyDeclaration pd = this.propertyByName(cssName);
        return pd == null ? null : pd.asIdentValue();
    }

    public Iterator getCascadedPropertyDeclarations() {
        ArrayList list = new ArrayList(this.cascadedProperties.size());
        Iterator iter = this.cascadedProperties.values().iterator();
        while (iter.hasNext()) {
            list.add(iter.next());
        }
        return list.iterator();
    }

    public int countAssigned() {
        return this.cascadedProperties.size();
    }

    public String getFingerprint() {
        if (this.fingerprint == null) {
            StringBuffer sb = new StringBuffer();
            Iterator iter = this.cascadedProperties.values().iterator();
            while (iter.hasNext()) {
                sb.append(((PropertyDeclaration)iter.next()).getFingerprint());
            }
            this.fingerprint = sb.toString();
        }
        return this.fingerprint;
    }
}

