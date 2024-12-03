/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.dev;

import java.io.PrintStream;
import java.util.ArrayList;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.util.StringUtil;

public class TypesLister {
    public void listByName(PrintStream out) {
        ArrayList<MAPIProperty> all = new ArrayList<MAPIProperty>(MAPIProperty.getAll());
        all.sort((a, b) -> a.name.compareTo(b.name));
        this.list(all, out);
    }

    public void listById(PrintStream out) {
        ArrayList<MAPIProperty> all = new ArrayList<MAPIProperty>(MAPIProperty.getAll());
        all.sort((a, b) -> Integer.compare(a.id, b.id));
        this.list(all, out);
    }

    private void list(ArrayList<MAPIProperty> list, PrintStream out) {
        for (MAPIProperty attr : list) {
            StringBuilder id = new StringBuilder(Integer.toHexString(attr.id));
            int need0count = 4 - id.length();
            if (need0count > 0) {
                id.insert(0, StringUtil.repeat('0', need0count));
            }
            int typeId = attr.usualType.getId();
            String typeIdStr = Integer.toString(typeId);
            if (typeId > 0) {
                typeIdStr = typeIdStr + " / 0x" + Integer.toHexString(typeId);
            }
            out.println("0x" + id + " - " + attr.name);
            out.println("   " + attr.id + " - " + attr.usualType.getName() + " (" + typeIdStr + ") - " + attr.mapiProperty);
        }
    }

    public static void main(String[] args) {
        TypesLister lister = new TypesLister();
        lister.listByName(System.out);
        System.out.println();
        lister.listById(System.out);
    }
}

