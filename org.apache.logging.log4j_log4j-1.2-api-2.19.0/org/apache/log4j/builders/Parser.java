/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.builders;

import org.apache.log4j.builders.Builder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.w3c.dom.Element;

public interface Parser<T>
extends Builder<T> {
    public T parse(Element var1, XmlConfiguration var2);

    public T parse(PropertiesConfiguration var1);
}

