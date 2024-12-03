/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class COSArrayList<E>
implements List<E> {
    private final COSArray array;
    private final List<E> actual;
    private boolean isFiltered = false;
    private COSDictionary parentDict;
    private COSName dictKey;

    public COSArrayList() {
        this.array = new COSArray();
        this.actual = new ArrayList();
    }

    public COSArrayList(List<E> actualList, COSArray cosArray) {
        this.actual = actualList;
        this.array = cosArray;
        if (this.actual.size() != this.array.size()) {
            this.isFiltered = true;
        }
    }

    public COSArrayList(COSDictionary dictionary, COSName dictionaryKey) {
        this.array = new COSArray();
        this.actual = new ArrayList();
        this.parentDict = dictionary;
        this.dictKey = dictionaryKey;
    }

    public COSArrayList(E actualObject, COSBase item, COSDictionary dictionary, COSName dictionaryKey) {
        this.array = new COSArray();
        this.array.add(item);
        this.actual = new ArrayList();
        this.actual.add(actualObject);
        this.parentDict = dictionary;
        this.dictKey = dictionaryKey;
    }

    @Override
    public int size() {
        return this.actual.size();
    }

    @Override
    public boolean isEmpty() {
        return this.actual.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.actual.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.actual.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.actual.toArray();
    }

    @Override
    public <X> X[] toArray(X[] a) {
        return this.actual.toArray(a);
    }

    @Override
    public boolean add(E o) {
        if (this.parentDict != null) {
            this.parentDict.setItem(this.dictKey, (COSBase)this.array);
            this.parentDict = null;
        }
        if (o instanceof String) {
            this.array.add(new COSString((String)o));
        } else if (this.array != null) {
            this.array.add(((COSObjectable)o).getCOSObject());
        }
        return this.actual.add(o);
    }

    @Override
    public boolean remove(Object o) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("removing entries from a filtered List is not permitted");
        }
        boolean retval = true;
        int index = this.actual.indexOf(o);
        if (index >= 0) {
            this.actual.remove(index);
            this.array.remove(index);
        } else {
            retval = false;
        }
        return retval;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.actual.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("Adding to a filtered List is not permitted");
        }
        if (this.parentDict != null && c.size() > 0) {
            this.parentDict.setItem(this.dictKey, (COSBase)this.array);
            this.parentDict = null;
        }
        this.array.addAll(this.toCOSObjectList(c));
        return this.actual.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("Inserting to a filtered List is not permitted");
        }
        if (this.parentDict != null && c.size() > 0) {
            this.parentDict.setItem(this.dictKey, (COSBase)this.array);
            this.parentDict = null;
        }
        this.array.addAll(index, this.toCOSObjectList(c));
        return this.actual.addAll(index, c);
    }

    public static List<Integer> convertIntegerCOSArrayToList(COSArray intArray) {
        COSArrayList retval = null;
        if (intArray != null) {
            ArrayList<Integer> numbers = new ArrayList<Integer>();
            for (int i = 0; i < intArray.size(); ++i) {
                COSNumber num = intArray.get(i) instanceof COSObject ? (COSNumber)((COSObject)intArray.get(i)).getObject() : (COSNumber)intArray.get(i);
                numbers.add(num.intValue());
            }
            retval = new COSArrayList(numbers, intArray);
        }
        return retval;
    }

    public static List<Float> convertFloatCOSArrayToList(COSArray floatArray) {
        COSArrayList retval = null;
        if (floatArray != null) {
            ArrayList<Float> numbers = new ArrayList<Float>(floatArray.size());
            for (int i = 0; i < floatArray.size(); ++i) {
                COSBase base = floatArray.getObject(i);
                if (base instanceof COSNumber) {
                    numbers.add(Float.valueOf(((COSNumber)base).floatValue()));
                    continue;
                }
                numbers.add(null);
            }
            retval = new COSArrayList(numbers, floatArray);
        }
        return retval;
    }

    public static List<String> convertCOSNameCOSArrayToList(COSArray nameArray) {
        COSArrayList retval = null;
        if (nameArray != null) {
            ArrayList<String> names = new ArrayList<String>();
            for (int i = 0; i < nameArray.size(); ++i) {
                names.add(((COSName)nameArray.getObject(i)).getName());
            }
            retval = new COSArrayList(names, nameArray);
        }
        return retval;
    }

    public static List<String> convertCOSStringCOSArrayToList(COSArray stringArray) {
        COSArrayList retval = null;
        if (stringArray != null) {
            ArrayList<String> string = new ArrayList<String>();
            for (int i = 0; i < stringArray.size(); ++i) {
                string.add(((COSString)stringArray.getObject(i)).getString());
            }
            retval = new COSArrayList(string, stringArray);
        }
        return retval;
    }

    public static COSArray convertStringListToCOSNameCOSArray(List<String> strings) {
        COSArray retval = new COSArray();
        for (String string : strings) {
            retval.add(COSName.getPDFName(string));
        }
        return retval;
    }

    public static COSArray convertStringListToCOSStringCOSArray(List<String> strings) {
        COSArray retval = new COSArray();
        for (String string : strings) {
            retval.add(new COSString(string));
        }
        return retval;
    }

    public static COSArray converterToCOSArray(List<?> cosObjectableList) {
        COSArray array = null;
        if (cosObjectableList != null) {
            if (cosObjectableList instanceof COSArrayList) {
                array = ((COSArrayList)cosObjectableList).array;
            } else {
                array = new COSArray();
                for (Object next : cosObjectableList) {
                    if (next instanceof String) {
                        array.add(new COSString((String)next));
                        continue;
                    }
                    if (next instanceof Integer || next instanceof Long) {
                        array.add(COSInteger.get(((Number)next).longValue()));
                        continue;
                    }
                    if (next instanceof Float || next instanceof Double) {
                        array.add(new COSFloat(((Number)next).floatValue()));
                        continue;
                    }
                    if (next instanceof COSObjectable) {
                        COSObjectable object = (COSObjectable)next;
                        array.add(object.getCOSObject());
                        continue;
                    }
                    if (next == null) {
                        array.add(COSNull.NULL);
                        continue;
                    }
                    throw new IllegalArgumentException("Error: Don't know how to convert type to COSBase '" + next.getClass().getName() + "'");
                }
            }
        }
        return array;
    }

    private List<COSBase> toCOSObjectList(Collection<?> list) {
        ArrayList<COSBase> cosObjects = new ArrayList<COSBase>(list.size());
        for (Object next : list) {
            if (next instanceof String) {
                cosObjects.add(new COSString((String)next));
                continue;
            }
            COSObjectable cos = (COSObjectable)next;
            cosObjects.add(cos.getCOSObject());
        }
        return cosObjects;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Iterator<?> iterator = c.iterator();
        while (iterator.hasNext()) {
            COSBase itemCOSBase = ((COSObjectable)iterator.next()).getCOSObject();
            for (int i = this.array.size() - 1; i >= 0; --i) {
                if (!itemCOSBase.equals(this.array.getObject(i))) continue;
                this.array.remove(i);
            }
        }
        return this.actual.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Iterator<?> iterator = c.iterator();
        while (iterator.hasNext()) {
            COSBase itemCOSBase = ((COSObjectable)iterator.next()).getCOSObject();
            for (int i = this.array.size() - 1; i >= 0; --i) {
                if (itemCOSBase.equals(this.array.getObject(i))) continue;
                this.array.remove(i);
            }
        }
        return this.actual.retainAll(c);
    }

    @Override
    public void clear() {
        if (this.parentDict != null) {
            this.parentDict.setItem(this.dictKey, null);
        }
        this.actual.clear();
        this.array.clear();
    }

    @Override
    public boolean equals(Object o) {
        return this.actual.equals(o);
    }

    @Override
    public int hashCode() {
        return this.actual.hashCode();
    }

    @Override
    public E get(int index) {
        return this.actual.get(index);
    }

    @Override
    public E set(int index, E element) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("Replacing an element in a filtered List is not permitted");
        }
        if (element instanceof String) {
            COSString item = new COSString((String)element);
            if (this.parentDict != null && index == 0) {
                this.parentDict.setItem(this.dictKey, (COSBase)item);
            }
            this.array.set(index, item);
        } else {
            if (this.parentDict != null && index == 0) {
                this.parentDict.setItem(this.dictKey, ((COSObjectable)element).getCOSObject());
            }
            this.array.set(index, ((COSObjectable)element).getCOSObject());
        }
        return this.actual.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("Adding an element in a filtered List is not permitted");
        }
        if (this.parentDict != null) {
            this.parentDict.setItem(this.dictKey, (COSBase)this.array);
            this.parentDict = null;
        }
        this.actual.add(index, element);
        if (element instanceof String) {
            this.array.add(index, new COSString((String)element));
        } else {
            this.array.add(index, ((COSObjectable)element).getCOSObject());
        }
    }

    @Override
    public E remove(int index) {
        if (this.isFiltered) {
            throw new UnsupportedOperationException("removing entries from a filtered List is not permitted");
        }
        this.array.remove(index);
        return this.actual.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.actual.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.actual.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return this.actual.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return this.actual.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return this.actual.subList(fromIndex, toIndex);
    }

    public String toString() {
        return "COSArrayList{" + this.array.toString() + "}";
    }

    public COSArray getCOSArray() {
        return this.array;
    }

    @Deprecated
    public COSArray toList() {
        return this.array;
    }
}

