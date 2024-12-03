/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.apache.xml.security.binding.xmldsig.pss;

import javax.xml.bind.annotation.XmlRegistry;
import org.apache.xml.security.binding.xmldsig.pss.MaskGenerationFunctionType;
import org.apache.xml.security.binding.xmldsig.pss.RSAPSSParams;
import org.apache.xml.security.binding.xmldsig.pss.RSAPSSParamsType;

@XmlRegistry
public class ObjectFactory {
    public RSAPSSParams createRSAPSSParams() {
        return new RSAPSSParams();
    }

    public RSAPSSParamsType createRSAPSSParamsType() {
        return new RSAPSSParamsType();
    }

    public MaskGenerationFunctionType createMaskGenerationFunctionType() {
        return new MaskGenerationFunctionType();
    }
}

