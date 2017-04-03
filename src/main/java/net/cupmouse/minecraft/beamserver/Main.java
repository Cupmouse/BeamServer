package net.cupmouse.minecraft.beamserver;

public class Main {
    private static Main instance;

    public static void main(String[] args) {
        instance = new Main();

        BeamServer beamServer = new BeamServer(35324);
        try {
            beamServer.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Main getInstance() {
        return instance;
    }

}
