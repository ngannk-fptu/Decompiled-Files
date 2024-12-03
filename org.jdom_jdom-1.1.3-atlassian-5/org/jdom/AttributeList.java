/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.IllegalAddException;
import org.jdom.Namespace;
import org.jdom.Verifier;

class AttributeList
extends AbstractList
implements List,
Serializable {
    private static final String CVS_ID = "@(#) $RCSfile: AttributeList.java,v $ $Revision: 1.24 $ $Date: 2007/11/10 05:28:58 $ $Name:  $";
    private static final int INITIAL_ARRAY_SIZE = 5;
    private Attribute[] elementData;
    private int size;
    private Element parent;

    private AttributeList() {
    }

    AttributeList(Element parent) {
        this.parent = parent;
    }

    final void uncheckedAddAttribute(Attribute a) {
        a.parent = this.parent;
        this.ensureCapacity(this.size + 1);
        this.elementData[this.size++] = a;
        ++this.modCount;
    }

    @Override
    public boolean add(Object obj) {
        if (obj instanceof Attribute) {
            Attribute attribute = (Attribute)obj;
            int duplicate = this.indexOfDuplicate(attribute);
            if (duplicate < 0) {
                this.add(this.size(), attribute);
            } else {
                this.set(duplicate, attribute);
            }
        } else {
            if (obj == null) {
                throw new IllegalAddException("Cannot add null attribute");
            }
            throw new IllegalAddException("Class " + obj.getClass().getName() + " is not an attribute");
        }
        return true;
    }

    public void add(int index, Object obj) {
        Attribute attribute;
        if (obj instanceof Attribute) {
            attribute = (Attribute)obj;
            int duplicate = this.indexOfDuplicate(attribute);
            if (duplicate >= 0) {
                throw new IllegalAddException("Cannot add duplicate attribute");
            }
        } else {
            if (obj == null) {
                throw new IllegalAddException("Cannot add null attribute");
            }
            throw new IllegalAddException("Class " + obj.getClass().getName() + " is not an attribute");
        }
        this.add(index, attribute);
        ++this.modCount;
    }

    void add(int index, Attribute attribute) {
        if (attribute.getParent() != null) {
            throw new IllegalAddException("The attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"");
        }
        String reason = Verifier.checkNamespaceCollision(attribute, this.parent);
        if (reason != null) {
            throw new IllegalAddException(this.parent, attribute, reason);
        }
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        attribute.setParent(this.parent);
        this.ensureCapacity(this.size + 1);
        if (index == this.size) {
            this.elementData[this.size++] = attribute;
        } else {
            System.arraycopy(this.elementData, index, this.elementData, index + 1, this.size - index);
            this.elementData[index] = attribute;
            ++this.size;
        }
        ++this.modCount;
    }

    @Override
    public boolean addAll(Collection collection) {
        return this.addAll(this.size(), collection);
    }

    public boolean addAll(int index, Collection collection) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        if (collection == null || collection.size() == 0) {
            return false;
        }
        this.ensureCapacity(this.size() + collection.size());
        int count = 0;
        try {
            for (Object obj : collection) {
                this.add(index + count, obj);
                ++count;
            }
        }
        catch (RuntimeException exception) {
            for (int i = 0; i < count; ++i) {
                this.remove(index);
            }
            throw exception;
        }
        return true;
    }

    @Override
    public void clear() {
        if (this.elementData != null) {
            for (int i = 0; i < this.size; ++i) {
                Attribute attribute = this.elementData[i];
                attribute.setParent(null);
            }
            this.elementData = null;
            this.size = 0;
        }
        ++this.modCount;
    }

    void clearAndSet(Collection collection) {
        Attribute[] old = this.elementData;
        int oldSize = this.size;
        this.elementData = null;
        this.size = 0;
        if (collection != null && collection.size() != 0) {
            this.ensureCapacity(collection.size());
            try {
                this.addAll(0, collection);
            }
            catch (RuntimeException exception) {
                this.elementData = old;
                this.size = oldSize;
                throw exception;
            }
        }
        if (old != null) {
            for (int i = 0; i < oldSize; ++i) {
                Attribute attribute = old[i];
                attribute.setParent(null);
            }
        }
        ++this.modCount;
    }

    private void ensureCapacity(int minCapacity) {
        if (this.elementData == null) {
            this.elementData = new Attribute[Math.max(minCapacity, 5)];
        } else {
            int oldCapacity = this.elementData.length;
            if (minCapacity > oldCapacity) {
                Attribute[] oldData = this.elementData;
                int newCapacity = oldCapacity * 3 / 2 + 1;
                if (newCapacity < minCapacity) {
                    newCapacity = minCapacity;
                }
                this.elementData = new Attribute[newCapacity];
                System.arraycopy(oldData, 0, this.elementData, 0, this.size);
            }
        }
    }

    public Object get(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        return this.elementData[index];
    }

    Object get(String name, Namespace namespace) {
        int index = this.indexOf(name, namespace);
        if (index < 0) {
            return null;
        }
        return this.elementData[index];
    }

    int indexOf(String name, Namespace namespace) {
        String uri = namespace.getURI();
        if (this.elementData != null) {
            for (int i = 0; i < this.size; ++i) {
                Attribute old = this.elementData[i];
                String oldURI = old.getNamespaceURI();
                String oldName = old.getName();
                if (!oldURI.equals(uri) || !oldName.equals(name)) continue;
                return i;
            }
        }
        return -1;
    }

    public Object remove(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        Attribute old = this.elementData[index];
        old.setParent(null);
        int numMoved = this.size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData, index, numMoved);
        }
        this.elementData[--this.size] = null;
        ++this.modCount;
        return old;
    }

    boolean remove(String name, Namespace namespace) {
        int index = this.indexOf(name, namespace);
        if (index < 0) {
            return false;
        }
        this.remove(index);
        return true;
    }

    public Object set(int index, Object obj) {
        if (obj instanceof Attribute) {
            Attribute attribute = (Attribute)obj;
            int duplicate = this.indexOfDuplicate(attribute);
            if (duplicate >= 0 && duplicate != index) {
                throw new IllegalAddException("Cannot set duplicate attribute");
            }
            return this.set(index, attribute);
        }
        if (obj == null) {
            throw new IllegalAddException("Cannot add null attribute");
        }
        throw new IllegalAddException("Class " + obj.getClass().getName() + " is not an attribute");
    }

    Object set(int index, Attribute attribute) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        if (attribute.getParent() != null) {
            throw new IllegalAddException("The attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"");
        }
        String reason = Verifier.checkNamespaceCollision(attribute, this.parent);
        if (reason != null) {
            throw new IllegalAddException(this.parent, attribute, reason);
        }
        Attribute old = this.elementData[index];
        old.setParent(null);
        this.elementData[index] = attribute;
        attribute.setParent(this.parent);
        return old;
    }

    private int indexOfDuplicate(Attribute attribute) {
        int duplicate = -1;
        String name = attribute.getName();
        Namespace namespace = attribute.getNamespace();
        duplicate = this.indexOf(name, namespace);
        return duplicate;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

