/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.configuration2.ex.ConfigurationException;

public interface InputStreamSupport {
    public void read(InputStream var1) throws ConfigurationException, IOException;
}

