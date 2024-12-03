/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.Category
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.builder.EqualsBuilder
 */
package com.atlassian.gadgets.directory.internal.jaxb;

import com.atlassian.gadgets.directory.Category;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.EqualsBuilder;

@XmlRootElement
public final class JAXBCategory {
    @XmlElement
    private final String name;

    private JAXBCategory() {
        this.name = null;
    }

    public JAXBCategory(Category category) {
        this.name = category.getName();
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (this == that) {
            return true;
        }
        if (that.getClass() != this.getClass()) {
            return false;
        }
        JAXBCategory other = (JAXBCategory)that;
        return new EqualsBuilder().append((Object)this.name, (Object)other.name).isEquals();
    }
}

