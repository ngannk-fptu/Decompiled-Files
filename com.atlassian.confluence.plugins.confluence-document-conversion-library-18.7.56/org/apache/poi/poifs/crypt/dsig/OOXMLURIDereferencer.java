/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.poifs.crypt.dsig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;

public class OOXMLURIDereferencer
implements URIDereferencer {
    private static final Logger LOG = LogManager.getLogger(OOXMLURIDereferencer.class);
    private SignatureInfo signatureInfo;
    private URIDereferencer baseUriDereferencer;

    public void setSignatureInfo(SignatureInfo signatureInfo) {
        this.signatureInfo = signatureInfo;
        this.baseUriDereferencer = signatureInfo.getSignatureFactory().getURIDereferencer();
    }

    @Override
    public Data dereference(URIReference uriReference, XMLCryptoContext context) throws URIReferenceException {
        InputStream dataStream;
        URI uri;
        block20: {
            if (uriReference == null) {
                throw new NullPointerException("URIReference cannot be null - call setSignatureInfo(...) before");
            }
            if (context == null) {
                throw new NullPointerException("XMLCryptoContext cannot be null");
            }
            try {
                uri = new URI(uriReference.getURI());
            }
            catch (URISyntaxException e) {
                throw new URIReferenceException("could not URL decode the uri: " + uriReference.getURI(), e);
            }
            PackagePart part = this.findPart(uri);
            if (part == null) {
                LOG.atDebug().log("cannot resolve {}, delegating to base DOM URI dereferencer", (Object)uri);
                return this.baseUriDereferencer.dereference(uriReference, context);
            }
            dataStream = null;
            try {
                dataStream = part.getInputStream();
                if (!part.getPartName().toString().endsWith(".rels")) break block20;
                try (UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();){
                    int ch;
                    while ((ch = dataStream.read()) != -1) {
                        if (ch == 10 || ch == 13) continue;
                        bos.write(ch);
                    }
                    dataStream = bos.toInputStream();
                }
            }
            catch (IOException e) {
                IOUtils.closeQuietly((InputStream)dataStream);
                throw new URIReferenceException("I/O error: " + e.getMessage(), e);
            }
        }
        return new OctetStreamData(dataStream, uri.toString(), null);
    }

    private PackagePart findPart(URI uri) {
        LOG.atDebug().log("dereference: {}", (Object)uri);
        String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            LOG.atDebug().log("illegal part name (expected): {}", (Object)uri);
            return null;
        }
        try {
            PackagePartName ppn = PackagingURIHelper.createPartName(path);
            return this.signatureInfo.getOpcPackage().getPart(ppn);
        }
        catch (InvalidFormatException e) {
            LOG.atWarn().log("illegal part name (not expected) in {}", (Object)uri);
            return null;
        }
    }
}

