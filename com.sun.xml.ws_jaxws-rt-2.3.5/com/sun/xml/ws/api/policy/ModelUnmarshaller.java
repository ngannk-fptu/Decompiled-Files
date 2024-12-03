/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.sourcemodel.PolicySourceModel
 *  com.sun.xml.ws.policy.sourcemodel.XmlPolicyModelUnmarshaller
 *  com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion
 */
package com.sun.xml.ws.api.policy;

import com.sun.xml.ws.api.policy.SourceModel;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.sourcemodel.XmlPolicyModelUnmarshaller;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;

public class ModelUnmarshaller
extends XmlPolicyModelUnmarshaller {
    private static final ModelUnmarshaller INSTANCE = new ModelUnmarshaller();

    private ModelUnmarshaller() {
    }

    public static ModelUnmarshaller getUnmarshaller() {
        return INSTANCE;
    }

    protected PolicySourceModel createSourceModel(NamespaceVersion nsVersion, String id, String name) {
        return SourceModel.createSourceModel(nsVersion, id, name);
    }
}

