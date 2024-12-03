/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.multipdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDFCloneUtility {
    private static final Log LOG = LogFactory.getLog(PDFCloneUtility.class);
    private final PDDocument destination;
    private final Map<Object, COSBase> clonedVersion = new HashMap<Object, COSBase>();
    private final Set<COSBase> clonedValues = new HashSet<COSBase>();

    public PDFCloneUtility(PDDocument dest) {
        this.destination = dest;
    }

    public PDDocument getDestination() {
        return this.destination;
    }

    public COSBase cloneForNewDocument(Object base) throws IOException {
        if (base == null) {
            return null;
        }
        COSBase retval = this.clonedVersion.get(base);
        if (retval != null) {
            return retval;
        }
        if (base instanceof COSBase && this.clonedValues.contains(base)) {
            return (COSBase)base;
        }
        if (base instanceof List) {
            COSArray array = new COSArray();
            List list = (List)base;
            for (Object obj : list) {
                array.add(this.cloneForNewDocument(obj));
            }
            retval = array;
        } else if (base instanceof COSObjectable && !(base instanceof COSBase)) {
            retval = this.cloneForNewDocument(((COSObjectable)base).getCOSObject());
        } else if (base instanceof COSObject) {
            COSObject object = (COSObject)base;
            retval = this.cloneForNewDocument(object.getObject());
        } else if (base instanceof COSArray) {
            COSArray newArray = new COSArray();
            COSArray array = (COSArray)base;
            for (int i = 0; i < array.size(); ++i) {
                COSBase value = array.get(i);
                if (this.hasSelfReference(base, value)) {
                    newArray.add(newArray);
                    continue;
                }
                newArray.add(this.cloneForNewDocument(value));
            }
            retval = newArray;
        } else if (base instanceof COSStream) {
            COSStream originalStream = (COSStream)base;
            COSStream stream = this.destination.getDocument().createCOSStream();
            OutputStream output = stream.createRawOutputStream();
            InputStream input = originalStream.createRawInputStream();
            IOUtils.copy(input, output);
            input.close();
            output.close();
            this.clonedVersion.put(base, stream);
            for (Map.Entry<COSName, COSBase> entry : originalStream.entrySet()) {
                COSBase value = entry.getValue();
                if (this.hasSelfReference(base, value)) {
                    stream.setItem(entry.getKey(), (COSBase)stream);
                    continue;
                }
                stream.setItem(entry.getKey(), this.cloneForNewDocument(value));
            }
            retval = stream;
        } else if (base instanceof COSDictionary) {
            COSDictionary dic = (COSDictionary)base;
            retval = new COSDictionary();
            this.clonedVersion.put(base, retval);
            for (Map.Entry<COSName, COSBase> entry : dic.entrySet()) {
                COSBase value = entry.getValue();
                if (this.hasSelfReference(base, value)) {
                    ((COSDictionary)retval).setItem(entry.getKey(), retval);
                    continue;
                }
                ((COSDictionary)retval).setItem(entry.getKey(), this.cloneForNewDocument(value));
            }
        } else {
            retval = (COSBase)base;
        }
        this.clonedVersion.put(base, retval);
        this.clonedValues.add(retval);
        return retval;
    }

    public void cloneMerge(COSObjectable base, COSObjectable target) throws IOException {
        if (base == null || base == target) {
            return;
        }
        COSBase retval = this.clonedVersion.get(base);
        if (retval != null) {
            return;
        }
        if (!(base instanceof COSBase)) {
            this.cloneMerge(base.getCOSObject(), target.getCOSObject());
        } else if (base instanceof COSObject) {
            if (target instanceof COSObject) {
                this.cloneMerge(((COSObject)base).getObject(), ((COSObject)target).getObject());
            } else if (target instanceof COSDictionary || target instanceof COSArray) {
                this.cloneMerge(((COSObject)base).getObject(), target);
            }
        } else if (base instanceof COSArray) {
            if (target instanceof COSObject) {
                this.cloneMerge(base, ((COSObject)target).getObject());
            } else {
                COSArray array = (COSArray)base;
                for (int i = 0; i < array.size(); ++i) {
                    ((COSArray)target).add(this.cloneForNewDocument(array.get(i)));
                }
            }
        } else if (base instanceof COSStream) {
            COSStream originalStream = (COSStream)base;
            COSStream stream = this.destination.getDocument().createCOSStream();
            OutputStream output = stream.createOutputStream(originalStream.getFilters());
            IOUtils.copy(originalStream.createInputStream(), output);
            output.close();
            this.clonedVersion.put(base, stream);
            for (Map.Entry<COSName, COSBase> entry : originalStream.entrySet()) {
                stream.setItem(entry.getKey(), this.cloneForNewDocument(entry.getValue()));
            }
            retval = stream;
        } else if (base instanceof COSDictionary) {
            if (target instanceof COSObject) {
                this.cloneMerge(base, ((COSObject)target).getObject());
            } else {
                COSDictionary dic = (COSDictionary)base;
                this.clonedVersion.put(base, retval);
                for (Map.Entry<COSName, COSBase> entry : dic.entrySet()) {
                    COSName key = entry.getKey();
                    COSBase value = entry.getValue();
                    if (((COSDictionary)target).getItem(key) != null) {
                        this.cloneMerge(value, ((COSDictionary)target).getItem(key));
                        continue;
                    }
                    ((COSDictionary)target).setItem(key, this.cloneForNewDocument(value));
                }
            }
        } else {
            retval = (COSBase)base;
        }
        this.clonedVersion.put(base, retval);
        this.clonedValues.add(retval);
    }

    private boolean hasSelfReference(Object parent, COSBase value) {
        COSBase actual;
        if (value instanceof COSObject && (actual = ((COSObject)value).getObject()) == parent) {
            COSObject cosObj = (COSObject)value;
            LOG.warn((Object)(parent.getClass().getSimpleName() + " object has a reference to itself: " + cosObj.getObjectNumber() + " " + cosObj.getGenerationNumber() + " R"));
            return true;
        }
        return false;
    }
}

