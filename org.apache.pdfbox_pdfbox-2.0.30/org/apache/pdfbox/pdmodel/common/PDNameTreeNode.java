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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public abstract class PDNameTreeNode<T extends COSObjectable>
implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(PDNameTreeNode.class);
    private final COSDictionary node;
    private PDNameTreeNode<T> parent;

    protected PDNameTreeNode() {
        this.node = new COSDictionary();
    }

    protected PDNameTreeNode(COSDictionary dict) {
        this.node = dict;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.node;
    }

    public PDNameTreeNode<T> getParent() {
        return this.parent;
    }

    public void setParent(PDNameTreeNode<T> parentNode) {
        this.parent = parentNode;
        this.calculateLimits();
    }

    public boolean isRootNode() {
        return this.parent == null;
    }

    public List<PDNameTreeNode<T>> getKids() {
        COSArrayList retval = null;
        COSArray kids = this.node.getCOSArray(COSName.KIDS);
        if (kids != null) {
            ArrayList<PDNameTreeNode<T>> pdObjects = new ArrayList<PDNameTreeNode<T>>(kids.size());
            for (int i = 0; i < kids.size(); ++i) {
                pdObjects.add(this.createChildNode((COSDictionary)kids.getObject(i)));
            }
            retval = new COSArrayList(pdObjects, kids);
        }
        return retval;
    }

    public void setKids(List<? extends PDNameTreeNode<T>> kids) {
        if (kids != null && !kids.isEmpty()) {
            for (PDNameTreeNode<T> kidsNode : kids) {
                kidsNode.setParent(this);
            }
            this.node.setItem(COSName.KIDS, (COSBase)COSArrayList.converterToCOSArray(kids));
            if (this.isRootNode()) {
                this.node.setItem(COSName.NAMES, null);
            }
        } else {
            this.node.setItem(COSName.KIDS, null);
            this.node.setItem(COSName.LIMITS, null);
        }
        this.calculateLimits();
    }

    private void calculateLimits() {
        if (this.isRootNode()) {
            this.node.setItem(COSName.LIMITS, null);
        } else {
            List<PDNameTreeNode<T>> kids = this.getKids();
            if (kids != null && !kids.isEmpty()) {
                PDNameTreeNode<T> firstKid = kids.get(0);
                PDNameTreeNode<T> lastKid = kids.get(kids.size() - 1);
                String lowerLimit = firstKid.getLowerLimit();
                this.setLowerLimit(lowerLimit);
                String upperLimit = lastKid.getUpperLimit();
                this.setUpperLimit(upperLimit);
            } else {
                try {
                    Map<String, T> names = this.getNames();
                    if (names != null && names.size() > 0) {
                        Set<String> strings = names.keySet();
                        String[] keys = strings.toArray(new String[strings.size()]);
                        String lowerLimit = keys[0];
                        this.setLowerLimit(lowerLimit);
                        String upperLimit = keys[keys.length - 1];
                        this.setUpperLimit(upperLimit);
                    } else {
                        this.node.setItem(COSName.LIMITS, null);
                    }
                }
                catch (IOException exception) {
                    this.node.setItem(COSName.LIMITS, null);
                    LOG.error((Object)"Error while calculating the Limits of a PageNameTreeNode:", (Throwable)exception);
                }
            }
        }
    }

    public T getValue(String name) throws IOException {
        Map<String, T> names = this.getNames();
        if (names != null) {
            return (T)((COSObjectable)names.get(name));
        }
        List<PDNameTreeNode<T>> kids = this.getKids();
        if (kids != null) {
            for (int i = 0; i < kids.size(); ++i) {
                PDNameTreeNode<T> childNode = kids.get(i);
                String upperLimit = childNode.getUpperLimit();
                String lowerLimit = childNode.getLowerLimit();
                if (upperLimit != null && lowerLimit != null && upperLimit.compareTo(lowerLimit) >= 0 && (lowerLimit.compareTo(name) > 0 || upperLimit.compareTo(name) < 0)) continue;
                return childNode.getValue(name);
            }
        } else {
            LOG.warn((Object)"NameTreeNode does not have \"names\" nor \"kids\" objects.");
        }
        return null;
    }

    public Map<String, T> getNames() throws IOException {
        COSArray namesArray = this.node.getCOSArray(COSName.NAMES);
        if (namesArray != null) {
            LinkedHashMap<String, T> names = new LinkedHashMap<String, T>();
            if (namesArray.size() % 2 != 0) {
                LOG.warn((Object)("Names array has odd size: " + namesArray.size()));
            }
            int i = 0;
            while (i + 1 < namesArray.size()) {
                COSBase base = namesArray.getObject(i);
                if (!(base instanceof COSString)) {
                    throw new IOException("Expected string, found " + base + " in name tree at index " + i);
                }
                COSString key = (COSString)base;
                COSBase cosValue = namesArray.getObject(i + 1);
                names.put(key.getString(), this.convertCOSToPD(cosValue));
                i += 2;
            }
            return Collections.unmodifiableMap(names);
        }
        return null;
    }

    protected abstract T convertCOSToPD(COSBase var1) throws IOException;

    protected abstract PDNameTreeNode<T> createChildNode(COSDictionary var1);

    public void setNames(Map<String, T> names) {
        if (names == null) {
            this.node.setItem(COSName.NAMES, (COSObjectable)null);
            this.node.setItem(COSName.LIMITS, (COSObjectable)null);
        } else {
            COSArray array = new COSArray();
            ArrayList<String> keys = new ArrayList<String>(names.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                array.add(new COSString(key));
                array.add((COSObjectable)names.get(key));
            }
            this.node.setItem(COSName.NAMES, (COSBase)array);
            this.calculateLimits();
        }
    }

    public String getUpperLimit() {
        String retval = null;
        COSArray arr = this.node.getCOSArray(COSName.LIMITS);
        if (arr != null) {
            retval = arr.getString(1);
        }
        return retval;
    }

    private void setUpperLimit(String upper) {
        COSArray arr = this.node.getCOSArray(COSName.LIMITS);
        if (arr == null) {
            arr = new COSArray();
            arr.add(null);
            arr.add(null);
            this.node.setItem(COSName.LIMITS, (COSBase)arr);
        }
        arr.setString(1, upper);
    }

    public String getLowerLimit() {
        String retval = null;
        COSArray arr = this.node.getCOSArray(COSName.LIMITS);
        if (arr != null) {
            retval = arr.getString(0);
        }
        return retval;
    }

    private void setLowerLimit(String lower) {
        COSArray arr = this.node.getCOSArray(COSName.LIMITS);
        if (arr == null) {
            arr = new COSArray();
            arr.add(null);
            arr.add(null);
            this.node.setItem(COSName.LIMITS, (COSBase)arr);
        }
        arr.setString(0, lower);
    }
}

