/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class TransformInputOutput {
    private static URI currentJavaWorkingDirectory = new File(System.getProperty("user.dir")).toURI();

    public void parse(String[] args) throws Exception {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        if (args.length == 0) {
            in = new BufferedInputStream(System.in);
            out = new BufferedOutputStream(System.out);
        } else if (args.length == 1) {
            in = new BufferedInputStream(new FileInputStream(args[0]));
            out = new BufferedOutputStream(System.out);
        } else if (args.length == 2) {
            in = new BufferedInputStream(new FileInputStream(args[0]));
            out = new BufferedOutputStream(new FileOutputStream(args[1]));
        } else {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.optinalFileNotSpecified"));
        }
        this.parse(in, out);
    }

    public abstract void parse(InputStream var1, OutputStream var2) throws Exception;

    public void parse(InputStream in, OutputStream out, String workingDirectory) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected static EntityResolver createRelativePathResolver(final String workingDirectory) {
        return new EntityResolver(){

            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                if (systemId != null && systemId.startsWith("file:/")) {
                    URI workingDirectoryURI = new File(workingDirectory).toURI();
                    try {
                        URI workingFile = TransformInputOutput.convertToNewWorkingDirectory(currentJavaWorkingDirectory, workingDirectoryURI, new File(new URI(systemId)).toURI());
                        return new InputSource(workingFile.toString());
                    }
                    catch (URISyntaxException uRISyntaxException) {
                        // empty catch block
                    }
                }
                return null;
            }
        };
    }

    private static URI convertToNewWorkingDirectory(URI oldwd, URI newwd, URI file) throws IOException, URISyntaxException {
        int i;
        int diffNew;
        int diff;
        String oldwdStr = oldwd.toString();
        String newwdStr = newwd.toString();
        String fileStr = file.toString();
        String cmpStr = null;
        if (fileStr.startsWith(oldwdStr) && (cmpStr = fileStr.substring(oldwdStr.length())).indexOf(47) == -1) {
            return new URI(newwdStr + '/' + cmpStr);
        }
        String[] oldwdSplit = oldwdStr.split("/");
        String[] newwdSplit = newwdStr.split("/");
        String[] fileSplit = fileStr.split("/");
        for (diff = 0; diff < oldwdSplit.length && diff < fileSplit.length && oldwdSplit[diff].equals(fileSplit[diff]); ++diff) {
        }
        for (diffNew = 0; diffNew < newwdSplit.length && diffNew < fileSplit.length && newwdSplit[diffNew].equals(fileSplit[diffNew]); ++diffNew) {
        }
        if (diffNew > diff) {
            return file;
        }
        int elemsToSub = oldwdSplit.length - diff;
        StringBuffer resultStr = new StringBuffer(100);
        for (i = 0; i < newwdSplit.length - elemsToSub; ++i) {
            resultStr.append(newwdSplit[i]);
            resultStr.append('/');
        }
        for (i = diff; i < fileSplit.length; ++i) {
            resultStr.append(fileSplit[i]);
            if (i >= fileSplit.length - 1) continue;
            resultStr.append('/');
        }
        return new URI(resultStr.toString());
    }
}

