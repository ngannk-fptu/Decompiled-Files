/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef.dev;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.hmef.attribute.MAPIAttribute;
import org.apache.poi.hmef.attribute.TNEFAttribute;
import org.apache.poi.hmef.attribute.TNEFDateAttribute;
import org.apache.poi.hmef.attribute.TNEFProperty;
import org.apache.poi.hmef.attribute.TNEFStringAttribute;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class HMEFDumper {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;
    private InputStream inp;
    private boolean truncatePropertyData;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Filename must be given");
        }
        boolean truncatePropData = true;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--full")) {
                truncatePropData = false;
                continue;
            }
            try (FileInputStream stream = new FileInputStream(arg);){
                HMEFDumper dumper = new HMEFDumper(stream);
                dumper.setTruncatePropertyData(truncatePropData);
                dumper.dump();
            }
        }
    }

    public HMEFDumper(InputStream inp) throws IOException {
        this.inp = inp;
        int sig = LittleEndian.readInt(inp);
        if (sig != 574529400) {
            throw new IllegalArgumentException("TNEF signature not detected in file, expected 574529400 but got " + sig);
        }
        LittleEndian.readUShort(inp);
    }

    public void setTruncatePropertyData(boolean truncate) {
        this.truncatePropertyData = truncate;
    }

    private void dump() throws IOException {
        int level;
        int attachments = 0;
        while ((level = this.inp.read()) != -1) {
            TNEFAttribute attr = TNEFAttribute.create(this.inp);
            if (level == 2 && attr.getProperty() == TNEFProperty.ID_ATTACHRENDERDATA) {
                System.out.println();
                System.out.println("Attachment # " + ++attachments);
                System.out.println();
            }
            System.out.println("Level " + level + " : Type " + attr.getType() + " : ID " + attr.getProperty());
            String indent = "  ";
            if (attr instanceof TNEFStringAttribute) {
                System.out.println(indent + indent + indent + ((TNEFStringAttribute)attr).getString());
            }
            if (attr instanceof TNEFDateAttribute) {
                System.out.println(indent + indent + indent + ((TNEFDateAttribute)attr).getDate());
            }
            System.out.println(indent + "Data of length " + attr.getData().length);
            if (attr.getData().length > 0) {
                int loops;
                int len = attr.getData().length;
                if (this.truncatePropertyData) {
                    len = Math.min(attr.getData().length, 48);
                }
                if ((loops = len / 16) == 0) {
                    loops = 1;
                }
                for (int i = 0; i < loops; ++i) {
                    int thisLen = 16;
                    int offset = i * 16;
                    if (i == loops - 1) {
                        thisLen = len - offset;
                    }
                    byte[] data = IOUtils.safelyClone(attr.getData(), offset, thisLen, MAX_RECORD_LENGTH);
                    System.out.print(indent + HexDump.dump(data, 0L, 0));
                }
            }
            System.out.println();
            if (attr.getProperty() != TNEFProperty.ID_MAPIPROPERTIES && attr.getProperty() != TNEFProperty.ID_ATTACHMENT) continue;
            List<MAPIAttribute> attrs = MAPIAttribute.create(attr);
            for (MAPIAttribute ma : attrs) {
                System.out.println(indent + indent + ma);
            }
            System.out.println();
        }
    }
}

