/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.stdlib.auth;

import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BValueArray;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * Configuration auth provider testcase.
 */
public class ConfigAuthProviderTest {

    private static final String BALLERINA_CONF = "ballerina.conf";
    private CompileResult compileResult;
    private Path secretCopyPath;

    @BeforeClass
    public void setup() throws IOException {
        String resourceRoot = Paths.get("src", "test", "resources").toAbsolutePath().toString();
        Path sourceRoot = Paths.get(resourceRoot, "test-src");
        Path ballerinaConfPath = Paths.get(resourceRoot, "datafiles", "config", "authprovider", BALLERINA_CONF);

        compileResult = BCompileUtil.compile(sourceRoot.resolve("config_auth_provider_test.bal").toString());

        String secretFile = "secret.txt";
        Path secretFilePath = Paths.get(resourceRoot, "datafiles", "config", secretFile);
        secretCopyPath = Paths.get(resourceRoot, "datafiles", "config", "authprovider", secretFile);
        Files.deleteIfExists(secretCopyPath);
        copySecretFile(secretFilePath.toString(), secretCopyPath.toString());

        // load configs
        ConfigRegistry registry = ConfigRegistry.getInstance();
        registry.initRegistry(Collections.singletonMap("b7a.config.secret", secretCopyPath.toString()),
                ballerinaConfPath.toString(), null);
    }

    private void copySecretFile(String from, String to) throws IOException {
        Files.copy(Paths.get(from), Paths.get(to));
    }

    @Test(description = "Test case for creating file based userstore")
    public void testCreateConfigAuthProvider() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testCreateConfigAuthProvider");
        Assert.assertNotNull(returns);
        Assert.assertTrue(returns[0] instanceof BMap);
    }

    @Test(description = "Test case for authenticating non-existing user")
    public void testAuthenticationOfNonExistingUser() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationOfNonExistingUser");
        Assert.assertNotNull(returns);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for authenticating with invalid password")
    public void testAuthenticationOfNonExistingPassword() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationOfNonExistingPassword");
        Assert.assertNotNull(returns);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for successful authentication")
    public void testAuthentication() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthentication");
        Assert.assertNotNull(returns);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for reading groups of non-existing user")
    public void testReadScopesOfNonExistingUser() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testReadScopesOfNonExistingUser");
        Assert.assertNotNull(returns);
        Assert.assertEquals(((BValueArray) returns[0]).size(), 0);
    }

    @Test(description = "Test case for reading groups of a user")
    public void testReadScopesOfUser() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testReadScopesOfUser");
        Assert.assertNotNull(returns);
        BValueArray groups = ((BValueArray) returns[0]);
        Assert.assertEquals(groups.size(), 1);

        Assert.assertEquals(groups.getString(0), "scope1");
    }

    @Test(description = "Test case for unsuccessful authentication when username is empty")
    public void testAuthenticationWithEmptyUsername() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationWithEmptyUsername");
        Assert.assertNotNull(returns);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for unsuccessful authentication when password is empty")
    public void testAuthenticationWithEmptyPassword() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationWithEmptyPassword");
        Assert.assertNotNull(returns);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for unsuccessful authentication when password is empty and username is invalid")
    public void testAuthenticationWithEmptyPasswordAndInvalidUsername() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationWithEmptyPasswordAndInvalidUsername");
        Assert.assertNotNull(returns);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for unsuccessful authentication when username and password are empty")
    public void testAuthenticationWithEmptyUsernameAndEmptyPassword() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationWithEmptyUsernameAndEmptyPassword");
        Assert.assertNotNull(returns);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for successful authentication with sha-256 hashed password")
    public void testAuthenticationSha256() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationSha256");
        Assert.assertNotNull(returns);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for successful authentication with sha-384 hashed password")
    public void testAuthenticationSha384() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationSha384");
        Assert.assertNotNull(returns);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for successful authentication with sha-512 hashed password")
    public void testAuthenticationSha512() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationSha512");
        Assert.assertNotNull(returns);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for successful authentication with plain text password")
    public void testAuthenticationPlain() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationPlain");
        Assert.assertNotNull(returns);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for unsuccessful authentication with sha-512 hashed password, using invalid " +
            "password")
    public void testAuthenticationSha512Negative() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationSha512Negative");
        Assert.assertNotNull(returns);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for unsuccessful authentication with plain text password, using invalid password")
    public void testAuthenticationPlainNegative() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationPlainNegative");
        Assert.assertNotNull(returns);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @AfterClass
    public void tearDown() throws IOException {
        Files.deleteIfExists(secretCopyPath);
    }
}
