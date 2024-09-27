package connection_pool;

public interface Pool<T> {
    T borrow();
    void release(T obj);
    void shutDown();
    void initialize();
}
