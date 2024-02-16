package org.example.sorted_set;

import java.util.Comparator;

public class ScoreComparator implements Comparator<Score> {
    @Override
    public int compare(Score o1, Score o2) {
        if (o1.key == o2.key) {
            if (o1.score != null && o2.score != null) {
                if (o1.score > o2.score) {
                    return 1;
                } else if (o1.score < o2.score) {
                    return -1;
                }
            }
            return 0;
        } else if (o1.key < o2.key) {
            return -1;
        } else if (o1.key > o2.key) {
            return 1;
        }
        return 0;
    }
}
