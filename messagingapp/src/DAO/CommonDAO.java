package DAO;

public interface CommonDAO<T> {
boolean create(T entity)throws Exception;
T find(int id) throws Exception;
boolean update(T entity) throws Exception;
boolean delete(int id) throws Exception;
}
