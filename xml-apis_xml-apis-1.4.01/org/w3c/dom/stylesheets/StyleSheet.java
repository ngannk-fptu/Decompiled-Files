/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.stylesheets;

import org.w3c.dom.Node;
import org.w3c.dom.stylesheets.MediaList;

public interface StyleSheet {
    public String getType();

    public boolean getDisabled();

    public void setDisabled(boolean var1);

    public Node getOwnerNode();

    public StyleSheet getParentStyleSheet();

    public String getHref();

    public String getTitle();

    public MediaList getMedia();
}

