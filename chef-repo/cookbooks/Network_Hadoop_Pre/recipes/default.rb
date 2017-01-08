#
# Cookbook Name:: Network_Hadoop_Pre
# Recipe:: To configure the network and hadoop configuration
# To be run on master only
# Copyright 2014, Ankit Swarnkar
#
# All rights reserved - Do Not Redistribute
# Things to be done more :
# addd the code for the  hadoop config to enter in bin/slaves and bin/master

# Ensure that Hadoop user is present if not present
# NOTE : NOT TESTED>.. Last PART patch added n0t tested on 25 feb
# 1st MARCH : Bootstarp error : removed by letting hadoop mastre make /etc/host and copying it to all the data nodes.

#UPDATING the /etc/hosts

file "/etc/hosts" do
  action:delete
end
#Remove the already /etc/hosts and create a new one
file "/etc/hosts" do
 action:create
end

cookbook_file "/etc/hosts" do
 source "dup_hosts"
 action:create
end

#Fetchng the user_id and getting its ip
USER_ID = ""
require 'socket'
FQDN_MACHINE = Socket.gethostname
IP_MACHINE = IPSocket.getaddress(Socket.gethostname)
search(:USER,"master:*" ) do |test|
ip2= test["master"]
if(IP_MACHINE==ip2) then
TASK_ID = test["id"]
end
end

ip_master= IP_MACHINE
fqdn_master = FQDN_MACHINE
#Adding the FQDN MAPPING of master
ruby_block "insert_master" do
block do
File.open('/etc/hosts') { |source_file|
  contents = source_file.read
  contents.gsub!(/IP_OF_MASTER/,ip_master)
  contents.gsub!(/FQDN_OF_MASTER/,fqdn_master) 
  File.open("/etc/hosts", "w+") { |f| f.write(contents) } #check this 
}
end
end
#Adding the fqdn of slaves of the user
item = Chef::DataBagItem.load("USER",TASK_ID);
No_of_machine = item['No_of_Machine'].to_i
nodes= Array.new
$i =1

while $i<=No_of_machine do
  num = $i
  pop = "ip" + num.to_s
  nodes.push(item[pop])
  $i +=1
end
# Now the nodes cointain aal the slaves ip (s)
#Need to be test
hosts = search(:node,'role:slave')
ruby_block "insert_slave" do
block do
 hosts.each do |host|
 ip_test = host['ipaddress']
 if(nodes.include?(ip_test)) then

  file = Chef::Util::FileEdit.new("/etc/hosts")
  file.insert_line_if_no_match("#{host['fqdn']}" ,"#{host['ipaddress']}  #{host['fqdn']}" )
  file.write_file
  #puts host['fqdn']
   end
  end
 end
end

# Now copy the made file to the all the data nodes.. 

      hosts.each do |host|
        ip_test = host['ipaddress']
        if(nodes.include?(ip_test)) then
            
            execute"sc_hosts" do
             command "sshpass -p \"root123\" scp /etc/hosts #{host['ipaddress']}:/etc/hosts"
             user "root"
            end
        end
      end

