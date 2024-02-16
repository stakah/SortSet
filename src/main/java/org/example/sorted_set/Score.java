package org.example.sorted_set;

public class Score {
    Integer key;
    Integer score;

    public Score(Integer key, Integer score) {
        this.key = key;
        this.score = score;
    }

    public Score(Integer key) {
        this.key = key;
        this.score = null;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (obj instanceof Score) {
            if (this.key == ((Score) obj).key) {
                return true;
            }
        }

        return false;
    }
}
