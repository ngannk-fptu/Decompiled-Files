/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.Policy
 *  com.sun.xml.ws.policy.sourcemodel.PolicyModelGenerator
 *  com.sun.xml.ws.policy.sourcemodel.PolicyModelGenerator$PolicySourceModelCreator
 *  com.sun.xml.ws.policy.sourcemodel.PolicySourceModel
 *  com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion
 */
package com.sun.xml.ws.api.policy;

import com.sun.xml.ws.api.policy.SourceModel;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.sourcemodel.PolicyModelGenerator;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;

public abstract class ModelGenerator
extends PolicyModelGenerator {
    private static final SourceModelCreator CREATOR = new SourceModelCreator();

    private ModelGenerator() {
    }

    public static PolicyModelGenerator getGenerator() {
        return PolicyModelGenerator.getCompactGenerator((PolicyModelGenerator.PolicySourceModelCreator)CREATOR);
    }

    protected static class SourceModelCreator
    extends PolicyModelGenerator.PolicySourceModelCreator {
        protected SourceModelCreator() {
        }

        protected PolicySourceModel create(Policy policy) {
            return SourceModel.createPolicySourceModel((NamespaceVersion)policy.getNamespaceVersion(), (String)policy.getId(), (String)policy.getName());
        }
    }
}

