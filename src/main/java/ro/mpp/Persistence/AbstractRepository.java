package ro.mpp.Persistence;

import java.util.Map;

/**
 * In-memory data persistence abstract class
 * @param <ID> - unique identifier for each T
 * @param <T> - entity which will be stored
 */
public abstract class AbstractRepository<ID,T> implements Repository<ID,T> {
    protected Map<ID,T> entities;

}
