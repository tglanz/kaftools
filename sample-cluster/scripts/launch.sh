#!/bin/bash

readonly LOG_LEVEL_INFO="INFO"
readonly LOG_LEVEL_ERROR="EROR"
readonly TASK_COLOR_CODES=(32 34 33 35 36)

function taskLog {
    local task_id=$1
    local level=$2
    local message=$3
    local color_code=${TASK_COLOR_CODES[$task_id]}
    echo -e "\e[${color_code}m[$task_id] [$level] $message\e[0m"
    if [[ $level == $LOG_LEVEL_ERROR ]]; then
        exit 1
    fi
}

function info {
    echo -e "\e[32m[$LOG_LEVEL_INFO]\e[0m $1"
}

function error {
    echo -e "\e[31m[$LOG_LEVEL_ERROR]\e[0m $1"
    exit 1
}

function extractNodeConfig {
    config="$1"
    id="$(grep -E "^node.id=" $config | grep -o "[^=]*$")"
    listeners="$(grep -E "^listeners=" $config | grep -o "[^=]*$")"
    roles="$(grep -E "^process.roles=" $config | grep -o "[^=]*$")"
    echo "(id=$id, roles=\"$roles\", listeners=$listeners)"
}

kafka_home=${KAFKA_HOME:-~/workspaces/tera/src/kafka}
node_configs=()
storage_uuid=
start_nodes=false
format_storage=false

readonly USAGE="usage: $0 [OPTIONS]

OPTIONS
  --kafka-home     Sets kafka's home directory.
                   Default: $kafka_home

  -c,--node-config A path to a node configuration file.

  --format-storage Perform a storage format for all configured nodes - data will be lost.

  --start-nodes    Start the configured nodes.

  --storage-uuid   The storage uuid during format.
                   If provided, used as the storage uuid, oterhwise will be generated.
                   Only required when doing a storage format.
                        
EXAMPLES
  # Launch the cluster with defaults
  $0
"

while [[ $# -gt 0 ]]; do
    case $1 in
        -c|--node-config) node_configs+=($2);
            shift; shift;;
        --kafka-home) kafka_home=$2;
            shift; shift;;
        --format-storage) format_storage=true;
            shift;;
        --storage-uuid) storage_uuid=$2;
            shift; shift;;
        --start-nodes) start_nodes=true;
            shift;;
        -h|--help) echo "$USAGE" && exit 1;
            shift;;
        *) echo "$USAGE" && error "Unknown argument: $1"; 
            shift;;
    esac
done

kafka_bin="$kafka_home/bin"
[[ -d $kafka_home ]] || error "Kafka Home - no such directory: $kafka_home"
[[ -d $kafka_bin ]] || error "Kafka Bin - no such directory: $kafka_bin"


[[ ${#node_configs[@]} == 0 ]] && error "No node configs provided, aborting."
for config in ${node_configs[@]}; do
    [[ -f $config ]] || error "Node config - file not found: $config"
done

if [[ $format_storage == true ]]; then
    if [[ -z $storage_uuid ]]; then
        info "Storage UUID was not provided, generating a new one."
        storage_uuid="$($kafka_bin/kafka-storage.sh random-uuid 2>/dev/null)"
    fi

    info "Formatting storage using uuid=${storage_uuid}"
    for config in ${node_configs[@]}; do
        info "Formatting node $(extractNodeConfig $config) configured by: $config"
        $kafka_bin/kafka-storage.sh format -t $storage_uuid -c $config 2>/dev/null
        [[ $? ]] || error "Encountered an error while formatting node configured by: $config, aborting."
    done
fi

if [[ $start_nodes == true ]]; then
    error "Starting nodes is still buggy, aborting."
    for config in ${node_configs[@]}; do
        info "Starting node configured by: $config"
        $kafka_bin/kafka-server-start.sh $config &
    done
fi

wait
