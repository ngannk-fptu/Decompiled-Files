/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import org.apache.axis.client.Call;
import org.apache.axis.utils.Messages;

public class Version {
    public static String getVersion() {
        return Messages.getMessage("axisVersion") + "\n" + Messages.getMessage("builtOn");
    }

    public static String getVersionText() {
        return Messages.getMessage("axisVersionRaw") + " " + Messages.getMessage("axisBuiltOnRaw");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(Version.getVersion());
        } else {
            try {
                Call call = new Call(args[0]);
                String result = (String)call.invoke("Version", "getVersion", null);
                System.out.println(result);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

