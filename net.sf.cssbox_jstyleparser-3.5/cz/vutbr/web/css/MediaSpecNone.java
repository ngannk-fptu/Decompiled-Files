/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.MediaExpression;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.MediaSpec;
import java.util.List;

public class MediaSpecNone
extends MediaSpec {
    public MediaSpecNone() {
        super("!");
    }

    @Override
    public boolean matches(MediaQuery q) {
        return false;
    }

    @Override
    public boolean matches(MediaExpression e) {
        return false;
    }

    @Override
    public boolean matchesOneOf(List<MediaQuery> queries) {
        return false;
    }

    @Override
    public boolean matchesEmpty() {
        return false;
    }

    @Override
    public String toString() {
        return "(no media)";
    }
}

