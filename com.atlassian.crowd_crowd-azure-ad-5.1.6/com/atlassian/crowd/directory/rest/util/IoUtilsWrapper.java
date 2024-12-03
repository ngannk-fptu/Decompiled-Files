/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.crowd.directory.rest.util;

import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;

public class IoUtilsWrapper {
    public InputStream toInputStream(String input, Charset encoding) {
        return IOUtils.toInputStream((String)input, (Charset)encoding);
    }
}

