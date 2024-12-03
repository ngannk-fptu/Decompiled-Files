/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.commons.name.HashCache;

public class NameFactoryImpl
implements NameFactory {
    private static final NameFactory INSTANCE = new NameFactoryImpl();
    private final HashCache<Name> cache = new HashCache();

    private NameFactoryImpl() {
    }

    public static NameFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public Name create(String namespaceURI, String localName) throws IllegalArgumentException {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("No namespaceURI specified");
        }
        if (localName == null) {
            throw new IllegalArgumentException("No localName specified");
        }
        return this.cache.get(new NameImpl(namespaceURI, localName));
    }

    @Override
    public Name create(String nameString) throws IllegalArgumentException {
        if (nameString == null || "".equals(nameString)) {
            throw new IllegalArgumentException("No Name literal specified");
        }
        if (nameString.charAt(0) != '{') {
            throw new IllegalArgumentException("Invalid Name literal: " + nameString);
        }
        int i = nameString.indexOf(125);
        if (i == -1) {
            throw new IllegalArgumentException("Invalid Name literal: " + nameString);
        }
        if (i == nameString.length() - 1) {
            throw new IllegalArgumentException("Invalid Name literal: " + nameString);
        }
        return this.cache.get(new NameImpl(nameString.substring(1, i), nameString.substring(i + 1)));
    }

    private static class NameImpl
    implements Name {
        private static final String EMPTY = "".intern();
        private transient int hash;
        private transient String string;
        private final String namespaceURI;
        private final String localName;

        private NameImpl(String namespaceURI, String localName) {
            this.namespaceURI = namespaceURI.length() == 0 ? EMPTY : namespaceURI.intern();
            this.localName = localName;
            this.hash = 0;
        }

        @Override
        public String getLocalName() {
            return this.localName;
        }

        @Override
        public String getNamespaceURI() {
            return this.namespaceURI;
        }

        public String toString() {
            if (this.string == null) {
                this.string = '{' + this.namespaceURI + '}' + this.localName;
            }
            return this.string;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof NameImpl) {
                NameImpl other = (NameImpl)obj;
                return this.namespaceURI == other.namespaceURI && this.localName.equals(other.localName);
            }
            if (obj instanceof Name) {
                Name other = (Name)obj;
                return this.namespaceURI.equals(other.getNamespaceURI()) && this.localName.equals(other.getLocalName());
            }
            return false;
        }

        public int hashCode() {
            int h = this.hash;
            if (h == 0) {
                h = 17;
                h = 37 * h + this.namespaceURI.hashCode();
                this.hash = h = 37 * h + this.localName.hashCode();
            }
            return h;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public int compareTo(Object o) {
            if (this == o) {
                return 0;
            }
            Name other = (Name)o;
            if (this.namespaceURI.equals(other.getNamespaceURI())) {
                return this.localName.compareTo(other.getLocalName());
            }
            return this.namespaceURI.compareTo(other.getNamespaceURI());
        }

        private Object readResolve() {
            return new NameImpl(this.namespaceURI, this.localName);
        }
    }
}

