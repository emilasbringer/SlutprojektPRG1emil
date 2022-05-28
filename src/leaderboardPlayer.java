public class leaderboardPlayer {
    private final int score;
    private final String name;

    public leaderboardPlayer(int score, String name) {
        this.score = score;
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }
}
