/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;

public interface HTMLDocument
extends Document {
    public String getTitle();

    public void setTitle(String var1);

    public String getReferrer();

    public String getDomain();

    public String getURL();

    public HTMLElement getBody();

    public void setBody(HTMLElement var1);

    public HTMLCollection getImages();

    public HTMLCollection getApplets();

    public HTMLCollection getLinks();

    public HTMLCollection getForms();

    public HTMLCollection getAnchors();

    public String getCookie();

    public void setCookie(String var1);

    public void open();

    public void close();

    public void write(String var1);

    public void writeln(String var1);

    public Element getElementById(String var1);

    public NodeList getElementsByName(String var1);
}

