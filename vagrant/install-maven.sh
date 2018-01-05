#!/bin/bash
set -e
set -o pipefail

MAVEN_VERSION=3.5.2
MAVEN_ARCHIVE=apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Install Maven
if [ ! -s "/tmp/vagrant-cache/${MAVEN_ARCHIVE}" ]; then
	echo "Downloading Maven ${MAVEN_VERSION} ..."
	wget --no-verbose http://mirror.evowise.com/apache/maven/maven-3/${MAVEN_VERSION}/binaries/${MAVEN_ARCHIVE} -O /tmp/vagrant-cache/${MAVEN_ARCHIVE}
	echo "... done."
fi

echo "Installing Maven ..."
mkdir $HOME/opt
cd $HOME/opt
tar xf /tmp/vagrant-cache/${MAVEN_ARCHIVE}
sudo ln -s $HOME/opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/local/bin
echo "export MAVEN_OPTS=\"-Xmx2048m\"" >> $HOME/.profile
echo "... done."
