package ru.uniteller.teststandhelper.repository;

import com.intellij.openapi.project.Project;

import java.util.List;

public interface Repository<T> {

    /**
     * Получение всех данных репзитория
     * @return Данные репозитория
     */
    public List<T> getAll();

    /**
     * Обновление данных репозитория
     */
    public void update();


}
