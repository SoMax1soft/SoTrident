package so.max1soft.sotrident;

import org.bukkit.entity.Trident;

public class TridentAttraction {
    private final Trident trident;
    private final long startTime;

    public TridentAttraction(Trident trident) {
        this.trident = trident;
        this.startTime = System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return (System.currentTimeMillis() - startTime) >= 5000; // 5 seconds
    }

    public Trident getTrident() {
        return trident;
    }
}
