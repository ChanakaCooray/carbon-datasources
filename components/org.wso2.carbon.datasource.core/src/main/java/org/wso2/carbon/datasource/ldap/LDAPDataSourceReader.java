package org.wso2.carbon.datasource.ldap;

import com.sun.jndi.ldap.LdapCtxFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.datasource.core.exception.DataSourceException;
import org.wso2.carbon.datasource.core.spi.DataSourceReader;
import org.wso2.carbon.datasource.ldap.utils.LDAPDataSourceUtils;

import javax.naming.spi.InitialContextFactory;

/**
 *
 */
@Component(
        name = "org.wso2.carbon.datasource.ldap.LDAPDataSourceReader",
        immediate = true
)
public class LDAPDataSourceReader implements DataSourceReader {

    @Activate
    protected void activate(BundleContext bundleContext) {
//        bundleContext.registerService(DataSourceReader.class, this, null);
        bundleContext
                .registerService(new String[] { InitialContextFactory.class.getName(), LdapCtxFactory.class.getName() },
                        new LdapCtxFactory(), null);
    }

    @Override
    public String getType() {
        return LDAPConstants.LDAP_DATASOURCE_TYPE;
    }

    @Override
    public Object createDataSource(String xmlConfiguration, boolean isDataSourceFactoryReference)
            throws DataSourceException {
        LDAPDataSource dataSource = new LDAPDataSource(LDAPDataSourceUtils.buildConfiguration(xmlConfiguration));
        if (isDataSourceFactoryReference) {
            return dataSource.getDataSourceFactoryReference();
        } else {
            return dataSource.getDataSource();
        }
    }
}
