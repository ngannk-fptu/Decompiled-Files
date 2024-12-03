/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.MediaExpression;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.MediaSpec;
import java.util.List;

public class MediaSpecAll
extends MediaSpec {
    public MediaSpecAll() {
        super("*");
    }

    @Override
    public boolean matches(MediaQuery q) {
        return true;
    }

    @Override
    public boolean matches(MediaExpression e) {
        return true;
    }

    @Override
    public boolean matchesOneOf(List<MediaQuery> queries) {
        return !queries.isEmpty();
    }

    @Override
    public String toString() {
        return "(all media)";
    }
}

