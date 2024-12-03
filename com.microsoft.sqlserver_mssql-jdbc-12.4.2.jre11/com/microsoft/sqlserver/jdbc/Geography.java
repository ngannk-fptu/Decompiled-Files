/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSpatialDatatype;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Geography
extends SQLServerSpatialDatatype {
    Geography() {
    }

    Geography(String wkt, int srid) throws SQLServerException {
        if (null == wkt || wkt.length() <= 0) {
            this.throwIllegalWKT();
        }
        this.wkt = wkt;
        this.srid = srid;
        this.parseWKTForSerialization(this, this.currentWktPos, -1, false);
        this.serializeToClr(false, this);
        this.isNull = false;
    }

    protected Geography(byte[] clr) throws SQLServerException {
        if (null == clr || clr.length <= 0) {
            this.throwIllegalByteArray();
        }
        this.clr = clr;
        this.buffer = ByteBuffer.wrap(clr);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
        this.parseClr(this);
        this.wktSb = new StringBuffer();
        this.wktSbNoZM = new StringBuffer();
        this.constructWKT(this, this.internalType, this.numberOfPoints, this.numberOfFigures, this.numberOfSegments, this.numberOfShapes);
        this.wkt = this.wktSb.toString();
        this.wktNoZM = this.wktSbNoZM.toString();
        this.isNull = false;
    }

    public static Geography STGeomFromText(String wkt, int srid) throws SQLServerException {
        return new Geography(wkt, srid);
    }

    public static Geography STGeomFromWKB(byte[] wkb) throws SQLServerException {
        return new Geography(wkb);
    }

    public static Geography deserialize(byte[] clr) throws SQLServerException {
        return new Geography(clr);
    }

    public static Geography parse(String wkt) throws SQLServerException {
        return new Geography(wkt, 4326);
    }

    public static Geography point(double lat, double lon, int srid) throws SQLServerException {
        return new Geography("POINT (" + lon + " " + lat + ")", srid);
    }

    public String STAsText() throws SQLServerException {
        if (null == this.wktNoZM) {
            this.buffer = ByteBuffer.wrap(this.clr);
            this.buffer.order(ByteOrder.LITTLE_ENDIAN);
            this.parseClr(this);
            this.wktSb = new StringBuffer();
            this.wktSbNoZM = new StringBuffer();
            this.constructWKT(this, this.internalType, this.numberOfPoints, this.numberOfFigures, this.numberOfSegments, this.numberOfShapes);
            this.wktNoZM = this.wktSbNoZM.toString();
        }
        return this.wktNoZM;
    }

    public byte[] STAsBinary() {
        if (null == this.wkb) {
            this.serializeToWkb(this);
        }
        return this.wkb;
    }

    public byte[] serialize() {
        return this.clr;
    }

    public boolean hasM() {
        return this.hasMvalues;
    }

    public boolean hasZ() {
        return this.hasZvalues;
    }

    public Double getLatitude() {
        if (null != this.internalType && this.internalType == SQLServerSpatialDatatype.InternalSpatialDatatype.POINT && this.yValues.length == 1) {
            return this.yValues[0];
        }
        return null;
    }

    public Double getLongitude() {
        if (null != this.internalType && this.internalType == SQLServerSpatialDatatype.InternalSpatialDatatype.POINT && this.xValues.length == 1) {
            return this.xValues[0];
        }
        return null;
    }

    public Double getM() {
        if (null != this.internalType && this.internalType == SQLServerSpatialDatatype.InternalSpatialDatatype.POINT && this.hasM()) {
            return this.mValues[0];
        }
        return null;
    }

    public Double getZ() {
        if (null != this.internalType && this.internalType == SQLServerSpatialDatatype.InternalSpatialDatatype.POINT && this.hasZ()) {
            return this.zValues[0];
        }
        return null;
    }

    public int getSrid() {
        return this.srid;
    }

    public boolean isNull() {
        return this.isNull;
    }

    public int STNumPoints() {
        return this.numberOfPoints;
    }

    public String STGeographyType() {
        if (null != this.internalType) {
            return this.internalType.getTypeName();
        }
        return null;
    }

    public String asTextZM() {
        return this.wkt;
    }

    public String toString() {
        return this.wkt;
    }
}

