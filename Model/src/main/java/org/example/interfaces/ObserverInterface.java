package org.example.interfaces;

import org.example.SwimmingException;
import org.example.domain.Participant;
import org.example.domain.Tuple3;

public interface ObserverInterface {
     void updateContests(Iterable<Tuple3> contests) throws SwimmingException;
}
