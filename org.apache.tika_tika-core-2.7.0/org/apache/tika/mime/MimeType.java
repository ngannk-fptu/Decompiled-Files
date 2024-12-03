/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tika.mime.Magic;
import org.apache.tika.mime.MediaType;

public final class MimeType
implements Comparable<MimeType>,
Serializable {
    private static final long serialVersionUID = 4357830439860729201L;
    private final MediaType type;
    private final int minLength = 0;
    private String acronym = "";
    private String uti = "";
    private List<URI> links = Collections.emptyList();
    private String description = "";
    private List<Magic> magics = null;
    private List<RootXML> rootXML = null;
    private List<String> extensions = null;
    private boolean isInterpreted = false;

    MimeType(MediaType type) {
        if (type == null) {
            throw new IllegalArgumentException("Media type name is missing");
        }
        this.type = type;
    }

    public static boolean isValid(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is missing");
        }
        boolean slash = false;
        for (int i = 0; i < name.length(); ++i) {
            char ch = name.charAt(i);
            if (ch <= ' ' || ch >= '\u007f' || ch == '(' || ch == ')' || ch == '<' || ch == '>' || ch == '@' || ch == ',' || ch == ';' || ch == ':' || ch == '\\' || ch == '\"' || ch == '[' || ch == ']' || ch == '?' || ch == '=') {
                return false;
            }
            if (ch != '/') continue;
            if (slash || i == 0 || i + 1 == name.length()) {
                return false;
            }
            slash = true;
        }
        return slash;
    }

    public MediaType getType() {
        return this.type;
    }

    public String getName() {
        return this.type.toString();
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Description is missing");
        }
        this.description = description;
    }

    public String getAcronym() {
        return this.acronym;
    }

    void setAcronym(String v) {
        if (v == null) {
            throw new IllegalArgumentException("Acronym is missing");
        }
        this.acronym = v;
    }

    public String getUniformTypeIdentifier() {
        return this.uti;
    }

    void setUniformTypeIdentifier(String v) {
        if (v == null) {
            throw new IllegalArgumentException("Uniform Type Identifier is missing");
        }
        this.uti = v;
    }

    public List<URI> getLinks() {
        return this.links;
    }

    void addLink(URI link) {
        if (link == null) {
            throw new IllegalArgumentException("Missing Link");
        }
        ArrayList<URI> copy = new ArrayList<URI>(this.links.size() + 1);
        copy.addAll(this.links);
        copy.add(link);
        this.links = Collections.unmodifiableList(copy);
    }

    void addRootXML(String namespaceURI, String localName) {
        if (this.rootXML == null) {
            this.rootXML = new ArrayList<RootXML>();
        }
        this.rootXML.add(new RootXML(this, namespaceURI, localName));
    }

    boolean matchesXML(String namespaceURI, String localName) {
        if (this.rootXML != null) {
            for (RootXML xml : this.rootXML) {
                if (!xml.matches(namespaceURI, localName)) continue;
                return true;
            }
        }
        return false;
    }

    boolean hasRootXML() {
        return this.rootXML != null;
    }

    List<Magic> getMagics() {
        if (this.magics != null) {
            return this.magics;
        }
        return Collections.emptyList();
    }

    void addMagic(Magic magic) {
        if (magic == null) {
            return;
        }
        if (this.magics == null) {
            this.magics = new ArrayList<Magic>();
        }
        this.magics.add(magic);
    }

    int getMinLength() {
        return 0;
    }

    public boolean hasMagic() {
        return this.magics != null;
    }

    public boolean matchesMagic(byte[] data) {
        for (int i = 0; this.magics != null && i < this.magics.size(); ++i) {
            Magic magic = this.magics.get(i);
            if (!magic.eval(data)) continue;
            return true;
        }
        return false;
    }

    public boolean matches(byte[] data) {
        return this.matchesMagic(data);
    }

    boolean isInterpreted() {
        return this.isInterpreted;
    }

    void setInterpreted(boolean interpreted) {
        this.isInterpreted = interpreted;
    }

    @Override
    public int compareTo(MimeType mime) {
        return this.type.compareTo(mime.type);
    }

    public boolean equals(Object o) {
        if (o instanceof MimeType) {
            MimeType that = (MimeType)o;
            return this.type.equals(that.type);
        }
        return false;
    }

    public int hashCode() {
        return this.type.hashCode();
    }

    public String toString() {
        return this.type.toString();
    }

    public String getExtension() {
        if (this.extensions == null) {
            return "";
        }
        return this.extensions.get(0);
    }

    public List<String> getExtensions() {
        if (this.extensions != null) {
            return Collections.unmodifiableList(this.extensions);
        }
        return Collections.emptyList();
    }

    void addExtension(String extension) {
        if (this.extensions == null) {
            this.extensions = Collections.singletonList(extension);
        } else if (this.extensions.size() == 1) {
            this.extensions = new ArrayList<String>(this.extensions);
        }
        if (!this.extensions.contains(extension)) {
            this.extensions.add(extension);
        }
    }

    static class RootXML
    implements Serializable {
        private static final long serialVersionUID = 5140496601491000730L;
        private MimeType type = null;
        private String namespaceURI = null;
        private String localName = null;

        RootXML(MimeType type, String namespaceURI, String localName) {
            if (this.isEmpty(namespaceURI) && this.isEmpty(localName)) {
                throw new IllegalArgumentException("Both namespaceURI and localName cannot be empty");
            }
            this.type = type;
            this.namespaceURI = namespaceURI;
            this.localName = localName;
        }

        boolean matches(String namespaceURI, String localName) {
            if (!this.isEmpty(this.namespaceURI) ? !this.namespaceURI.equals(namespaceURI) : !this.isEmpty(namespaceURI)) {
                return false;
            }
            if (!this.isEmpty(this.localName)) {
                return this.localName.equals(localName);
            }
            return this.isEmpty(localName);
        }

        private boolean isEmpty(String str) {
            return str == null || str.equals("");
        }

        MimeType getType() {
            return this.type;
        }

        String getNameSpaceURI() {
            return this.namespaceURI;
        }

        String getLocalName() {
            return this.localName;
        }

        public String toString() {
            return this.type + ", " + this.namespaceURI + ", " + this.localName;
        }
    }
}

