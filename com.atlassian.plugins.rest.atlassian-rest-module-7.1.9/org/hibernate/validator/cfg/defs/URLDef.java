/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.Pattern$Flag
 */
package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.URL;

public class URLDef
extends ConstraintDef<URLDef, URL> {
    public URLDef() {
        super(URL.class);
    }

    public URLDef protocol(String protocol) {
        this.addParameter("protocol", protocol);
        return this;
    }

    public URLDef host(String host) {
        this.addParameter("host", host);
        return this;
    }

    public URLDef port(int port) {
        this.addParameter("port", port);
        return this;
    }

    public URLDef regexp(String regexp) {
        this.addParameter("regexp", regexp);
        return this;
    }

    public URLDef flags(Pattern.Flag ... flags) {
        this.addParameter("flags", flags);
        return this;
    }
}

