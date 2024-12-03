/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaAnnotated;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaIdentityConstraint;

public interface SchemaLocalElement
extends SchemaField,
SchemaAnnotated {
    public boolean blockExtension();

    public boolean blockRestriction();

    public boolean blockSubstitution();

    public boolean isAbstract();

    public SchemaIdentityConstraint[] getIdentityConstraints();
}

