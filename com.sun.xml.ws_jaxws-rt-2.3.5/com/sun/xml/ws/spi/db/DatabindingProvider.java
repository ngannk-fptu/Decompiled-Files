/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.spi.db;

import com.oracle.webservices.api.databinding.Databinding;
import com.oracle.webservices.api.databinding.WSDLGenerator;
import com.sun.xml.ws.api.databinding.DatabindingConfig;
import java.util.Map;

public interface DatabindingProvider {
    public boolean isFor(String var1);

    public void init(Map<String, Object> var1);

    public Databinding create(DatabindingConfig var1);

    public WSDLGenerator wsdlGen(DatabindingConfig var1);
}

