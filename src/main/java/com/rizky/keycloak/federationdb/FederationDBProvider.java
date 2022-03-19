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
 * <h1>FederationDBProvider</h1>
 * The class contain code to create Federation DB Provider 
 * for keycloak. It implements several interface from keycloak
 * that made the provider possible to:
 * - Create Local User 
 * - Lookup User from Federation DB
 * - Matched password from inputted value in Keycloak with value in Federation DB
 * <p>
 * 
 * @author  Rizky Satrio
 * @version 1.0
 * @since   2020-10-22 
 */

package com.rizky.keycloak.federationdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.UserModelDelegate;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rizky.keycloak.federationdb.model.UserData;
import com.rizky.keycloak.federationdb.model.UserDatabase;

public class FederationDBProvider  implements UserStorageProvider,
UserLookupProvider
,CredentialInputValidator

{

    private  KeycloakSession session;
    private  ComponentModel model;
    private UserModel userData;
    private UserDatabase userDb;
    private Logger log1=LoggerFactory.getLogger(FederationDBProvider.class);
    private InitialContext initCtx;



    public InitialContext getInitCtx() {
        return initCtx;
    }

    public void setInitCtx(InitialContext initCtx) {
        this.initCtx = initCtx;
    }

    public FederationDBProvider(KeycloakSession sess,ComponentModel model) {
        this.session=sess;
        this.model=model;
        try	{
            initCtx=new InitialContext();
        }
        catch(Exception e)	{
            log1.error("Cannot create InitialContext",e);
        }

    }

    @Override
    public void close() {
        log1.info("Closing FederationDB Provider");

    }


    @Override
    public boolean isConfiguredFor(RealmModel arg0, UserModel arg1, String credentialType) {

        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel arg0, UserModel arg1, CredentialInput passwd) {
        Connection conn1=null;
        PreparedStatement prep=null;


        try	{
            String dataSource1=model.getConfig().getFirst("Jndi_Name");
            String query1=model.getConfig().getFirst("Passwd_query");


            DataSource ds=(DataSource) initCtx
                    .lookup(dataSource1);
            conn1=ds.getConnection();
            prep=conn1.prepareStatement(query1);
            prep.setString(1, arg1.getEmail());

            ResultSet rs=prep.executeQuery();
            rs.next();
            String passwdDb=rs.getString("password");

            if(passwd.getChallengeResponse().equals(passwdDb))	{
                LoggerFactory.getLogger(FederationDBProvider.class).info("Password Matched for:"+arg1.getEmail());
                return true;
            }
            else	{
                LoggerFactory.getLogger(FederationDBProvider.class).info("Password Not Matched for:"+arg1.getEmail());
                return false;
            }
        }
        catch(Exception e)	{
            LoggerFactory.getLogger(FederationDBProvider.class).error("Password Validation Error",e);
            return false;
        }
        finally {
            try {
                if(prep!=null)  {
                    prep.close();
                }
                if(conn1!=null) {
                    conn1.close();
                }
            }
            catch(Exception e)  {

            }

        }


    }

    @Override
    public boolean supportsCredentialType(String arg0) {

        return arg0.equals(CredentialModel.PASSWORD);
    }

    @Override
    public UserModel getUserByEmail(String arg0, RealmModel arg1) {
        // TODO Auto-generated method stub

        Connection conn1=null;
        PreparedStatement prep=null;

        try	{
            log1.debug("Get User By Email: "+arg0);

            String dataSource1=model.getConfig().getFirst("Jndi_Name");
            String query1=model.getConfig().getFirst("User_query");

            DataSource ds=(DataSource) initCtx
                    .lookup(dataSource1);

            conn1=ds.getConnection();
            prep=conn1.prepareStatement(query1);
            prep.setString(1, arg0);

            ResultSet rs=prep.executeQuery();

            if(!rs.next())	{

                log1.info("Email: "+arg0+" not found");
                return null;
            }

            UserData userData=new UserData(session, arg1, model);
            userData.setEmail(rs.getString("email"));
            userData.setUsername(rs.getString("email"));
            userData.setFirstName(rs.getString("firstName"));

            UserModel local = session.userLocalStorage().getUserByEmail(arg0, arg1);
            if(local==null)	{
                log1.debug("Local User Not Found, adding user to Local");
                local = session.userLocalStorage().addUser(arg1, userData.getUsername());
                local.setFederationLink(model.getId());
                local.setEmail(userData.getEmail());
                local.setUsername(userData.getEmail());
                local.setCreatedTimestamp(System.currentTimeMillis());
                local.setFirstName(userData.getFirstName());
                local.setEnabled(true);
                local.setEmailVerified(true);
                log1.info("Local User Succesfully Created for email:"+userData.getEmail());

            }

            if(local!=null)	{
                log1.debug("Local User Exist,Delegating to Local User...");
                return new UserModelDelegate(local) {
                    @Override
                    public void setUsername(String username) {
                        super.setUsername(userData.getEmail());
                    }
                };
            }
            else	{
                log1.debug("Local User not found");
                return null;
            }
        }
        catch(Exception e)	{
            log1.error("Error getting user with email: "+arg0,e);
            return null;
        }
        finally {
            try {
                if(prep!=null)  {
                    prep.close();
                }
                if(conn1!=null) {
                    conn1.close();
                }
            }
            catch(Exception e)  {

            }

        }
    }

    @Override
    public UserModel getUserById(String arg0, RealmModel arg1) {
        // TODO Auto-generated method stub
        return getUserByEmail(arg0, arg1);
    }

    @Override
    public UserModel getUserByUsername(String arg0, RealmModel arg1) {
        // TODO Auto-generated method stub
        return getUserByEmail(arg0, arg1);
    }


}
