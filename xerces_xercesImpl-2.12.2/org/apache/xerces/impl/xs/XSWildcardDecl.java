/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSWildcard;

public class XSWildcardDecl
implements XSWildcard {
    public static final String ABSENT = null;
    public short fType = 1;
    public short fProcessContents = 1;
    public String[] fNamespaceList;
    public XSObjectList fAnnotations = null;
    private String fDescription = null;

    public boolean allowNamespace(String string) {
        int n;
        int n2;
        if (this.fType == 1) {
            return true;
        }
        if (this.fType == 2) {
            n2 = 0;
            n = this.fNamespaceList.length;
            for (int i = 0; i < n && n2 == 0; ++i) {
                if (string != this.fNamespaceList[i]) continue;
                n2 = 1;
            }
            if (n2 == 0) {
                return true;
            }
        }
        if (this.fType == 3) {
            n2 = this.fNamespaceList.length;
            for (n = 0; n < n2; ++n) {
                if (string != this.fNamespaceList[n]) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isSubsetOf(XSWildcardDecl xSWildcardDecl) {
        if (xSWildcardDecl == null) {
            return false;
        }
        if (xSWildcardDecl.fType == 1) {
            return true;
        }
        if (this.fType == 2 && xSWildcardDecl.fType == 2 && this.fNamespaceList[0] == xSWildcardDecl.fNamespaceList[0]) {
            return true;
        }
        if (this.fType == 3) {
            if (xSWildcardDecl.fType == 3 && this.subset2sets(this.fNamespaceList, xSWildcardDecl.fNamespaceList)) {
                return true;
            }
            if (xSWildcardDecl.fType == 2 && !this.elementInSet(xSWildcardDecl.fNamespaceList[0], this.fNamespaceList) && !this.elementInSet(ABSENT, this.fNamespaceList)) {
                return true;
            }
        }
        return false;
    }

    public boolean weakerProcessContents(XSWildcardDecl xSWildcardDecl) {
        return this.fProcessContents == 3 && xSWildcardDecl.fProcessContents == 1 || this.fProcessContents == 2 && xSWildcardDecl.fProcessContents != 2;
    }

    public XSWildcardDecl performUnionWith(XSWildcardDecl xSWildcardDecl, short s) {
        if (xSWildcardDecl == null) {
            return null;
        }
        XSWildcardDecl xSWildcardDecl2 = new XSWildcardDecl();
        xSWildcardDecl2.fProcessContents = s;
        if (this.areSame(xSWildcardDecl)) {
            xSWildcardDecl2.fType = this.fType;
            xSWildcardDecl2.fNamespaceList = this.fNamespaceList;
        } else if (this.fType == 1 || xSWildcardDecl.fType == 1) {
            xSWildcardDecl2.fType = 1;
        } else if (this.fType == 3 && xSWildcardDecl.fType == 3) {
            xSWildcardDecl2.fType = (short)3;
            xSWildcardDecl2.fNamespaceList = this.union2sets(this.fNamespaceList, xSWildcardDecl.fNamespaceList);
        } else if (this.fType == 2 && xSWildcardDecl.fType == 2) {
            xSWildcardDecl2.fType = (short)2;
            xSWildcardDecl2.fNamespaceList = new String[2];
            xSWildcardDecl2.fNamespaceList[0] = ABSENT;
            xSWildcardDecl2.fNamespaceList[1] = ABSENT;
        } else if (this.fType == 2 && xSWildcardDecl.fType == 3 || this.fType == 3 && xSWildcardDecl.fType == 2) {
            String[] stringArray = null;
            String[] stringArray2 = null;
            if (this.fType == 2) {
                stringArray = this.fNamespaceList;
                stringArray2 = xSWildcardDecl.fNamespaceList;
            } else {
                stringArray = xSWildcardDecl.fNamespaceList;
                stringArray2 = this.fNamespaceList;
            }
            boolean bl = this.elementInSet(ABSENT, stringArray2);
            if (stringArray[0] != ABSENT) {
                boolean bl2 = this.elementInSet(stringArray[0], stringArray2);
                if (bl2 && bl) {
                    xSWildcardDecl2.fType = 1;
                } else if (bl2 && !bl) {
                    xSWildcardDecl2.fType = (short)2;
                    xSWildcardDecl2.fNamespaceList = new String[2];
                    xSWildcardDecl2.fNamespaceList[0] = ABSENT;
                    xSWildcardDecl2.fNamespaceList[1] = ABSENT;
                } else {
                    if (!bl2 && bl) {
                        return null;
                    }
                    xSWildcardDecl2.fType = (short)2;
                    xSWildcardDecl2.fNamespaceList = stringArray;
                }
            } else if (bl) {
                xSWildcardDecl2.fType = 1;
            } else {
                xSWildcardDecl2.fType = (short)2;
                xSWildcardDecl2.fNamespaceList = stringArray;
            }
        }
        return xSWildcardDecl2;
    }

    public XSWildcardDecl performIntersectionWith(XSWildcardDecl xSWildcardDecl, short s) {
        if (xSWildcardDecl == null) {
            return null;
        }
        XSWildcardDecl xSWildcardDecl2 = new XSWildcardDecl();
        xSWildcardDecl2.fProcessContents = s;
        if (this.areSame(xSWildcardDecl)) {
            xSWildcardDecl2.fType = this.fType;
            xSWildcardDecl2.fNamespaceList = this.fNamespaceList;
        } else if (this.fType == 1 || xSWildcardDecl.fType == 1) {
            XSWildcardDecl xSWildcardDecl3 = this;
            if (this.fType == 1) {
                xSWildcardDecl3 = xSWildcardDecl;
            }
            xSWildcardDecl2.fType = xSWildcardDecl3.fType;
            xSWildcardDecl2.fNamespaceList = xSWildcardDecl3.fNamespaceList;
        } else if (this.fType == 2 && xSWildcardDecl.fType == 3 || this.fType == 3 && xSWildcardDecl.fType == 2) {
            String[] stringArray = null;
            String[] stringArray2 = null;
            if (this.fType == 2) {
                stringArray2 = this.fNamespaceList;
                stringArray = xSWildcardDecl.fNamespaceList;
            } else {
                stringArray2 = xSWildcardDecl.fNamespaceList;
                stringArray = this.fNamespaceList;
            }
            int n = stringArray.length;
            String[] stringArray3 = new String[n];
            int n2 = 0;
            for (int i = 0; i < n; ++i) {
                if (stringArray[i] == stringArray2[0] || stringArray[i] == ABSENT) continue;
                stringArray3[n2++] = stringArray[i];
            }
            xSWildcardDecl2.fType = (short)3;
            xSWildcardDecl2.fNamespaceList = new String[n2];
            System.arraycopy(stringArray3, 0, xSWildcardDecl2.fNamespaceList, 0, n2);
        } else if (this.fType == 3 && xSWildcardDecl.fType == 3) {
            xSWildcardDecl2.fType = (short)3;
            xSWildcardDecl2.fNamespaceList = this.intersect2sets(this.fNamespaceList, xSWildcardDecl.fNamespaceList);
        } else if (this.fType == 2 && xSWildcardDecl.fType == 2) {
            if (this.fNamespaceList[0] != ABSENT && xSWildcardDecl.fNamespaceList[0] != ABSENT) {
                return null;
            }
            XSWildcardDecl xSWildcardDecl4 = this;
            if (this.fNamespaceList[0] == ABSENT) {
                xSWildcardDecl4 = xSWildcardDecl;
            }
            xSWildcardDecl2.fType = xSWildcardDecl4.fType;
            xSWildcardDecl2.fNamespaceList = xSWildcardDecl4.fNamespaceList;
        }
        return xSWildcardDecl2;
    }

    private boolean areSame(XSWildcardDecl xSWildcardDecl) {
        if (this.fType == xSWildcardDecl.fType) {
            if (this.fType == 1) {
                return true;
            }
            if (this.fType == 2) {
                return this.fNamespaceList[0] == xSWildcardDecl.fNamespaceList[0];
            }
            if (this.fNamespaceList.length == xSWildcardDecl.fNamespaceList.length) {
                for (int i = 0; i < this.fNamespaceList.length; ++i) {
                    if (this.elementInSet(this.fNamespaceList[i], xSWildcardDecl.fNamespaceList)) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    String[] intersect2sets(String[] stringArray, String[] stringArray2) {
        String[] stringArray3 = new String[Math.min(stringArray.length, stringArray2.length)];
        int n = 0;
        for (int i = 0; i < stringArray.length; ++i) {
            if (!this.elementInSet(stringArray[i], stringArray2)) continue;
            stringArray3[n++] = stringArray[i];
        }
        String[] stringArray4 = new String[n];
        System.arraycopy(stringArray3, 0, stringArray4, 0, n);
        return stringArray4;
    }

    String[] union2sets(String[] stringArray, String[] stringArray2) {
        String[] stringArray3 = new String[stringArray.length];
        int n = 0;
        for (int i = 0; i < stringArray.length; ++i) {
            if (this.elementInSet(stringArray[i], stringArray2)) continue;
            stringArray3[n++] = stringArray[i];
        }
        String[] stringArray4 = new String[n + stringArray2.length];
        System.arraycopy(stringArray3, 0, stringArray4, 0, n);
        System.arraycopy(stringArray2, 0, stringArray4, n, stringArray2.length);
        return stringArray4;
    }

    boolean subset2sets(String[] stringArray, String[] stringArray2) {
        for (int i = 0; i < stringArray.length; ++i) {
            if (this.elementInSet(stringArray[i], stringArray2)) continue;
            return false;
        }
        return true;
    }

    boolean elementInSet(String string, String[] stringArray) {
        boolean bl = false;
        for (int i = 0; i < stringArray.length && !bl; ++i) {
            if (string != stringArray[i]) continue;
            bl = true;
        }
        return bl;
    }

    public String toString() {
        if (this.fDescription == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("WC[");
            switch (this.fType) {
                case 1: {
                    stringBuffer.append("##any");
                    break;
                }
                case 2: {
                    stringBuffer.append("##other");
                    stringBuffer.append(":\"");
                    if (this.fNamespaceList[0] != null) {
                        stringBuffer.append(this.fNamespaceList[0]);
                    }
                    stringBuffer.append("\"");
                    break;
                }
                case 3: {
                    if (this.fNamespaceList.length == 0) break;
                    stringBuffer.append("\"");
                    if (this.fNamespaceList[0] != null) {
                        stringBuffer.append(this.fNamespaceList[0]);
                    }
                    stringBuffer.append("\"");
                    for (int i = 1; i < this.fNamespaceList.length; ++i) {
                        stringBuffer.append(",\"");
                        if (this.fNamespaceList[i] != null) {
                            stringBuffer.append(this.fNamespaceList[i]);
                        }
                        stringBuffer.append("\"");
                    }
                    break;
                }
            }
            stringBuffer.append(']');
            this.fDescription = stringBuffer.toString();
        }
        return this.fDescription;
    }

    @Override
    public short getType() {
        return 9;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public short getConstraintType() {
        return this.fType;
    }

    @Override
    public StringList getNsConstraintList() {
        return new StringListImpl(this.fNamespaceList, this.fNamespaceList == null ? 0 : this.fNamespaceList.length);
    }

    @Override
    public short getProcessContents() {
        return this.fProcessContents;
    }

    public String getProcessContentsAsString() {
        switch (this.fProcessContents) {
            case 2: {
                return "skip";
            }
            case 3: {
                return "lax";
            }
            case 1: {
                return "strict";
            }
        }
        return "invalid value";
    }

    @Override
    public XSAnnotation getAnnotation() {
        return this.fAnnotations != null ? (XSAnnotation)this.fAnnotations.item(0) : null;
    }

    @Override
    public XSObjectList getAnnotations() {
        return this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
}

