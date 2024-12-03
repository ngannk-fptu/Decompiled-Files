/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.optionalcontent;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;

public class PDOptionalContentProperties
implements COSObjectable {
    private final COSDictionary dict;

    public PDOptionalContentProperties() {
        this.dict = new COSDictionary();
        this.dict.setItem(COSName.OCGS, (COSBase)new COSArray());
        COSDictionary d = new COSDictionary();
        d.setString(COSName.NAME, "Top");
        this.dict.setItem(COSName.D, (COSBase)d);
    }

    public PDOptionalContentProperties(COSDictionary props) {
        this.dict = props;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dict;
    }

    private COSArray getOCGs() {
        COSArray ocgs = this.dict.getCOSArray(COSName.OCGS);
        if (ocgs == null) {
            ocgs = new COSArray();
            this.dict.setItem(COSName.OCGS, (COSBase)ocgs);
        }
        return ocgs;
    }

    private COSDictionary getD() {
        COSBase base = this.dict.getDictionaryObject(COSName.D);
        if (base instanceof COSDictionary) {
            return (COSDictionary)base;
        }
        COSDictionary d = new COSDictionary();
        d.setString(COSName.NAME, "Top");
        this.dict.setItem(COSName.D, (COSBase)d);
        return d;
    }

    public PDOptionalContentGroup getGroup(String name) {
        COSArray ocgs = this.getOCGs();
        for (COSBase o : ocgs) {
            COSDictionary ocg = this.toDictionary(o);
            String groupName = ocg.getString(COSName.NAME);
            if (!groupName.equals(name)) continue;
            return new PDOptionalContentGroup(ocg);
        }
        return null;
    }

    public void addGroup(PDOptionalContentGroup ocg) {
        COSArray ocgs = this.getOCGs();
        ocgs.add(ocg.getCOSObject());
        COSArray order = (COSArray)this.getD().getDictionaryObject(COSName.ORDER);
        if (order == null) {
            order = new COSArray();
            this.getD().setItem(COSName.ORDER, (COSBase)order);
        }
        order.add(ocg);
    }

    public Collection<PDOptionalContentGroup> getOptionalContentGroups() {
        ArrayList<PDOptionalContentGroup> coll = new ArrayList<PDOptionalContentGroup>();
        COSArray ocgs = this.getOCGs();
        for (COSBase base : ocgs) {
            coll.add(new PDOptionalContentGroup(this.toDictionary(base)));
        }
        return coll;
    }

    public BaseState getBaseState() {
        COSDictionary d = this.getD();
        COSName name = (COSName)d.getItem(COSName.BASE_STATE);
        return BaseState.valueOf(name);
    }

    public void setBaseState(BaseState state) {
        COSDictionary d = this.getD();
        d.setItem(COSName.BASE_STATE, (COSBase)state.getName());
    }

    public String[] getGroupNames() {
        COSArray ocgs = this.dict.getCOSArray(COSName.OCGS);
        if (ocgs == null) {
            return new String[0];
        }
        int size = ocgs.size();
        String[] groups = new String[size];
        for (int i = 0; i < size; ++i) {
            COSBase obj = ocgs.get(i);
            COSDictionary ocg = this.toDictionary(obj);
            groups[i] = ocg.getString(COSName.NAME);
        }
        return groups;
    }

    public boolean hasGroup(String groupName) {
        String[] layers;
        for (String layer : layers = this.getGroupNames()) {
            if (!layer.equals(groupName)) continue;
            return true;
        }
        return false;
    }

    public boolean isGroupEnabled(String groupName) {
        boolean result = false;
        COSArray ocgs = this.getOCGs();
        for (COSBase o : ocgs) {
            COSDictionary ocg = this.toDictionary(o);
            String name = ocg.getString(COSName.NAME);
            if (!groupName.equals(name) || !this.isGroupEnabled(new PDOptionalContentGroup(ocg))) continue;
            result = true;
        }
        return result;
    }

    public boolean isGroupEnabled(PDOptionalContentGroup group) {
        COSDictionary dictionary;
        boolean enabled;
        BaseState baseState = this.getBaseState();
        boolean bl = enabled = !baseState.equals((Object)BaseState.OFF);
        if (group == null) {
            return enabled;
        }
        COSDictionary d = this.getD();
        COSBase base = d.getDictionaryObject(COSName.ON);
        if (base instanceof COSArray) {
            for (COSBase o : (COSArray)base) {
                dictionary = this.toDictionary(o);
                if (dictionary != group.getCOSObject()) continue;
                return true;
            }
        }
        if ((base = d.getDictionaryObject(COSName.OFF)) instanceof COSArray) {
            for (COSBase o : (COSArray)base) {
                dictionary = this.toDictionary(o);
                if (dictionary != group.getCOSObject()) continue;
                return false;
            }
        }
        return enabled;
    }

    private COSDictionary toDictionary(COSBase o) {
        if (o instanceof COSObject) {
            return (COSDictionary)((COSObject)o).getObject();
        }
        return (COSDictionary)o;
    }

    public boolean setGroupEnabled(String groupName, boolean enable) {
        boolean result = false;
        COSArray ocgs = this.getOCGs();
        for (COSBase o : ocgs) {
            COSDictionary ocg = this.toDictionary(o);
            String name = ocg.getString(COSName.NAME);
            if (!groupName.equals(name) || !this.setGroupEnabled(new PDOptionalContentGroup(ocg), enable)) continue;
            result = true;
        }
        return result;
    }

    public boolean setGroupEnabled(PDOptionalContentGroup group, boolean enable) {
        COSArray off;
        COSArray on;
        COSDictionary d = this.getD();
        COSBase base = d.getDictionaryObject(COSName.ON);
        if (!(base instanceof COSArray)) {
            on = new COSArray();
            d.setItem(COSName.ON, (COSBase)on);
        } else {
            on = (COSArray)base;
        }
        base = d.getDictionaryObject(COSName.OFF);
        if (!(base instanceof COSArray)) {
            off = new COSArray();
            d.setItem(COSName.OFF, (COSBase)off);
        } else {
            off = (COSArray)base;
        }
        boolean found = false;
        if (enable) {
            for (COSBase o : off) {
                COSDictionary groupDictionary = this.toDictionary(o);
                if (groupDictionary != group.getCOSObject()) continue;
                off.remove(o);
                on.add(o);
                found = true;
                break;
            }
        } else {
            for (COSBase o : on) {
                COSDictionary groupDictionary = this.toDictionary(o);
                if (groupDictionary != group.getCOSObject()) continue;
                on.remove(o);
                off.add(o);
                found = true;
                break;
            }
        }
        if (!found) {
            if (enable) {
                on.add(group.getCOSObject());
            } else {
                off.add(group.getCOSObject());
            }
        }
        return found;
    }

    public static enum BaseState {
        ON(COSName.ON),
        OFF(COSName.OFF),
        UNCHANGED(COSName.UNCHANGED);

        private final COSName name;

        private BaseState(COSName value) {
            this.name = value;
        }

        public COSName getName() {
            return this.name;
        }

        public static BaseState valueOf(COSName state) {
            if (state == null) {
                return ON;
            }
            return BaseState.valueOf(state.getName().toUpperCase());
        }
    }
}

