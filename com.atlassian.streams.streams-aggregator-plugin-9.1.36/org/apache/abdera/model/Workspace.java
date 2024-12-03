/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 */
package org.apache.abdera.model;

import java.util.List;
import javax.activation.MimeType;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Text;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Workspace
extends ExtensibleElement {
    public String getTitle();

    public Text setTitle(String var1);

    public Text setTitleAsHtml(String var1);

    public Text setTitleAsXHtml(String var1);

    public Text getTitleElement();

    public List<Collection> getCollections();

    public Collection getCollection(String var1);

    public Workspace addCollection(Collection var1);

    public Collection addCollection(String var1, String var2);

    public Collection addMultipartCollection(String var1, String var2);

    public Collection getCollectionThatAccepts(MimeType ... var1);

    public Collection getCollectionThatAccepts(String ... var1);

    public List<Collection> getCollectionsThatAccept(MimeType ... var1);

    public List<Collection> getCollectionsThatAccept(String ... var1);
}

