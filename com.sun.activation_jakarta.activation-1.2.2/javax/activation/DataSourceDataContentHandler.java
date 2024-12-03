/*
 * Decompiled with CFR 0.152.
 */
package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.activation.UnsupportedDataTypeException;

class DataSourceDataContentHandler
implements DataContentHandler {
    private DataSource ds = null;
    private DataFlavor[] transferFlavors = null;
    private DataContentHandler dch = null;

    public DataSourceDataContentHandler(DataContentHandler dch, DataSource ds) {
        this.ds = ds;
        this.dch = dch;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        if (this.transferFlavors == null) {
            if (this.dch != null) {
                this.transferFlavors = this.dch.getTransferDataFlavors();
            } else {
                this.transferFlavors = new DataFlavor[1];
                this.transferFlavors[0] = new ActivationDataFlavor(this.ds.getContentType(), this.ds.getContentType());
            }
        }
        return this.transferFlavors;
    }

    @Override
    public Object getTransferData(DataFlavor df, DataSource ds) throws UnsupportedFlavorException, IOException {
        if (this.dch != null) {
            return this.dch.getTransferData(df, ds);
        }
        if (df.equals(this.getTransferDataFlavors()[0])) {
            return ds.getInputStream();
        }
        throw new UnsupportedFlavorException(df);
    }

    @Override
    public Object getContent(DataSource ds) throws IOException {
        if (this.dch != null) {
            return this.dch.getContent(ds);
        }
        return ds.getInputStream();
    }

    @Override
    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        if (this.dch == null) {
            throw new UnsupportedDataTypeException("no DCH for content type " + this.ds.getContentType());
        }
        this.dch.writeTo(obj, mimeType, os);
    }
}

