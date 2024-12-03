/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.sun.xml.bind.v2.runtime;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class SwaRefAdapterMarker
extends XmlAdapter<String, DataHandler> {
    public DataHandler unmarshal(String v) throws Exception {
        throw new IllegalStateException("Not implemented");
    }

    public String marshal(DataHandler v) throws Exception {
        throw new IllegalStateException("Not implemented");
    }
}

