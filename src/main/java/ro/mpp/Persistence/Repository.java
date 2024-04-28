package ro.mpp.Persistence;

import java.util.List;

public interface Repository<ID,T> {
    void addAll(List<T> entities);
    List<T> getAll();
}
