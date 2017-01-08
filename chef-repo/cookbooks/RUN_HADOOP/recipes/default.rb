# Cookbook Name:: RUN_HADOOP
# Recipe:: Receipe to run the hadoop cluster and start the job flow
# INPUT : User_ID (from DATA_BAG )
#          INPUT_BUCKET (---" "---)
#          JAR FILE ("---")
#          DATA("---")
# Copyright 2014, YOUR_COMPANY_NAME
# Author : ANKIT SWARNKAR
# All rights reserved - Do Not Redistribute

Str_namespaceID = ""
IP_machine= ""
Server_IP = "11.11.3.216"
include_recipe "RUN_HADOOP::pre_run"

#Fetchng the user_id and getting its ip

USER_ID = ""
TASK_ID =""
require 'socket'

IP_MACHINE = IPSocket.getaddress(Socket.gethostname)
search(:USER,"master:*" ) do |test|
ip2= test["master"]
if(IP_MACHINE==ip2) then
  TASK_ID = test["id"]
  USER_ID = test["USER_ID"]
 end
end


#case 1 start
execute "start" do
command "java mysqlprogram/Status 1 #{TASK_ID}"
user "root"
cwd "/root/Java_CODE/src/"
action:run
end


#format the cluster
 execute "format_hadoop" do
  command "echo \"Y\" | bin/hadoop namenode -format"
  cwd "/opt/hadoop/hadoop"
  user "hadoop"
  group "hadoop"
  action :run
 end

#ensure that namespace id of namenode and data node are same

ruby_block "check_namespaceid" do
 block do
 File.open('/opt/hadoop/hadoop/dfs/name/current/VERSION') do |f|
 f.each_line do |line|
   if line =~ /^namespaceID=([0-9]*)/
      Str_namespaceID = line
   end
 end
end

File.open('/opt/hadoop/hadoop/dfs/name/data/current/VERSION')  { |source_file|
  contents = source_file.read
  contents.gsub!(/^namespaceID=([0-9]*)/,Str_namespaceID)
  File.open("/opt/hadoop/hadoop/dfs/name/data/current/VERSION", "w+") { |f| f.write(contents) }
}


 end
end

ip_master= IP_MACHINE

#CHANGE THE SCRIPT not to seach by role
#Adding the fqdn of slaves of the user
item = Chef::DataBagItem.load("USER",USER_ID);
No_of_machine = item['No_of_Machine'].to_i
nodes= Array.new
$i =1
while $i<=No_of_machine do
num = $i
pop = "ip" + num.to_s
nodes.push(item[pop])
$i +=1
end


hosts = search(:node, 'role:slave')
hosts.each do |host|
ip_test = host['ipaddress']
if(nodes.include?(ip_test)) then
  execute "Datanode_update" do
  cwd "/opt/hadoop/hadoop"
  user "hadoop"
  group "hadoop"
  command "scp -r dfs/name/data/ hadoop@#{host['ipaddress']}:/opt/hadoop/hadoop/dfs/name/ "
     end
end
end

#case 2 Cluster_setup
execute "Cluster_setup" do
command "java mysqlprogram/Status 2 #{TASK_ID}"
cwd "/root/Java_CODE/src/"
action :run
end


#Start the cluster
execute "start_hadoop" do
command "bin/start-all.sh"
cwd "/opt/hadoop/hadoop"
user "hadoop"
group "hadoop"
action :run
end

#### Get the user detail from the databags####


item = Chef::DataBagItem.load("USER",USER_ID);
JAR_NAME = item['JAR_NAME']
JAR_NAME_DIR= "/tmp/User_hadoop/jar/"+JAR_NAME
OUT_DIR = "/tmp/User_hadoop/output"
DATA_DIR ="/tmp/User_hadoop/data"
CLASS_NAME = item['CLASS_NAME']


#copy the data to HDFS
execute "copy_to_hdfs" do
command "bin/hadoop dfs -copyFromLocal #{DATA_DIR} /user/hduser/user_hadoop"
cwd "/opt/hadoop/hadoop"
user "hadoop"
group "hadoop"
action :run
end


#case 3 "JOB_Started"
execute "JOB_Started" do
command "java mysqlprogram/Status 3 #{TASK_ID}"
cwd "/root/Java_CODE/src/"
action :run
end

#Run the JOB
execute "run_hdfs" do
cwd "/opt/hadoop/hadoop"
user "hadoop"
group "hadoop"
command "bin/hadoop jar #{JAR_NAME_DIR} #{CLASS_NAME} /user/hduser/user_hadoop  /user/hduser/out_hadoop"
action :run
end

# Copy the data from HDFS
execute "copy_from_hdfs" do
cwd "/opt/hadoop/hadoop"
user "hadoop"
group "hadoop"
command "bin/hadoop dfs -getmerge /user/hduser/out_hadoop #{OUT_DIR}"
action :run
end

#Remove the user
execute "remove_hdfs" do
cwd "/opt/hadoop/hadoop"
user "hadoop"
group "hadoop"
command "bin/hadoop dfs -rmr /user/hduser/user_hadoop"
action :run
end

#Remove the Out
execute "remove_out" do
cwd "/opt/hadoop/hadoop"
user "hadoop"
group "hadoop"
command "bin/hadoop dfs -rmr /user/hduser/out_hadoop"
action :run
end

#Stop the hadoop cluster
execute "stop_hadoop" do
command "bin/stop-all.sh"
cwd "/opt/hadoop/hadoop"
user "hadoop"
group "hadoop"
action :run
end

#Copy the log data to server folder.
Log_Path = "/opt/hadoop/hadoop/libexec/../logs/"
execute "Copy_log" do
command "scp -r #{Log_Path}  #{Server_IP}:/home/hadoop/#{USER_ID}/#{TASK_ID}/logs/"
user "hadoop"
group "hadoop"
end

#Copy the temporary local directory to user
execute "Copy_tmp_server" do
command "scp -r /tmp/User_hadoop/output  #{Server_IP}:/home/hadoop/#{USER_ID}/#{TASK_ID}/output/"
user "hadoop"
group "hadoop"
end


#case 4 Finished
execute "Finished" do
command "java mysqlprogram/Status 4 #{TASK_ID}"
user "root"
cwd "/root/Java_CODE/src/"
action :run
end

