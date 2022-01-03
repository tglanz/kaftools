package tglanz.kaftools.cli.model;

public final class NodeInfo {
    private final int id;
    private final String host;
    private final int port;
    private final boolean hasRack;
    private final String rack;

    public NodeInfo(int id, String host, int port, boolean hasRack, String rack) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.hasRack = hasRack;
        this.rack = rack;
    }
}
