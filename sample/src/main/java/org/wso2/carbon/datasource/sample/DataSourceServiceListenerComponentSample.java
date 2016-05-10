/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.datasource.sample;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.jndi.JNDIContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.datasource.core.api.DataSourceManagementService;
import org.wso2.carbon.datasource.core.api.DataSourceService;
import org.wso2.carbon.datasource.core.beans.DataSourceMetadata;
import org.wso2.carbon.datasource.core.exception.DataSourceException;
import org.wso2.carbon.datasource.ldap.beans.LDAPDataSource;

import javax.naming.Context;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Example class.
 */
@Component(
        name = "org.wso2.carbon.kernel.datasource.sample",
        immediate = true
)
public class DataSourceServiceListenerComponentSample {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceServiceListenerComponentSample.class);
    private JNDIContextManager jndiContextManager;

    @Activate
    protected void start(BundleContext bundleContext) {
    }


    @Reference(
            name = "org.wso2.carbon.datasource.DataSourceService",
            service = DataSourceService.class,
            cardinality = ReferenceCardinality.AT_LEAST_ONE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unregisterDataSourceService"
    )
    protected void onDataSourceServiceReady(DataSourceService service) {
        LDAPDataSource dsObject;
        try {
            dsObject = (LDAPDataSource) service.getDataSource("WSO2_CARBON_DB");
            logger.info("Fetched data source: " + dsObject.toString());
        } catch (DataSourceException e) {
            e.printStackTrace();
        }
    }

    @Reference(
            name = "org.wso2.carbon.datasource.jndi",
            service = JNDIContextManager.class,
            cardinality = ReferenceCardinality.AT_LEAST_ONE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "onJNDIUnregister"
    )
    protected void onJNDIReady(JNDIContextManager service) {

        try {
            Context ctx = service.newInitialContext();
            Object obj = ctx.lookup("java:comp/env/ldap/WSO2CarbonDB/test");
            logger.info("Fetched data source: " + obj.toString());
            listProperties((LDAPDataSource) obj);
        } catch (NamingException e) {
            logger.info("Error occurred while jndi lookup", e);
        }
    }

    public void listProperties(LDAPDataSource source){

        logger.info("url of Dns: "+source.getUrlOfDns());
        logger.info("dns Domain Name: "+source.getDnsDomainName());

        logger.info("Environment Properties: ");
        source.getEnvironment().forEach((key, value) -> logger.info(key+": "+value));

        logger.info("Pooling Properties: ");
        source.getPoolingProperties().forEach((key, value) -> logger.info(key+": "+value));

    }

    protected void onJNDIUnregister(JNDIContextManager jndiContextManager) {
        logger.info("Unregistering data sources sample");
    }

    protected void unregisterDataSourceService(DataSourceService dataSourceService) {
        logger.info("Unregistering data sources sample");
    }
}
