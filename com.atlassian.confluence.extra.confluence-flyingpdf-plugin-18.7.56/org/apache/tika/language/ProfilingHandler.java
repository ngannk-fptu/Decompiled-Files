/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language;

import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.LanguageProfile;
import org.apache.tika.language.ProfilingWriter;
import org.apache.tika.sax.WriteOutContentHandler;

@Deprecated
public class ProfilingHandler
extends WriteOutContentHandler {
    private final ProfilingWriter writer;

    public ProfilingHandler(ProfilingWriter writer) {
        super(writer);
        this.writer = writer;
    }

    public ProfilingHandler(LanguageProfile profile) {
        this(new ProfilingWriter(profile));
    }

    public ProfilingHandler() {
        this(new ProfilingWriter());
    }

    public LanguageProfile getProfile() {
        return this.writer.getProfile();
    }

    public LanguageIdentifier getLanguage() {
        return this.writer.getLanguage();
    }
}

