#
# Cookbook Name:: only_master
# Recipe:: default
#
# Copyright 2014, MAE101, 
# Creator: Ankit Swarnkar
#
# All rights reserved - Do Not Redistribute
# only for master.
#This is to add all the slaves ips in the conf/slaves and the master to the conf/master

include_recipe "only_master::remove"

TASK_ID = ""
require 'socket'
IP_MACHINE = IPSocket.getaddress(Socket.gethostname)
search(:USER,"master:*" ) do |test|
ip2= test["master"]
if(IP_MACHINE==ip2) then
TASK_ID = test["id"]
end
end

ip_master= IP_MACHINE

item = Chef::DataBagItem.load("USER",TASK_ID);
No_of_machine = item['No_of_Machine'].to_i
nodes= Array.new
$i =1
while $i<=No_of_machine-1 do
 num = $i
 pop = "ip" + num.to_s
 nodes.push(item[pop])
 $i +=1
end


hosts = search(:node,'role:slave')

ruby_block "update_slave" do     
block do
 hosts.each do |host|
 ip_test = host['ipaddress']
 if(nodes.include?(ip_test)) then
    file = Chef::Util::FileEdit.new("opt/hadoop/hadoop/conf/slaves")
    file.insert_line_if_no_match("#{host['fqdn']}" ,"#{host['fqdn']}" )
    file.write_file
  end
 end
end
end


