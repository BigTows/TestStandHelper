package ru.uniteller.teststandhelper.entity;

import com.jetbrains.php.lang.psi.elements.PhpClass;
import ru.uniteller.teststandhelper.exception.TestStandHelperException;
import ru.uniteller.teststandhelper.util.ClassHelper;

import java.util.List;

import static ru.uniteller.teststandhelper.util.Config.NAME_SUBJECT_INTERFACE;

public class Subject {
    private SubjectEntity classSubject;

    public Subject(SubjectEntity classSubject) throws TestStandHelperException {
        this.classSubject = classSubject;
    }

    public SubjectEntity getClassSubject() {
        return classSubject;
    }

    public List<CommandSubject> getCommands() {
        return classSubject.getCommandSubject();
    }


    public static boolean isSubject(PhpClass phpClass) {
        return ClassHelper.isAncestor(phpClass, NAME_SUBJECT_INTERFACE);
    }


   /* public static Subject getInstance(PhpClass classSubject) {
        SubjectEntity
       return new Subject(new SubjectEntity(classSubject),

    }*/


}
