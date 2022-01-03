Generate a random uuid for the cluster

    ./bin/kafka-storage.sh random-uuid

    # assume result is ivZQuvSGTJOAAE2OEXvysg  

Format and ramp up first server

    ./bin/kafka-storage.sh format -t ivZQuvSGTJOAAE2OEXvysg -c ./config/kraft/server-1.properties
    ./bin/kafka-server-start.sh ./config/kraft/server-1.properties

Format and ramp up second server

    ./bin/kafka-storage.sh format -t ivZQuvSGTJOAAE2OEXvysg -c ./config/kraft/server-2.properties
    ./bin/kafka-server-start.sh ./config/kraft/server-2.properties
