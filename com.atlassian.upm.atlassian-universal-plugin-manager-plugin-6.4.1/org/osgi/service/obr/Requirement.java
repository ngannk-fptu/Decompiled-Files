/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.obr;

import org.osgi.service.obr.Capability;

public interface Requirement {
    public String getName();

    public String getFilter();

    public boolean isMultiple();

    public boolean isOptional();

    public boolean isExtend();

    public String getComment();

    public boolean isSatisfied(Capability var1);
}

