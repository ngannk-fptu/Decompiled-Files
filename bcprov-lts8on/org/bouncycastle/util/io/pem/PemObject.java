/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.io.pem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.util.io.pem.PemObjectGenerator;

public class PemObject
implements PemObjectGenerator {
    private static final List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());
    private String type;
    private List headers;
    private byte[] content;

    public PemObject(String type, byte[] content) {
        this(type, EMPTY_LIST, content);
    }

    public PemObject(String type, List headers, byte[] content) {
        this.type = type;
        this.headers = Collections.unmodifiableList(headers);
        this.content = content;
    }

    public String getType() {
        return this.type;
    }

    public List getHeaders() {
        return this.headers;
    }

    public byte[] getContent() {
        return this.content;
    }

    @Override
    public PemObject generate() throws PemGenerationException {
        return this;
    }
}

