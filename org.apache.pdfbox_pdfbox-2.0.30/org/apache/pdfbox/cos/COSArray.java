/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.cos.COSUpdateInfo;
import org.apache.pdfbox.cos.ICOSVisitor;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class COSArray
extends COSBase
implements Iterable<COSBase>,
COSUpdateInfo {
    private final List<COSBase> objects = new ArrayList<COSBase>();
    private boolean needToBeUpdated;

    public void add(COSBase object) {
        this.objects.add(object);
    }

    public void add(COSObjectable object) {
        this.objects.add(object.getCOSObject());
    }

    public void add(int i, COSBase object) {
        this.objects.add(i, object);
    }

    public void clear() {
        this.objects.clear();
    }

    public void removeAll(Collection<COSBase> objectsList) {
        this.objects.removeAll(objectsList);
    }

    public void retainAll(Collection<COSBase> objectsList) {
        this.objects.retainAll(objectsList);
    }

    public void addAll(Collection<COSBase> objectsList) {
        this.objects.addAll(objectsList);
    }

    public void addAll(COSArray objectList) {
        if (objectList != null) {
            this.objects.addAll(objectList.objects);
        }
    }

    public void addAll(int i, Collection<COSBase> objectList) {
        this.objects.addAll(i, objectList);
    }

    public void set(int index, COSBase object) {
        this.objects.set(index, object);
    }

    public void set(int index, int intVal) {
        this.objects.set(index, COSInteger.get(intVal));
    }

    public void set(int index, COSObjectable object) {
        COSBase base = null;
        if (object != null) {
            base = object.getCOSObject();
        }
        this.objects.set(index, base);
    }

    public COSBase getObject(int index) {
        COSBase obj = this.objects.get(index);
        if (obj instanceof COSObject) {
            obj = ((COSObject)obj).getObject();
        }
        if (obj instanceof COSNull) {
            obj = null;
        }
        return obj;
    }

    public COSBase get(int index) {
        return this.objects.get(index);
    }

    public int getInt(int index) {
        return this.getInt(index, -1);
    }

    public int getInt(int index, int defaultValue) {
        COSBase obj;
        int retval = defaultValue;
        if (index < this.size() && (obj = this.objects.get(index)) instanceof COSNumber) {
            retval = ((COSNumber)obj).intValue();
        }
        return retval;
    }

    public void setInt(int index, int value) {
        this.set(index, COSInteger.get(value));
    }

    public void setName(int index, String name) {
        this.set(index, COSName.getPDFName(name));
    }

    public String getName(int index) {
        return this.getName(index, null);
    }

    public String getName(int index, String defaultValue) {
        COSBase obj;
        String retval = defaultValue;
        if (index < this.size() && (obj = this.objects.get(index)) instanceof COSName) {
            retval = ((COSName)obj).getName();
        }
        return retval;
    }

    public void setString(int index, String string) {
        if (string != null) {
            this.set(index, new COSString(string));
        } else {
            this.set(index, null);
        }
    }

    public String getString(int index) {
        return this.getString(index, null);
    }

    public String getString(int index, String defaultValue) {
        COSBase obj;
        String retval = defaultValue;
        if (index < this.size() && (obj = this.objects.get(index)) instanceof COSString) {
            retval = ((COSString)obj).getString();
        }
        return retval;
    }

    public int size() {
        return this.objects.size();
    }

    public COSBase remove(int i) {
        return this.objects.remove(i);
    }

    public boolean remove(COSBase o) {
        return this.objects.remove(o);
    }

    public boolean removeObject(COSBase o) {
        boolean removed = this.remove(o);
        if (!removed) {
            for (int i = 0; i < this.size(); ++i) {
                COSObject objEntry;
                COSBase entry = this.get(i);
                if (!(entry instanceof COSObject) || !(objEntry = (COSObject)entry).getObject().equals(o)) continue;
                return this.remove(entry);
            }
        }
        return removed;
    }

    public String toString() {
        return "COSArray{" + this.objects + "}";
    }

    @Override
    public Iterator<COSBase> iterator() {
        return this.objects.iterator();
    }

    public int indexOf(COSBase object) {
        for (int i = 0; i < this.size(); ++i) {
            COSBase item = this.get(i);
            if (!(item == null ? object == null : item.equals(object))) continue;
            return i;
        }
        return -1;
    }

    public int indexOfObject(COSBase object) {
        for (int i = 0; i < this.size(); ++i) {
            COSBase item = this.get(i);
            if (!(item == null ? item == object : item.equals(object) || item instanceof COSObject && ((COSObject)item).getObject().equals(object))) continue;
            return i;
        }
        return -1;
    }

    public void growToSize(int size) {
        this.growToSize(size, null);
    }

    public void growToSize(int size, COSBase object) {
        while (this.size() < size) {
            this.add(object);
        }
    }

    @Override
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromArray(this);
    }

    @Override
    public boolean isNeedToBeUpdated() {
        return this.needToBeUpdated;
    }

    @Override
    public void setNeedToBeUpdated(boolean flag) {
        this.needToBeUpdated = flag;
    }

    public float[] toFloatArray() {
        float[] retval = new float[this.size()];
        for (int i = 0; i < retval.length; ++i) {
            COSBase base = this.getObject(i);
            retval[i] = base instanceof COSNumber ? ((COSNumber)base).floatValue() : 0.0f;
        }
        return retval;
    }

    public void setFloatArray(float[] value) {
        this.clear();
        for (float aValue : value) {
            this.add(new COSFloat(aValue));
        }
    }

    public List<? extends COSBase> toList() {
        return new ArrayList<COSBase>(this.objects);
    }
}

