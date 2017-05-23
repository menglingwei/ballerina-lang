/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package runconfig;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class BallerinaRunLineMarkerTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testMainWithoutPackageRunLineMarker() {
        myFixture.configureByText("a.bal", "function <caret>main(string[] args){}\nfunction test(){}\n");
        assertEquals(1, myFixture.findGuttersAtCaret().size());
        assertEquals(1, myFixture.findAllGutters().size());
    }

    public void testServiceWithoutPackageRunLineMarker() {
        myFixture.configureByText("a.bal", "service <caret>main{}");
        assertEquals(1, myFixture.findGuttersAtCaret().size());
        assertEquals(1, myFixture.findAllGutters().size());
    }

    public void testMainWithPackageRunLineMarker() {
        myFixture.configureByText("a.bal", "package test;\nfunction <caret>main(string[] args){}\nfunction test(){}\n");
        assertEquals(1, myFixture.findGuttersAtCaret().size());
        assertEquals(1, myFixture.findAllGutters().size());
    }

    public void testServiceWithPackageRunLineMarker() {
        myFixture.configureByText("a.bal", "package test;\nservice <caret>main{}");
        assertEquals(1, myFixture.findGuttersAtCaret().size());
        assertEquals(1, myFixture.findAllGutters().size());
    }

    public void testMainAndServiceWithoutPackageRunLineMarker() {
        myFixture.configureByText("a.bal", "function <caret>main(string[] args){}\nservice main{}\n");
        assertEquals(1, myFixture.findGuttersAtCaret().size());
        assertEquals(2, myFixture.findAllGutters().size());
    }

    public void testMainAndServiceWithPackageRunLineMarker() {
        myFixture.configureByText("a.bal", "package test; function <caret>main(string[] args){}\nservice main{}\n");
        assertEquals(1, myFixture.findGuttersAtCaret().size());
        assertEquals(2, myFixture.findAllGutters().size());
    }
}
