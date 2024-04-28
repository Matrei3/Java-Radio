package ro.mpp.Persistence;

import java.util.Map;

public abstract class AbstractRepository<ID,T> implements Repository<ID,T> {
    protected Map<ID,T> entities;

}
