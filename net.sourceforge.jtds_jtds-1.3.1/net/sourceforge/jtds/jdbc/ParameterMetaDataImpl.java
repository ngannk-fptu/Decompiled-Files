/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.Support;

public class ParameterMetaDataImpl
implements ParameterMetaData {
    private final ParamInfo[] parameterList;
    private final int maxPrecision;
    private final boolean useLOBs;

    public ParameterMetaDataImpl(ParamInfo[] parameterList, JtdsConnection connection) {
        if (parameterList == null) {
            parameterList = new ParamInfo[]{};
        }
        this.parameterList = parameterList;
        this.maxPrecision = connection.getMaxPrecision();
        this.useLOBs = connection.getUseLOBs();
    }

    @Override
    public int getParameterCount() throws SQLException {
        return this.parameterList.length;
    }

    @Override
    public int isNullable(int param) throws SQLException {
        return 2;
    }

    @Override
    public int getParameterType(int param) throws SQLException {
        if (this.useLOBs) {
            return this.getParameter((int)param).jdbcType;
        }
        return Support.convertLOBType(this.getParameter((int)param).jdbcType);
    }

    @Override
    public int getScale(int param) throws SQLException {
        ParamInfo pi = this.getParameter(param);
        return pi.scale >= 0 ? pi.scale : 0;
    }

    @Override
    public boolean isSigned(int param) throws SQLException {
        ParamInfo pi = this.getParameter(param);
        switch (pi.jdbcType) {
            case -5: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getPrecision(int param) throws SQLException {
        ParamInfo pi = this.getParameter(param);
        return pi.precision >= 0 ? pi.precision : this.maxPrecision;
    }

    @Override
    public String getParameterTypeName(int param) throws SQLException {
        return this.getParameter((int)param).sqlType;
    }

    @Override
    public String getParameterClassName(int param) throws SQLException {
        return Support.getClassName(this.getParameterType(param));
    }

    @Override
    public int getParameterMode(int param) throws SQLException {
        ParamInfo pi = this.getParameter(param);
        if (pi.isOutput) {
            return pi.isSet ? 2 : 4;
        }
        return pi.isSet ? 1 : 0;
    }

    private ParamInfo getParameter(int param) throws SQLException {
        if (param < 1 || param > this.parameterList.length) {
            throw new SQLException(Messages.get("error.prepare.paramindex", Integer.toString(param)), "07009");
        }
        return this.parameterList[param - 1];
    }

    public boolean isWrapperFor(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }
}

