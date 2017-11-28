package ru.uniteller.teststandhelper.reposyitory;

import java.util.List;

public abstract class AbstractRepository<T> implements Repository<T> {

    private List<T> data;

    @Override
    public List<T> getAll() {
        return data;
    }

    @Override
    public void update() {
        this.remove();
    }

    /**
     * Очистка репозитория
     */
    private void remove() {
        data.clear();
    }
}
