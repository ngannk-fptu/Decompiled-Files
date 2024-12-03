/*
 * Decompiled with CFR 0.152.
 */
package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.activation.UnsupportedDataTypeException;

class ObjectDataContentHandler
implements DataContentHandler {
    private DataFlavor[] transferFlavors = null;
    private Object obj;
    private String mimeType;
    private DataContentHandler dch = null;

    public ObjectDataContentHandler(DataContentHandler dch, Object obj, String mimeType) {
        this.obj = obj;
        this.mimeType = mimeType;
        this.dch = dch;
    }

    public DataContentHandler getDCH() {
        return this.dch;
    }

    @Override
    public synchronized DataFlavor[] getTransferDataFlavors() {
        if (this.transferFlavors == null) {
            if (this.dch != null) {
                this.transferFlavors = this.dch.getTransferDataFlavors();
            } else {
                this.transferFlavors = new DataFlavor[1];
                this.transferFlavors[0] = new ActivationDataFlavor(this.obj.getClass(), this.mimeType, this.mimeType);
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
            return this.obj;
        }
        throw new UnsupportedFlavorException(df);
    }

    @Override
    public Object getContent(DataSource ds) {
        return this.obj;
    }

    @Override
    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        if (this.dch != null) {
            this.dch.writeTo(obj, mimeType, os);
        } else if (obj instanceof byte[]) {
            os.write((byte[])obj);
        } else if (obj instanceof String) {
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write((String)obj);
            osw.flush();
        } else {
            throw new UnsupportedDataTypeException("no object DCH for MIME type " + this.mimeType);
        }
    }
}

