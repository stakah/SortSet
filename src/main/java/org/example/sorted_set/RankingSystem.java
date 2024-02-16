package org.example.sorted_set;

import java.util.*;

public class RankingSystem {
    Map<Integer, TreeSet<Score>> scoreSet;

    public synchronized Map<Integer, TreeSet<Score>> getScoreSet() {
        if (scoreSet == null) scoreSet = new HashMap<>();
        return scoreSet;
    }
    public synchronized TreeSet<Score> getOrAddSet(Integer set) {
        TreeSet<Score> theSet;
        if (getScoreSet().keySet().contains(set) == false) {
            theSet = new TreeSet<>(new ScoreComparator());
            getScoreSet().put(set, theSet);
            scoreSet.put(set, theSet);
        } else {
            theSet = getScoreSet().get(set);
        }
        return theSet;
    }
    public synchronized Integer addScore(Integer set, Integer key, Integer score) {
        TreeSet<Score> theSet = getOrAddSet(set);
        theSet.add(new Score(key, score));
        return 0;
    }

    public synchronized Integer removeKey(Integer set, Integer key) {
        TreeSet<Score> theSet = getOrAddSet(set);
        theSet.remove(new Score(key));
        return 0;
    }

    public synchronized Integer getSize(Integer set) {
        if (getScoreSet().containsKey(set) == true) {
            return getScoreSet().get(set).size();
        }
        return 0;
    }

    public synchronized Integer getScore(Integer set, Integer key) {
        if (getScoreSet().containsKey(set)) {
            TreeSet<Score> theSet = getScoreSet().get(set);
            Score theScore = new Score(key);
            if (theSet.contains(theScore) == true) {
                Score aScore = theSet.floor(theScore);
                return aScore.key == key ? aScore.score : 0;
            }
        }
        return 0;
    }

    public  synchronized List<Score> getScoreInRange(List<Integer> sets, Integer lower, Integer upper) {
        List<Score> result = new ArrayList<>();
        Map<Integer, TreeSet<Score>> scoreSet = getScoreSet();
        for (Integer set : sets) {
            Set<Score> theSet = scoreSet.containsKey(set) ? scoreSet.get(set) : null;

            if (theSet != null) {
                List<Score> scores = new ArrayList<>();

                for (Score score : theSet) {
                    if (score.score >= lower && score.score <= upper) {
                        scores.add(score);
                    }
                }
                result.addAll(scores);
            }
        }
        return result;
    }
}
