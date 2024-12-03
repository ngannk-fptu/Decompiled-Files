/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.spec;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.spec.GadgetSpec;
import java.net.URI;

public interface GadgetSpecFactory {
    public GadgetSpec getGadgetSpec(GadgetState var1, GadgetRequestContext var2) throws GadgetParsingException;

    public GadgetSpec getGadgetSpec(URI var1, GadgetRequestContext var2) throws GadgetParsingException;
}

