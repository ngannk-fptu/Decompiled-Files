/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.Predictor;
import org.apache.pdfbox.io.IOUtils;

final class FlateFilter
extends Filter {
    private static final Log LOG = LogFactory.getLog(FlateFilter.class);

    FlateFilter() {
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        COSDictionary decodeParams = this.getDecodeParams(parameters, index);
        try {
            this.decompress(encoded, Predictor.wrapPredictor(decoded, decodeParams));
        }
        catch (DataFormatException e) {
            LOG.error((Object)"FlateFilter: stop reading corrupt stream due to a DataFormatException");
            throw new IOException(e);
        }
        return new DecodeResult(parameters);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void decompress(InputStream in, OutputStream out) throws IOException, DataFormatException {
        byte[] buf = new byte[2048];
        in.read();
        in.read();
        int read = in.read(buf);
        if (read > 0) {
            Inflater inflater = new Inflater(true);
            inflater.setInput(buf, 0, read);
            byte[] res = new byte[1024];
            boolean dataWritten = false;
            try {
                while (true) {
                    int resRead = 0;
                    try {
                        resRead = inflater.inflate(res);
                    }
                    catch (DataFormatException exception) {
                        if (dataWritten) {
                            LOG.warn((Object)"FlateFilter: premature end of stream due to a DataFormatException");
                            break;
                        }
                        throw exception;
                    }
                    if (resRead != 0) {
                        out.write(res, 0, resRead);
                        dataWritten = true;
                        continue;
                    }
                    if (inflater.finished() || inflater.needsDictionary()) break;
                    if (in.available() == 0) {
                        break;
                    }
                    read = in.read(buf);
                    inflater.setInput(buf, 0, read);
                }
            }
            finally {
                inflater.end();
            }
        }
        out.flush();
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        int compressionLevel = FlateFilter.getCompressionLevel();
        Deflater deflater = new Deflater(compressionLevel);
        DeflaterOutputStream out = new DeflaterOutputStream(encoded, deflater);
        IOUtils.copy(input, out);
        out.close();
        encoded.flush();
        deflater.end();
    }
}

