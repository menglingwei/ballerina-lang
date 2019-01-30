// Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

# Clone a given value.
#
# + value - Value to be cloned
# + return - Clone of the given value
extern function clone(anydata value) returns anydata;

# Freeze a given value.
#
# + value - Value to be frozen
# + return - Frozen value
extern function freeze(anydata value) returns anydata;

# Check freeze status of given value
#
# + value - Value to check freeze status
# + return - True for a frozen value
extern function isFrozen(anydata value) returns boolean;