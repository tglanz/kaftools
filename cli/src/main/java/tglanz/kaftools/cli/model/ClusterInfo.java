package tglanz.kaftools.cli.model;

public class ClusterInfo {
    private String id;
    private NodeInfo[] nodes;

    public ClusterInfo(String id, NodeInfo[] nodes) {
        this.id = id;
        this.nodes = nodes;
    }
}
