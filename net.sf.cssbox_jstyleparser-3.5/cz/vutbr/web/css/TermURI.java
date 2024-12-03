/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Term;
import java.net.URL;

public interface TermURI
extends Term<String> {
    public TermURI setBase(URL var1);

    public URL getBase();
}

