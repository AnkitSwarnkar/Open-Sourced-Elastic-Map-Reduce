# Open Sourced Elastic Map Reduce
This was my undergrad project done in 2014 in which I have open sourced amazon web service EMR.
Me and my team created a web portal to spin one master many slave hadoop cluster of required node on top of private eucalyptus cloud.
Working:
Data scientist submit three things,
* Number of nodes required(master + slave)
* Jar file with hadoop function 
* Dataset
Our portal will spin three cloud machine, pre configure machines setting like network, users, role etc for hadoop installation, install hadoop on these machines, execute the hadoop task and simultaneously update the user about the progress of each set and finally store the result in Walrus storage provide by cloud which is like S3 in amazon. Finally, it shut down the machine.
Dedicated Machines:
It require at least 3 nodes are require to serve as cloud controller, chef server and finally machine for cloud. We used KVM hypervisor for launching our cloud.  
Technologies:
* Opscode chef was used for scripting
* We used jsp for front end pages
* Hadoop 2.7.3 was used
* Eucalyptus private cloud
* Chef server was used for version control and node information storage
