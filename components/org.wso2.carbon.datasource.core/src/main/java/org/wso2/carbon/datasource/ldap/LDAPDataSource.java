package org.wso2.carbon.datasource.ldap;

import org.wso2.carbon.datasource.core.exception.DataSourceException;
import org.wso2.carbon.datasource.utils.DataSourceUtils;
import java.util.Map;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.directory.DirContext;

/**
 *
 */
public class LDAPDataSource {

    private static final String LDAP_DATASOURCE_CLASS = "javax.naming.Context";
    private static final String LDAP_JNDI_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    private DirContext dataSource;
    private Reference dataSourceFactoryReference;
    private LDAPConfiguration config;

    public LDAPDataSource(LDAPConfiguration config) throws DataSourceException {
        this.config = config;
        this.dataSourceFactoryReference = new Reference(LDAP_DATASOURCE_CLASS, LDAP_JNDI_FACTORY, null);
    }

    /**
     * Returns the LDAPDataSource by building it based on the configuration.
     *
     * @return {@link LDAPConnectionContext}
     */
    public DirContext getDataSource() {
        if (dataSource == null) {
            dataSource = new LDAPConnectionContext(config).getContext();
        }
        return dataSource;
    }

    /**
     * Returns a {@link Reference} object.
     *
     * @return {@link Reference}
     * @throws DataSourceException
     */
    public Reference getDataSourceFactoryReference() throws DataSourceException {
        Map<String, String> poolConfigMap = DataSourceUtils.extractPrimitiveFieldNameValuePairs(this.config);
        poolConfigMap.forEach((key, value) -> dataSourceFactoryReference.add(new StringRefAddr(key, value)));
        return dataSourceFactoryReference;
    }

}
