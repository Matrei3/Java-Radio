package ro.mpp.Persistence;

import java.nio.file.Path;

public abstract class FileRepository<ID,T> extends AbstractRepository<ID,T> {
    protected final Path filePath;

    protected FileRepository(Path filePath) {
        this.filePath = filePath;
    }
    protected abstract void loadStations();
}
