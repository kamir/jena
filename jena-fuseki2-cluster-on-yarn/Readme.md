Since, there isn't a fuseki cluster yet, this subproject aims on running
a set of Fuseki servers as distributed application in a YARN cluster. 
An alternative approach is based on parcels, which allow shipping of services
via Cloudera Manager for a more static resource management. Here we implement
autoscaling based on query statistics.

A first goal is to provide a better performance for a high query throughput
on a rather small dataset but for many concurrent users. Fault tolerance is
a great side effect. This approach uses a single but replicated databases, not
yet a distributed data layer. 

We track all requests in a proxy mock-server to enable for further analysis. 
Quer distribution to all available instances is done by a load-balancer.
