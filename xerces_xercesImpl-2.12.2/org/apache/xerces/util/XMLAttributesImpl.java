/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.PrimeNumberSequenceGenerator;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;

public class XMLAttributesImpl
implements XMLAttributes {
    protected static final int TABLE_SIZE = 101;
    protected static final int MAX_HASH_COLLISIONS = 40;
    protected static final int MULTIPLIERS_SIZE = 32;
    protected static final int MULTIPLIERS_MASK = 31;
    protected static final int SIZE_LIMIT = 20;
    protected boolean fNamespaces = true;
    protected int fLargeCount = 1;
    protected int fLength;
    protected Attribute[] fAttributes = new Attribute[4];
    protected Attribute[] fAttributeTableView;
    protected int[] fAttributeTableViewChainState;
    protected int fTableViewBuckets;
    protected boolean fIsTableViewConsistent;
    protected int[] fHashMultipliers;

    public XMLAttributesImpl() {
        this(101);
    }

    public XMLAttributesImpl(int n) {
        this.fTableViewBuckets = n;
        for (int i = 0; i < this.fAttributes.length; ++i) {
            this.fAttributes[i] = new Attribute();
        }
    }

    public void setNamespaces(boolean bl) {
        this.fNamespaces = bl;
    }

    @Override
    public int addAttribute(QName qName, String string, String string2) {
        Attribute[] attributeArray;
        int n;
        if (this.fLength < 20) {
            int n2 = n = qName.uri != null && qName.uri.length() != 0 ? this.getIndexFast(qName.uri, qName.localpart) : this.getIndexFast(qName.rawname);
            if (n == -1) {
                n = this.fLength;
                if (this.fLength++ == this.fAttributes.length) {
                    attributeArray = new Attribute[this.fAttributes.length + 4];
                    System.arraycopy(this.fAttributes, 0, attributeArray, 0, this.fAttributes.length);
                    for (int i = this.fAttributes.length; i < attributeArray.length; ++i) {
                        attributeArray[i] = new Attribute();
                    }
                    this.fAttributes = attributeArray;
                }
            }
        } else if (qName.uri == null || qName.uri.length() == 0 || (n = this.getIndexFast(qName.uri, qName.localpart)) == -1) {
            int n3;
            if (!this.fIsTableViewConsistent || this.fLength == 20 || this.fLength > 20 && this.fLength > this.fTableViewBuckets) {
                this.prepareAndPopulateTableView();
                this.fIsTableViewConsistent = true;
            }
            if (this.fAttributeTableViewChainState[n3 = this.getTableViewBucket(qName.rawname)] != this.fLargeCount) {
                n = this.fLength;
                if (this.fLength++ == this.fAttributes.length) {
                    Attribute[] attributeArray2 = new Attribute[this.fAttributes.length << 1];
                    System.arraycopy(this.fAttributes, 0, attributeArray2, 0, this.fAttributes.length);
                    for (int i = this.fAttributes.length; i < attributeArray2.length; ++i) {
                        attributeArray2[i] = new Attribute();
                    }
                    this.fAttributes = attributeArray2;
                }
                this.fAttributeTableViewChainState[n3] = this.fLargeCount;
                this.fAttributes[n].next = null;
                this.fAttributeTableView[n3] = this.fAttributes[n];
            } else {
                int n4 = 0;
                Attribute attribute = this.fAttributeTableView[n3];
                while (attribute != null && attribute.name.rawname != qName.rawname) {
                    attribute = attribute.next;
                    ++n4;
                }
                if (attribute == null) {
                    n = this.fLength;
                    if (this.fLength++ == this.fAttributes.length) {
                        Attribute[] attributeArray3 = new Attribute[this.fAttributes.length << 1];
                        System.arraycopy(this.fAttributes, 0, attributeArray3, 0, this.fAttributes.length);
                        for (int i = this.fAttributes.length; i < attributeArray3.length; ++i) {
                            attributeArray3[i] = new Attribute();
                        }
                        this.fAttributes = attributeArray3;
                    }
                    if (n4 >= 40) {
                        this.fAttributes[n].name.setValues(qName);
                        this.rebalanceTableView(this.fLength);
                    } else {
                        this.fAttributes[n].next = this.fAttributeTableView[n3];
                        this.fAttributeTableView[n3] = this.fAttributes[n];
                    }
                } else {
                    n = this.getIndexFast(qName.rawname);
                }
            }
        }
        attributeArray = this.fAttributes[n];
        attributeArray.name.setValues(qName);
        attributeArray.type = string;
        attributeArray.value = string2;
        attributeArray.nonNormalizedValue = string2;
        attributeArray.specified = false;
        attributeArray.augs.removeAllItems();
        return n;
    }

    @Override
    public void removeAllAttributes() {
        this.fLength = 0;
    }

    @Override
    public void removeAttributeAt(int n) {
        this.fIsTableViewConsistent = false;
        if (n < this.fLength - 1) {
            Attribute attribute = this.fAttributes[n];
            System.arraycopy(this.fAttributes, n + 1, this.fAttributes, n, this.fLength - n - 1);
            this.fAttributes[this.fLength - 1] = attribute;
        }
        --this.fLength;
    }

    @Override
    public void setName(int n, QName qName) {
        this.fAttributes[n].name.setValues(qName);
    }

    @Override
    public void getName(int n, QName qName) {
        qName.setValues(this.fAttributes[n].name);
    }

    @Override
    public void setType(int n, String string) {
        this.fAttributes[n].type = string;
    }

    @Override
    public void setValue(int n, String string) {
        Attribute attribute = this.fAttributes[n];
        attribute.value = string;
        attribute.nonNormalizedValue = string;
    }

    @Override
    public void setNonNormalizedValue(int n, String string) {
        if (string == null) {
            string = this.fAttributes[n].value;
        }
        this.fAttributes[n].nonNormalizedValue = string;
    }

    @Override
    public String getNonNormalizedValue(int n) {
        String string = this.fAttributes[n].nonNormalizedValue;
        return string;
    }

    @Override
    public void setSpecified(int n, boolean bl) {
        this.fAttributes[n].specified = bl;
    }

    @Override
    public boolean isSpecified(int n) {
        return this.fAttributes[n].specified;
    }

    @Override
    public int getLength() {
        return this.fLength;
    }

    @Override
    public String getType(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.getReportableType(this.fAttributes[n].type);
    }

    @Override
    public String getType(String string) {
        int n = this.getIndex(string);
        return n != -1 ? this.getReportableType(this.fAttributes[n].type) : null;
    }

    @Override
    public String getValue(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fAttributes[n].value;
    }

    @Override
    public String getValue(String string) {
        int n = this.getIndex(string);
        return n != -1 ? this.fAttributes[n].value : null;
    }

    public String getName(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fAttributes[n].name.rawname;
    }

    @Override
    public int getIndex(String string) {
        for (int i = 0; i < this.fLength; ++i) {
            Attribute attribute = this.fAttributes[i];
            if (attribute.name.rawname == null || !attribute.name.rawname.equals(string)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int getIndex(String string, String string2) {
        for (int i = 0; i < this.fLength; ++i) {
            Attribute attribute = this.fAttributes[i];
            if (attribute.name.localpart == null || !attribute.name.localpart.equals(string2) || string != attribute.name.uri && (string == null || attribute.name.uri == null || !attribute.name.uri.equals(string))) continue;
            return i;
        }
        return -1;
    }

    @Override
    public String getLocalName(int n) {
        if (!this.fNamespaces) {
            return "";
        }
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fAttributes[n].name.localpart;
    }

    @Override
    public String getQName(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        String string = this.fAttributes[n].name.rawname;
        return string != null ? string : "";
    }

    @Override
    public String getType(String string, String string2) {
        if (!this.fNamespaces) {
            return null;
        }
        int n = this.getIndex(string, string2);
        return n != -1 ? this.getReportableType(this.fAttributes[n].type) : null;
    }

    @Override
    public String getPrefix(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        String string = this.fAttributes[n].name.prefix;
        return string != null ? string : "";
    }

    @Override
    public String getURI(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        String string = this.fAttributes[n].name.uri;
        return string;
    }

    @Override
    public String getValue(String string, String string2) {
        int n = this.getIndex(string, string2);
        return n != -1 ? this.getValue(n) : null;
    }

    @Override
    public Augmentations getAugmentations(String string, String string2) {
        int n = this.getIndex(string, string2);
        return n != -1 ? this.fAttributes[n].augs : null;
    }

    @Override
    public Augmentations getAugmentations(String string) {
        int n = this.getIndex(string);
        return n != -1 ? this.fAttributes[n].augs : null;
    }

    @Override
    public Augmentations getAugmentations(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fAttributes[n].augs;
    }

    @Override
    public void setAugmentations(int n, Augmentations augmentations) {
        this.fAttributes[n].augs = augmentations;
    }

    public void setURI(int n, String string) {
        this.fAttributes[n].name.uri = string;
    }

    public int getIndexFast(String string) {
        for (int i = 0; i < this.fLength; ++i) {
            Attribute attribute = this.fAttributes[i];
            if (attribute.name.rawname != string) continue;
            return i;
        }
        return -1;
    }

    public void addAttributeNS(QName qName, String string, String string2) {
        Attribute[] attributeArray;
        int n = this.fLength;
        if (this.fLength++ == this.fAttributes.length) {
            attributeArray = this.fLength < 20 ? new Attribute[this.fAttributes.length + 4] : new Attribute[this.fAttributes.length << 1];
            System.arraycopy(this.fAttributes, 0, attributeArray, 0, this.fAttributes.length);
            for (int i = this.fAttributes.length; i < attributeArray.length; ++i) {
                attributeArray[i] = new Attribute();
            }
            this.fAttributes = attributeArray;
        }
        attributeArray = this.fAttributes[n];
        attributeArray.name.setValues(qName);
        attributeArray.type = string;
        attributeArray.value = string2;
        attributeArray.nonNormalizedValue = string2;
        attributeArray.specified = false;
        attributeArray.augs.removeAllItems();
    }

    public QName checkDuplicatesNS() {
        int n = this.fLength;
        if (n <= 20) {
            Attribute[] attributeArray = this.fAttributes;
            for (int i = 0; i < n - 1; ++i) {
                Attribute attribute = attributeArray[i];
                for (int j = i + 1; j < n; ++j) {
                    Attribute attribute2 = attributeArray[j];
                    if (attribute.name.localpart != attribute2.name.localpart || attribute.name.uri != attribute2.name.uri) continue;
                    return attribute2.name;
                }
            }
            return null;
        }
        return this.checkManyDuplicatesNS();
    }

    private QName checkManyDuplicatesNS() {
        this.fIsTableViewConsistent = false;
        this.prepareTableView();
        int n = this.fLength;
        Attribute[] attributeArray = this.fAttributes;
        Attribute[] attributeArray2 = this.fAttributeTableView;
        int[] nArray = this.fAttributeTableViewChainState;
        int n2 = this.fLargeCount;
        for (int i = 0; i < n; ++i) {
            Attribute attribute = attributeArray[i];
            int n3 = this.getTableViewBucket(attribute.name.localpart, attribute.name.uri);
            if (nArray[n3] != n2) {
                nArray[n3] = n2;
                attribute.next = null;
                attributeArray2[n3] = attribute;
                continue;
            }
            int n4 = 0;
            Attribute attribute2 = attributeArray2[n3];
            while (attribute2 != null) {
                if (attribute2.name.localpart == attribute.name.localpart && attribute2.name.uri == attribute.name.uri) {
                    return attribute.name;
                }
                attribute2 = attribute2.next;
                ++n4;
            }
            if (n4 >= 40) {
                this.rebalanceTableViewNS(i + 1);
                n2 = this.fLargeCount;
                continue;
            }
            attribute.next = attributeArray2[n3];
            attributeArray2[n3] = attribute;
        }
        return null;
    }

    public int getIndexFast(String string, String string2) {
        for (int i = 0; i < this.fLength; ++i) {
            Attribute attribute = this.fAttributes[i];
            if (attribute.name.localpart != string2 || attribute.name.uri != string) continue;
            return i;
        }
        return -1;
    }

    private String getReportableType(String string) {
        if (string.charAt(0) == '(') {
            return "NMTOKEN";
        }
        return string;
    }

    protected int getTableViewBucket(String string) {
        return (this.hash(string) & Integer.MAX_VALUE) % this.fTableViewBuckets;
    }

    protected int getTableViewBucket(String string, String string2) {
        if (string2 == null) {
            return (this.hash(string) & Integer.MAX_VALUE) % this.fTableViewBuckets;
        }
        return (this.hash(string, string2) & Integer.MAX_VALUE) % this.fTableViewBuckets;
    }

    private int hash(String string) {
        if (this.fHashMultipliers == null) {
            return string.hashCode();
        }
        return this.hash0(string);
    }

    private int hash(String string, String string2) {
        if (this.fHashMultipliers == null) {
            return string.hashCode() + string2.hashCode() * 31;
        }
        return this.hash0(string) + this.hash0(string2) * this.fHashMultipliers[32];
    }

    private int hash0(String string) {
        int n = 0;
        int n2 = string.length();
        int[] nArray = this.fHashMultipliers;
        for (int i = 0; i < n2; ++i) {
            n = n * nArray[i & 0x1F] + string.charAt(i);
        }
        return n;
    }

    protected void cleanTableView() {
        if (++this.fLargeCount < 0) {
            if (this.fAttributeTableViewChainState != null) {
                for (int i = this.fTableViewBuckets - 1; i >= 0; --i) {
                    this.fAttributeTableViewChainState[i] = 0;
                }
            }
            this.fLargeCount = 1;
        }
    }

    private void growTableView() {
        int n = this.fLength;
        int n2 = this.fTableViewBuckets;
        do {
            if ((n2 = (n2 << 1) + 1) >= 0) continue;
            n2 = Integer.MAX_VALUE;
            break;
        } while (n > n2);
        this.fTableViewBuckets = n2;
        this.fAttributeTableView = null;
        this.fLargeCount = 1;
    }

    protected void prepareTableView() {
        if (this.fLength > this.fTableViewBuckets) {
            this.growTableView();
        }
        if (this.fAttributeTableView == null) {
            this.fAttributeTableView = new Attribute[this.fTableViewBuckets];
            this.fAttributeTableViewChainState = new int[this.fTableViewBuckets];
        } else {
            this.cleanTableView();
        }
    }

    protected void prepareAndPopulateTableView() {
        this.prepareAndPopulateTableView(this.fLength);
    }

    private void prepareAndPopulateTableView(int n) {
        this.prepareTableView();
        for (int i = 0; i < n; ++i) {
            Attribute attribute = this.fAttributes[i];
            int n2 = this.getTableViewBucket(attribute.name.rawname);
            if (this.fAttributeTableViewChainState[n2] != this.fLargeCount) {
                this.fAttributeTableViewChainState[n2] = this.fLargeCount;
                attribute.next = null;
                this.fAttributeTableView[n2] = attribute;
                continue;
            }
            attribute.next = this.fAttributeTableView[n2];
            this.fAttributeTableView[n2] = attribute;
        }
    }

    private void prepareAndPopulateTableViewNS(int n) {
        this.prepareTableView();
        for (int i = 0; i < n; ++i) {
            Attribute attribute = this.fAttributes[i];
            int n2 = this.getTableViewBucket(attribute.name.localpart, attribute.name.uri);
            if (this.fAttributeTableViewChainState[n2] != this.fLargeCount) {
                this.fAttributeTableViewChainState[n2] = this.fLargeCount;
                attribute.next = null;
                this.fAttributeTableView[n2] = attribute;
                continue;
            }
            attribute.next = this.fAttributeTableView[n2];
            this.fAttributeTableView[n2] = attribute;
        }
    }

    private void rebalanceTableView(int n) {
        if (this.fHashMultipliers == null) {
            this.fHashMultipliers = new int[33];
        }
        PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
        this.prepareAndPopulateTableView(n);
    }

    private void rebalanceTableViewNS(int n) {
        if (this.fHashMultipliers == null) {
            this.fHashMultipliers = new int[33];
        }
        PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
        this.prepareAndPopulateTableViewNS(n);
    }

    static class Attribute {
        public final QName name = new QName();
        public String type;
        public String value;
        public String nonNormalizedValue;
        public boolean specified;
        public Augmentations augs = new AugmentationsImpl();
        public Attribute next;

        Attribute() {
        }
    }
}

