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

package org.apache.provisionr.commands;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import java.io.PrintStream;
import java.util.List;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.apache.provisionr.api.Provisionr;

@Command(scope = "provisionr", name = "services", description = "List provisioning services")
public class ListServicesCommand extends OsgiCommandSupport {

    private PrintStream out = System.out;

    private final List<Provisionr> services;

    public ListServicesCommand(List<Provisionr> services) {
        this.services = checkNotNull(services, "services is null");
    }

    @Override
    protected Object doExecute() {
        List<String> ids = Lists.newArrayList();
        for (Provisionr service : services) {
            ids.add(service.getId());
        }
        out.printf("Services: %s%n", Joiner.on(", ").join(ids));
        return null;
    }

    @VisibleForTesting
    void setOut(PrintStream out) {
        this.out = checkNotNull(out, "out is null");
    }
}
