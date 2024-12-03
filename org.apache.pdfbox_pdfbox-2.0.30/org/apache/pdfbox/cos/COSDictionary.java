/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.cos.COSUpdateInfo;
import org.apache.pdfbox.cos.ICOSVisitor;
import org.apache.pdfbox.cos.UnmodifiableCOSDictionary;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.util.DateConverter;
import org.apache.pdfbox.util.SmallMap;

public class COSDictionary
extends COSBase
implements COSUpdateInfo {
    private static final String PATH_SEPARATOR = "/";
    private static final int MAP_THRESHOLD = 1000;
    private boolean needToBeUpdated;
    protected Map<COSName, COSBase> items = new SmallMap<COSName, COSBase>();

    public COSDictionary() {
    }

    public COSDictionary(COSDictionary dict) {
        this.addAll(dict);
    }

    public boolean containsValue(Object value) {
        boolean contains = this.items.containsValue(value);
        if (!contains && value instanceof COSObject) {
            contains = this.items.containsValue(((COSObject)value).getObject());
        }
        return contains;
    }

    public COSName getKeyForValue(Object value) {
        for (Map.Entry<COSName, COSBase> entry : this.items.entrySet()) {
            COSBase nextValue = entry.getValue();
            if (!nextValue.equals(value) && (!(nextValue instanceof COSObject) || !((COSObject)nextValue).getObject().equals(value))) continue;
            return entry.getKey();
        }
        return null;
    }

    public int size() {
        return this.items.size();
    }

    public void clear() {
        this.items.clear();
    }

    public COSBase getDictionaryObject(String key) {
        return this.getDictionaryObject(COSName.getPDFName(key));
    }

    public COSBase getDictionaryObject(COSName firstKey, COSName secondKey) {
        COSBase retval = this.getDictionaryObject(firstKey);
        if (retval == null && secondKey != null) {
            retval = this.getDictionaryObject(secondKey);
        }
        return retval;
    }

    public COSBase getDictionaryObject(String[] keyList) {
        COSBase retval = null;
        for (int i = 0; i < keyList.length && retval == null; ++i) {
            retval = this.getDictionaryObject(COSName.getPDFName(keyList[i]));
        }
        return retval;
    }

    public COSBase getDictionaryObject(COSName key) {
        COSBase retval = this.items.get(key);
        if (retval instanceof COSObject) {
            retval = ((COSObject)retval).getObject();
        }
        if (retval instanceof COSNull) {
            retval = null;
        }
        return retval;
    }

    public void setItem(COSName key, COSBase value) {
        if (value == null) {
            this.removeItem(key);
        } else {
            if (this.items instanceof SmallMap && this.items.size() >= 1000) {
                this.items = new LinkedHashMap<COSName, COSBase>(this.items);
            }
            this.items.put(key, value);
        }
    }

    public void setItem(COSName key, COSObjectable value) {
        COSBase base = null;
        if (value != null) {
            base = value.getCOSObject();
        }
        this.setItem(key, base);
    }

    public void setItem(String key, COSObjectable value) {
        this.setItem(COSName.getPDFName(key), value);
    }

    public void setBoolean(String key, boolean value) {
        this.setItem(COSName.getPDFName(key), (COSBase)COSBoolean.getBoolean(value));
    }

    public void setBoolean(COSName key, boolean value) {
        this.setItem(key, (COSBase)COSBoolean.getBoolean(value));
    }

    public void setItem(String key, COSBase value) {
        this.setItem(COSName.getPDFName(key), value);
    }

    public void setName(String key, String value) {
        this.setName(COSName.getPDFName(key), value);
    }

    public void setName(COSName key, String value) {
        COSName name = null;
        if (value != null) {
            name = COSName.getPDFName(value);
        }
        this.setItem(key, (COSBase)name);
    }

    public void setDate(String key, Calendar date) {
        this.setDate(COSName.getPDFName(key), date);
    }

    public void setDate(COSName key, Calendar date) {
        this.setString(key, DateConverter.toString(date));
    }

    public void setEmbeddedDate(String embedded, String key, Calendar date) {
        this.setEmbeddedDate(embedded, COSName.getPDFName(key), date);
    }

    public void setEmbeddedDate(String embedded, COSName key, Calendar date) {
        COSDictionary dic = (COSDictionary)this.getDictionaryObject(embedded);
        if (dic == null && date != null) {
            dic = new COSDictionary();
            this.setItem(embedded, (COSBase)dic);
        }
        if (dic != null) {
            dic.setDate(key, date);
        }
    }

    public void setString(String key, String value) {
        this.setString(COSName.getPDFName(key), value);
    }

    public void setString(COSName key, String value) {
        COSString name = null;
        if (value != null) {
            name = new COSString(value);
        }
        this.setItem(key, name);
    }

    public void setEmbeddedString(String embedded, String key, String value) {
        this.setEmbeddedString(embedded, COSName.getPDFName(key), value);
    }

    public void setEmbeddedString(String embedded, COSName key, String value) {
        COSDictionary dic = (COSDictionary)this.getDictionaryObject(embedded);
        if (dic == null && value != null) {
            dic = new COSDictionary();
            this.setItem(embedded, (COSBase)dic);
        }
        if (dic != null) {
            dic.setString(key, value);
        }
    }

    public void setInt(String key, int value) {
        this.setInt(COSName.getPDFName(key), value);
    }

    public void setInt(COSName key, int value) {
        this.setItem(key, (COSBase)COSInteger.get(value));
    }

    public void setLong(String key, long value) {
        this.setLong(COSName.getPDFName(key), value);
    }

    public void setLong(COSName key, long value) {
        COSInteger intVal = COSInteger.get(value);
        this.setItem(key, (COSBase)intVal);
    }

    public void setEmbeddedInt(String embeddedDictionary, String key, int value) {
        this.setEmbeddedInt(embeddedDictionary, COSName.getPDFName(key), value);
    }

    public void setEmbeddedInt(String embeddedDictionary, COSName key, int value) {
        COSDictionary embedded = (COSDictionary)this.getDictionaryObject(embeddedDictionary);
        if (embedded == null) {
            embedded = new COSDictionary();
            this.setItem(embeddedDictionary, (COSBase)embedded);
        }
        embedded.setInt(key, value);
    }

    public void setFloat(String key, float value) {
        this.setFloat(COSName.getPDFName(key), value);
    }

    public void setFloat(COSName key, float value) {
        COSFloat fltVal = new COSFloat(value);
        this.setItem(key, (COSBase)fltVal);
    }

    public void setFlag(COSName field, int bitFlag, boolean value) {
        int currentFlags = this.getInt(field, 0);
        currentFlags = value ? (currentFlags |= bitFlag) : (currentFlags &= ~bitFlag);
        this.setInt(field, currentFlags);
    }

    public COSName getCOSName(COSName key) {
        COSBase name = this.getDictionaryObject(key);
        if (name instanceof COSName) {
            return (COSName)name;
        }
        return null;
    }

    public COSObject getCOSObject(COSName key) {
        COSBase object = this.getItem(key);
        if (object instanceof COSObject) {
            return (COSObject)object;
        }
        return null;
    }

    public COSDictionary getCOSDictionary(COSName key) {
        COSBase dictionary = this.getDictionaryObject(key);
        if (dictionary instanceof COSDictionary) {
            return (COSDictionary)dictionary;
        }
        return null;
    }

    public COSStream getCOSStream(COSName key) {
        COSBase base = this.getDictionaryObject(key);
        if (base instanceof COSStream) {
            return (COSStream)base;
        }
        return null;
    }

    public COSArray getCOSArray(COSName key) {
        COSBase array = this.getDictionaryObject(key);
        if (array instanceof COSArray) {
            return (COSArray)array;
        }
        return null;
    }

    public COSName getCOSName(COSName key, COSName defaultValue) {
        COSBase name = this.getDictionaryObject(key);
        if (name instanceof COSName) {
            return (COSName)name;
        }
        return defaultValue;
    }

    public String getNameAsString(String key) {
        return this.getNameAsString(COSName.getPDFName(key));
    }

    public String getNameAsString(COSName key) {
        String retval = null;
        COSBase name = this.getDictionaryObject(key);
        if (name instanceof COSName) {
            retval = ((COSName)name).getName();
        } else if (name instanceof COSString) {
            retval = ((COSString)name).getString();
        }
        return retval;
    }

    public String getNameAsString(String key, String defaultValue) {
        return this.getNameAsString(COSName.getPDFName(key), defaultValue);
    }

    public String getNameAsString(COSName key, String defaultValue) {
        String retval = this.getNameAsString(key);
        if (retval == null) {
            retval = defaultValue;
        }
        return retval;
    }

    public String getString(String key) {
        return this.getString(COSName.getPDFName(key));
    }

    public String getString(COSName key) {
        String retval = null;
        COSBase value = this.getDictionaryObject(key);
        if (value instanceof COSString) {
            retval = ((COSString)value).getString();
        }
        return retval;
    }

    public String getString(String key, String defaultValue) {
        return this.getString(COSName.getPDFName(key), defaultValue);
    }

    public String getString(COSName key, String defaultValue) {
        String retval = this.getString(key);
        if (retval == null) {
            retval = defaultValue;
        }
        return retval;
    }

    public String getEmbeddedString(String embedded, String key) {
        return this.getEmbeddedString(embedded, COSName.getPDFName(key), null);
    }

    public String getEmbeddedString(String embedded, COSName key) {
        return this.getEmbeddedString(embedded, key, null);
    }

    public String getEmbeddedString(String embedded, String key, String defaultValue) {
        return this.getEmbeddedString(embedded, COSName.getPDFName(key), defaultValue);
    }

    public String getEmbeddedString(String embedded, COSName key, String defaultValue) {
        String retval = defaultValue;
        COSBase base = this.getDictionaryObject(embedded);
        if (base instanceof COSDictionary) {
            retval = ((COSDictionary)base).getString(key, defaultValue);
        }
        return retval;
    }

    public Calendar getDate(String key) {
        return this.getDate(COSName.getPDFName(key));
    }

    public Calendar getDate(COSName key) {
        COSBase base = this.getDictionaryObject(key);
        if (base instanceof COSString) {
            return DateConverter.toCalendar((COSString)base);
        }
        return null;
    }

    public Calendar getDate(String key, Calendar defaultValue) {
        return this.getDate(COSName.getPDFName(key), defaultValue);
    }

    public Calendar getDate(COSName key, Calendar defaultValue) {
        Calendar retval = this.getDate(key);
        if (retval == null) {
            retval = defaultValue;
        }
        return retval;
    }

    public Calendar getEmbeddedDate(String embedded, String key) throws IOException {
        return this.getEmbeddedDate(embedded, COSName.getPDFName(key), null);
    }

    public Calendar getEmbeddedDate(String embedded, COSName key) throws IOException {
        return this.getEmbeddedDate(embedded, key, null);
    }

    public Calendar getEmbeddedDate(String embedded, String key, Calendar defaultValue) throws IOException {
        return this.getEmbeddedDate(embedded, COSName.getPDFName(key), defaultValue);
    }

    public Calendar getEmbeddedDate(String embedded, COSName key, Calendar defaultValue) throws IOException {
        Calendar retval = defaultValue;
        COSDictionary eDic = (COSDictionary)this.getDictionaryObject(embedded);
        if (eDic != null) {
            retval = eDic.getDate(key, defaultValue);
        }
        return retval;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.getBoolean(COSName.getPDFName(key), defaultValue);
    }

    public boolean getBoolean(COSName key, boolean defaultValue) {
        return this.getBoolean(key, null, defaultValue);
    }

    public boolean getBoolean(COSName firstKey, COSName secondKey, boolean defaultValue) {
        boolean retval = defaultValue;
        COSBase bool = this.getDictionaryObject(firstKey, secondKey);
        if (bool instanceof COSBoolean) {
            retval = bool == COSBoolean.TRUE;
        }
        return retval;
    }

    public int getEmbeddedInt(String embeddedDictionary, String key) {
        return this.getEmbeddedInt(embeddedDictionary, COSName.getPDFName(key));
    }

    public int getEmbeddedInt(String embeddedDictionary, COSName key) {
        return this.getEmbeddedInt(embeddedDictionary, key, -1);
    }

    public int getEmbeddedInt(String embeddedDictionary, String key, int defaultValue) {
        return this.getEmbeddedInt(embeddedDictionary, COSName.getPDFName(key), defaultValue);
    }

    public int getEmbeddedInt(String embeddedDictionary, COSName key, int defaultValue) {
        int retval = defaultValue;
        COSDictionary embedded = (COSDictionary)this.getDictionaryObject(embeddedDictionary);
        if (embedded != null) {
            retval = embedded.getInt(key, defaultValue);
        }
        return retval;
    }

    public int getInt(String key) {
        return this.getInt(COSName.getPDFName(key), -1);
    }

    public int getInt(COSName key) {
        return this.getInt(key, -1);
    }

    public int getInt(String[] keyList, int defaultValue) {
        int retval = defaultValue;
        COSBase obj = this.getDictionaryObject(keyList);
        if (obj instanceof COSNumber) {
            retval = ((COSNumber)obj).intValue();
        }
        return retval;
    }

    public int getInt(String key, int defaultValue) {
        return this.getInt(COSName.getPDFName(key), defaultValue);
    }

    public int getInt(COSName key, int defaultValue) {
        return this.getInt(key, null, defaultValue);
    }

    public int getInt(COSName firstKey, COSName secondKey) {
        return this.getInt(firstKey, secondKey, -1);
    }

    public int getInt(COSName firstKey, COSName secondKey, int defaultValue) {
        int retval = defaultValue;
        COSBase obj = this.getDictionaryObject(firstKey, secondKey);
        if (obj instanceof COSNumber) {
            retval = ((COSNumber)obj).intValue();
        }
        return retval;
    }

    public long getLong(String key) {
        return this.getLong(COSName.getPDFName(key), -1L);
    }

    public long getLong(COSName key) {
        return this.getLong(key, -1L);
    }

    public long getLong(String[] keyList, long defaultValue) {
        long retval = defaultValue;
        COSBase obj = this.getDictionaryObject(keyList);
        if (obj instanceof COSNumber) {
            retval = ((COSNumber)obj).longValue();
        }
        return retval;
    }

    public long getLong(String key, long defaultValue) {
        return this.getLong(COSName.getPDFName(key), defaultValue);
    }

    public long getLong(COSName key, long defaultValue) {
        long retval = defaultValue;
        COSBase obj = this.getDictionaryObject(key);
        if (obj instanceof COSNumber) {
            retval = ((COSNumber)obj).longValue();
        }
        return retval;
    }

    public float getFloat(String key) {
        return this.getFloat(COSName.getPDFName(key), -1.0f);
    }

    public float getFloat(COSName key) {
        return this.getFloat(key, -1.0f);
    }

    public float getFloat(String key, float defaultValue) {
        return this.getFloat(COSName.getPDFName(key), defaultValue);
    }

    public float getFloat(COSName key, float defaultValue) {
        float retval = defaultValue;
        COSBase obj = this.getDictionaryObject(key);
        if (obj instanceof COSNumber) {
            retval = ((COSNumber)obj).floatValue();
        }
        return retval;
    }

    public boolean getFlag(COSName field, int bitFlag) {
        int ff = this.getInt(field, 0);
        return (ff & bitFlag) == bitFlag;
    }

    public void removeItem(COSName key) {
        this.items.remove(key);
    }

    public COSBase getItem(COSName key) {
        return this.items.get(key);
    }

    public COSBase getItem(String key) {
        return this.getItem(COSName.getPDFName(key));
    }

    public COSBase getItem(COSName firstKey, COSName secondKey) {
        COSBase retval = this.getItem(firstKey);
        if (retval == null && secondKey != null) {
            retval = this.getItem(secondKey);
        }
        return retval;
    }

    public Set<COSName> keySet() {
        return this.items.keySet();
    }

    public Set<Map.Entry<COSName, COSBase>> entrySet() {
        return this.items.entrySet();
    }

    public Collection<COSBase> getValues() {
        return this.items.values();
    }

    @Override
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromDictionary(this);
    }

    @Override
    public boolean isNeedToBeUpdated() {
        return this.needToBeUpdated;
    }

    @Override
    public void setNeedToBeUpdated(boolean flag) {
        this.needToBeUpdated = flag;
    }

    public void addAll(COSDictionary dict) {
        if (this.items instanceof SmallMap && this.items.size() + dict.items.size() >= 1000) {
            this.items = new LinkedHashMap<COSName, COSBase>(this.items);
        }
        this.items.putAll(dict.items);
    }

    public boolean containsKey(COSName name) {
        return this.items.containsKey(name);
    }

    public boolean containsKey(String name) {
        return this.containsKey(COSName.getPDFName(name));
    }

    @Deprecated
    public void mergeInto(COSDictionary dic) {
        for (Map.Entry<COSName, COSBase> entry : dic.entrySet()) {
            if (this.getItem(entry.getKey()) != null) continue;
            this.setItem(entry.getKey(), entry.getValue());
        }
    }

    public COSBase getObjectFromPath(String objPath) {
        String[] path = objPath.split(PATH_SEPARATOR);
        COSBase retval = this;
        for (String pathString : path) {
            if (retval instanceof COSArray) {
                int idx = Integer.parseInt(pathString.replace("\\[", "").replace("\\]", ""));
                retval = ((COSArray)retval).getObject(idx);
                continue;
            }
            if (!(retval instanceof COSDictionary)) continue;
            retval = retval.getDictionaryObject(pathString);
        }
        return retval;
    }

    public COSDictionary asUnmodifiableDictionary() {
        return new UnmodifiableCOSDictionary(this);
    }

    public String toString() {
        try {
            return COSDictionary.getDictionaryString(this, new ArrayList<COSBase>());
        }
        catch (IOException e) {
            return "COSDictionary{" + e.getMessage() + "}";
        }
    }

    private static String getDictionaryString(COSBase base, List<COSBase> objs) throws IOException {
        if (base == null) {
            return "null";
        }
        if (objs.contains(base)) {
            return String.valueOf(base.hashCode());
        }
        objs.add(base);
        if (base instanceof COSDictionary) {
            StringBuilder sb = new StringBuilder("COSDictionary{");
            for (Map.Entry<COSName, COSBase> x : ((COSDictionary)base).entrySet()) {
                sb.append(x.getKey());
                sb.append(":");
                sb.append(COSDictionary.getDictionaryString(x.getValue(), objs));
                sb.append(";");
            }
            sb.append("}");
            if (base instanceof COSStream) {
                InputStream stream = ((COSStream)base).createRawInputStream();
                byte[] b = IOUtils.toByteArray(stream);
                sb.append("COSStream{").append(Arrays.hashCode(b)).append("}");
                stream.close();
            }
            return sb.toString();
        }
        if (base instanceof COSArray) {
            StringBuilder sb = new StringBuilder("COSArray{");
            for (COSBase x : (COSArray)base) {
                sb.append(COSDictionary.getDictionaryString(x, objs));
                sb.append(";");
            }
            sb.append("}");
            return sb.toString();
        }
        if (base instanceof COSObject) {
            COSObject obj = (COSObject)base;
            return "COSObject{" + COSDictionary.getDictionaryString(obj.getObject(), objs) + "}";
        }
        return base.toString();
    }
}

