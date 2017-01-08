#
#
#
#Cookbook: Remove the conf/slaves
#
#
#

HADOOP_CONF_DIR = '/opt/hadoop/hadoop'

file "#{HADOOP_CONF_DIR}/conf/slaves" do
action:delete
only_if{::File.exists?("#{HADOOP_CONF_DIR}/conf/slaves")}
end

file "#{HADOOP_CONF_DIR}/conf/slaves" do
 owner "hadoop"
 group "hadoop"
 mode 0644
action:create
end


