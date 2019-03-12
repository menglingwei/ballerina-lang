#!/bin/bash
# Copyright (c) 2019, WSO2 Inc. (http://wso2.org) All Rights Reserved.
#
# WSO2 Inc. licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
grand_parent_path=$(dirname ${parent_path})

cluster_name="ballerina-testgrid-cluster-v2"

# Read a property file to a given associative array
#
# $1 - Property file
# $2 - associative array
# How to call
# declare -A somearray
# read_property_file testplan-props.properties somearray
function read_property_file() {
    local testplan_properties=$1
    # Read configuration into an associative array
    # IFS is the 'internal field separator'. In this case, your file uses '='
    local -n configArray=$2
    IFS="="
    while read -r key value
    do
         configArray[$key]=$value
    done < ${testplan_properties}
    unset IFS
}

# Write key value pairs in a given associative array to a given property file
#
# $1 - file path
# $2 - associative array of key value pairs
function write_to_properties_file() {
    local properties_file_path=$1
    local -n properties_array=$2

    # Keys are accessed through exclamation point
    for i in ${!properties_array[@]}
    do
      echo ${i}=${properties_array[$i]} >> ${properties_file_path}
    done
}

# Install the provided Ballerina version
#
# $1 - Ballerina version
function install_ballerina() {
    local ballerina_version=$1
    wget https://product-dist.ballerina.io/downloads/${ballerina_version}/ballerina-${ballerina_version}.zip --quiet
    unzip ballerina-${ballerina_version}.zip -d ${parent_path}
    ${parent_path}/ballerina-${ballerina_version}/bin/ballerina version
    ballerina_home=${parent_path}/ballerina-${ballerina_version}
}

function download_and_extract_mysql_connector() {
    wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.47.tar.gz --quiet

    tar -xzf mysql-connector-java-5.1.47.tar.gz --directory ${parent_path}

    ls ${parent_path}/mysql-connector-java-5.1.47
}

# Generate a random namespace name
function generate_random_namespace() {
    echo "kubernetes-namespace"-$(generate_random_name)
}

# Generate a random database name
function generate_random_database_name() {
    echo "test-database"-$(generate_random_name)
}

function generate_random_name() {
    local prefix=$1
    local new_uuid=$(cat /dev/urandom | tr -dc 'a-z0-9' | fold -w 8 | head -n 1)
    echo ${new_uuid}
}

# Wait for pod readiness
function wait_for_pod_readiness() {
    TIMEOUT=300
    INTERVAL=20
    bash ${parent_path}/wait_for_pod_ready.sh ${TIMEOUT} ${INTERVAL}

    # Temporary sleep to check whether app eventually becomes ready..
    # Ideally there should have been a kubernetes readiness probe
    # which would make sure the "Ready" status would actually mean
    # the pod is ready to accept requests (app is ready) so the above
    # readiness script would suffice
    sleep 120s
}

# Set up environment
function setup_env() {
    input_dir=$1
    output_dir=$2
    bash ${parent_path}/setup_env.sh ${input_dir} ${output_dir}
}

# $1 - BBG section directory name
# $2 - Associative array of system property-value pairs
function build_bbg_section() {
    maven_profile=$1
    bbg_section=$2
    local -n properties_array=$3
    input_dir=$4
    output_dir=$5
    local sys_prop_str=""
    bash --version
    for x in "${!properties_array[@]}"; do str+="-D$x=${properties_array[$x]} " ; done

    mvn clean install -f ${grand_parent_path}/pom.xml -fae -Ddata.bucket.location=${input_dir} ${str} -P ${maven_profile}

    mkdir -p ${output_dir}/scenarios

    cp -r ${grand_parent_path}/bbg/${bbg_section}/target ${output_dir}/scenarios/${bbg_section}/
}
