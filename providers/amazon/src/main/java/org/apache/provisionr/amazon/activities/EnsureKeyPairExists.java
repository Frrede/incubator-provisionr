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

package org.apache.provisionr.amazon.activities;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.ImportKeyPairRequest;
import com.amazonaws.services.ec2.model.ImportKeyPairResult;
import org.apache.provisionr.amazon.core.ErrorCodes;
import org.apache.provisionr.amazon.core.KeyPairs;
import org.apache.provisionr.amazon.core.ProviderClientCache;
import org.apache.provisionr.api.pool.Pool;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsureKeyPairExists extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(EnsureKeyPairExists.class);

    public EnsureKeyPairExists(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        String keyName = KeyPairs.formatNameFromBusinessKey(execution.getProcessBusinessKey());
        LOG.info(">> Importing admin access key pair as {}", keyName);

        final String publicKey = pool.getAdminAccess().getPublicKey();
        try {
            importPoolPublicKeyPair(client, keyName, publicKey);

        } catch (AmazonServiceException e) {
            if (e.getErrorCode().equals(ErrorCodes.DUPLICATE_KEYPAIR)) {
                LOG.info("<< Duplicate key pair found. Re-importing from pool description");

                client.deleteKeyPair(new DeleteKeyPairRequest().withKeyName(keyName));
                importPoolPublicKeyPair(client, keyName, publicKey);
            }
        }
    }

    private void importPoolPublicKeyPair(AmazonEC2 client, String keyName, String publicKey) {
        ImportKeyPairResult result = client.importKeyPair(new ImportKeyPairRequest()
            .withKeyName(keyName).withPublicKeyMaterial(publicKey));
        LOG.info("<< Created remote key with fingerprint {}", result.getKeyFingerprint());
    }
}
