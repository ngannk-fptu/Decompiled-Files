/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset;

import com.opensymphony.module.propertyset.IllegalPropertyException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.verifiers.PropertyVerifier;
import com.opensymphony.module.propertyset.verifiers.VerifyException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class PropertySchema
implements Serializable {
    private Collection verifiers;
    private String name;
    private int type;

    public PropertySchema() {
        this(null);
    }

    public PropertySchema(String name) {
        this.name = name;
        this.verifiers = new HashSet();
    }

    public void setPropertyName(String s) {
        this.name = s;
    }

    public String getPropertyName() {
        return this.name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public Collection getVerifiers() {
        return Collections.unmodifiableCollection(this.verifiers);
    }

    public boolean addVerifier(PropertyVerifier pv) {
        return this.verifiers.add(pv);
    }

    public boolean removeVerifier(PropertyVerifier pv) {
        return this.verifiers.remove(pv);
    }

    public void validate(Object value) throws PropertyException {
        Iterator i = this.verifiers.iterator();
        while (i.hasNext()) {
            PropertyVerifier pv = (PropertyVerifier)i.next();
            try {
                pv.verify(value);
            }
            catch (VerifyException ex) {
                throw new IllegalPropertyException(ex.getMessage());
            }
        }
    }
}

