package ru.uniteller.teststandhelper.repository;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRepository<T> implements Repository<T> {

    private List<T> data = new ArrayList<>();
    private Project project;

    public AbstractRepository(Project project) {
        this.project = project;
        this.update();
    }

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
    protected void remove() {
        data.clear();
    }

    protected void add(T data) {
        this.data.add(data);
    }

    protected PhpIndex getPhpIndex(){
        return PhpIndex.getInstance(project);
    }

    public Project getProject() {
        return project;
    }
}
