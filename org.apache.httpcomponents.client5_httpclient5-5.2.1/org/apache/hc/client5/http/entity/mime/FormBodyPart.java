/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.entity.mime;

import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.Header;
import org.apache.hc.client5.http.entity.mime.MultipartPart;
import org.apache.hc.core5.util.Args;

public class FormBodyPart
extends MultipartPart {
    private final String name;

    FormBodyPart(String name, ContentBody body, Header header) {
        super(body, header);
        Args.notNull((Object)name, (String)"Name");
        Args.notNull((Object)body, (String)"Body");
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void addField(String name, String value) {
        Args.notNull((Object)name, (String)"Field name");
        super.addField(name, value);
    }
}

