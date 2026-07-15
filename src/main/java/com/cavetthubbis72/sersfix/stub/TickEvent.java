package glitchcore.event;

public abstract class TickEvent {
    private final Phase phase;

    public TickEvent(Phase phase) {
        this.phase = phase;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public enum Phase { START, END }

    public static class Level extends TickEvent {
        private final net.minecraft.world.level.Level level;

        public Level(Phase phase, net.minecraft.world.level.Level level) {
            super(phase);
            this.level = level;
        }

        public net.minecraft.world.level.Level getLevel() {
            return this.level;
        }
    }
}
