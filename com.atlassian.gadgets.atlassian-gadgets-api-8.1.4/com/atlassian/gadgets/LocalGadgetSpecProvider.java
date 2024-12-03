/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.GadgetSpecProvider;
import com.atlassian.gadgets.GadgetSpecUriNotAllowedException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;

public interface LocalGadgetSpecProvider
extends GadgetSpecProvider {
    public void writeGadgetSpecTo(URI var1, OutputStream var2) throws GadgetSpecUriNotAllowedException, IOException;

    public Date getLastModified(URI var1);
}

