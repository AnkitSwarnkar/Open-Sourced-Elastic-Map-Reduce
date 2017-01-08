#
#PRE Recipe
#Purpose ensuring system is in correct state before running run hadoop script 
#Author : Ankit Swarnkar
#
#


#make sure /tmp/hadoop is removed so as to ensure that previous data is not res

SERVER_IP = "11.11.3.216"

execute "MAKE_DIR_User_Hadoop" do
  command "mkdir /tmp/User_hadoop/"
  user "hadoop"
  group "hadoop"
  not_if{ Dir.exist?("/tmp/User_hadoop/")}
end


execute "DEL_DIR_User_Hadoop" do
  command "rm -rf /tmp/User_hadoop/*"
  user "hadoop"
  group "hadoop"
  only_if{ Dir.exist?("/tmp/User_hadoop/")}
end

#Authenticate the server and machine master
  execute "sshcopy" do
    command "ssh-keyscan -t rsa,dsa #{SERVER_IP} 2>&1 | sort -u - /home/hadoop/.ssh/known_hosts > /home/hadoop/.ssh/tmp_hosts"
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
#



# get the user credentials
IP_MACHINE = IPSocket.getaddress(Socket.gethostname)
search(:USER,"master:*" ) do |test|
  ip2= test["master"]
  if(IP_MACHINE==ip2) then
    TASK_ID = test["id"]
    USER_ID = test["USER_ID"]
    break
  end
end


#copy the content from user bucket to local machine. (this machine)
execute "copy_from_server" do
 command "sshpass -p \"hadoop123\" scp -r hadoop@#{SERVER_IP}:/home/hadoop/#{USER_ID}/#{TASK_ID}/* /tmp/User_hadoop/"
 user "hadoop"
 cwd "/home/hadoop"
 group "hadoop"
end


