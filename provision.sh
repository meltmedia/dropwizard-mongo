#!/bin/bash
#
# Copyright (C) 2014 meltmedia (christian.trimble@meltmedia.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# Provisioning script executed by Vagrant.
#

# configure apt for mongo install
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | sudo tee /etc/apt/sources.list.d/mongodb.list

# add source for Java 8
sudo add-apt-repository -y ppa:webupd8team/java

sudo apt-get update

# install mongo
sudo apt-get install -y mongodb-org=2.6.4 mongodb-org-server=2.6.4 mongodb-org-shell=2.6.4 mongodb-org-mongos=2.6.4 mongodb-org-tools=2.6.4

# reconfigure mongo to bind to all interfaces.
sudo sed -i"" 's/^bind_ip = 127.0.0.1/bind_ip = 0.0.0.0/' /etc/mongod.conf 

# restart mongo to apply config changes.
sudo service mongod restart

# install Java 8
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
sudo apt-get install oracle-java8-installer --assume-yes

# add the example user.
mongo example --eval 'db.createUser({user: "example", pwd: "example", roles: ["readWrite"]});'
