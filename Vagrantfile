# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.provider "virtualbox" do |v|
    v.memory = 2048
  end
  config.vm.network :forwarded_port, guest: 8080, host: 8180
  config.vm.network :forwarded_port, guest: 8081, host: 8181
  config.vm.network :forwarded_port, guest: 27017, host: 27017
  config.vm.network :forwarded_port, guest: 28017, host: 28017
  config.vm.provision :shell, :path => "provision.sh"
end
