public class leaderboardPlayer {
    private int score;
    private String name;

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
