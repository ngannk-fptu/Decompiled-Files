/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDNumberTreeNode
implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(PDNumberTreeNode.class);
    private final COSDictionary node;
    private Class<? extends COSObjectable> valueType = null;

    public PDNumberTreeNode(Class<? extends COSObjectable> valueClass) {
        this.node = new COSDictionary();
        this.valueType = valueClass;
    }

    public PDNumberTreeNode(COSDictionary dict, Class<? extends COSObjectable> valueClass) {
        this.node = dict;
        this.valueType = valueClass;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.node;
    }

    public List<PDNumberTreeNode> getKids() {
        COSArrayList retval = null;
        COSArray kids = (COSArray)this.node.getDictionaryObject(COSName.KIDS);
        if (kids != null) {
            ArrayList<PDNumberTreeNode> pdObjects = new ArrayList<PDNumberTreeNode>();
            for (int i = 0; i < kids.size(); ++i) {
                pdObjects.add(this.createChildNode((COSDictionary)kids.getObject(i)));
            }
            retval = new COSArrayList(pdObjects, kids);
        }
        return retval;
    }

    public void setKids(List<? extends PDNumberTreeNode> kids) {
        if (kids != null && !kids.isEmpty()) {
            PDNumberTreeNode firstKid = kids.get(0);
            PDNumberTreeNode lastKid = kids.get(kids.size() - 1);
            Integer lowerLimit = firstKid.getLowerLimit();
            this.setLowerLimit(lowerLimit);
            Integer upperLimit = lastKid.getUpperLimit();
            this.setUpperLimit(upperLimit);
        } else if (this.node.getDictionaryObject(COSName.NUMS) == null) {
            this.node.setItem(COSName.LIMITS, null);
        }
        this.node.setItem(COSName.KIDS, (COSBase)COSArrayList.converterToCOSArray(kids));
    }

    public Object getValue(Integer index) throws IOException {
        Map<Integer, COSObjectable> numbers = this.getNumbers();
        if (numbers != null) {
            return numbers.get(index);
        }
        Object retval = null;
        List<PDNumberTreeNode> kids = this.getKids();
        if (kids != null) {
            for (int i = 0; i < kids.size() && retval == null; ++i) {
                PDNumberTreeNode childNode = kids.get(i);
                if (childNode.getLowerLimit().compareTo(index) > 0 || childNode.getUpperLimit().compareTo(index) < 0) continue;
                retval = childNode.getValue(index);
            }
        } else {
            LOG.warn((Object)"NumberTreeNode does not have \"nums\" nor \"kids\" objects.");
        }
        return retval;
    }

    public Map<Integer, COSObjectable> getNumbers() throws IOException {
        Map<Integer, COSObjectable> indices = null;
        COSBase numBase = this.node.getDictionaryObject(COSName.NUMS);
        if (numBase instanceof COSArray) {
            COSArray numbersArray = (COSArray)numBase;
            indices = new HashMap();
            if (numbersArray.size() % 2 != 0) {
                LOG.warn((Object)("Numbers array has odd size: " + numbersArray.size()));
            }
            int i = 0;
            while (i + 1 < numbersArray.size()) {
                COSBase base = numbersArray.getObject(i);
                if (!(base instanceof COSInteger)) {
                    LOG.error((Object)("page labels ignored, index " + i + " should be a number, but is " + base));
                    return null;
                }
                COSInteger key = (COSInteger)base;
                COSBase cosValue = numbersArray.getObject(i + 1);
                indices.put(key.intValue(), cosValue == null ? null : this.convertCOSToPD(cosValue));
                i += 2;
            }
            indices = Collections.unmodifiableMap(indices);
        }
        return indices;
    }

    protected COSObjectable convertCOSToPD(COSBase base) throws IOException {
        try {
            return this.valueType.getDeclaredConstructor(base.getClass()).newInstance(base);
        }
        catch (Throwable t) {
            throw new IOException("Error while trying to create value in number tree:" + t.getMessage(), t);
        }
    }

    protected PDNumberTreeNode createChildNode(COSDictionary dic) {
        return new PDNumberTreeNode(dic, this.valueType);
    }

    public void setNumbers(Map<Integer, ? extends COSObjectable> numbers) {
        if (numbers == null) {
            this.node.setItem(COSName.NUMS, (COSObjectable)null);
            this.node.setItem(COSName.LIMITS, (COSObjectable)null);
        } else {
            ArrayList<Integer> keys = new ArrayList<Integer>(numbers.keySet());
            Collections.sort(keys);
            COSArray array = new COSArray();
            for (Integer key : keys) {
                array.add(COSInteger.get(key.intValue()));
                COSObjectable obj = numbers.get(key);
                array.add(obj == null ? COSNull.NULL : obj);
            }
            Integer lower = null;
            Integer upper = null;
            if (!keys.isEmpty()) {
                lower = (Integer)keys.get(0);
                upper = (Integer)keys.get(keys.size() - 1);
            }
            this.setUpperLimit(upper);
            this.setLowerLimit(lower);
            this.node.setItem(COSName.NUMS, (COSBase)array);
        }
    }

    public Integer getUpperLimit() {
        Integer retval = null;
        COSArray arr = (COSArray)this.node.getDictionaryObject(COSName.LIMITS);
        if (arr != null && arr.get(1) != null) {
            retval = arr.getInt(1);
        }
        return retval;
    }

    private void setUpperLimit(Integer upper) {
        COSArray arr = (COSArray)this.node.getDictionaryObject(COSName.LIMITS);
        if (arr == null) {
            arr = new COSArray();
            arr.add(null);
            arr.add(null);
            this.node.setItem(COSName.LIMITS, (COSBase)arr);
        }
        if (upper != null) {
            arr.setInt(1, upper);
        } else {
            arr.set(1, null);
        }
    }

    public Integer getLowerLimit() {
        Integer retval = null;
        COSArray arr = (COSArray)this.node.getDictionaryObject(COSName.LIMITS);
        if (arr != null && arr.get(0) != null) {
            retval = arr.getInt(0);
        }
        return retval;
    }

    private void setLowerLimit(Integer lower) {
        COSArray arr = (COSArray)this.node.getDictionaryObject(COSName.LIMITS);
        if (arr == null) {
            arr = new COSArray();
            arr.add(null);
            arr.add(null);
            this.node.setItem(COSName.LIMITS, (COSBase)arr);
        }
        if (lower != null) {
            arr.setInt(0, lower);
        } else {
            arr.set(0, null);
        }
    }
}

