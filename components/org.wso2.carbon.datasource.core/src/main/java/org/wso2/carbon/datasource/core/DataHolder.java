package org.wso2.carbon.datasource.core;

import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIContextManager;
import org.wso2.carbon.datasource.core.spi.DataSourceReader;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DataHolder {

    private static DataHolder dataHolderInstance;
    private JNDIContextManager jndiContextManager;
    private BundleContext bundleContext;
    private Map<String, DataSourceReader> readers;

    private DataHolder() {
        readers = new HashMap<>();
    }

    public static DataHolder getDataHolderInstance() {
        if (dataHolderInstance == null) {
            dataHolderInstance = new DataHolder();
        }
        return dataHolderInstance;
    }

    public void addReader(DataSourceReader reader) {
        readers.put(reader.getType(), reader);
    }

    public void removeReader(DataSourceReader reader) {
        readers.remove(reader.getType());
    }

    public Map<String, DataSourceReader> getReaders() {
        return readers;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public JNDIContextManager getJndiContextManager() {
        return jndiContextManager;
    }

    public void setJndiContextManager(JNDIContextManager jndiContextManager) {
        this.jndiContextManager = jndiContextManager;
    }
}
