/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.provisionr.cloudstack.commands;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import java.io.PrintStream;
import java.util.Properties;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.apache.provisionr.api.provider.Provider;
import org.apache.provisionr.cloudstack.DefaultProviderConfig;
import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.RestContext;


/**
 * Base class for CloudStack  Karaf Shell commands. It takes care of creating and cleaning a
 * {@link org.jclouds.cloudstack.CloudStackContext} for each command.
 */
public abstract class CommandSupport extends OsgiCommandSupport {

    public static final String CLOUDSTACK_SCOPE = "cloudstack";

    private final Provider provider;

    protected CommandSupport(DefaultProviderConfig providerConfig) {
        this.provider = providerConfig.createProvider().get();
    }

    public abstract Object doExecuteWithContext(CloudStackClient client, PrintStream out) throws Exception;

    @Override
    protected Object doExecute() throws Exception {
        RestContext<CloudStackClient, CloudStackAsyncClient> context = null;
        try {
            context = newCloudStackContext(provider);
            return doExecuteWithContext(context.getApi(), System.out);

        } finally {
            Closeables.closeQuietly(context);
        }
    }

    protected RestContext<CloudStackClient, CloudStackAsyncClient> newCloudStackContext(Provider provider) {
        Properties overrides = new Properties();
        overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
        return ContextBuilder.newBuilder(new CloudStackApiMetadata())
            .endpoint(provider.getEndpoint().get())
            .modules(ImmutableSet.of(new SLF4JLoggingModule()))
            .credentials(provider.getAccessKey(), provider.getSecretKey())
            .overrides(overrides)
            .build(CloudStackApiMetadata.CONTEXT_TOKEN);
    }

    public Provider getProvider() {
        return provider;
    }
}
