#
# Cookbook Name:: ssh_known_hosts
# Recipe:: Establish the ssh connection
#

# Ensure pakage sshpass is availble

#package "sshpass" do
#  action :install
#end

# Generate the key for ssh
execute "keygen" do
  command "ssh-keygen -b 2048 -t rsa -f /home/hadoop/.ssh/id_rsa -q -N \"\""
  creates "~/.ssh/id_rsa"
  user "hadoop"
  group "hadoop"
  not_if {::File.exists?("/home/hadoop/.ssh/id_rsa")}
end

#Get the Ip and the task_id

require 'socket'
IP_MACHINE = IPSocket.getaddress(Socket.gethostname)
Fqdn_master = Socket.gethostname
TASK_ID = ""
#puts "IP_MACHINE is #{IP_MACHINE}"
search(:hadoop,"master:*" ) do |test|
   ip2= test["master"]
   if (IP_MACHINE==ip2) then
      TASK_ID = test["id"]
   end
end

Server_IP = "11.11.3.201"

item = Chef::DataBagItem.load("USER",TASK_ID);
No_of_machine = item['No_of_Machine'].to_i
nodes= Array.new
$i =1
while $i<=(No_of_machine-1) do
  num = $i
  pop = "ip" + num.to_s
  nodes.push(item[pop])
  $i +=1
end

 nodes.push(IP_MACHINE)
 nodes.push(Server_IP)
#Seaching the nodes from Chef-server which have already bootstrapped

 hosts = search(:node,'*:*')

##Remove the stpoage due to man in middle approach

 execute "make_known_host" do
      command "echo \"#\" >> /home/hadoop/.ssh/known_hosts"
      user "hadoop"
  end

 
 execute "remove_Known_host" do
     command "\rm /home/hadoop/.ssh/known_hosts"
     user "hadoop"
     group "hadoop"
     cwd "/home/hadoop/"
     only_if {::File.exists?("~/.ssh/known_hosts")}
  end
  execute "make_known_host" do
      command "echo \"#\" >> /home/hadoop/.ssh/known_hosts"
      user "hadoop"
  end


HOST_FQDN = ""
# Loop over the hosts collection and runt the various commands
 hosts.each do |host|
  ip_test = host['ipaddress']
  if(nodes.include?(ip_test)) then
       HOST_FQDN = host['fqdn']
       if(host['fqdn']== Fqdn_master) then
        HOST_FQDN = "master" 
      end

#Sort the known hosts and verify for the duplicate entry
  execute "sshcopy" do
    command "ssh-keyscan -t rsa,dsa #{HOST_FQDN} 2>&1 | sort -u - /home/hadoop/.ssh/known_hosts > /home/hadoop/.ssh/tmp_hosts"
    user "hadoop"
    group "hadoop"
  end
# Cat the temporary file to known_hosts

   execute "sshcat" do
     command "cat /home/hadoop/.ssh/tmp_hosts >> /home/hadoop/.ssh/known_hosts"
     user "hadoop"
     group "hadoop"
     cwd "/home/hadoop"
   end

#Copy the key to the authorized key of host

   execute "sshauthetication" do
      command "cat /home/hadoop/.ssh/id_rsa.pub | sshpass -p \"hadoop123\" ssh hadoop@#{HOST_FQDN} \'cat >> /home/hadoop/.ssh/authorized_keys\'"
      user "hadoop"
      cwd "/home/hadoop"
      group "hadoop"
   end

# change the mode

   execute "ssh_modes" do
      command "sshpass -p \"hadoop123\" ssh hadoop@#{host['ipaddress']} \"chmod 700 /home/hadoop/.ssh; chmod 640 /home/hadoop/.ssh/authorized_keys\""
      user "hadoop"
      group "hadoop"
   end
 end
end


