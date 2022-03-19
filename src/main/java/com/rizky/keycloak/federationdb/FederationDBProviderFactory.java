/*
 * Copyright 2020 Rizky Satrio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
* <h1>FederationDBProvideFactory</h1>
* The class contain factory to create User Federation Factory 
* for keycloak
* <p>
* 
*
* @author  Rizky Satrio
* @version 1.0
* @since   2020-10-22 
*/


package com.rizky.keycloak.federationdb;

import java.util.List;

import org.keycloak.Config.Scope;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FederationDBProviderFactory implements UserStorageProviderFactory<FederationDBProvider>{


	private Logger log1=LoggerFactory.getLogger(FederationDBProviderFactory.class);
	@Override
	public void close() {

		log1.info("Closing FederationDB Factory...");

	}


	@Override
	public void init(Scope arg0) {

		log1.info("Creating FederationProvider Factory...");
	}

	@Override
	public void postInit(KeycloakSessionFactory arg0) {

		log1.info("Finish creating FederationProvider Factory...");
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {

		return ProviderConfigurationBuilder.create()
				.property("Jndi_Name", "Database JNDI Name", "Database JNDI Name", 
						ProviderConfigProperty.STRING_TYPE, "java:jboss/datasources/PasswordDB", null)
						.property("Passwd_query", "Query to get Password by Email", 
								"Query to get Password by Email", 
								ProviderConfigProperty.STRING_TYPE, "select password from ws_user where email=?",
								null)
								.property("User_query", "Query to get User Data", 
										"Query to get User Data", 
										ProviderConfigProperty.STRING_TYPE, 
										"select email,name as firstName from ws_user where email=?",
										null)
										.build();
	}

	@Override
	public String getHelpText() {

		return "Federation DB Provider";
	}

	@Override
	public FederationDBProvider create(KeycloakSession session, ComponentModel model) {

		return new FederationDBProvider(session, model);
	}

	@Override
	public String getId() {

		return "Federation DB Provider";
	}

}
