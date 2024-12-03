/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.mappers;

import java.io.StringReader;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.UnsupportedAttributeException;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;

public class FilterMapper
extends FilterChain
implements FileNameMapper {
    private static final int BUFFER_SIZE = 8192;

    @Override
    public void setFrom(String from) {
        throw new UnsupportedAttributeException("filtermapper doesn't support the \"from\" attribute.", "from");
    }

    @Override
    public void setTo(String to) {
        throw new UnsupportedAttributeException("filtermapper doesn't support the \"to\" attribute.", "to");
    }

    @Override
    public String[] mapFileName(String sourceFileName) {
        if (sourceFileName == null) {
            return null;
        }
        try {
            StringReader stringReader = new StringReader(sourceFileName);
            ChainReaderHelper helper = new ChainReaderHelper();
            helper.setBufferSize(8192);
            helper.setPrimaryReader(stringReader);
            helper.setProject(this.getProject());
            Vector<FilterChain> filterChains = new Vector<FilterChain>();
            filterChains.add(this);
            helper.setFilterChains(filterChains);
            String result = FileUtils.safeReadFully(helper.getAssembledReader());
            if (result.isEmpty()) {
                return null;
            }
            return new String[]{result};
        }
        catch (BuildException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new BuildException(ex);
        }
    }
}

