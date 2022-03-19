/**
 * <h1>FederationDBTest</h1>
 * The class contain testing get User 
 * and password matched 
 * for keycloak federation
 * <p>
 * 
 *
 * @author  Rizky Satrio
 * @version 1.0
 * @since   2020-10-22 
 */

package com.rizky.keycloak.federationdb.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.mockito.Mockito;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.mockito.internal.verification.AtLeast;
import org.mockito.internal.verification.Times;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.verification.VerificationMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rizky.keycloak.federationdb.FederationDBProvider;
import com.rizky.keycloak.federationdb.FederationDBProviderFactory;
import com.rizky.keycloak.federationdb.model.UserData;
import com.rizky.keycloak.federationdb.model.UserDatabase;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FederationDBTest {

	private static UserData userData;
	private static UserDatabase userDb;
	private static KeycloakSession session;
	private static RealmModel rModel;
	private static ComponentModel cModel;
	private static FederationDBProvider fdbProvider;
	private static MultivaluedHashMap<String, String> config;
	private static Logger log1=LoggerFactory.getLogger(FederationDBTest.class);

	@BeforeClass
	public static void prepareDataForTest() throws Exception	{
		session=Mockito.mock(KeycloakSession.class);
		rModel=Mockito.mock(RealmModel.class);
		cModel=Mockito.mock(ComponentModel.class);
		config=Mockito.mock(MultivaluedHashMap.class);

		fdbProvider=new FederationDBProviderFactory().create(session, cModel);
		userData=new UserData(session, rModel, cModel);
		userDb=new UserDatabase();
	}

	@Test
	public void Test1GettingUser() throws Exception	{
		
		String emailValue="test1@test.com";
		String emailValueFalse="test2@test.com";
		
		log1.info("Start");
		InitialContext initCtx=Mockito.mock(InitialContext.class);
		DataSource ds=Mockito.mock(DataSource.class);
		fdbProvider.setInitCtx(initCtx);
		

		Connection connect=Mockito.mock(Connection.class);
		Mockito.when(cModel.getConfig()).thenReturn(config);
		Mockito.when(config.getFirst(Mockito.anyString())).thenReturn("");
		Mockito.when(initCtx.lookup(Mockito.anyString())).thenReturn(ds);
		Mockito.when(ds.getConnection()).thenReturn(connect);
		PreparedStatement ps=Mockito.mock(PreparedStatement.class);
		Mockito.when(connect.prepareStatement(Mockito.anyString())).thenReturn(ps);

		ResultSet rs=Mockito.mock(ResultSet.class);
		Mockito.when(ps.executeQuery()).thenReturn(rs);

		Mockito.when(rs.next()).thenReturn(true);
		Mockito.when(rs.getString("email")).thenReturn(emailValue);
		Mockito.when(rs.getString("firstName")).thenReturn("Test User");

		UserProvider uProv=Mockito.mock(UserProvider.class);
		Mockito.when(session.userLocalStorage()).thenReturn(uProv);
		Mockito.when(uProv.getUserByEmail(Mockito.anyString(), Mockito.any())).thenReturn(null);
		Mockito.when(uProv.addUser(Mockito.any(), Mockito.anyString())).thenReturn(userData);

		UserModel return1=fdbProvider.getUserByEmail(emailValue, rModel);
		
		Assert.assertThat(return1.getEmail(), CoreMatchers.is(emailValue));
		
		Mockito.when(rs.next()).thenReturn(false);
		UserModel return2=fdbProvider.getUserByEmail(emailValueFalse, rModel);
		
		Assert.assertThat(return2, CoreMatchers.is(IsNull.nullValue()));



	}

	@Test
	public void Test2PasswordMatched() throws Exception	{
		
		String passwordValue="passTest";
		String passwordValueFalse="passTest2";
		
		log1.info("Start");
		InitialContext initCtx=Mockito.mock(InitialContext.class);
		DataSource ds=Mockito.mock(DataSource.class);
		fdbProvider.setInitCtx(initCtx);
		

		Connection connect=Mockito.mock(Connection.class);
		Mockito.when(cModel.getConfig()).thenReturn(config);
		Mockito.when(config.getFirst(Mockito.anyString())).thenReturn("");
		Mockito.when(initCtx.lookup(Mockito.anyString())).thenReturn(ds);
		Mockito.when(ds.getConnection()).thenReturn(connect);
		PreparedStatement ps=Mockito.mock(PreparedStatement.class);
		Mockito.when(connect.prepareStatement(Mockito.anyString())).thenReturn(ps);

		ResultSet rs=Mockito.mock(ResultSet.class);
		Mockito.when(ps.executeQuery()).thenReturn(rs);

		Mockito.when(rs.getString("password")).thenReturn(passwordValue);
		CredentialInput input1=Mockito.mock(CredentialInput.class);
		Mockito.when(input1.getChallengeResponse()).thenReturn(passwordValue);
		
		boolean passResult=fdbProvider.isValid(rModel, userData, input1);

		Assert.assertThat(passResult, CoreMatchers.is(true));

		Mockito.when(input1.getChallengeResponse()).thenReturn(passwordValueFalse);
		boolean passResult2=fdbProvider.isValid(rModel, userData, input1);
		Assert.assertThat(passResult2, CoreMatchers.is(false));


	}




}
