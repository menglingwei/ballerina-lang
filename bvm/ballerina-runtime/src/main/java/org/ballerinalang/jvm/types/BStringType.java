/*
*  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.jvm.types;

import org.ballerinalang.jvm.util.BLangConstants;

/**
 * {@code BStringType} represents a String type in ballerina.
 *
 * @since 0.995.0
 */
@SuppressWarnings("unchecked")
public class BStringType extends BType {

    /**
     * Create a {@code BStringType} which represents the boolean type.
     *
     * @param typeName string name of the type
     */
    BStringType(String typeName, String pkgPath) {
        super(typeName, pkgPath, String.class);
    }

    public <V extends Object> V getZeroValue() {
        return (V) new String(BLangConstants.STRING_EMPTY_VALUE);
    }

    @Override
    public <V extends Object> V getEmptyValue() {
        return (V) new String(BLangConstants.STRING_EMPTY_VALUE);
    }

    @Override
    public int getTag() {
        return TypeTags.STRING_TAG;
    }
}
