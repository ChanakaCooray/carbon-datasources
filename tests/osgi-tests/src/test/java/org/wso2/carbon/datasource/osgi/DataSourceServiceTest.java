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
package org.wso2.carbon.datasource.osgi;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.testng.listener.PaxExam;
import org.osgi.framework.BundleContext;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.wso2.carbon.datasource.core.api.DataSourceService;
import org.wso2.carbon.datasource.core.exception.DataSourceException;
import org.wso2.carbon.kernel.utils.CarbonServerInfo;

import java.nio.file.Paths;
import javax.inject.Inject;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.wso2.carbon.container.options.CarbonDistributionOption.copyDropinsBundle;
import static org.wso2.carbon.container.options.CarbonDistributionOption.copyFile;

@Listeners(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class DataSourceServiceTest {

    @Inject
    private BundleContext bundleContext;
    @Inject
    private CarbonServerInfo carbonServerInfo;
    @Inject
    private DataSourceService dataSourceService;

    /**
     * Replace the existing master-datasources.xml file with populated master-datasources.xml file.
     */
    private static Option copyDSConfigFile() {
        return copyFile(Paths.get("src", "test", "resources", "conf", "datasources", "master-datasources.xml"),
                Paths.get("conf", "datasources", "master-datasources.xml"));
    }

    @Configuration
    public Option[] createConfiguration() {
        return new Option[] {
                copyDropinsBundle(maven().artifactId("commons-io").groupId("commons-io.wso2").version("2.4.0.wso2v1")),
                copyDropinsBundle(maven().artifactId("HikariCP").groupId("com.zaxxer").version("2.4.1")),
                copyDropinsBundle(maven().artifactId("h2").groupId("com.h2database").version("1.4.191")),
                copyDSConfigFile(),
        };
    }

    @Test
    public void testDataSourceServiceInject() {
        Assert.assertNotNull(dataSourceService, "DataSourceService not found");
    }

    @Test
    public void testGetAllDataSources() {
        try {
            Object obj = dataSourceService.getDataSource("WSO2_CARBON_DB");
            Assert.assertNotNull(obj, "WSO2_CARBON_DB should exist");
        } catch (DataSourceException e) {
            Assert.fail("Threw an exception while retrieving data sources");
        }
    }
}
