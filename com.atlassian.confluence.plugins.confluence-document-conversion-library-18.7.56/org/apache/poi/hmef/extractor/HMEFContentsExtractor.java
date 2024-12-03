/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.hmef.Attachment;
import org.apache.poi.hmef.HMEFMessage;
import org.apache.poi.hmef.attribute.MAPIAttribute;
import org.apache.poi.hmef.attribute.MAPIRtfAttribute;
import org.apache.poi.hmef.attribute.MAPIStringAttribute;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.util.StringUtil;

public final class HMEFContentsExtractor {
    private final HMEFMessage message;

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Use:");
            System.err.println("  HMEFContentsExtractor <filename> <output dir>");
            System.err.println();
            System.err.println();
            System.err.println("Where <filename> is the winmail.dat file to extract,");
            System.err.println(" and <output dir> is where to place the extracted files");
            System.exit(2);
        }
        String filename = args[0];
        String outputDir = args[1];
        HMEFContentsExtractor ext = new HMEFContentsExtractor(new File(filename));
        File dir = new File(outputDir);
        File rtf = new File(dir, "message.rtf");
        if (!dir.exists()) {
            throw new FileNotFoundException("Output directory " + dir.getName() + " not found");
        }
        System.out.println("Extracting...");
        ext.extractMessageBody(rtf);
        ext.extractAttachments(dir);
        System.out.println("Extraction completed");
    }

    public HMEFContentsExtractor(File filename) throws IOException {
        this(new HMEFMessage(new FileInputStream(filename)));
    }

    public HMEFContentsExtractor(HMEFMessage message) {
        this.message = message;
    }

    public void extractMessageBody(File dest) throws IOException {
        MAPIAttribute body = this.getBodyAttribute();
        if (body == null) {
            System.err.println("No message body found, " + dest + " not created");
            return;
        }
        if (body instanceof MAPIStringAttribute) {
            String name = dest.toString();
            if (name.endsWith(".rtf")) {
                name = name.substring(0, name.length() - 4);
            }
            dest = new File(name + ".txt");
        }
        try (FileOutputStream fout = new FileOutputStream(dest);){
            if (body instanceof MAPIStringAttribute) {
                String text = ((MAPIStringAttribute)body).getDataString();
                ((OutputStream)fout).write(text.getBytes(StringUtil.UTF8));
            } else {
                ((OutputStream)fout).write(body.getData());
            }
        }
    }

    protected MAPIAttribute getBodyAttribute() {
        MAPIAttribute body = this.message.getMessageMAPIAttribute(MAPIProperty.RTF_COMPRESSED);
        if (body != null) {
            return body;
        }
        MAPIProperty uncompressedBody = MAPIProperty.createCustom(16345, Types.ASCII_STRING, "Uncompressed Body");
        return this.message.getMessageMAPIAttribute(uncompressedBody);
    }

    public void extractMessageBody(OutputStream out) throws IOException {
        MAPIRtfAttribute body = (MAPIRtfAttribute)this.message.getMessageMAPIAttribute(MAPIProperty.RTF_COMPRESSED);
        if (body != null) {
            out.write(body.getData());
        }
    }

    public void extractAttachments(File dir) throws IOException {
        int count = 0;
        for (Attachment att : this.message.getAttachments()) {
            ++count;
            String filename = att.getLongFilename();
            if (filename == null || filename.length() == 0) {
                filename = att.getFilename();
            }
            if (filename == null || filename.length() == 0) {
                filename = "attachment" + count;
                if (att.getExtension() != null) {
                    filename = filename + att.getExtension();
                }
            }
            File file = new File(dir, filename);
            FileOutputStream fout = new FileOutputStream(file);
            Throwable throwable = null;
            try {
                ((OutputStream)fout).write(att.getContents());
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                if (fout == null) continue;
                if (throwable != null) {
                    try {
                        ((OutputStream)fout).close();
                    }
                    catch (Throwable throwable3) {
                        throwable.addSuppressed(throwable3);
                    }
                    continue;
                }
                ((OutputStream)fout).close();
            }
        }
    }
}

