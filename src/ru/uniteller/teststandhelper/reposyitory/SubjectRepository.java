package ru.uniteller.teststandhelper.reposyitory;

import com.intellij.openapi.project.impl.ProjectImpl;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.List;

public class SubjectRepository extends AbstractRepository<PhpClass> {

    public SubjectRepository() {
        this.update();

    }


    @Override
    public void update() {
        super.update();
        
    }
}
