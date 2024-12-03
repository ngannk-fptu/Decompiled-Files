/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.management;

import java.util.Comparator;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

public class ManagementUtils {
    public static final Comparator PARAM_INFO_COMPARATOR = new Comparator(){

        public int compare(Object object, Object object2) {
            MBeanParameterInfo mBeanParameterInfo = (MBeanParameterInfo)object;
            MBeanParameterInfo mBeanParameterInfo2 = (MBeanParameterInfo)object2;
            int n = mBeanParameterInfo.getType().compareTo(mBeanParameterInfo2.getType());
            if (n == 0 && (n = mBeanParameterInfo.getName().compareTo(mBeanParameterInfo2.getName())) == 0) {
                String string = mBeanParameterInfo.getDescription();
                String string2 = mBeanParameterInfo2.getDescription();
                n = string == null && string2 == null ? 0 : (string == null ? -1 : (string2 == null ? 1 : string.compareTo(string2)));
            }
            return n;
        }
    };
    public static final Comparator OP_INFO_COMPARATOR = new Comparator(){

        public int compare(Object object, Object object2) {
            String string;
            MBeanOperationInfo mBeanOperationInfo = (MBeanOperationInfo)object;
            MBeanOperationInfo mBeanOperationInfo2 = (MBeanOperationInfo)object2;
            String string2 = mBeanOperationInfo.getName();
            int n = String.CASE_INSENSITIVE_ORDER.compare(string2, string = mBeanOperationInfo2.getName());
            if (n == 0) {
                if (string2.equals(string)) {
                    MBeanParameterInfo[] mBeanParameterInfoArray;
                    MBeanParameterInfo[] mBeanParameterInfoArray2 = mBeanOperationInfo.getSignature();
                    if (mBeanParameterInfoArray2.length < (mBeanParameterInfoArray = mBeanOperationInfo2.getSignature()).length) {
                        n = -1;
                    } else if (mBeanParameterInfoArray2.length > mBeanParameterInfoArray.length) {
                        n = 1;
                    } else {
                        int n2 = mBeanParameterInfoArray2.length;
                        for (int i = 0; i < n2 && (n = PARAM_INFO_COMPARATOR.compare(mBeanParameterInfoArray2[i], mBeanParameterInfoArray[i])) == 0; ++i) {
                        }
                    }
                } else {
                    n = string2.compareTo(string);
                }
            }
            return n;
        }
    };
}

