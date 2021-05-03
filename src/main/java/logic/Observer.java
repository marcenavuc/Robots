package logic;

import utils.Tuple;

public interface Observer {
    void update(Tuple<Double, Double> m_robotPos, double m_robotDirect, Tuple<Integer, Integer> m_targetPos, long id);
}
