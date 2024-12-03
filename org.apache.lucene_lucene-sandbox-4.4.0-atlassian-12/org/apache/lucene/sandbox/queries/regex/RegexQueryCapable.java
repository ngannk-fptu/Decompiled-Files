/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.sandbox.queries.regex;

import org.apache.lucene.sandbox.queries.regex.RegexCapabilities;

public interface RegexQueryCapable {
    public void setRegexImplementation(RegexCapabilities var1);

    public RegexCapabilities getRegexImplementation();
}

